package main.java.structures.db.file.index;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import main.java.structures.db.config.AppConfig;
import main.java.structures.db.file.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Log
public class IndexFile implements AutoCloseable {

    private static final int PAGE_SIZE = AppConfig.getInstance().getPageBlockFactor() * IndexInfo.getSize();

    private final RandomAccessFile file;

    @Getter
    private final Page<IndexInfo> buffer;

    private int currentPageIndex = -1;

    @Setter
    private PageMode currentMode = PageMode.READ;

    private int potentialPageNumber = -1;

    public IndexFile(String fileName) throws FileNotFoundException {
        file = new RandomAccessFile(fileName, "rw");
        buffer = new Page<>();
    }

    public void displayWholeFile() {
        try {
            int pages = (int) Math.ceil((double) file.length() / PAGE_SIZE);
            IntStream.range(0, pages).forEach(page -> {
                readPage(page);
                System.out.println(buffer);
            });
        } catch (IOException e) {
            log.severe("Error reading file: " + e.getMessage());
        }
    }

    public void createFirstPage() {
        buffer.clear(new IndexInfo(-1, -1));
        buffer.getData().set(0, new IndexInfo(AppConfig.getInstance().getMinKey(), 0));
        currentPageIndex = 0;
        writePage(0);
        currentMode = PageMode.READ;
    }

    public void readPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        try {
            file.seek((long) pageNumber * PAGE_SIZE);
            buffer.read(file, IndexInfo.getSize(), IndexInfo::deserialize);
            currentMode = PageMode.READ;
            currentPageIndex = pageNumber;
        } catch (IOException e) {
            log.severe("Error reading page " + pageNumber + ": " + e.getMessage());
        }
    }

    protected void writePage(int pageNumber) {
        try {
            file.seek((long) pageNumber * PAGE_SIZE);
            buffer.write(file, IndexInfo::serialize);
            currentMode = PageMode.READ;
            currentPageIndex = pageNumber;
        } catch (IOException e) {
            log.severe("Error writing page " + pageNumber + ": " + e.getMessage());
        }
    }

    public int search(int key) throws IOException {
        int pages = (int) Math.ceil((double) file.length() / PAGE_SIZE);
        Optional<Integer> result = binarySearch(key, 0, pages - 1);
        return result.orElse(-1);
    }

    private Optional<Integer> binarySearch(int key, int left, int right) {
        if (left > right) {
            if (potentialPageNumber != -1) {
                int pageNumber = potentialPageNumber;
                potentialPageNumber = -1;
                return Optional.of(pageNumber);
            }
            return Optional.empty();
        }

        int middle = (left + right) / 2;
        readPage(middle);

        List<IndexInfo> contents = buffer.getData();
        if (contents.stream().allMatch(indexInfo -> indexInfo.getKey() == -1) && potentialPageNumber != -1) {
            int pageNumber = potentialPageNumber;
            potentialPageNumber = -1;
            return Optional.of(pageNumber);
        }

        for (int i = 1; i < contents.size(); i++) {
            int currentKey = contents.get(i).getKey();
            int previousKey = contents.get(i - 1).getKey();
            if (key >= previousKey && (key < currentKey || currentKey == -1)) {
                return Optional.of(contents.get(i - 1).getPageNumber());
            }
            if (key == currentKey) {
                return Optional.of(contents.get(i).getPageNumber());
            }
            if (i == contents.size() - 1 && key >= currentKey) {
                potentialPageNumber = contents.get(i).getPageNumber();
            }
        }

        int lastKey = contents.getLast().getKey();
        if (key > lastKey) {
            return binarySearch(key, middle + 1, right);
        }
        return binarySearch(key, left, middle - 1);
    }

    public void addIndex(int key, int pageNumber) throws IOException {
        currentMode = PageMode.WRITE;
        for (int i = 0; i < buffer.getData().size(); i++) {
            if (buffer.getData().get(i).getKey() == -1) {
                buffer.getData().set(i, new IndexInfo(key, pageNumber));
                if (i == buffer.getData().size() - 1) {
                    createPage(currentPageIndex + 1);
                }
                return;
            }
        }
    }

    private void createPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        buffer.clear(new IndexInfo(-1, -1));
        currentPageIndex = pageNumber;
        writePage(pageNumber);
        currentMode = PageMode.READ;
    }

    public boolean isEmpty() {
        try {
            return file.length() == 0;
        } catch (IOException e) {
            log.severe("Error checking if file is empty: " + e.getMessage());
            return true;
        }
    }

    @Override
    public void close() throws Exception {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        file.close();
    }
}
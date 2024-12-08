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
public class IndexFile {

    private static final int PAGE_SIZE = AppConfig.getInstance().getPageBlockFactor() * IndexInfo.getSize();

    private final RandomAccessFile file;

    @Getter
    private final Page<IndexInfo> buffer;

    private int currentPageIndex = -1;

    @Setter
    private PageMode currentMode = PageMode.READ;

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
        createPage(0);
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
            return Optional.empty();
        }

        int middle = (left + right) / 2;
        readPage(middle);

        List<IndexInfo> contents = buffer.getData();
        if (contents.isEmpty()) {
            return Optional.empty();
        }
        for (int i = 0; i < contents.size() - 1; i++) {
            int currentKey = contents.get(i).getKey();
            int nextKey = contents.get(i + 1).getKey();
            if (nextKey == currentKey && nextKey == -1) {
                return Optional.of(middle);
            }
            if (key >= currentKey && key < nextKey) {
                return Optional.of(middle);
            }
        }

        int lastKey = contents.getLast().getKey();
        if (key >= lastKey) {
            return binarySearch(key, middle + 1, right);
        } else {
            return binarySearch(key, left, middle - 1);
        }
    }

    public void addIndex(int key, int pageNumber) throws IOException {
        currentMode = PageMode.WRITE;
        buffer.getData().add(new IndexInfo(key, pageNumber));
        if (buffer.getData().size() == AppConfig.getInstance().getPageBlockFactor()) {
            int lastPage = (int) Math.ceil((double) file.length() / PAGE_SIZE) - 1;
            writePage(lastPage);
            createPage(buffer.getData().size());
        }
    }

    private void createPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        buffer.clear();
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
}
package main.java.structures.db.file;

import lombok.extern.java.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log
public class IndexFile extends File<IndexFileContents> {

    public IndexFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @Override
    protected void readPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        try {
            raFile.seek((long) pageNumber * PAGE_SIZE);
            buffer.read(raFile, IndexFileContents.getSize(), IndexFileContents::deserialize);
            currentPageIndex = pageNumber;
        } catch (Exception e) {
            log.severe("Error reading page " + pageNumber + ": " + e.getMessage());
        }
    }

    @Override
    protected void writePage(int pageNumber) {
        try {
            raFile.seek((long) pageNumber * PAGE_SIZE);
            buffer.write(raFile, IndexFileContents::serialize);
        } catch (Exception e) {
            log.severe("Error writing page " + pageNumber + ": " + e.getMessage());
        }
    }

    public int search(int key) throws IOException {
        int pages = (int) Math.ceil((double) raFile.length() / PAGE_SIZE);
        Optional<Integer> result = binarySearch(key, 0, pages - 1);
        return result.orElse(-1);
    }

    private Optional<Integer> binarySearch(int key, int left, int right) {
        if (left > right) {
            return Optional.empty();
        }

        int middle = (left + right) / 2;
        readPage(middle);

        List<PageElementContents<IndexFileContents>> contents = buffer.getData();
        if (contents.isEmpty()) {
            return Optional.empty();
        }
        for (int i = 0; i < contents.size() - 1; i++) {
            int currentKey = contents.get(i).getData().getKey();
            int nextKey = contents.get(i + 1).getData().getKey();
            if (key >= currentKey && key < nextKey) {
                return Optional.of(middle);
            }
        }

        int lastKey = contents.getLast().getData().getKey();
        if (key >= lastKey) {
            return binarySearch(key, middle + 1, right);
        } else {
            return binarySearch(key, left, middle - 1);
        }
    }
}

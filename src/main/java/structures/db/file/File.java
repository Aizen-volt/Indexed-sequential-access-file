package main.java.structures.db.file;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import main.java.structures.db.config.AppConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.IntStream;

@Log
abstract class File<T> implements AutoCloseable {

    protected static final int PAGE_SIZE = AppConfig.getInstance().getPageSize();

    protected final RandomAccessFile raFile;

    @Getter
    protected final Page<T> buffer;

    protected int currentPageIndex = -1;

    @Setter
    protected PageMode currentMode = PageMode.READ;

    protected File(String path) throws FileNotFoundException {
        raFile = new RandomAccessFile(path, "rw");
        buffer = new Page<>();
    }

    public void displayWholeFile() {
        try {
            int pages = (int) Math.ceil((double) raFile.length() / PAGE_SIZE);
            IntStream.range(0, pages).forEach(page -> {
                readPage(page);
                System.out.println(buffer);
            });
        } catch (IOException e) {
            log.severe("Error reading file: " + e.getMessage());
        }
    }

    protected void createPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        buffer.clear();
        currentPageIndex = pageNumber;
        writePage(pageNumber);
        currentMode = PageMode.READ;
    }

    protected abstract void readPage(int pageNumber);

    protected abstract void writePage(int pageNumber);

    @Override
    public void close() throws IOException {
        if (!buffer.isEmpty()) {
            writePage(currentPageIndex);
        }
        raFile.close();
    }

}

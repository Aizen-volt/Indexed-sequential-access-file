package main.java.structures.db.file;

import main.java.structures.db.config.AppConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

abstract class File<T> implements AutoCloseable {

    protected static final int PAGE_SIZE = AppConfig.getInstance().getPageSize();

    protected final RandomAccessFile raFile;
    protected final Page<T> buffer;
    protected int currentPageIndex = -1;
    protected PageMode currentMode = PageMode.READ;

    protected File(String path) throws FileNotFoundException {
        raFile = new RandomAccessFile(path, "rw");
        buffer = new Page<>();
    }

    protected void readPage(int pageNumber) {}

    protected void writePage(int pageNumber) {}

    @Override
    public void close() throws IOException {
        if (!buffer.isEmpty()) {
            writePage(currentPageIndex);
        }
        raFile.close();
    }

}

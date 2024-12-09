package main.java.structures.db.file.main;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import main.java.structures.db.config.AppConfig;
import main.java.structures.db.file.Page;
import main.java.structures.db.file.PageMode;
import main.java.structures.db.model.Element;
import main.java.structures.db.model.ElementInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.IntStream;

@Log
public class MainFile implements AutoCloseable {

    private static final int PAGE_SIZE = AppConfig.getInstance().getPageBlockFactor() * ElementInfo.getSize();

    private final RandomAccessFile file;

    @Getter
    private final Page<ElementInfo> buffer;

    @Getter
    private int currentPageIndex = -1;

    @Setter
    private PageMode currentMode = PageMode.READ;

    public MainFile(String fileName) throws FileNotFoundException {
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
        buffer.clear(new ElementInfo(new Element(-1, -1, -1, -1), false, -1));
        buffer.getData().set(0, new ElementInfo(new Element(AppConfig.getInstance().getMinKey(), -1, -1, -1), true, -1));
        currentPageIndex = 0;
        writePage(0);
        currentMode = PageMode.READ;
    }

    public void createPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        buffer.clear(new ElementInfo(new Element(-1, -1, -1, -1), false, -1));
        currentPageIndex = pageNumber;
        writePage(pageNumber);
        currentMode = PageMode.READ;
    }

    public void readPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        try {
            file.seek((long) pageNumber * PAGE_SIZE);
            buffer.read(file, ElementInfo.getSize(), ElementInfo::deserialize);
            currentMode = PageMode.READ;
            currentPageIndex = pageNumber;
        } catch (IOException e) {
            currentPageIndex = -1;
            buffer.clear(new ElementInfo(new Element(-1, -1, -1, -1), false, -1));
        }
    }

    private void writePage(int pageNumber) {
        try {
            file.seek((long) pageNumber * PAGE_SIZE);
            buffer.write(file, ElementInfo::serialize);
            currentMode = PageMode.READ;
            currentPageIndex = pageNumber;
        } catch (IOException e) {
            log.severe("Error writing page " + pageNumber + ": " + e.getMessage());
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

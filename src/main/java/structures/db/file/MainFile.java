package main.java.structures.db.file;

import lombok.extern.java.Log;
import main.java.structures.db.model.Element;

import java.io.FileNotFoundException;

@Log
public class MainFile extends File<Element> {

    public MainFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @Override
    protected void readPage(int pageNumber) {
        if (!currentMode.equals(PageMode.READ) && currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        try {
            raFile.seek((long) pageNumber * PAGE_SIZE);
            buffer.read(raFile, Element.getSize(), Element::deserialize);
            currentPageIndex = pageNumber;
        } catch (Exception e) {
            currentPageIndex = -1;
            buffer.clear();
        }
    }

    @Override
    protected void writePage(int pageNumber) {
        try {
            raFile.seek((long) pageNumber * PAGE_SIZE);
            buffer.write(raFile, Element::serialize);
        } catch (Exception e) {
            log.severe("Error writing page " + pageNumber + ": " + e.getMessage());
        }
    }

}

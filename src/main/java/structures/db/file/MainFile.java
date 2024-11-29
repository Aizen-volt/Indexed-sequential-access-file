package main.java.structures.db.file;

import lombok.extern.java.Log;
import main.java.structures.db.model.Element;

import java.io.FileNotFoundException;

@Log
public class MainFile extends File<Element> {

    public MainFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    protected void readPage(int pageNumber) {
        if (currentPageIndex != -1) {
            writePage(currentPageIndex);
        }
        try {
            file.seek((long) pageNumber * PAGE_SIZE);
            buffer.read(file, Element.getSize(), Element::deserialize);
            currentPageIndex = pageNumber;
        } catch (Exception e) {
            log.severe("Error reading page " + pageNumber + ": " + e.getMessage());
        }
    }

    protected void writePage(int pageNumber) {
        try {
            file.seek((long) pageNumber * PAGE_SIZE);
            buffer.write(file, Element::serialize);
        } catch (Exception e) {
            log.severe("Error writing page " + pageNumber + ": " + e.getMessage());
        }
    }

}

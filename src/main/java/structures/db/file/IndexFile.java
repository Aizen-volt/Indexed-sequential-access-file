package main.java.structures.db.file;

import lombok.extern.java.Log;

import java.io.FileNotFoundException;

@Log
public class IndexFile extends File<IndexFileContents> {

    public IndexFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    protected void readPage(int pageNumber) {
        if (currentPageIndex != -1) {
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

    protected void writePage(int pageNumber) {
        try {
            raFile.seek((long) pageNumber * PAGE_SIZE);
            buffer.write(raFile, IndexFileContents::serialize);
        } catch (Exception e) {
            log.severe("Error writing page " + pageNumber + ": " + e.getMessage());
        }
    }

}

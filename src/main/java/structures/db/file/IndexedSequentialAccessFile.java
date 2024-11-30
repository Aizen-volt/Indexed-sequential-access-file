package main.java.structures.db.file;

import lombok.extern.java.Log;
import main.java.structures.db.config.AppConfig;
import main.java.structures.db.model.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Log
public class IndexedSequentialAccessFile {

    private final IndexFile indexFile;
    private final MainFile mainFile;

    public IndexedSequentialAccessFile() throws FileNotFoundException {
        this.indexFile = new IndexFile(AppConfig.getInstance().getIndexFilePath());
        this.mainFile = new MainFile(AppConfig.getInstance().getMainFileName());
    }

    public void insertRecord(int key, int a, int b, int c) throws IOException {
        try {
            Optional<Integer> recordIndex = findRecord(key, true);
            recordIndex.ifPresent(integer ->
                    mainFile.buffer.getData().set(integer,
                            new PageElementContents<>(new Element(key, a, b, c), false, -1)));
            mainFile.setCurrentMode(PageMode.WRITE);
        } catch (RecordExistsException e) {
            log.warning("Record already exists");
        }
    }

    public void readRecord(int key) throws IOException {
        Optional<Integer> recordIndex = findRecord(key, false);
        recordIndex.ifPresent(integer -> System.out.println(mainFile.buffer.getData().get(integer).getData()));
    }

    private Optional<Integer> findRecord(int key, boolean forInsert) throws IOException, RecordExistsException {
        int pageNumber = indexFile.search(key);
        if (pageNumber == -1) {
            log.warning("Page for record not found");
            if (!forInsert) {
                return Optional.empty();
            }
            mainFile.createPage(mainFile.getBuffer().getData().size());
            indexFile.buffer.getData().add(
                    new PageElementContents<>(
                            new IndexFileContents(key, mainFile.getBuffer().getData().size() - 1), true, -1));
            indexFile.setCurrentMode(PageMode.WRITE);
            return Optional.of(0);
        }

        mainFile.readPage(pageNumber);

        PageElementContents<Element> previousRecord = null;
        for (var pageContents : mainFile.getBuffer().getData()) {
            if (!pageContents.isOccupied()) {
                continue;
            }
            if (pageContents.getData() == null) {
                continue;
            }
            if (pageContents.getData().key() == key) {
                return Optional.of(mainFile.getBuffer().getData().indexOf(pageContents));
            }
            if (pageContents.getData().key() > key) {
                // we have reached record with key higher than what we are looking for
                // we need to look by overflow pointer of previous record
                if (previousRecord == null) {
                    log.warning("Record not found");
                    return Optional.empty();
                }
                if (previousRecord.getOverflowPointer() == -1) {
                    log.warning("Record not found");
                    return Optional.empty();
                }

            }
            previousRecord = pageContents;
        }
    }

    public void displayAllRecords() {
        mainFile.displayWholeFile();
    }

    public void displayIndexFile() {
        indexFile.displayWholeFile();
    }

    public void reorganizeFile() {
    }

    public void deleteRecord(int key) throws IOException {
        boolean recordDeleted = false;
        try {
            findRecord(key, false);
        } catch (RecordExistsException e) {
            mainFile.buffer.getData().set(mainFile.getBuffer().getData().indexOf(e.getRecord()),
                    new PageElementContents<>(new Element(key, 0, 0, 0), false, e.getRecord().getOverflowPointer()));
            recordDeleted = true;
            mainFile.setCurrentMode(PageMode.WRITE);
        }
        if (!recordDeleted) {
            log.warning("No record found to delete");
        }
    }

    public void updateRecord(int key, int a, int b, int c) throws IOException {
        boolean recordUpdated = false;
        try {
            findRecord(key, false);
        } catch (RecordExistsException e) {
            mainFile.buffer.getData().set(mainFile.getBuffer().getData().indexOf(e.getRecord()),
                    new PageElementContents<>(new Element(key, a, b, c), true, e.getRecord().getOverflowPointer()));
            recordUpdated = true;
            mainFile.setCurrentMode(PageMode.WRITE);
        }
        if (!recordUpdated) {
            log.warning("No record found to update");
        }
    }

}

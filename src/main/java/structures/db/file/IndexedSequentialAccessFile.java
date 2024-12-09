package main.java.structures.db.file;

import lombok.Setter;
import lombok.extern.java.Log;
import main.java.structures.db.config.AppConfig;
import main.java.structures.db.file.index.IndexFile;
import main.java.structures.db.model.ElementInfo;
import main.java.structures.db.file.main.MainFile;
import main.java.structures.db.file.overflow.OverflowFile;
import main.java.structures.db.model.Element;

import java.io.IOException;
import java.util.Optional;

@Log
public class IndexedSequentialAccessFile {

    private final IndexFile indexFile;
    private final MainFile mainFile;
    private final OverflowFile overflowFile;

    @Setter
    private FileType currentFileType = FileType.NONE;

    private static Optional<Integer> overflowInMainFilePointer = Optional.empty();
    private static Optional<Integer> overflowInOverflowFilePointer = Optional.empty();

    public IndexedSequentialAccessFile() throws IOException {
        this.indexFile = new IndexFile(AppConfig.getInstance().getIndexFilePath());
        this.mainFile = new MainFile(AppConfig.getInstance().getMainFilePath());
        this.overflowFile = new OverflowFile(AppConfig.getInstance().getOverflowFilePath());

        if (indexFile.isEmpty()) {
            indexFile.createFirstPage();
            mainFile.createFirstPage();
            overflowFile.createFirstPage();
        }
    }

    public void insertRecord(int key, int a, int b, int c) throws IOException {
        try {
            Optional<Integer> recordIndex = findRecord(key, true);
            recordIndex.ifPresent(integer -> {
                if (currentFileType == FileType.MAIN) {
                    mainFile.getBuffer().getData().set(integer,
                            new ElementInfo(new Element(key, a, b, c), true, -1));
                    mainFile.setCurrentMode(PageMode.WRITE);
                } else {
                    overflowFile.getBuffer().getData().set(integer,
                            new ElementInfo(new Element(key, a, b, c), true, -1));
                    overflowFile.setCurrentMode(PageMode.WRITE);
                    if (overflowInMainFilePointer.isPresent()) {
                        mainFile.getBuffer().getData().get(overflowInMainFilePointer.get()).setOverflowPointer(integer);
                        mainFile.setCurrentMode(PageMode.WRITE);
                        overflowInMainFilePointer = Optional.empty();
                    }
                    if (overflowInOverflowFilePointer.isPresent()) {
                        overflowFile.getBuffer().getData().get(overflowInOverflowFilePointer.get()).setOverflowPointer(integer);
                        overflowFile.setCurrentMode(PageMode.WRITE);
                        overflowInOverflowFilePointer = Optional.empty();
                    }
                }
                currentFileType = FileType.NONE;
            });
            mainFile.setCurrentMode(PageMode.WRITE);
        } catch (RecordExistsException e) {
            log.warning("Record already exists");
        }
    }

    public void readRecord(int key) throws IOException {
        Optional<Integer> recordIndex = findRecord(key, false);
        recordIndex.ifPresent(integer -> System.out.println(mainFile.getBuffer().getData().get(integer).getData()));
    }

    private Optional<Integer> findRecord(int key, boolean forInsert) throws IOException, RecordExistsException {
        int pageNumber = indexFile.search(key);
        if (pageNumber == -1) {
            log.warning("Page for record not found");
            if (!forInsert) {
                return Optional.empty();
            }
            mainFile.createPage(mainFile.getBuffer().getData().size());
            indexFile.addIndex(key, mainFile.getBuffer().getData().size() - 1);
            return Optional.of(0);
        }

        mainFile.readPage(pageNumber);

        ElementInfo previousRecord = null;
        for (var pageContents : mainFile.getBuffer().getData()) {
            if (pageContents.getData().key() == -1) {
                currentFileType = FileType.MAIN;
                return Optional.of(mainFile.getBuffer().getData().indexOf(pageContents));
            }
            if (pageContents.getData().key() < key && previousRecord != null) {
                if (pageContents.getOverflowPointer() == -1) {
                    currentFileType = FileType.MAIN;
                    return Optional.of(mainFile.getBuffer().getData().indexOf(pageContents));
                }
                return findRecordInOverflow(key, pageContents.getOverflowPointer());
            }
            previousRecord = pageContents;
        }
        log.warning("Record not found");
        return Optional.empty();
    }

    private void insertToOverflow(int key, ElementInfo previousRecord) throws IOException {
        overflowFile.readPage(0);
        for (var pageContents : overflowFile.getBuffer().getData()) {
            if (!pageContents.isOccupied()) {
                continue;
            }
            if (pageContents.getData().key() == key) {
                throw new RecordExistsException("", pageContents);
            }
        }
        overflowFile.push(new ElementInfo(new Element(key, -1, -1, -1), true, -1));
        overflowInMainFilePointer = Optional.of(mainFile.getBuffer().getData().indexOf(previousRecord));
        overflowInOverflowFilePointer = Optional.of(overflowFile.getBuffer().getData().size() - 1);
    }

    private Optional<Integer> findRecordInOverflow(int key, int overflowPointer) throws IOException {
        int currentPhysicalAddress = overflowPointer;
        while (true) {
            int pageNumber = currentPhysicalAddress / AppConfig.getInstance().getPageBlockFactor();
            int index = currentPhysicalAddress % AppConfig.getInstance().getPageBlockFactor();
            overflowFile.readPage(pageNumber);
            ElementInfo elementInfo = overflowFile.getBuffer().getData().get(index);
            if (elementInfo.getData().key() == key) {
                currentFileType = FileType.OVERFLOW;
                return Optional.of(index);
            }
            if (elementInfo.getOverflowPointer() == -1) {
                log.warning("Record not found");
                return Optional.empty();
            }
            currentPhysicalAddress = elementInfo.getOverflowPointer();
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
            mainFile.getBuffer().getData().set(mainFile.getBuffer().getData().indexOf(e.getRecord()),
                    new ElementInfo(new Element(-1, -1, -1, -1), false, e.getRecord().getOverflowPointer()));
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
            mainFile.getBuffer().getData().set(mainFile.getBuffer().getData().indexOf(e.getRecord()),
                    new ElementInfo(new Element(key, a, b, c), true, e.getRecord().getOverflowPointer()));
            recordUpdated = true;
            mainFile.setCurrentMode(PageMode.WRITE);
        }
        if (!recordUpdated) {
            log.warning("No record found to update");
        }
    }

}

package main.java.structures.db.file;

import main.java.structures.db.config.AppConfig;

import java.io.FileNotFoundException;

public class IndexSequentialFile {

    private final IndexFile indexFile;
    private final MainFile mainFile;

    public IndexSequentialFile() throws FileNotFoundException {
        this.indexFile = new IndexFile(AppConfig.getInstance().getIndexFileName());
        this.mainFile = new MainFile(AppConfig.getInstance().getMainFileName());
    }

    public void insertRecord(int key, int a, int b, int c) {
    }

    public void readRecord(int key) {
    }

    public void displayAllRecords() {
        mainFile.displayWholeFile();
    }

    public void displayIndexFile() {
        indexFile.displayWholeFile();
    }

    public void reorganizeFile() {
    }

    public void deleteRecord(int key) {
    }

    public void updateRecord(int key, int a, int b, int c) {
    }

}

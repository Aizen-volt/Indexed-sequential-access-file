package main.java.structures.db.file;

import main.java.structures.db.model.Element;

class RecordExistsException extends RuntimeException {

    private PageElementContents<Element> record;

    RecordExistsException(String message, PageElementContents<Element> record) {
        super(message);
    }

    PageElementContents<Element> getRecord() {
        return record;
    }
}

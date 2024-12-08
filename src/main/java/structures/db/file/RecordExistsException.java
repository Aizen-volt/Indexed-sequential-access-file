package main.java.structures.db.file;

import main.java.structures.db.model.ElementInfo;

class RecordExistsException extends RuntimeException {

    private ElementInfo record;

    RecordExistsException(String message, ElementInfo record) {
        super(message);
    }

    ElementInfo getRecord() {
        return record;
    }

}

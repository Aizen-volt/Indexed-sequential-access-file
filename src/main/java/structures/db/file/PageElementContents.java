package main.java.structures.db.file;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
class PageElementContents<T> {

    private T data;

    private boolean occupied;

    private int overflowPointer;

    PageElementContents(T data, boolean occupied, int overflowPointer) {
        this.data = data;
        this.occupied = occupied;
        this.overflowPointer = overflowPointer;
    }

    public PageElementContents<T> empty() {
        return new PageElementContents<>(null, false, -1);
    }

    @Override
    public String toString() {
        return occupied ? data.toString() : "";
    }

}

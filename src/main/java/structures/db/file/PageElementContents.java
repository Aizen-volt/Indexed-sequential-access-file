package main.java.structures.db.file;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
class PageElementContents<T> {

    private T data;

    private boolean occupied;

    PageElementContents(T data, boolean occupied) {
        this.data = data;
        this.occupied = occupied;
    }

    public PageElementContents<T> empty() {
        return new PageElementContents<>(null, false);
    }

    @Override
    public String toString() {
        return occupied ? data.toString() : "";
    }

}

package main.java.structures.db.file;

import lombok.Getter;
import lombok.Setter;

public class PageElementContents<T> {

    @Getter
    @Setter
    private T data;

    @Getter
    @Setter
    private boolean occupied;

    PageElementContents(T data, boolean occupied) {
        this.data = data;
        this.occupied = occupied;
    }

    public PageElementContents<T> empty() {
        return new PageElementContents<>(null, false);
    }
}

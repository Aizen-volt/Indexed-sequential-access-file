package main.java.structures.db.file;

import main.java.structures.db.config.AppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Page<T> {
    private final List<PageElementContents<T>> data;

    public Page() {
        this.data = new ArrayList<>(AppConfig.getInstance().getPageBlockFactor());
    }

    public Page(List<PageElementContents<T>> data) {
        this.data = data;
    }

    public void set(int index, T element) {
        if (index >= AppConfig.getInstance().getPageBlockFactor() || index < 0) {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bound!", index));
        }
        if (data.get(index).isOccupied()) {
            throw new IndexOccupiedException(String.format("Element at index %d already exists!", index));
        }
        data.set(index, new PageElementContents<>(
                element, true
        ));
    }

    public void remove(int index) {
        if (index >= AppConfig.getInstance().getPageBlockFactor() || index < 0) {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bound!", index));
        }
        if (!data.get(index).isOccupied()) {
            throw new EmptyIndexException(String.format("Element at index %d is empty!", index));
        }
        data.set(index, data.get(index).empty());
    }

    public boolean isFull() {
        return data.size() >= AppConfig.getInstance().getPageBlockFactor();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void clear() {
        IntStream.range(0, data.size()).forEach(i ->
            data.set(i, data.get(i).empty()));
    }
}

package main.java.structures.db.file;

import main.java.structures.db.config.AppConfig;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

class Page<T> {

    private static final int PAGE_BLOCK_FACTOR = AppConfig.getInstance().getPageBlockFactor();

    private final List<PageElementContents<T>> data;

    Page() {
        this.data = new ArrayList<>(PAGE_BLOCK_FACTOR);
    }

    void read(RandomAccessFile file, int elementSize, Function<byte[], T> deserializer) {
        data.clear();
        IntStream.range(0, PAGE_BLOCK_FACTOR).forEach(i -> {
            try {
                byte[] bytes = new byte[elementSize];
                file.read(bytes);
                if (bytes[0] != 0) {
                    data.set(i, new PageElementContents<>(deserializer.apply(bytes), true));
                }
            } catch (Exception e) {
                throw new PageAccessException("Error reading page: " + e.getMessage());
            }
        });
    }

    void write(RandomAccessFile file, Function<T, byte[]> serializer) {
        IntStream.range(0, PAGE_BLOCK_FACTOR).forEach(i -> {
            try {
                file.write(serializer.apply(data.get(i).getData()));
            } catch (Exception e) {
                throw new PageAccessException("Error writing to page: " + e.getMessage());
            }
        });
    }

    void set(int index, T element) {
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

    void remove(int index) {
        if (index >= AppConfig.getInstance().getPageBlockFactor() || index < 0) {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bound!", index));
        }
        if (!data.get(index).isOccupied()) {
            throw new EmptyIndexException(String.format("Element at index %d is empty!", index));
        }
        data.set(index, data.get(index).empty());
    }

    boolean isFull() {
        return data.size() >= AppConfig.getInstance().getPageBlockFactor();
    }

    boolean isEmpty() {
        return data.isEmpty();
    }

    void clear() {
        IntStream.range(0, data.size()).forEach(i ->
            data.set(i, data.get(i).empty()));
    }

}

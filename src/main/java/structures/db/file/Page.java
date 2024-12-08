package main.java.structures.db.file;

import lombok.Getter;
import main.java.structures.db.config.AppConfig;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

@Getter
public class Page<T> {

    private final List<T> data;

    public Page() {
        this.data = new ArrayList<>();
    }

    public void read(RandomAccessFile file, int elementSize, Function<byte[], T> deserializer) {
        data.clear();
        IntStream.range(0, AppConfig.getInstance().getPageBlockFactor()).forEach(i -> {
            try {
                byte[] bytes = new byte[elementSize];
                file.read(bytes);
                data.add(i, deserializer.apply(bytes));
            } catch (Exception e) {
                throw new PageAccessException("Error reading from page: " + e.getMessage());
            }
        });
    }

    public void write(RandomAccessFile file, Function<T, byte[]> serializer) {
        data.forEach(element -> {
            try {
                file.write(serializer.apply(element));
            } catch (Exception e) {
                throw new PageAccessException("Error writing to page: " + e.getMessage());
            }
        });
        data.clear();
    }

    void set(int index, T data) {
        if (index >= AppConfig.getInstance().getPageBlockFactor() || index < 0) {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bound!", index));
        }
        if (this.data.size() <= index) {
            IntStream.range(this.data.size(), index).forEach(i -> this.data.add(i, null));
        }
        this.data.set(index, data);
    }

    public boolean isFull() {
        return data.size() == AppConfig.getInstance().getPageBlockFactor();
    }

    boolean isEmpty() {
        return data.isEmpty();
    }

    public void clear() {
        data.clear();
        IntStream.range(0, AppConfig.getInstance().getPageBlockFactor()).forEach(i -> data.add(i, null));
    }

    @Override
    public String toString() {
        return "Page {\n" +
                "data = {\n" + data.stream().map(Objects::toString).reduce((a, b) -> a + ",\n" + b).orElse("") +
                "}\n";
    }

}

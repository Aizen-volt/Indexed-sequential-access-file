package main.java.structures.db.file;

import lombok.Getter;
import lombok.Setter;
import main.java.structures.db.utils.SerializationUtils;

import java.util.Arrays;

@Setter
@Getter
class IndexFileContents {

    private int key;
    private int pageNumber;

    public IndexFileContents(int key, int pageNumber) {
        this.key = key;
        this.pageNumber = pageNumber;
    }

    public static int getSize() {
        return Integer.BYTES * 2;
    }

    public byte[] serialize() {
        byte[] keyBytes = SerializationUtils.serialize(key);
        byte[] pageNumberBytes = SerializationUtils.serialize(pageNumber);
        return SerializationUtils.concat(keyBytes, pageNumberBytes);
    }

    public static IndexFileContents deserialize(byte[] bytes) {
        int key = SerializationUtils.deserialize(Arrays.copyOfRange(bytes, 0, Integer.BYTES), Integer.class);
        int pageNumber = SerializationUtils.deserialize(Arrays.copyOfRange(bytes, Integer.BYTES, Integer.BYTES * 2), Integer.class);
        return new IndexFileContents(key, pageNumber);
    }

    @Override
    public String toString() {
        return String.format("IndexFileContents(%d: %d)", key, pageNumber);
    }

}

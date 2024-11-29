package main.java.structures.db.file;

import lombok.Getter;
import lombok.Setter;

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
        return new byte[]{
                (byte) (key >>> 24),
                (byte) (key >>> 16),
                (byte) (key >>> 8),
                (byte) key,
                (byte) (pageNumber >>> 24),
                (byte) (pageNumber >>> 16),
                (byte) (pageNumber >>> 8),
                (byte) pageNumber
        };
    }

    public static IndexFileContents deserialize(byte[] bytes) {
        int key = (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | bytes[3];
        int pageNumber = (bytes[4] << 24) | (bytes[5] << 16) | (bytes[6] << 8) | bytes[7];
        return new IndexFileContents(key, pageNumber);
    }
}

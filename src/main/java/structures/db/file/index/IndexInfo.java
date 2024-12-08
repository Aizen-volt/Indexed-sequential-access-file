package main.java.structures.db.file.index;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;


@Setter
@Getter
class IndexInfo {

    private int key;
    private int pageNumber;

    public IndexInfo(int key, int pageNumber) {
        this.key = key;
        this.pageNumber = pageNumber;
    }

    public static int getSize() {
        return Integer.BYTES * 2;
    }

    public static byte[] serialize(IndexInfo instance) {
        if (instance == null) {
            return IndexInfo.serialize(new IndexInfo(-1, -1));
        }
        return ByteBuffer.allocate(IndexInfo.getSize())
                .putInt(instance.key)
                .putInt(instance.pageNumber)
                .array();
    }

    public static IndexInfo deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new IndexInfo(
                buffer.getInt(),
                buffer.getInt()
        );
    }

    @Override
    public String toString() {
        return String.format("(%d: %d)", key, pageNumber);
    }


}
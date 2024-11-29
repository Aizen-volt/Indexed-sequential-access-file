package main.java.structures.db.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

public record Element(int key, int a, int b, int c) implements Comparable<Element> {

    private static final int MAX_RANDOM_VALUE = 256;
    private static final Random RANDOM = new Random();

    public static Element random(int key) {
        return new Element(
                key,
                RANDOM.nextInt(MAX_RANDOM_VALUE),
                RANDOM.nextInt(MAX_RANDOM_VALUE),
                RANDOM.nextInt(MAX_RANDOM_VALUE)
        );
    }

    public byte[] serialize() {
        return new byte[]{
                (byte) (key >>> 24),
                (byte) (key >>> 16),
                (byte) (key >>> 8),
                (byte) key,
                (byte) (a >>> 24),
                (byte) (a >>> 16),
                (byte) (a >>> 8),
                (byte) a,
                (byte) (b >>> 24),
                (byte) (b >>> 16),
                (byte) (b >>> 8),
                (byte) b,
                (byte) (c >>> 24),
                (byte) (c >>> 16),
                (byte) (c >>> 8),
                (byte) c
        };
    }

    public static Element deserialize(byte[] bytes) {
        int key = (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | bytes[3];
        int a = (bytes[4] << 24) | (bytes[5] << 16) | (bytes[6] << 8) | bytes[7];
        int b = (bytes[8] << 24) | (bytes[9] << 16) | (bytes[10] << 8) | bytes[11];
        int c = (bytes[12] << 24) | (bytes[13] << 16) | (bytes[14] << 8) | bytes[15];
        return new Element(key, a, b, c);
    }

    public static int getSize() {
        return Integer.BYTES * 4;
    }

    @Override
    public int compareTo(Element other) {
        return Integer.compare(this.key, other.key);
    }

    @Override
    public String toString() {
        return String.format("Element(%d: {%d, %d, %d})", key, a, b, c);
    }
}
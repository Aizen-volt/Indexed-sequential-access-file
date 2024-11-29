package main.java.structures.db.model;

import main.java.structures.db.utils.SerializationUtils;

import java.util.Arrays;
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
        byte[] keyBytes = SerializationUtils.serialize(key);
        byte[] aBytes = SerializationUtils.serialize(a);
        byte[] bBytes = SerializationUtils.serialize(b);
        byte[] cBytes = SerializationUtils.serialize(c);
        return SerializationUtils.concat(keyBytes, aBytes, bBytes, cBytes);
    }

    public static Element deserialize(byte[] bytes) {
        int key = SerializationUtils.deserialize(Arrays.copyOfRange(bytes, 0, Integer.BYTES), Integer.class);
        int a = SerializationUtils.deserialize(Arrays.copyOfRange(bytes, Integer.BYTES, Integer.BYTES * 2), Integer.class);
        int b = SerializationUtils.deserialize(Arrays.copyOfRange(bytes, Integer.BYTES * 2, Integer.BYTES * 3), Integer.class);
        int c = SerializationUtils.deserialize(Arrays.copyOfRange(bytes, Integer.BYTES * 3, Integer.BYTES * 4), Integer.class);
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
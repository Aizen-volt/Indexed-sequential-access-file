package main.java.structures.db.model;


import java.nio.ByteBuffer;
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

    public static byte[] serialize(Element instance) {
        if (instance == null) {
            return Element.serialize(new Element(-1, -1, -1, -1));
        }
        return ByteBuffer.allocate(Element.getSize())
                .putInt(instance.key)
                .putInt(instance.a)
                .putInt(instance.b)
                .putInt(instance.c)
                .array();
    }

    public static Element deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new Element(
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt()
        );
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
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

    public void serialize(DataOutput out) throws IOException {
        out.writeInt(key);
        out.writeInt(a);
        out.writeInt(b);
        out.writeInt(c);
    }

    public static Element deserialize(DataInput in) throws IOException {
        int key = in.readInt();
        int a = in.readInt();
        int b = in.readInt();
        int c = in.readInt();
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
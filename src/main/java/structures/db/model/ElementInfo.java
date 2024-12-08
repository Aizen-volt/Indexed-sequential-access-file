package main.java.structures.db.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Getter
@Setter
public class ElementInfo {

    private Element data;

    private boolean occupied;

    private int overflowPointer;

    public ElementInfo(Element data, boolean occupied, int overflowPointer) {
        this.data = data;
        this.occupied = occupied;
        this.overflowPointer = overflowPointer;
    }

    public ElementInfo empty() {
        return new ElementInfo(new Element(-1, -1, -1, -1), false, -1);
    }

    public static byte[] serialize(ElementInfo instance) {
        if (instance == null) {
            return ElementInfo.serialize(new ElementInfo(new Element(-1, -1, -1, -1), false, -1));
        }
        return ByteBuffer.allocate(instance.getSize())
                .put(Element.serialize(instance.getData()))
                .put((byte) (instance.isOccupied() ? 1 : 0))
                .putInt(instance.overflowPointer)
                .array();
    }

    public static ElementInfo deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Element data = Element.deserialize(Arrays.copyOfRange(bytes, 0, Element.getSize()));
        buffer.position(Element.getSize());
        boolean occupied = buffer.get() == 1;
        int overflowPointer = buffer.getInt();
        return new ElementInfo(data, occupied, overflowPointer);
    }

    public static int getSize() {
        return Element.getSize() + 5;
    }

    @Override
    public String toString() {
        return occupied ? data.toString() : "";
    }

}

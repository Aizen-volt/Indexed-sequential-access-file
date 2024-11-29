package main.java.structures.db.utils;

import java.io.*;

public class SerializationUtils {

    public static byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ObjectStreamFailedException("Error serializing object of type " + object.getClass().getName());
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> type) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return type.cast(objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new ObjectStreamFailedException("Error deserializing object of type " + type.getName());
        }
    }

    public static byte[] concat(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        return result;
    }

}

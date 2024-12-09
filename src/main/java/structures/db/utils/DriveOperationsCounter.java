package main.java.structures.db.utils;

import lombok.Getter;

public class DriveOperationsCounter {

        @Getter
        private static int readCounter = 0;

        @Getter
        private static int writeCounter = 0;

        public static void incrementReadCounter() {
            readCounter++;
        }

        public static void incrementWriteCounter() {
            writeCounter++;
        }

        public static void resetCounters() {
            readCounter = 0;
            writeCounter = 0;
        }
}

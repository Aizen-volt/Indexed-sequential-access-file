package main.java.structures.db;

import lombok.extern.java.Log;
import main.java.structures.db.file.IndexedSequentialAccessFile;
import main.java.structures.db.utils.DriveOperationsCounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

@Log
public class Main {

    private static IndexedSequentialAccessFile indexedSequentialAccessFile;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            indexedSequentialAccessFile = new IndexedSequentialAccessFile();
            while (true) {
                displayMenu();
                int choice = Integer.parseInt(scanner.nextLine());
                DriveOperationsCounter.resetCounters();
                menuOptions.get(choice).run();
                System.out.println("Reads: " + DriveOperationsCounter.getReadCounter() + ", Writes: " + DriveOperationsCounter.getWriteCounter());
            }
        } catch (IOException e) {
            log.severe("Error opening file: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("=============================== MENU ===============================");
        System.out.println("1. Insert record");
        System.out.println("2. Read record");
        System.out.println("3. Display all records");
        System.out.println("4. Display index file");
        System.out.println("5. Reorganize file");
        System.out.println("6. Delete record");
        System.out.println("7. Update record");
        System.out.println("8. Execute commands from file");
        System.out.println("9. Exit");
        System.out.println("===================================================================");
        System.out.print("Enter your choice: ");
    }

    private static void insertRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter a:");
        int a = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter b:");
        int b = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter c:");
        int c = Integer.parseInt(scanner.nextLine());

        System.out.println("Inserting record...");
        try {
            indexedSequentialAccessFile.insertRecord(key, a, b, c);
        } catch (IOException e) {
            log.warning("Error inserting record: " + e.getMessage());
        }
    }

    private static void readRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(scanner.nextLine());
        System.out.println("Reading record...");
        try {
            indexedSequentialAccessFile.readRecord(key);
        } catch (IOException e) {
            log.warning("Error reading record: " + e.getMessage());
        }
    }

    private static void displayAllRecords() {
        System.out.println("Displaying all records...");
        indexedSequentialAccessFile.displayAllRecords();
    }

    private static void displayIndexFile() {
        System.out.println("Displaying index file...");
        indexedSequentialAccessFile.displayIndexFile();
    }

    private static void reorganizeFile() {
        System.out.println("Reorganizing file...");
        indexedSequentialAccessFile.reorganizeFile();
    }

    private static void deleteRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(scanner.nextLine());

        System.out.println("Deleting record...");
        try {
            indexedSequentialAccessFile.deleteRecord(key);
        } catch (IOException e) {
            log.warning("Error deleting record: " + e.getMessage());
        }
    }

    private static void updateRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter a:");
        int a = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter b:");
        int b = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter c:");
        int c = Integer.parseInt(scanner.nextLine());

        System.out.println("Updating record...");
        try {
            indexedSequentialAccessFile.updateRecord(key, a, b, c);
        } catch (IOException e) {
            log.warning("Error updating record: " + e.getMessage());
        }
    }

    private static void executeCommandsFromFile() {
        System.out.println("Enter file name:");
        String fileName = scanner.nextLine();

        System.out.println("Executing commands from file...");
        parseCommandsFromFile(fileName);
    }

    private static void exit() {
        System.out.println("Exiting...");
        System.exit(0);
    }

    private static void parseCommandsFromFile(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                System.out.println("Executing command: " + line);
                String[] tokens = line.split(" ");
                String command = tokens[0];
                DriveOperationsCounter.resetCounters();
                switch (command) {
                    case "insert":
                        try {
                            int key = Integer.parseInt(tokens[1]);
                            int a = Integer.parseInt(tokens[2]);
                            int b = Integer.parseInt(tokens[3]);
                            int c = Integer.parseInt(tokens[4]);
                            indexedSequentialAccessFile.insertRecord(key, a, b, c);
                        } catch (Exception e) {
                            log.warning("Error inserting record: " + e.getMessage());
                        }
                        break;
                    case "delete":
                        try {
                            int key = Integer.parseInt(tokens[1]);
                            indexedSequentialAccessFile.deleteRecord(key);
                        } catch (Exception e) {
                            log.warning("Error deleting record: " + e.getMessage());
                        }
                        break;
                    case "update":
                        try {
                            int key = Integer.parseInt(tokens[1]);
                            int a = Integer.parseInt(tokens[2]);
                            int b = Integer.parseInt(tokens[3]);
                            int c = Integer.parseInt(tokens[4]);
                            indexedSequentialAccessFile.updateRecord(key, a, b, c);
                        } catch (Exception e) {
                            log.warning("Error updating record: " + e.getMessage());
                        }
                        break;
                    default:
                        log.warning("Invalid command: " + line);
                }
                System.out.println("Reads: " + DriveOperationsCounter.getReadCounter() + ", Writes: " + DriveOperationsCounter.getWriteCounter());
            }
        } catch (IOException e) {
            log.severe("Error reading file: " + e.getMessage());
        }
    }

    private static final Map<Integer, Runnable> menuOptions = Map.of(
            1, Main::insertRecord,
            2, Main::readRecord,
            3, Main::displayAllRecords,
            4, Main::displayIndexFile,
            5, Main::reorganizeFile,
            6, Main::deleteRecord,
            7, Main::updateRecord,
            8, Main::executeCommandsFromFile,
            9, Main::exit
    );

}
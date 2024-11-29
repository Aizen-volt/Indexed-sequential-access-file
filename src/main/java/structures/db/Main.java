package main.java.structures.db;

import lombok.extern.java.Log;
import main.java.structures.db.file.IndexSequentialFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

@Log
public class Main {

    private static IndexSequentialFile indexSequentialFile;

    public static void main(String[] args) {
        try {
            indexSequentialFile = new IndexSequentialFile();
            while (true) {
                displayMenu();
                int choice = Integer.parseInt(System.console().readLine());
                menuOptions.get(choice).run();
            }
        } catch (FileNotFoundException e) {
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
        int key = Integer.parseInt(System.console().readLine());

        System.out.println("Enter a:");
        int a = Integer.parseInt(System.console().readLine());

        System.out.println("Enter b:");
        int b = Integer.parseInt(System.console().readLine());

        System.out.println("Enter c:");
        int c = Integer.parseInt(System.console().readLine());

        System.out.println("Inserting record...");
        indexSequentialFile.insertRecord(key, a, b, c);
    }

    private static void readRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(System.console().readLine());
        System.out.println("Reading record...");
        indexSequentialFile.readRecord(key);
    }

    private static void displayAllRecords() {
        System.out.println("Displaying all records...");
        indexSequentialFile.displayAllRecords();
    }

    private static void displayIndexFile() {
        System.out.println("Displaying index file...");
        indexSequentialFile.displayIndexFile();
    }

    private static void reorganizeFile() {
        System.out.println("Reorganizing file...");
        indexSequentialFile.reorganizeFile();
    }

    private static void deleteRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(System.console().readLine());

        System.out.println("Deleting record...");
        indexSequentialFile.deleteRecord(key);
    }

    private static void updateRecord() {
        System.out.println("Enter key:");
        int key = Integer.parseInt(System.console().readLine());

        System.out.println("Enter a:");
        int a = Integer.parseInt(System.console().readLine());

        System.out.println("Enter b:");
        int b = Integer.parseInt(System.console().readLine());

        System.out.println("Enter c:");
        int c = Integer.parseInt(System.console().readLine());

        System.out.println("Updating record...");
        indexSequentialFile.updateRecord(key, a, b, c);
    }

    private static void executeCommandsFromFile() {
        System.out.println("Enter file name:");
        String fileName = System.console().readLine();

        System.out.println("Executing commands from file...");
        parseCommandsFromFile(fileName);
    }

    private static void exit() {
        System.out.println("Exiting...");
        System.exit(0);
    }

    private static void parseCommandsFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                String command = tokens[0];
                switch (command) {
                    case "insert":
                        try {
                            int key = Integer.parseInt(tokens[1]);
                            int a = Integer.parseInt(tokens[2]);
                            int b = Integer.parseInt(tokens[3]);
                            int c = Integer.parseInt(tokens[4]);
                            indexSequentialFile.insertRecord(key, a, b, c);
                        } catch (Exception e) {
                            log.warning("Error inserting record: " + e.getMessage());
                        }
                        break;
                    case "delete":
                        try {
                            int key = Integer.parseInt(tokens[1]);
                            indexSequentialFile.deleteRecord(key);
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
                            indexSequentialFile.updateRecord(key, a, b, c);
                        } catch (Exception e) {
                            log.warning("Error updating record: " + e.getMessage());
                        }
                        break;
                    default:
                        log.warning("Invalid command: " + line);
                }
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

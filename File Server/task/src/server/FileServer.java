package server;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileServer extends Thread {
    private final Scanner SCANNER = new Scanner(System.in);
    private boolean isExit = false;

    private void parseInput() {
        String[] input = SCANNER.nextLine().split(" ");
        switch (input[0]) {
            case "add" -> add(input[1]);
            case "get" -> get(input[1]);
            case "delete" -> delete(input[1]);
            case "exit" -> exit();
        }
    }

    public void run() {
        while (!isExit) {
            parseInput();
        }
    }

    private void add(String fileName) {
        File file = new File(fileName);
        try {
            if (checkFileName(fileName) && file.createNewFile()) {
                System.out.println("The file " + fileName + " added successfully");
            } else {
                System.out.println("Cannot add the file " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Cannot add the file " + fileName);
        }
    }

    private void get(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            System.out.println("The file " + fileName + " was sent");
        } else {
            System.out.println("The file " + fileName + " not found");
        }
    }

    private void delete(String fileName) {
        File file = new File(fileName);
        if (file.delete()) {
            System.out.println("The file " + fileName + " was deleted");
        } else {
            System.out.println("The file " + fileName + " not found");
        }
    }

    private void exit() {
        isExit = true;
        for (int i = 1; i <= 10; i++) {
            new File("file" + i).delete();
        }
    }

    private boolean checkFileName(String fileName) {
        for (int i = 1; i <= 10; i++) {
            if (("file" + i).equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}

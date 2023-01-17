package client;

import request.Request;
import request.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String PATH = "C:\\Users\\ivana\\IdeaProjects\\File Server\\File Server\\task\\src\\client\\data\\";
    private final Scanner scanner = new Scanner(System.in);
    private final String address = "127.0.0.1";
    private int port = 23456;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private boolean isExit = false;

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        client.run();
    }


    private void run() throws InterruptedException {
        Thread.sleep(350);
        //while (!isExit) {
            try (Socket socket = new Socket(InetAddress.getByName(address), port)) {
                objectOutputStream = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
                objectInputStream = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
                System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
                String input = scanner.nextLine();
                parseCommandFromStandardInput(input);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
       // }
    }

    private void parseCommandFromStandardInput(String input) throws IOException, ClassNotFoundException {
        switch (input) {
            case "1" -> get();
            case "2" -> put();
            case "3" -> delete();
            case "exit" -> exit();
        }
    }

    private String readFileNameFromStandardInput() {
        System.out.print("Enter name of the file: ");
        return scanner.nextLine();
    }

    private void get() {
        Request request = new Request();
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        String input = scanner.nextLine();
        if ("1".equals(input)) {
            request.setFileName(readFileNameFromStandardInput());
            request.setAction(1);
            request.setHowToFind(1);
        } else if ("2".equals(input)) {
            System.out.print("Enter id: ");
            request.setFileID(Integer.parseInt(scanner.nextLine()));
            request.setAction(1);
            request.setHowToFind(2);
        } else {
            return;
        }
        Response response = sendRequest(request);
        //System.out.println(response);
        if (response != null && response.getCode() == 200) {
            System.out.print("The file was downloaded! Specify a name for it: ");
            String fileName = scanner.nextLine();
            File file = new File(PATH + fileName);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(response.getFileContent());
                fileOutputStream.close();
                System.out.println("File saved on the hard drive!");
            } catch (IOException e) {
                System.out.println("Cannot save file on the hard drive!");
            }
        } else {
            System.out.println("The response says that this file is not found!");
        }
    }

    private void put() {
        System.out.print("Enter name of the file: ");
        String fileName = scanner.nextLine();
        System.out.print("Enter name of the file to be saved on server: ");
        String fileNameOnServer = scanner.nextLine();
        byte[] bytes;
        try (FileInputStream fileInputStream = new FileInputStream(PATH + fileName)) {
            bytes = fileInputStream.readAllBytes();
            Request request = new Request(2, fileNameOnServer, bytes); //collecting all in one Object
            objectOutputStream.writeObject(request);
            System.out.println("The request was sent.");
            //System.out.println(request);
            Response response = (Response) objectInputStream.readObject();
            // System.out.println(response.toString());
            if (response.getCode() == 200) {
                System.out.println("Response says that file is saved! ID = " + response.getID());
            } else {
                System.out.println("The response says that creating the file was forbidden!");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while reading file");
        }
    }

    private void delete() {
        Request request = new Request();
        System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
        String input = scanner.nextLine();
        if ("1".equals(input)) {
            request.setFileName(readFileNameFromStandardInput());
            request.setAction(3);
            request.setHowToFind(1);
        } else if ("2".equals(input)) {
            System.out.print("Enter id: ");
            request.setFileID(Integer.parseInt(scanner.nextLine()));
            request.setAction(3);
            request.setHowToFind(2);
        } else {
            return;
        }
        Response response = sendRequest(request);
        if (response != null && response.getCode() == 200) {
            System.out.println("The response says that this file was deleted successfully!");
        } else {
            System.out.println("The response says that this file is not found!");
        }
    }

    private void exit() {
        try {
            Request request = new Request(4);
            objectOutputStream.writeObject(request);
            System.out.println("The request was sent.");
            //System.out.println(request.toString());
            isExit = true;
            //System.out.println(request);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error sending request");
        }
    }

    private Response sendRequest(Request request) {
        try {
            objectOutputStream.writeObject(request);
            System.out.println("The request was sent.");
            //System.out.println(request);
            Response response = (Response) objectInputStream.readObject();
            // System.out.println(response.toString());
            return response;
        } catch (IOException | ClassNotFoundException e) {
            {
                System.out.println(e.getMessage());
                System.out.println("Error while getting response");
                return null;
            }
        }
    }
}
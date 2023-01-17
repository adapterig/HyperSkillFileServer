package server;

import request.Request;
import request.Response;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class Worker extends Thread{
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final String PATH;
    private final String PATH_TO_MAP;
    private final Map<Integer, String> fileMap;
    private final Server server;
    Worker(ObjectInputStream ois, ObjectOutputStream oos, Server server) {
        this.ois = ois;
        this.oos = oos;
        this.PATH = server.getPATH();
        this.fileMap = server.getFileMap();
        this.PATH_TO_MAP = server.getPATH_TO_MAP();
        this.server = server;
    }
    public void run() {
        try {
            Request request = (Request) ois.readObject();
            switch (request.getAction()) {
                case 1 -> get(request);
                case 2 -> put(request);
                case 3 -> delete(request);
                case 4 -> exit();
                default -> wrongRequest();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void put(Request request) throws IOException {
        if (request.getFileName() == null || request.getFileName().length() < 1) {
            request.setFileName(String.valueOf(new Date().getTime()) + Arrays.hashCode(request.getFileContent()));
        }
        File file = new File(PATH + request.getFileName());
        Response response = new Response();
        try {
            if (file.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(request.getFileContent());
                fileOutputStream.close();
                int id = generateID();
                server.putToFileMap(id, request.getFileName());
                response.setCode(200);
                response.setID(id);
            } else {
                response.setCode(403);
            }
        } catch (IOException e) {
            response.setCode(500);
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        oos.writeObject(response);
    }

    private void get(Request request) throws IOException {
        String fileName;
        Integer id = null;
        Response response = new Response();
        if (request.getHowToFind() == 2) {
            id = request.getFileID();
            fileName = fileMap.get(id);
        } else if (request.getHowToFind() == 1) {
            fileName = request.getFileName();
            for (Map.Entry<Integer, String> pair : fileMap.entrySet()) {
                if (fileName.equals(pair.getValue())) {
                    id = pair.getKey();
                    break;
                }
            }
        } else {
            response.setCode(400);
            return;
        }
        if (fileName != null && id != null) {
            File file = new File(PATH + fileName);
            if (!file.exists()) {
                response.setCode(500);
                return;
            }
            try (FileInputStream fileInputStream = new FileInputStream(file);) {
                response.setFileContent(fileInputStream.readAllBytes());
                response.setCode(200);
            } catch (IOException e) {
                response.setCode(500);
            }
        } else {
            response.setCode(400);
        }
        oos.writeObject(response);
    }

    private void delete(Request request) throws IOException {// переписать

        String fileName;
        Integer id = null;
        Response response = new Response();
        if (request.getHowToFind() == 2) {
            id = request.getFileID();
            fileName = fileMap.get(id);
        } else if (request.getHowToFind() == 1) {
            fileName = request.getFileName();
            for (Map.Entry<Integer, String> pair : fileMap.entrySet()) {
                if (fileName.equals(pair.getValue())) {
                    id = pair.getKey();
                    break;
                }
            }
        } else {
            response.setCode(400);
            return;
        }
        writeFile(fileName, id, response);
    }

    private void exit() throws IOException {
        File file = new File(PATH_TO_MAP);
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
             oos.writeObject(fileMap);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            server.setExit();
        }
    }

    private void wrongRequest() throws IOException {
        System.out.println("wrongRequest");
    }

    private int generateID() {
        return fileMap.size() + 1;
    }

    private synchronized void writeFile(String fileName, Integer id, Response response) throws IOException {
        if (fileName != null && id != null) {
            File file = new File(PATH + fileName);
            if (!file.exists()) {
                response.setCode(404);
                oos.writeObject(response);
                return;
            }
            if (file.delete()) {
                response.setCode(200);
                fileMap.remove(id);
            }
        } else {
            response.setCode(400);
        }
        oos.writeObject(response);
    }
}

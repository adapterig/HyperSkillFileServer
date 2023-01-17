package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private boolean isExit = false;
    final String PATH = "C:\\Users\\ivana\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\";
    final String PATH_TO_MAP = "C:\\Users\\ivana\\IdeaProjects\\File Server\\File Server\\task\\src\\map\\map";
    private final String address = "127.0.0.1";
    private final int port = 23456;
    private int lastID;
    private Map<Integer, String> fileMap;
    private Socket socket;

    { //init map before server start
        File file = new File(PATH_TO_MAP);
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fileInputStream);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                fileMap = (HashMap<Integer, String>) ois.readObject();
                if (fileMap.isEmpty()) {
                    lastID = 0;
                } else {
                    lastID = fileMap.size();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            fileMap = new HashMap<>();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public void run() {
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        {
            try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address))) {
                System.out.println("Server started!");
                while (!isExit) {
                    //System.out.println("waiting for connection");
                    socket = serverSocket.accept();
                    if (isExit) {
                        socket.close();
                        break;
                    }
                    //System.out.println("generating streams");
                    ObjectInputStream ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
                    ObjectOutputStream oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
                    //System.out.println("adding to executors pool");
                    Worker worker = new Worker(ois, oos, this);
                    executor.submit(worker);
                }
               // System.out.println("exiting after whileloop");
               //  executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
                executor.shutdown();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public void setExit() throws IOException {
        isExit = true;
        Socket socket = new Socket(InetAddress.getByName(address), port);
        try {
            socket.getOutputStream().close();
            socket.getInputStream().close();
            this.socket.close();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getPATH() {
        return PATH;
    }

    public String getPATH_TO_MAP() {
        return PATH_TO_MAP;
    }

    public Map<Integer, String> getFileMap() {
        return fileMap;
    }
    public synchronized void putToFileMap (Integer integer, String string){
        fileMap.put(integer, string);
    }
}

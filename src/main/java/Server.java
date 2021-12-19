import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static int PORT_NUMBER;
    private static String dir;
    private static Socket clientSocket;
    private static ServerSocket serverSocket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    public Server(int port, String d) {
        PORT_NUMBER = port;
        dir = d;
    }

    public static void runServer() throws Exception {
        System.out.println("Starting Server!");
        serverSocket = new ServerSocket(PORT_NUMBER);
        System.out.println("Port is: " + PORT_NUMBER + ". Directory to synchronize: " + dir);

        while (true) {
            clientSocket = serverSocket.accept();
            ois = new ObjectInputStream(clientSocket.getInputStream());
            String clientDir = (String) ois.readObject();
            oos = new ObjectOutputStream(clientSocket.getOutputStream());

            System.out.println("New client connected! IP: " + clientSocket.getInetAddress().toString() + " Directory: " + clientDir);

            oos.writeObject(dir);
            oos.flush();

            File file = new File(dir);

            sendSynchronisation(file);
            oos.writeObject("End_of_S-U_synchronisation");
            receiveSynchronisation(file, clientDir);
            clientSocket.close();

            System.out.println("Synchronisation ended! Do you want to continue with another User? Y/N");
            Scanner sc = new Scanner(System.in);

            String answer = sc.next();

            switch (answer) {
                case "Y":
                    System.out.println("Do you want to change folder? Y/N");
                    String answer_dir = sc.next();
                    switch (answer_dir) {
                        case "Y":
                            System.out.println("Type new directory name");
                            String newDir = sc.next();
                            dir = newDir;
                            break;
                        case "N":
                            System.out.println("Directory will not change");
                            break;
                        default:
                            System.out.println("I can't understand you. Directory will not change");
                            break;
                    }
                    break;
                case "N":
                    System.out.println("Ending...");
                    System.out.println("End");
                    System.exit(0);
                    break;
                default:
                    System.out.println("I can't understand you. Ending...");
                    System.out.println("End");
                    System.exit(0);
                    break;
            }
        }
    }

    public static void receiveSynchronisation(File file, String clientDir) {
        try {
            while (true) {
                String pack = (String) ois.readObject();
                if (pack.contains("End_of_U-S_synchronisation")) {
                    System.out.println("U-to-S ended\n");
                    break;
                }

                if (pack.contains("$Directory")) {
                    String[] incomingDir = pack.split("\\s");
                    String newDirName = incomingDir[1];
                    System.out.println("Found new subdirectory named " + newDirName);
                    File newDir = new File(dir + "/" + newDirName);
                    System.out.println("Creating a new dir named " + newDir.getPath());
                    newDir.mkdir();
                } else {

                    String[] parameter = pack.split("\\s");

                    String relPath = SyncUtil.difference(clientDir, parameter[2]);

                    Boolean collision = SyncUtil.checkCollisions(file, relPath, dir);
                    if (collision) {
                        System.out.println("Collision found! Do you want to rewrite this file? Y/N");
                        Scanner sc = new Scanner(System.in);

                        String answer = sc.next();
                        if (answer.equals("Y")) {
                            oos.writeObject(answer);
                            SyncUtil.receiveFile(clientSocket, new File(dir + relPath), Integer.parseInt(parameter[1]));
                        } else if (answer.equals("N")) {
                            oos.writeObject(answer);
                        } else {
                            System.out.println("I can't understand you! Skipping this file!");
                            oos.writeObject("N");
                        }

                    } else {
                        System.out.println("No collisions found! Loading file!");
                        String answer = "Y";
                        oos.writeObject(answer);
                        SyncUtil.receiveFile(clientSocket, new File(dir + relPath), Integer.parseInt(parameter[1]));
                    }
                    oos.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendSynchronisation(File dir) {
        if (dir.isDirectory()) {
            try {
                if (SyncUtil.difference(Server.dir, dir.getPath()) != null)
                    oos.writeObject("$Directory " + SyncUtil.difference(Server.dir, dir.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                sendSynchronisation(new File(dir, children[i]));
            }
        } else {
            long len = dir.length();
            String name = dir.getName();
            String path = dir.getPath();
            String pack = name + " " + len + " " + path;
            try {
                oos.writeObject(pack);
                oos.flush();

                String response = (String) ois.readObject();

                if (response.contains("N")) {
                    Thread.sleep(1000);
                    return;
                } else if (response.contains("Y")) {
                    SyncUtil.sendFile(clientSocket, dir);
                }

                Thread.sleep(1000);
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
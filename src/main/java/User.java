import java.io.*;
import java.net.*;
import java.util.Scanner;

public class User {
    private static Socket server;
    private static String dirName;
    private static String serverIP;
    private static int PORT_NUMBER;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

    public User(String IP, int port, String dir) {
        dirName = dir;
        serverIP = IP;
        PORT_NUMBER = port;

        System.out.println("Client Selected!");
        System.out.println("Dir to sync: " + dirName);
        System.out.println("Server IP: " + serverIP);
    }

    public static void runUser() throws Exception {
        System.out.println("Starting Client!");
        server = new Socket(serverIP, PORT_NUMBER);
        oos = new ObjectOutputStream(server.getOutputStream()); // send directory name to server
        oos.writeObject(dirName);
        oos.flush();
        ois = new ObjectInputStream(server.getInputStream());
        String serverPath = (String) ois.readObject();
        File dir = new File(dirName);

        receiveSynchronisation(dir, serverPath);
        sendSynchronisation(dir);
        oos.writeObject("End_of_U-S_synchronisation");
        server.close();
        System.out.println("End");
    }

    public static void receiveSynchronisation(File dir, String serverPath) {
        try {
            while (true) {
                String pack = (String) ois.readObject();
                if (pack.contains("End_of_S-U_synchronisation")) {
                    System.out.println("S-to-U ended\n");
                    break;
                }
                if (pack.contains("$Directory")) {
                    String[] incomingDir = pack.split("\\s");
                    String newDirName = incomingDir[1];
                    System.out.println("Found new subdirectory named " + newDirName);
                    File newDir = new File(dirName + "/" + newDirName);
                    System.out.println("Creating a new dir named " + newDir.getPath());
                    newDir.mkdir();
                } else {


                    String[] parameter = pack.split("\\s");

                    String relPath = SyncUtil.difference(serverPath, parameter[2]);


                    Boolean collision = SyncUtil.checkCollisions(dir, relPath, dirName);

                    if (collision) {
                        System.out.println("Collision found! Do you want to rewrite this file? Y/N");
                        Scanner sc = new Scanner(System.in);

                        String answer = sc.next();
                        if (answer.equals("Y")) {
                            oos.writeObject(answer);
                            SyncUtil.receiveFile(server, new File(dirName + relPath), Integer.parseInt(parameter[1]));
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
                        SyncUtil.receiveFile(server, new File(dirName + relPath), Integer.parseInt(parameter[1]));
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
                if (SyncUtil.difference(dirName, dir.getPath()) != null)
                    oos.writeObject("$Directory " + SyncUtil.difference(dirName, dir.getPath()));
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
                    SyncUtil.sendFile(server, dir);
                }

                Thread.sleep(1000);
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
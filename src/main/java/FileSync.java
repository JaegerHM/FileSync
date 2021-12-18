import java.io.*;
import java.util.Scanner;


public class FileSync {
    public static void main(String[] args) throws Exception {
        System.out.println("Choose you mode: \n 1) Receive connection \n 2) Connect to server");
        Scanner sc = new Scanner(System.in);
        int type = sc.nextInt();

        if (type == 1) {
            System.out.println("You selected Server mode! Type port number and directory to synchronize");
            int port_number = sc.nextInt();
            String dir = sc.next();
            Server server = new Server(port_number, dir);
            server.runServer();

        } else if((type == 2)) {
            System.out.println("You selected User mode! Type Server IP, port number and directory to synchronize");
            String IP = sc.next();
            int port_number = sc.nextInt();
            String dir = sc.next();
            User user = new User(IP, port_number, dir);
            user.runUser();
        }
        else{
            System.out.println("I can't understand you!");
        }
    }
}

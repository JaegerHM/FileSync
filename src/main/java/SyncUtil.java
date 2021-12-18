import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SyncUtil {

    public static void sendFile(Socket socket, File file) {
        try {
            OutputStream os = socket.getOutputStream();
            InputStream is = new FileInputStream(file);
            int size = 128 * 1024;
            byte[] buffer = new byte[size];

            for (int count = -1; (count = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, count);
            }
            os.flush();
            is.close();
        } catch (IOException e) {
            System.out.println("sendFile error.");
            e.printStackTrace(System.out);
        }
    }


    public static void receiveFile(Socket socket, File file, long len) {
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = new FileOutputStream(file);
            int size = 128 * 1024;
            byte[] buffer = new byte[size];

            for (int count = is.read(buffer), total = count; count != -1; count = is.read(buffer), total += count) {
                os.write(buffer, 0, count);
                if (total == len) {
                    break;
                }
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            System.out.println("receive(): IO error.");
            e.printStackTrace(System.out);
        }
    }

    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return null;
        }
        return str2.substring(at);
    }

    public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return -1;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return -1;
    }

    public static boolean checkCollisions(File dir, String incomingFile, String relativePath, String baseDir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean check = checkCollisions(new File(dir, children[i]), incomingFile, relativePath, baseDir);
                if (check)
                    return check;
            }
        } else {
            if ((dir.getName().equals(incomingFile)) && (relativePath.equals(difference(baseDir, dir.getPath())))) {
                System.out.println(dir.getName() + " equals " + incomingFile);
                System.out.println(relativePath + " equals " + difference(baseDir, dir.getPath()));
                return true;
            }
        }
        return false;
    }
}

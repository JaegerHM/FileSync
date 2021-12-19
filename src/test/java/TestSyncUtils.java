import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.Socket;


public class TestSyncUtils {

    @Test
    public void testDifference() {
        String str1 = "E:\\FileSync\\test";
        String str2 = "E:\\FileSync\\test\\one.txt";

        assertEquals("\\one.txt", SyncUtil.difference(str1, str2));
    }

    @Test
    public void testCollisions() {
        String basePath = "E:\\FileSync\\test";
        String fullPath = "E:\\FileSync\\test\\one.txt";
        String relativePath = SyncUtil.difference(basePath, fullPath);

        assertTrue(SyncUtil.checkCollisions(new File(basePath), relativePath, basePath));
    }

    @Test
    public void testSending() {

        final Socket socket = mock(Socket.class);

        File file = new File("E:\\FileSync\\test\\one.txt");
        try {
            when(socket.getOutputStream()).thenReturn(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(SyncUtil.sendFile(socket, file));
    }

    @Test
    public void testReceiving() {
        final Socket socket = mock(Socket.class);

        File file = new File("E:\\FileSync\\test\\one.txt");
        try {
            InputStream in = new FileInputStream(file);
            when(socket.getInputStream()).thenReturn(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SyncUtil.receiveFile(socket, file, file.length());

        assertTrue(SyncUtil.receiveFile(socket, file, file.length()));
    }


}

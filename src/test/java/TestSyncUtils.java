import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.*;

public class TestSyncUtils {

    @Test
    public void testDifference() {
        String str1 = "abcd";
        String str2 = "abcdefg";

        assertEquals("efg", SyncUtil.difference(str1, str2));
    }


    @Test
    public void testCollisions() {
        String basePath = "E:\\123\\test";
        String fullPath = "E:\\123\\test\\one.txt";
        String name = "one.txt";
        String relativePath = SyncUtil.difference(basePath, fullPath);



        assertEquals(true, SyncUtil.checkCollisions(new File(basePath), name, relativePath, basePath));
    }
}

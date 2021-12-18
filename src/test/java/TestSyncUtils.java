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

    /*
    @Test
    public void testCollisions() {
        String name = "one.txt";
        String relativePath = "/txt_files";
        String basePath = "E:/123/test";
        assertEquals(true, SyncUtil.checkCollisions(new File(basePath), name, relativePath, basePath));
    }
*/
}

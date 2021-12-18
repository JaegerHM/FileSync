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
        String str1 = "one.txt";

        assertEquals(true, SyncUtil.checkCollisions(new File("E:/123/test"),str1));
    }

}

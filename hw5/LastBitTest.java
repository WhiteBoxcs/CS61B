import org.junit.Test;
import static org.junit.Assert.*;

/** Tests of LastBit.
 *  @author P. N. Hilfinger
 */
public class LastBitTest {

    @Test
    public void testPowers() {
        assertEquals(1, LastBit.lastBit(1));
        assertEquals(2, LastBit.lastBit(2));
        assertEquals(32, LastBit.lastBit(32));
        assertEquals(1 << 30, LastBit.lastBit(1 << 30));
        assertEquals(-2147483648, LastBit.lastBit(-2147483648));
    }

    @Test
    public void testMiddle() {
        assertEquals(4, LastBit.lastBit(4 + 32 + 64 + 2048 + 65536));
        assertEquals(1, LastBit.lastBit(1 + 4 + 32 + 64 + 2048 + 65536));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(LastBitTest.class));
    }
}


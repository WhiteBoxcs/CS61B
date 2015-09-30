import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the P2 Pattern
 *  @author Josh Hug
 */

public class TestP2Pattern {

    /** Returns true if the string s matches pattern p */
    private static boolean checkMatch(Pattern p, String s) {
        Matcher mat = p.matcher(s);
        return mat.matches();
    }

    @Test
    public void testPattern() {
        Pattern p = Pattern.compile(P2Pattern.P2);
        String good1 = "(1, 2, 33, 1, 63)";
        String good2 = "(1, 0, 3, 4, 5, 6, 7, 12312, 41)";
        String good3 = "(512,    41, 7,     2, 9)";
        String bad1 = "6, 1, 4, 1, 2, 3";
        String bad2 = "(6, 1, 4, 1, 2, 3,)";
        String bad3 = "(, 6, 1, 4, 1, 2, 3)";
        String bad4 = "(,6, 1, 4, 1, 2, 3)";
        String bad5 = "()";
        assertTrue(checkMatch(p, good1));
        assertTrue(checkMatch(p, good2));
        assertTrue(checkMatch(p, good3));
        assertFalse(checkMatch(p, bad1));
        assertFalse(checkMatch(p, bad2));
        assertFalse(checkMatch(p, bad3));
        assertFalse(checkMatch(p, bad4));
        assertFalse(checkMatch(p, bad5));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TestP2Pattern.class));
    }
}

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author You, mostly
 */

public class EveryOtherWord {
    /**
     * Collects every other string from a list of strings starting from the 0th
     * string of the list. The order in which the words are returned does not
     * matter. For example, if L contains "hey", "this", "fish", "eats" "fish",
     * "that", "are", and "green", this method would return an iterable
     * containing "hey", "fish", and "are", in no particular order.
     */

    public static Iterable<String> everyOtherWord(List<String> L) {
        Set<String> other = new HashSet<String>();
        Iterator<String> lIter = L.iterator();
        
        while(lIter.hasNext()){
            other.add(lIter.next());
            
            if(lIter.hasNext()){
                lIter.next();
            }
        }
        return other;
    }

    /**
     * Tests whether or not your everyOtherWord method works correctly. ARGS is
     * unsued.
     */
    public static void main(String[] args) {
        List<String> L = new ArrayList<String>();
        L.add("hey");
        L.add("this");
        L.add("fish");
        L.add("eats");
        L.add("fish");
        L.add("that");
        L.add("are");
        L.add("green");
        Iterable<String> s = everyOtherWord(L);

        Set<String> expected = new HashSet<String>();
        expected.add("hey");
        expected.add("fish");
        expected.add("are");

        try {
            assertTrue(haveSameItems(expected, s));
        } catch (AssertionError e) {
            System.out.println("expected: " + expected);
            System.out.println("actual: " + s);
            throw e;
        }
    }

    /**
     * Returns two if the iterables C1 and C2 contain exactly the same items,
     * not necessarily in the same order.
     */

    public static boolean haveSameItems(Iterable<String> c1,
            Iterable<String> c2) {
        if (c1 == null && c2 != null) {
            return false;
        }

        if (c1 != null && c2 == null) {
            return false;
        }

        List<String> L1 = new ArrayList<String>();
        for (String s : c1) {
            L1.add(s);
        }
        Collections.sort(L1);

        List<String> L2 = new ArrayList<String>();
        for (String s : c2) {
            L2.add(s);
        }
        Collections.sort(L2);

        return L1.equals(L2);
    }

}

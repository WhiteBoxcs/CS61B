import static org.junit.Assert.*;
import org.junit.Test;

/** Perform tests of the IntDList class
 */

public class IntDListTest {

    /** Tests the constructor and size operations. */
    @Test
    public void testSize() {
        IntDList d;
        d = new IntDList();
        assertEquals("Size of empty", 0, d.size());
        d = new IntDList(9);
        assertEquals("Size of singleton", 1, d.size());
        d = new IntDList(5, 1, 0);
        assertEquals("Size of 3-element list", 3, d.size());
    }
        
    /** Test front and back. */
    @Test
    public void testFrontBack() {
        IntDList d = new IntDList(5, 10, 15, 20);
        assertEquals("getFront", 5, d.getFront());
        assertEquals("getBack", 20, d.getBack());
    }

    /** Tests the get operation. */
    @Test
    public void testGet() {
        IntDList d = new IntDList(5, 10, 15, 20);
        assertEquals(".get(0)", 5, d.get(0));
        assertEquals(".get(1)", 10, d.get(1));
        assertEquals(".get(2)", 15, d.get(2));
        assertEquals(".get(3)", 20, d.get(3));

        assertEquals(".get(-1)", 20, d.get(-1));
        assertEquals(".get(-2)", 15, d.get(-2));
        assertEquals(".get(-3)", 10, d.get(-3));
        assertEquals(".get(-4)", 5, d.get(-4));
    }

    /** Test insertBack and insertFront. */
    @Test
    public void testInsertBack() {
        IntDList d = new IntDList();
        d.insertBack(4);
        d.insertBack(10);
        d.insertFront(3);
        d.insertFront(1);
        
        assertEquals(".size()", 4, d.size());

        assertEquals(".get(0)", 1, d.get(0));
        assertEquals(".get(1)", 3, d.get(1));
        assertEquals(".get(2)", 4, d.get(2));
        assertEquals(".get(3)", 10, d.get(3));

        assertEquals(".get(-4)", 1, d.get(-4));
        assertEquals(".get(-3)", 3, d.get(-3));
        assertEquals(".get(-2)", 4, d.get(-2));
        assertEquals(".get(-1)", 10, d.get(-1));
    }

    /* Uncomment the following methods for challenge problems, if desired: 
    @Test
    public void testDeleteBack() {
        IntDList d = new IntDList(5, 10, 15, 20);
        assertEquals(".deleteBack() value (20)", 20, d.deleteBack());
        assertEquals(".getBack()", 15, d.getBack());
        assertEquals(".deleteBack() value (15)", 15, d.deleteBack());
        assertEquals(".deleteBack() value (10)", 10, d.deleteBack());
        assertEquals(".deleteBack() value (5)", 5, d.deleteBack());
        assertEquals(".size() after delete", 0, d.size());
    }

    @Test
    public void testToString() {
        IntDList d = new IntDList(5, 10, 15, 20);
        assertEquals(".toString()", "[5, 10, 15, 20]", d.toString());
        assertEquals(".toString() of empty", "[]", new IntDList().toString());
    }
    */

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(IntDListTest.class));
    }
}

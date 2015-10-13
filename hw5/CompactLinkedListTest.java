import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** Tests of CompactLinkedList.
 *  @author P. N. Hilfinger
 */
public class CompactLinkedListTest {

    private List<Integer> std, tested;
    private ListIterator<Integer> stdIter, testedIter;

    private void add(int v) {
        tested.add(v);
        std.add(v);
    }

    private void addIter(int v) {
        testedIter.add(v);
        stdIter.add(v);
    }

    private void setIter(int k) {
        stdIter = std.listIterator(k);
        testedIter = tested.listIterator(k);
    }

    private int nextIter() {
        assertEquals(stdIter.hasNext(), testedIter.hasNext());
        int r = stdIter.next();
        assertEquals(r, (int) testedIter.next());
        return r;
    }

    private int prevIter() {
        assertEquals(stdIter.hasPrevious(), testedIter.hasPrevious());
        int r = stdIter.previous();
        assertEquals(r, (int) testedIter.previous());
        return r;
    }

    @Before
    public void setUp() {
        std = new ArrayList<>();
        tested = new CompactLinkedList<>(20);
    }

    @Test
    public void sizeTest() {
        assertEquals(std.size(), tested.size());
        add(0);
        assertEquals(std.size(), tested.size());
        add(1);
        assertEquals(std.size(), tested.size());

        setIter(0);
        addIter(2);
        assertEquals(std.size(), tested.size());
        nextIter();
        assertEquals(std.size(), tested.size());
    }

    @Test
    public void forwardTest() {
        for (int i = 0; i < 10; i += 1) {
            add(-i);
        }
        for (int i = 0; i < 10; i += 1) {
            assertEquals(std.get(i), tested.get(i));
        }
        int c;
        c = 0;
        for (int k : tested) {
            assertEquals(c, k);
            c -= 1;
        }
    }

    @Test
    public void backwardTest() {
        for (int i = 0; i < 10; i += 1) {
            add(-i);
        }
        int c;
        c = -9;
        setIter(std.size());
        while (stdIter.hasPrevious()) {
            prevIter();
        }
    }

    @Test
    public void addIterTest() {
        for (int i = 0; i < 10; i += 1) {
            add(i);
        }
        setIter(0);
        while (testedIter.hasNext()) {
            addIter(-nextIter());
        }
        assertEquals(std, tested);
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(CompactLinkedListTest.class));
    }
}


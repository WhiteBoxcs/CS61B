import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** HW #8, Problem 3.
 *  @author
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns a
     *  count of the number of intervals that X stabs (i.e. that x belongs to.
     */
    public static int stabbingCount(List<int[]> intervals, int x) {
        // REPLACE WITH APPROPRIATE STATEMENTS. My solution adds 14 lines
        // and uses no additional imports beyond those at the top of this 
        // file.
        return 0;
    }

    /** Performs a basic functionality test on the stabbingCount method. */
    @Test
    public void basicTest() {
        int[][] rangeMatrix = {{3, 10}, {4, 5}, {6, 12}, {8, 15}, {19, 30}};
        List<int[]> intervals = new ArrayList<int[]>();
        for (int i = 0; i < rangeMatrix.length; i += 1) {
            intervals.add(rangeMatrix[i]);
        }
        assertEquals(3, stabbingCount(intervals, 9));
        assertEquals(0, stabbingCount(intervals, 17));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}

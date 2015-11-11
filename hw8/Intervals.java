import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** HW #8, Problem 3.
 *  @author
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        
        intervals.sort((arr1,arr2) ->  arr1[0]-arr2[0]);
        
        
        int outerMeasure = 0;
        
        int start = Integer.MIN_VALUE;
        int end = Integer.MIN_VALUE;
        
        for(int i = 0; i < intervals.size(); i++){
            if(intervals.get(i)[0] > end){
                outerMeasure += (end-start);
                start = intervals.get(i)[0];
                end = intervals.get(i)[1];
            } else if(intervals.get(i)[0] <= end && intervals.get(i)[1] > end ){
                    end = intervals.get(i)[1];
            }
        }
        
        outerMeasure += (end-start);

        return outerMeasure;
    }

    /** Performs a basic functionality test on the stabbingCount method. */
    @Test
    public void basicTest() {
        int[][] intervals = {
                {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
            };
            assertEquals(23, coveredLength(Arrays.asList(intervals)));
            
        intervals = new int[][]{
                {1, 1},
            };
        assertEquals(0, coveredLength(Arrays.asList(intervals)));
        
        intervals = new int[][]{
            {1, 10}, {2, 8}
        };
        assertEquals(9, coveredLength(Arrays.asList(intervals)));
        
        intervals = new int[][]{
            {1, 10}, {2,8}, {3,11}, {12,12}, {12,13}, {12,15}, {12,14}
        };
        assertEquals(13, coveredLength(Arrays.asList(intervals)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}

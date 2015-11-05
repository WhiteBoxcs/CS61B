import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

/** FIXME
 *  @author Josh Hug
 */

public class MySortingAlgorithmsTest {

    @Test
    public void correctnessTest() {
        /* Don't set maxValue too high or Distribution Sort will use
           up all available memory and your program will crash. */
        int numInts = 22;
        int maxValue = 1000;
        int[] original = BenchmarkUtility.randomInts(numInts, maxValue);
        int[] correct = BenchmarkUtility.copy(original);
        SortingAlgorithm javaSort = new MySortingAlgorithms.JavaSort();
        javaSort.sort(correct, correct.length);

        SortingAlgorithm[] algorithms = {
                 new MySortingAlgorithms.InsertionSort(),
                 new MySortingAlgorithms.SelectionSort(),
                 new MySortingAlgorithms.MergeSort(),
                 new MySortingAlgorithms.DistributionSort(),
                 new MySortingAlgorithms.HeapSort(),
                 new MySortingAlgorithms.QuickSort(),
                 new MySortingAlgorithms.LSDSort(),
                 new MySortingAlgorithms.MSDSort()};

        for (SortingAlgorithm sa : algorithms) {
            int[] input = BenchmarkUtility.copy(original);
            sa.sort(input, input.length);
            assertArrayEquals("Result for " + sa + " inorrect: ",
                              correct, input);
        }

        int k = 20;

        correct = BenchmarkUtility.copy(original);
        javaSort.sort(correct, k);

        for (SortingAlgorithm sa : algorithms) {
            int[] input = BenchmarkUtility.copy(original);
            sa.sort(input, k);
            assertArrayEquals("Result for " + sa + " inorrect: ",
                              correct, input);
        }
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MySortingAlgorithmsTest.class));
    }
} 
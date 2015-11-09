import org.junit.Test;
import static org.junit.Assert.*;

/** Runs timing tests on various sorting algorithms.
 *  @author Josh Hug
 */
public class RunBenchmarks {

    public static void printTime(SortingAlgorithm sa, int[] array,
                                 String inputDescription) {
        int[] copy = BenchmarkUtility.copy(array);
        double timeTaken = BenchmarkUtility.time(sa, copy);
        System.out.printf("%s took %.2f seconds to sort %s.\n",
                          sa.toString(), timeTaken, inputDescription);        
    }

    public static void largeArrayTest() {
        int numInts = 10000000;
        int maxVal = Integer.MAX_VALUE;
        int[] original = BenchmarkUtility.randomInts(numInts, Integer.MAX_VALUE);
        int[] input = BenchmarkUtility.copy(original);

        String inputDescription = String.format("%d numbers from 0 to %d",
                                                numInts, maxVal);

        printTime(new MySortingAlgorithms.MergeSort(), input, inputDescription);
        printTime(new MySortingAlgorithms.JavaSort(), input, inputDescription);
    }

    public static void almostSortedTest() {
        int numInts = 10000000;
        int maxVal= Integer.MAX_VALUE;
        int[] original = 
              BenchmarkUtility.randomNearlySortedInts(numInts, maxVal);
        int[] input = BenchmarkUtility.copy(original);

        String inputDescription = 
               String.format("%d partially sorted numbers from 0 to %d",
               numInts, maxVal);

        printTime(new MySortingAlgorithms.InsertionSort(), 
                  input, inputDescription);
    }    

    public static void main(String[] args) {
        largeArrayTest();
        almostSortedTest();
    }
} 
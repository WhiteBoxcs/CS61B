import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import utils.Filter;
import utils.Predicate;

/**
 * Exercises for Lab 6.
 * @author You.
 */
public class Iterating {

    /** A couple of test cases. */
    private static final Integer[][] TESTS = {
        { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
        { 1, 2, 3, 0, 7, 8, 6, 9, 10, 1 } };

    /** Print out the items returned by L. */
    static void printAll(Filter<Integer> L) {
        System.out.print("[");
        String sep;
        sep = "";
        for (Integer i : L) {
            System.out.print(sep + i);
            sep = ", ";
        }
        System.out.println("]");
    }

    /**
     * Returns a filter that delivers every fourth item of INPUT, starting with
     * the first. You should not need to define a new class.
     */
    static Filter<Integer> everyFourth(Iterator<Integer> input) {
        return null;  // FIXME
    }

    /** Test the Filter classes. */
    public static void main(String... unused) {
        for (Integer[] data : TESTS) {
            List<Integer> test = Arrays.asList(data);
            Filter<Integer> f1 = new TrivialFilter<Integer>(test.iterator());
            printAll(f1);

            Filter<Integer> f2 = new PredicateFilter<Integer>(new Even(),
                    test.iterator());
            printAll(f2);

            Filter<Integer> f3 = new AlternatingFilter<Integer>(
                    test.iterator());
            printAll(f3);

            Filter<Integer> f4 = everyFourth(test.iterator());
            printAll(f4);

        }
    }

    /** A class whose instances represent the test for eveness. */
    static class Even implements Predicate<Integer> {
        @Override
        public boolean test(Integer x) {
            return x % 2 == 0;
        }
        // FIXME
    }

}

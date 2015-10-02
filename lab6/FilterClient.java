import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import utils.Filter;
import utils.Predicate;

/**
 * Exercises for Lab 6.
 * @author You.
 */
public class FilterClient {

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
     * A sample space where you can experiment with your filter. ARGS is
     * unused.
     */
    public static void main(String[] args) {
        List<Integer> L = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        System.out.println(L);
        Filter<Integer> f1 = new TrivialFilter<Integer>(L.iterator());
        printAll(f1);

        /*
         * Extra Challenges that you should complete without creating any new
         * Filter implementations (i.e. you can create them using Trivial,
         * Alternating, Monotonic, and/or PredicateFilter) 1. Create a filter
         * everyFourth that prints very fourth item. 2. Create a filter that
         * prints only even valued items.
         */

        printAll(everyFourth(L.iterator()));
        printAll(everyEven(L.iterator()));
    }

    /** Below follow some additional puzzles you should feel free to attempt. */

    /**
     * Returns a filter that delivers every fourth item of INPUT, starting with
     * the first. You should not need to define a new class.
     */
    static Filter<Integer> everyFourth(Iterator<Integer> input) {
        Filter<Integer> evother = new AlternatingFilter<Integer>(input);
        Filter<Integer> evfourth = new AlternatingFilter<Integer>(evother);
        
        return evfourth;
    }
    
    static Filter<Integer> everyEven(Iterator<Integer> input){
        Filter<Integer> everyEven = 
                new PredicateFilter<Integer>( x-> x%2 == 0, input);
        
        return everyEven;
    }

}

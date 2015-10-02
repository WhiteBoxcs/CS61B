import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import utils.Filter;
import utils.Predicate;

/**
 * FIXME
 * @author Josh Hug
 */

public class TestFilters {
    private List<Integer> genList(int... ints) {
        List<Integer> L = new ArrayList<Integer>();
        for (int i : ints) {
            L.add(i);
        }
        return L;
    }

    @Test
    public void testTrivialFilter() {
        List<Integer> L = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        List<Integer> actual = new ArrayList<Integer>();
        Filter<Integer> tf = new TrivialFilter<Integer>(L.iterator());
        for (int i : tf) {
            actual.add(i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void testAlternatingFilter() {
        List<Integer> L = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        List<Integer> expected = Arrays.asList(0, 2, 4, 6);
        List<Integer> actual = new ArrayList<Integer>();
        Filter<Integer> tf = new AlternatingFilter<Integer>(L.iterator());
        for (int i : tf) {
            actual.add(i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void testMonotonicFilter() {
        List<Integer> L = Arrays.asList(1, 2, 3, 3, 2, 1, 5);
        List<Integer> expected = Arrays.asList(1, 2, 3, 5);
        List<Integer> actual = new ArrayList<Integer>();
        Filter<Integer> tf = new MonotonicFilter<Integer>(L.iterator());
        for (int i : tf) {
            actual.add(i);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void testPredicateFilters() {
        List<Integer> L = Arrays.asList(1, 2, 3, 3, 2, 1, 5);
        List<Integer> expected = Arrays.asList(1, 2, 3, 3, 2, 1);
        List<Integer> actual = new ArrayList<Integer>();
        Filter<Integer> tf = new PredicateFilter<Integer>(new LessThanFive(),
                L.iterator());
        for (int i : tf) {
            actual.add(i);
        }
        assertEquals(expected, actual);
    }

    /** A class whose instances represent the test for eveness. */
    static class LessThanFive implements Predicate<Integer> {
        @Override
        public boolean test(Integer x) {
            return x < 5;
        }
    }

    /**
     * Tests your filters
     */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TestFilters.class));
    }
}

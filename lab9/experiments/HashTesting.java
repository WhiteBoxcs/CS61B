import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import java.io.File;
import java.io.FileNotFoundException;

/** Methods for testing the speed of hashing.
 *  @author
 */
public class HashTesting {

    /** Return an array of N*N four-character strings of the form xxyy, for
     *  all pairs 1 <= x, y <= N. */
    static ArrayList<String> set0(int N) {
        ArrayList<String> result = new ArrayList<String>(N * N);
        for (int i = 1, n = 0; i <= N; i += 1) {
            for (int j = 1; j <= N; j += 1, n += 1) {
                result.add("" + (char) i + (char) i + (char) j + (char) j);
            }
        }
        return result;
    }

    /** Return an array of N*N four-character strings of the form xXyY, for
     *  X = 2**16 - x*31 - 1 and Y == 2**16 - y*31 - 1, for all pairs
     *  of values 1 <= x, y <= N.  (Here, ** denotes exponentiation). */
    static ArrayList<String> set1(int N) {
        ArrayList<String> result = new ArrayList<String>(N * N);

        for (int i = 1, n = 0; i <= N; i += 1) {
            for (int j = 1; j <= N; j += 1, n += 1) {
                result.add("" + (char) i + (char) (0xffff - 31 * i)
                            + (char) j + (char) (0xffff - 31 * j));
            }
        }
        return result;
    }

    /** Add all type T objects in ALLOBJECTS to ITEMS, check that all the
     *  objects in CHECKOBJECTS are in that set, and print the time
     *  required to do so. */
    static <T> void timeOne(Set<T> items,
                            List<? extends T> allObjects,
                            List<? extends T> checkObjects) {
        long start = System.currentTimeMillis();
        for (T s : allObjects) {
            items.add(s);
        }
        for (Object s : checkObjects) {
            if (!items.contains(s)) {
                System.err.printf("Did not find '%s'%n", s);
            }
        }
        long end = System.currentTimeMillis();
        double totalOps = allObjects.size() + checkObjects.size();

        System.out.printf("Added %d items and checked %d items in "
                          + "%.1f seconds (%.2g msec/string)%n",
                          allObjects.size(), checkObjects.size(),
                          (end - start) * 0.001,
                          (end - start) / totalOps);
    }

    /** Read strings from FILE, store them in a HashSet that uses a hashing
     *  function that looks only at every SKIP th character, timing how long
     *  it takes to store and retrieve them. */
    static void test1(String file, String skip) {
        int k = Integer.parseInt(skip);
        ArrayList<String1> items = new ArrayList<String1>();
        try {
            Scanner inp = new Scanner(new File(file));
            while (inp.hasNext()) {
                items.add(new String1(inp.next(), k));
            }
            System.out.printf("Strings from %s, skip=%d:%n\t", file, k);
            timeOne(new HashSet<Object>(), items, items);
        } catch (FileNotFoundException e) {
            System.err.printf("Could not open %s%n", file);
        }
    }

    /** Create N*N strings of the form "xxyy", where x and y vary
     *  independently between 1 and N.  Time how long it takes to add
     *  them to a HashSet and check that they are there. */
    static void test2(String N) {
        List<String> set = set0(Integer.parseInt(N));
        System.out.print("Strings xxyy: ");
        timeOne(new HashSet<Object>(), set, set);
    }

    /** Create N*N strings of the form "xXyY", where x and y vary
     *  independently between 1 and N, X = 2**16 - 31x - 1 and
     *  Y = 2**16 - 31y - 1. Time how long it takes to add
     *  them to a HashSet and check that they are there. */
    static void test3(String N) {
        List<String> set = set1(Integer.parseInt(N));
        System.out.print("Strings xXyY: ");
        timeOne(new HashSet<Object>(), set, set);
    }

    /** Add the strings in ARGS[1..] to a TreeSet and HashSet as FoldedStrings,
     *  then check that their upper-case versions are in the sets also. */
    static void test4(String[] args) {
        System.out.println("Folded strings....");
        TreeSet<FoldedString> set0 = new TreeSet<FoldedString>();
        HashSet<FoldedString> set1 =
            new HashSet<FoldedString>(args.length - 1, 2.0f);
        for (String x : args) {
            if (x == args[0]) {
                continue;
            }
            FoldedString s = new FoldedString(x);
            set0.add(s);
            set1.add(s);
        }
        for (String x : args) {
            if (x == args[0]) {
                continue;
            }
            FoldedString s = new FoldedString(x.toUpperCase());
            if (!set0.contains(s)) {
                System.out.printf("    TreeSet does not contain '%s'%n", s);
            }
            if (!set1.contains(s)) {
                System.out.printf("    HashSet does not contain '%s'%n", s);
            }
        }
    }


    /** Run test1, test2, test3, or test4, depending on ARGS[0]. */
    public static void main(String[] args) {
        if (args[0].equals("test1")) {
            test1(args[1], args[2]);
        } else if (args[0].equals("test2")) {
            test2(args[1]);
        } else if (args[0].equals("test3")) {
            test3(args[1]);
        } else if (args[0].equals("test4")) {
            test4(args);
        } else {
            System.err.println("I don't know that test.");
        }
    }

}

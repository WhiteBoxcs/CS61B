import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * @author Josh Hug
 */

public class ReadInts {

    /**
     * Takes a String INPUT and prints out all the integers available from the
     * scanner each on their own line. OK to assume scanner only contains
     * integers (i.e. letting Java implicitly throw an exception for bad inputs
     * is ok)
     */
    public static void printInts(String input) {
        Scanner s = new Scanner(input);
        while (s.hasNext()) {
            System.out.println(s.nextInt());
        }
    }

    /**
     * Takes a string INPUT and returns all ints in that string as a list in
     * the order they appeared in the string.
     */
    public static List<Integer> readInts(String input) {
        /*
         * We haven't discussed <> notation yet, but this means that the
         * ArrayList contains integers. Just accept this for now. This is the
         * last point of syntax we'll force you to accept.
         */
        ArrayList<Integer> L = new ArrayList<Integer>();

        Scanner s = new Scanner(input);
        while (s.hasNext()) {
            int nextInt = s.nextInt();
            L.add(nextInt);
        }
        return L;
    }

    /**
     * Read String INPUT into List but skip any non-integer tokens. Should not
     * throw an InputMismatchException, even if the string has non integer
     * tokens tokens. Returns this list of ints as a List<Integer> Use
     * hasNext(), next(), nextInt() and hasNextInt()
     */
    public static List<Integer> smartReadInts(String input) {
        ArrayList<Integer> L = new ArrayList<Integer>();

        Scanner s = new Scanner(input);
        while (s.hasNext()) {
            try {
                L.add(s.nextInt());
            } catch (InputMismatchException exp) {
                s.next();
            }

        }
        return L;
    }
}

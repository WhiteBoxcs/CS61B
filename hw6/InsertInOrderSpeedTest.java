import java.util.TreeSet;
import java.io.IOException;
import java.util.Scanner;

/** Performs a timing test on three different set implementations.
 *  @author Josh Hug
 */
public class InsertInOrderSpeedTest {
    /** Returns time needed to put N strings into SS in increasing order.
     *  You will find the StringUtils.nextString(String s) method useful for
     *  writing this test.
     */
    public static double insertInOrder(StringSet ss, int N) {
        Stopwatch sw = new Stopwatch();
        String s = "cat";
        for (int i = 0; i < N; i++) {
            s = StringUtils.nextString(s);
            ss.put(s);
        }
        return sw.elapsedTime();
    }

    /** Returns time needed to put N strings into TS in increasing order.
     */
    public static double insertInOrder(TreeSet<String> ts, int N) {
        Stopwatch sw = new Stopwatch();
        String s = "cat";
        for (int i = 0; i < N; i++) {
            s = StringUtils.nextString(s);
            ts.add(s);
        }
        return sw.elapsedTime();
    }

    /** Requests user input and performs tests of three different set
        implementations. ARGS is unused. */
    public static void main(String[] args) throws IOException {
        int N;
        Scanner input = new Scanner(System.in);


        System.out.println("This program inserts lexicographically increasing "
                           + "strings into a set of Strings.");
        System.out.print("\nEnter # strings to insert into linked-list set: ");
        N = input.nextInt();
        StringSet llBased = new LinkedListStringSet();
        double llTime = insertInOrder(llBased, N);
        System.out.printf("Linked List StringSet: %.2f sec\n", llTime);


        System.out.print("\n# of strings to insert into your BSTStringSet: ");
        N = input.nextInt();
        StringSet yourTreeBased = new BSTStringSet();
        double yourTime = insertInOrder(yourTreeBased, N);
        System.out.printf("Your BSTStringSet: %.2f sec\n", yourTime);


        System.out.print("\nEnter # strings to insert into Java's TreeSet: ");
        N = input.nextInt();
        TreeSet<String> javaTreeBased = new TreeSet<String>();
        double javaTime = insertInOrder(javaTreeBased, N);
        System.out.printf("Built-in Java TreeSet<String>: %.2f sec\n",
                          javaTime);
    }
}

import java.util.TreeSet;
import java.io.IOException;
import java.util.Scanner;

/** Performs a timing test on three different set implementations.
 *  @author Josh Hug
 */
public class InsertRandomSpeedTest {
    /** Returns time needed to put N random strings of length L into the
      * StringSet SS. */
    public static double insertRandomStrings(StringSet ss, int N, int L) {
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < N; i++) {
            String s = StringUtils.randomString(L);
            ss.put(s);
        }
        return sw.elapsedTime();
    }

    /** Returns time needed to put N random strings of length L into the
      * TreeSet TS. */
    public static double insertRandomStrings(TreeSet<String> ts, int N, int L) {
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < N; i++) {
            String s = StringUtils.randomString(L);
            ts.add(s);
        }
        return sw.elapsedTime();
    }

    /** Requests user input and performs tests of three different set
        implementations. ARGS is unused. */
    public static void main(String[] args) throws IOException {
        int N;
        Scanner input = new Scanner(System.in);


        System.out.println("This program inserts random length 10 strings "
                           + "into a set of Strings.");
        System.out.print("\nEnter # strings to insert into linked-list set: ");
        N = input.nextInt();
        StringSet llBased = new LinkedListStringSet();
        double llTime = insertRandomStrings(llBased, N, 10);
        System.out.printf("Linked List StringSet: %.2f sec\n", llTime);


        System.out.print("\n# of strings to insert into your BSTStringSet: ");
        N = input.nextInt();
        StringSet yourTreeBased = new BSTStringSet();
        double yourTime = insertRandomStrings(yourTreeBased, N, 10);
        System.out.printf("Your BSTStringSet: %.2f sec\n", yourTime);


        System.out.print("\nEnter # strings to insert into Java's TreeSet: ");
        N = input.nextInt();
        TreeSet<String> javaTreeBased = new TreeSet<String>();
        double javaTime = insertRandomStrings(javaTreeBased, N, 10);
        System.out.printf("Built-in Java TreeSet<String>: %.2f sec\n",
                          javaTime);
    }
}

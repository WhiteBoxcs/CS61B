/** Interface for a basic String set.
  * @author Josh Hug */
public interface StringSet {
    /** Adds the string S to the string set. If it is already present in the
      * set, do nothing.
      */
    void put(String s);

    /** Returns true if S is in the string set. */
    boolean contains(String s);
}

/** A wrapper class for Strings.
 *  Implements only public operations of Object and a constructor that takes
 *  a String.  The toString method returns the constructor's argument.
 *  @author
 */
class String1 {

    /** A new String1 that represents the string S and whose hash function
     *  looks at every Kth character.   When K==1, the hash function is
     *  identical to that for java.lang.String. */
    public String1(String s, int k) {
        rep = s;
        skip = k;
    }

    /** Returns my hash value. This is a duplicate of Java's actual hash
     *  function on String. */
    @Override
    public int hashCode() {
        int h;
        h = 0;
        for (int i = 0, len = rep.length(), step = skip; i < len; i += step) {
            h = h * 31 + rep.charAt(i);
        }
        return h;
    }

    @Override
    public String toString() {
        return rep;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return rep.equals(((String1) obj).rep);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /** Wrapped string from constructor. */
    private String rep;
    /** Spacing of characters considered. */
    private int skip;
}


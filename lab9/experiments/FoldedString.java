/** A wrapper class for String whose .equals and .compareTo methods ignore
 *  the difference between upper and lower case letters.
 *  @author
 */
class FoldedString implements Comparable<FoldedString> {

    /** A new FoldedString that represents the string S. */
    public FoldedString(String s) {
        rep = s;
    }

    @Override
    public String toString() {
        return rep;
    }

    @Override
    public int hashCode() {
        return rep.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return rep.equalsIgnoreCase(((FoldedString) obj).rep);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int compareTo(FoldedString anotherString) {
        return rep.compareToIgnoreCase(anotherString.rep);
    }

    /** The string given to the constructor. */
    private String rep;

}

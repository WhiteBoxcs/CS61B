/** Generalized set that can store any Comparable item.
 *
 *  Implementation of this interface is optional for hw6.
 *  @author Josh Hug
 */
public interface GenericSet<T extends Comparable> {
    /** Adds X the set. If it is already present in the set, do nothing. */
    void put(T x);

    /** Returns true if X is in the set. */
    boolean contains(T x);
}

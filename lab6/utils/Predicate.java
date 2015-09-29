package utils;

/** An object that stands for a test of values of type INPUT.
 *  @author P. N. Hilfinger
 */
public interface Predicate<Input> {
    /** Returns the value of this test when applied to X. */
    boolean test(Input x);
}

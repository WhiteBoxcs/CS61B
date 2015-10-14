/** Interface for a basic dictionary where words may have only one meaning.
 *
 *  @author Josh Hug
 */
public interface SimpleDictionary {
    /** Associates the given DEFINITION with the WORD. If the word already
      *  has a definition, overwrite it.
      */
    void put(String word, String definition);

    /** Returns true if WORD is in this dictionary. */
    boolean contains(String word);

    /** Return the definition of WORD, or null if the word is not in
      * the dictionary.
      */
    String get(String word);
}

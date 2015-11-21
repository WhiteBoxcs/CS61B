package loa.util;

/**
 * Provides a set of string tools for use in the project.
 * @author william
 */
public class StringTools {

    /**
     * Capitalizes the first letter of a string.
     * @param original
     *            The string to capitalize.
     * @return The capitalized string.
     */
    public static String capitalizeFirstLetter(String original) {
        if (original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}

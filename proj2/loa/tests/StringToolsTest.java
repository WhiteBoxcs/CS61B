package loa.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import loa.util.StringTools;

/**
 * Tests the string tools packaged with the project.
 * @author william
 */
public class StringToolsTest {

    /**
     * Tests the capitalizing of the first letter.
     */
    @Test
    public void testCapitalizeFirstLetter() {
        String empty = "";
        assertEquals(empty, StringTools.capitalizeFirstLetter(empty));

        String normal = "hi";
        assertEquals("Hi", StringTools.capitalizeFirstLetter(normal));

        String numericStart = "1isd";
        assertEquals(numericStart,
                StringTools.capitalizeFirstLetter(numericStart));
    }

}

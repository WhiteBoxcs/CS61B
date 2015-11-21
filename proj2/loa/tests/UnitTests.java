/**
 *
 */
package loa.tests;

import ucb.junit.textui;

/**
 * @author william Runs all the unit tests for the project
 */
public class UnitTests {
    /**
     * Runs all of the unit tests for this project.
     * @param args
     *            The arguments to the unit test
     */
    public static void main(String... args) {
        System.exit(textui.runClasses(AcyclicMachinePlayerTest.class,
                HumanPlayerTest.class, BitMatrixTest.class,
                StringToolsTest.class, BoardTest.class));
    }
}

package db61b;

import ucb.junit.textui;

public class UnitTests {
    /**
     * Runs all of the unit tests for this project.
     * @param args
     *            The arguments to the unit test
     */
    public static void main(String... args) {
        System.exit(textui.runClasses(RowTest.class, TableIteratorTest.class,
                TableTest.class, ConditionTest.class, ColumnTest.class));
    }
}

package db61b;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConditionTest {

    private static Table TAB;
    private static List<Row> ROWS;
    private static final String[] COLS =
            new String[] { "SID", "NAME", "LEVEL", "CLASS" };
    private static final String NAME = "TEST";

    private static Column COL1;
    private static Column COL2;
    private TableIterator tabIt;

    @BeforeClass
    public static void setUpOnce() {
        ROWS = new ArrayList<Row>();
        ROWS.add(new Row(new String[] { "1", "Timmy", "10", "Mage" }));
        ROWS.add(new Row(new String[] { "23", "Basd", "1", "Magec" }));
        ROWS.add(new Row(new String[] { "45", "Geortge", "10", "Maper" }));
        ROWS.add(new Row(new String[] { "231", "Halo", "10", "Warrior" }));

        TAB = new Table(NAME, COLS);

        for (Row row : ROWS) {
            TAB.add(row);
        }

        COL1 = new Column(TAB, COLS[0]);
        COL2 = new Column(TAB, COLS[1]);

    }

    @Before
    public void setUp() {
        this.tabIt = TAB.tableIterator();
        List<TableIterator> tList = new ArrayList<TableIterator>();
        tList.add(this.tabIt);
        COL1.resolve(tList);
        COL2.resolve(tList);
    }

    @Test
    public void testTest() {
        Condition gt = new Condition(COL1, ">", COL2);
        Condition lt = new Condition(COL1, "<", COL2);
        Condition geq = new Condition(COL1, ">=", COL2);
        Condition leq = new Condition(COL1, "<=", COL2);
        Condition neq = new Condition(COL1, "!=", COL2);
        Condition eq = new Condition(COL1, "=", COL2);

        do {
            assertFalse(gt.test());
            assertTrue(lt.test());
            assertFalse(eq.test());
            assertTrue(leq.test());
            assertFalse(geq.test());
            assertTrue(neq.test());

            this.tabIt.next();
        } while (this.tabIt.hasRow());

    }

}

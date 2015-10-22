package db61b;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ColumnTest {
    private static Table _tab;
    private static List<Row> _rows;
    private static final String[] COLS =
            new String[] { "SID", "NAME", "LEVEL", "CLASS" };
    private static final String NAME = "TEST";

    private static Column _col1;
    private static Column _col2;

    @BeforeClass
    public static void setUpOnce() {
        _rows = new ArrayList<Row>();
        _rows.add(new Row(new String[] { "1", "Timmy", "10", "Mage" }));
        _rows.add(new Row(new String[] { "23", "Basd", "1", "Magec" }));
        _rows.add(new Row(new String[] { "45", "Geortge", "10", "Maper" }));
        _rows.add(new Row(new String[] { "231", "Halo", "10", "Warrior" }));

        _tab = new Table(NAME, COLS);

        for (Row row : _rows) {
            _tab.add(row);
        }
    }

    @Before
    public void setUp() {
        _col1 = new Column(_tab, COLS[0]);
        _col2 = new Column(null, COLS[1]);
    }

    @Test
    public void testName() {
        assertEquals(COLS[0], _col1.name());
        assertEquals(COLS[1], _col2.name());
        Column anon = new Column("ANON");
        assertEquals(null, anon.name());
        anon.setAlias("ASDASD");
        assertEquals(null, anon.name());

        _col1.setAlias("qwe");
        assertEquals(COLS[0], _col1.name());
    }

    @Test
    public void testResolve() {
        Table other1 = new Table("LOL", new String[] {});
        Table other2 = new Table("CATS!", new String[] {});
        List<TableIterator> tabIts = new ArrayList<TableIterator>();

        tabIts.add(other1.tableIterator());
        tabIts.add(other2.tableIterator());
        tabIts.add(_tab.tableIterator());

        _col1.resolve(tabIts);
        assertEquals("1", _col1.value());
        _col2.resolve(tabIts);
        assertEquals("Timmy", _col2.value());
    }

    @Test
    public void testValue() {

        List<TableIterator> tList = new ArrayList<TableIterator>();
        tList.add(_tab.tableIterator());
        _col1.resolve(tList);
        _col2.resolve(tList);

        int i = 0;
        do {
            assertEquals(_rows.get(i).get(0), _col1.value());
            assertEquals(_rows.get(i).get(1), _col2.value());

            tList.get(0).next();
            i++;
        } while (tList.get(0).hasRow());
    }

    @Test
    public void testAlias() {
        assertEquals(COLS[0], _col1.alias());
        assertEquals(COLS[1], _col2.alias());
        Column anon = new Column("ANON");
        assertEquals(null, anon.alias());
        anon.setAlias("ASDASD");
        assertEquals("ASDASD", anon.alias());
        assertEquals(null, anon.name());

        _col1.setAlias("qwe");
        assertEquals(COLS[0], _col1.name());
        assertEquals("qwe", _col1.alias());
    }

}

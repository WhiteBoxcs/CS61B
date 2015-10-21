package db61b;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class TableIteratorTest {

	private Table testTable;
	private TableIterator tabIt;
	private List<Row> rows;
	
	@Before
	public void setUp() throws Exception {
		testTable = new Table("Test", new String[] {"SID", "NAME", "LEVEL", "CLASS"});
		
		rows = new ArrayList<Row>();
		rows.add(new Row(new String[] {"1", "Timmy", "10", "Mage"}));
		rows.add(new Row(new String[] {"23", "Basd", "1", "Magec"}));
		rows.add(new Row(new String[] {"45", "Geortge", "10", "Maper"}));
		rows.add(new Row(new String[] {"231", "Halo", "10", "Warrior"}));
		
		for(Row row : rows){
			testTable.add(row);
		}
		
		tabIt = testTable.tableIterator();
		
	}

	
	public void assertRowEqual(int k){
		for(int i = 0; i < testTable.numColumns(); i++){
			assertEquals(rows.get(k).get(i), tabIt.value(i));
		}
	}
	
	
	@Test
	public void testReset() {
		tabIt.next();
		tabIt.next();
		tabIt.reset();
		assertRowEqual(0);
		tabIt.next();
		tabIt.reset();
		assertRowEqual(0);
		
	}

	@Test
	public void testTable() {
		assertEquals(testTable, tabIt.table());
	}

	@Test
	public void testHasRow() {
		for(int i = 0; i < rows.size(); i++){
			assertTrue(tabIt.hasRow());
			tabIt.next();
		}
		assertFalse(tabIt.hasRow());
	}

	@Test
	public void testNext() {
		for(int i = 0; i < rows.size(); i++){
			assertRowEqual(i);
			tabIt.next();
		}
	}

	@Test
	public void testColumnIndex() {
		for(int i = 0; i < rows.size(); i++){
			for(int col = 0; col < testTable.numColumns(); col++){
				int colIndex = tabIt.columnIndex(testTable.title(col));
				assertEquals(col, colIndex);
				assertEquals(-1, tabIt.columnIndex("asdasdasd"));
				assertEquals(-1, tabIt.columnIndex(null));
			}
			tabIt.next();
		}
	}

	@Test
	public void testValue() {
		assertRowEqual(0);
	}
	

}

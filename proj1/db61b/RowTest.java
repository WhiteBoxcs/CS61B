package db61b;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RowTest {

    @Test
    public void testRowStringArray() {
        Row rTest = new Row(new String[] { "Test", "Box", "Other" });
        assertEquals("Box", rTest.get(1));
        assertEquals("Test", rTest.get(0));
        assertEquals("Other", rTest.get(2));
    }

    @Test
    public void testSize() {
        Row rTest = new Row(new String[] { "Test", "Box", "Other" });
        assertEquals(rTest.size(), 3);
        Row rEmpty = new Row(new String[] {});
        assertEquals(rEmpty.size(), 0);
    }

    @Test
    public void testGet() {
        Row rTest = new Row(new String[] { "Test", "Box", "Other" });
        assertEquals("Box", rTest.get(1));
        assertEquals("Test", rTest.get(0));
        assertEquals("Other", rTest.get(2));
    }

    @Test
    public void testToDBFormat() {
        Row rTest = new Row(new String[] { "Test", "Box", "Other" });
        assertEquals("Test,Box,Other", rTest.toDBFormat());
        Row rEmpty = new Row(new String[] {});
        assertEquals("", rEmpty.toDBFormat());
    }

}

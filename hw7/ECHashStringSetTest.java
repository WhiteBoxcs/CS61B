import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ECHashStringSetTest {
    private static String[] DATA =
            new String[] {"Ters", "asd", "ads", "abc", "abd", ":coont"};
    private ECHashStringSet hashSet;
    private ECHashStringSet emptySet;
    
    @Before
    public void setUp() throws Exception {
        hashSet = new ECHashStringSet();
        emptySet = new ECHashStringSet();
    }

    @Test
    public void testPut() {
        int i = 0;
        for(String data : DATA){
            hashSet.put(data);
            i++;
            assertEquals(i,hashSet.size());
        }
        
        emptySet.put(null);
        assertEquals(0,emptySet.size());
    }

    @Test
    public void testContains() {
        for(String data : DATA)
            hashSet.put(data);
        
        for(String data : DATA){
            assertFalse(emptySet.contains(data));
            assertTrue(hashSet.contains(data));
        }
        
        assertFalse(emptySet.contains(null));
        
        
    }

}

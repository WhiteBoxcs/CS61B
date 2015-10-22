import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BSTDictionaryTest {
    
    private static final String[] WORDS =
        {"asd","asdqwe","batqwe","iwsdnf","asdqweyhh"};
    private  static final String[] DEFS =
        {"asdwef", "g", "asdtgyg", "asti", "gtuibnjf"};
    
    private BSTDictionary fullDict;

    @Before
    public void setUp() throws Exception {
        fullDict = new BSTDictionary();
        for(int i = 0; i < WORDS.length; i++)
            fullDict.put(WORDS[i], DEFS[i]);
    
    }

    @Test
    public void testContains() {
        for(String word : WORDS)
            assertTrue(fullDict.contains(word));

        assertFalse(fullDict.contains(null));
        assertFalse(fullDict.contains("qwe"));
        
        BSTDictionary emptyDict = new BSTDictionary();
        for(String word : WORDS)
            assertFalse(emptyDict.contains(word));
        
    }


}

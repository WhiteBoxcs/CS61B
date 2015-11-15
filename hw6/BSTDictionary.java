
/**
 * @author William Hebgen Guss
 *
 */
public class BSTDictionary implements SimpleDictionary {
    public BSTDictionary(){
        this.store = new BSTGenericSet<KVP>();
    }
    
    /* (non-Javadoc)
     * @see SimpleDictionary#put(java.lang.String, java.lang.String)
     */
    @Override
    public void put(String word, String definition) {
        KVP data = new KVP(word, definition);
        
        store.put(data);

    }

    /* (non-Javadoc)
     * @see SimpleDictionary#contains(java.lang.String)
     */
    @Override
    public boolean contains(String word) {
        if(word == null)
            return false;
        return store.contains(new KVP(word,null));
    }

    /* (non-Javadoc)
     * @see SimpleDictionary#get(java.lang.String)
     */
    @Override
    public String get(String word) {
         KVP val = store.get(new KVP(word, null));
         if(val != null)
             return val.getValue();
         else
             return null;
    }
    
    BSTGenericSet<KVP> store = new BSTGenericSet<KVP>();
    
    
    private class KVP implements Comparable{
        private String key;
        private String value;

        public KVP(String key, String value){
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int compareTo(Object o) {
            KVP other = (KVP)o;
            
            return getKey().compareTo(other.getKey());
        }

    }

}

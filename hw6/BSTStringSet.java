
/**
 * @author William Hebgen Guss
 *
 */
public class BSTStringSet implements StringSet {
    public BSTStringSet() {
        this(null, null, null);
    }
    
    public BSTStringSet(String value, 
            BSTStringSet left, BSTStringSet right){
        this._left = left;
        this._right = right;
        this._value = value;
    }
    
    public BSTStringSet(String value){
        this(value,null,null);
    }
    
    
    
    /* (non-Javadoc)
     * @see StringSet#put(java.lang.String)
     */
    @Override
    public void put(String s) {
        if(_value == null)
            this._value = s;
        else
        {
            int compr = s.compareTo(_value);
            if(compr > 0)
            {
                if(_right == null)
                    _right = new BSTStringSet(s);
                else
                    _right.put(s);
            }
            else if(compr < 0){
                if(_left == null)
                    _left = new BSTStringSet(s);
                else
                    _left.put(s);
            }
        }
    }

    /* (non-Javadoc)
     * @see StringSet#contains(java.lang.String)
     */
    @Override
    public boolean contains(String s) {
        if(_value != null)
        {
            int compr = s.compareTo(_value);
            if(compr == 0)
                return true;
            else if(compr < 0 && _left != null)
                    return _left.contains(s);
            else if(compr > 0 && _right != null)
                return _right.contains(s);
        }
        
        
        return false;
    }
    
    public boolean isEmpty() {
        return _value == null;
    }
    
    
    private BSTStringSet _left = null;
    private BSTStringSet _right = null;
    private String _value = null;

}

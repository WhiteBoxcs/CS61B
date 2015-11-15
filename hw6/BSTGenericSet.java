/**
 * 
 */

/**
 * @author William Hebgen Guss
 *
 */
public class BSTGenericSet<Y extends Comparable> 
    implements GenericSet<Y> {

    public BSTGenericSet() {
        this(null, null, null);
    }
    
    public BSTGenericSet(Y value, 
            BSTGenericSet<Y> left, BSTGenericSet<Y> right){
        this._left = left;
        this._right = right;
        this._value = value;
    }
    
    public BSTGenericSet(Y value){
        this(value,null,null);
    }
    
    
    
    /* (non-Javadoc)
     * @see GenericSet#put(java.lang.Generic)
     */
    @Override
    public void put(Y s) {
        if(_value == null)
            this._value = s;
        else
        {
            int compr = s.compareTo(_value);
            if(compr > 0)
            {
                if(_right == null)
                    _right = new BSTGenericSet<Y>(s);
                else
                    _right.put(s);
            }
            else if(compr < 0){
                if(_left == null)
                    _left = new BSTGenericSet<Y>(s);
                else
                    _left.put(s);
            }
        }
    }

    /* (non-Javadoc)
     * @see GenericSet#contains(java.lang.Generic)
     */
    @Override
    public boolean contains(Y s) {
        if(_value != null && s != null)
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
    
    public Y get(Y s){
        if(_value != null)
        {
            int compr = s.compareTo(_value);
            if(compr == 0)
                return _value;
            else if(compr < 0 && _left != null)
                    return _left.get(s);
            else if(compr > 0 && _right != null)
                return _right.get(s);
        }
        
        
        return null;
    }
    
    public boolean isEmpty() {
        return _value == null;
    }
    
    
    private BSTGenericSet<Y> _left = null;
    private BSTGenericSet<Y> _right = null;
    private Y _value = null;
}

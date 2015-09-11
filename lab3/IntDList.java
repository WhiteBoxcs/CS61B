
public class IntDList {

    private DNode _front, _back;

    public IntDList() {
        _front = _back = null;
    }

    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    public int getFront() {
        return _front._val;
    }

    /** Returns the last item in the IntDList. */
    public int getBack() {
        return _back._val;
    }

    /** Return value #I in this list, where item 0 is the first, 1 is the
     *  second, ...., -1 is the last, -2 the second to last.... */
    public int get(int i) {

    	DNode cur = null;
    	if(i < 0){ //in the case that we must traverse backwards.
    		cur = _back;
    		while(cur != null && cur._next != null && i++ != -1)
    			cur = cur._next;
    	}
    	else
    	{
    		cur = _front;
    		while(cur != null && cur._prev != null && i-- != 0)
    			cur = cur._prev;
    	}
    	
    	if(cur != null)
			return cur._val;
    	return -1;
    }

    /** The length of this list. */
    public int size() {
    	DNode cur = _front;
    	int size = 0;
    	
    	//Iteratively traverses the list.
    	while(cur != null){
    		cur = cur._prev;
    		size++;
    	}
    	
    	return size;
    }

    /** Adds D to the front of the IntDList. */
    public void insertFront(int d) {
        DNode nFront = new DNode(d);
        DNode oFront = _front;
        
        nFront._prev =oFront; 
        if(oFront != null)
        	oFront._next=nFront;
        
        //Relink the front
    	_front = nFront;
    	if(_back == null)
    		_back = nFront;
    }
    /** Adds D to the back of the IntDList. */
    public void insertBack(int d) {
        DNode nBack = new DNode(d);
        DNode oBack = _back;
        
        nBack._next = oBack;
        if(oBack != null)
        	oBack._prev = nBack;
        _back = nBack;
        if(_front == null)
        	_front = nBack;
    }

    /** Removes the last item in the IntDList and returns it.
     * This is an extra challenge problem. */
    public int deleteBack() {
    	DNode oBack = _back;
    	if(oBack == null)
    		return -1;   // Your code here
    	else
    	{
    		_back = oBack._next;
    		if(_front == oBack)
    			_front = null;
    		return oBack._val;
    	}

    }

    /** Returns a string representation of the IntDList in the form
     *  [] (empty list) or [1, 2], etc. 
     * This is an extra challenge problem. */
    public String toString() {
        String retr = "[";
        
        //Collect the DList
        DNode cur = _front;
        while(cur != null){
        	retr  += cur._val + (cur != _back ? ", " : "" );
        	cur = cur._prev;
        
        }
        
        
        return retr + "]";
    }

    /* DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. */
    private static class DNode {
        protected DNode _prev;
        protected DNode _next;
        protected int _val;

        private DNode(int val) {
            this(null, val, null);
        }

        private DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}

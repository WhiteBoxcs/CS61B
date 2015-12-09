/**
 * 
 */
package gitlet;

import java.util.Iterator;
import java.util.List;

/**
 * @author william
 * Represents a concatination iterator.
 */
public class ConcatIterator<T> implements Iterator<T> {
    /**
     * The index of the iterator.
     */
    private int index;
    
    /**
     * The iterators to union.
     */
    private List<Iterator<T>> iterators;

    public ConcatIterator(List<Iterator<T>> iterators){
        this.iterators = iterators;
        this.index = 0;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
            while(index < iterators.size()
                    && !iterators.get(index).hasNext())
                index++;
            return index < iterators.size();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        return iterators.get(index).next();
    }

}

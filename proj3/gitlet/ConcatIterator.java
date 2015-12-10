/**
 *
 */
package gitlet;

import java.util.Iterator;
import java.util.List;

/**
 * @author william Represents a concatination iterator.
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

    /**
     * Creates concat iter.
     * @param iteratorss The iterator.
     */
    public ConcatIterator(List<Iterator<T>> iteratorss) {
        this.iterators = iteratorss;
        this.index = 0;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        while (this.index < this.iterators.size()
                && !this.iterators.get(this.index).hasNext()) {
            this.index++;
        }
        return this.index < this.iterators.size();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        return this.iterators.get(this.index).next();
    }

}

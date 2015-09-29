package utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An iterator that filters the output of another iterator.
 *  @author You. */
public abstract class Filter<Value>
    implements Iterator<Value>, Iterable<Value> {

    /** A Filter that accepts a sequence of values from INPUT, and, as
     *  an iterator, yields a subsequence of those values.  When used
     *  as an Iterable, simply returns itself. */
    public Filter(Iterator<Value> input) {
        _input = input;
        _valid = false;
    }

    /* "final" methods may not be overridden. */

    @Override
    public final boolean hasNext() {
        while (!_valid && _input.hasNext()) {
            _next = _input.next();
            _valid = keep();
        }
        return _valid;
    }

    @Override
    public final Value next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        _valid = false;
        return _next;
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Iterator<Value> iterator() {
        return this;
    }

    /** Returns true iff the value of _next should be delivered by
     *  next().  Override this to provide a particular type of
     *  Filter. */
    protected abstract boolean keep();

    /** Returns the current value of _next (from the input iterator), without
     *  advancing that iterator. */
    protected final Value candidateNext() {
        return _next;
    }

    /** True iff the value of _next is valid.  If not, one or more new
     *  values must be read from _input to set it. */
    private boolean _valid;

    /** The next value to be delivered by this Filter. */
    protected Value _next;

    /** The iterator that supplies my values. */
    private Iterator<Value> _input;

}

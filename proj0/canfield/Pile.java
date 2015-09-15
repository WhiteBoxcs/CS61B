package canfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static canfield.Main.*;

/** Represents a pile of cards.
 *  @author P. N. Hilfinger
 */
class Pile {

    /** A new Pile that initially contains CARDS. */
    Pile(Card... cards) {
        _cards = new ArrayList<>(Arrays.asList(cards));
    }

    /** A new, empty Pile. */
    Pile() {
        _cards = new ArrayList<>();
    }

    /** Copy the contents of PILE0 into me, replacing my previous contents. */
    void copyFrom(Pile pile0) {
        _cards.clear();
        _cards.addAll(pile0._cards);
    }

    /** Return my top card, or null if I am empty. */
    Card top() {
        if (isEmpty()) {
            return null;
        } else {
            return get(0);
        }
    }

    /** Return my bottom card, or null if I am empty. */
    Card bottom() {
        if (isEmpty()) {
            return null;
        } else {
            return get(size() - 1);
        }
    }

    /** Return my Kth from top card (0 <= K < size()).  Causes
     *  IllegalArgumentException if K is out of range. */
    Card get(int k) {
        try {
            return _cards.get(size() - 1 - k);
        } catch (IndexOutOfBoundsException excp) {
            return null;
        }
    }

    /** Return my current number of cards. */
    int size() {
        return _cards.size();
    }

    /** Return true iff I am empty. */
    boolean isEmpty() {
        return _cards.isEmpty();
    }

    /** Return and remove my top card.  Returns null and has no effect if
     *  I am empty. */
    Card dealTop() {
        return isEmpty() ? null : _cards.remove(_cards.size() - 1);
    }

    /** Add CARD to me as my top card.  Has no effect if CARD is null. */
    void add(Card card) {
        if (card != null) {
            _cards.add(card);
        }
    }

    /** Place all of the cards in PILE on top of my cards, so that PILE's
     *  former top card (if any) is now mine as well. Remove the cards
     *  from PILE. */
    void move(Pile pile) {
        move(pile, pile.size());
    }

    /** Place the top K cards in PILE on top of my cards, so that PILE's
     *  former top card (if any) is now mine as well.  If there are fewer than
     *  K cards in PILE, move all of them.  Removes the cards from PILE. */
    void move(Pile pile, int k) {
        List<Card> L = pile._cards.subList(Math.max(0, pile.size() - k),
                                           pile.size());
        _cards.addAll(L);
        L.clear();
    }

    /** Remove all my cards. */
    void clear() {
        _cards.clear();
    }

    /** Turn me over, so that my bottom card becomes my top. */
    void turnOver() {
        Collections.reverse(_cards);
    }

    /** Shuffle me, using RANDOM to determine the order. */
    void shuffle(Random random) {
        Collections.shuffle(_cards, random);
    }

    @Override
    public String toString() {
        return _cards.toString();
    }

    /** The cards in this pile.  The top card is last. */
    private final ArrayList<Card> _cards;
}

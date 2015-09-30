package canfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a pile of cards.
 * @author P. N. Hilfinger
 */
class Pile {

    /**
     * A new Pile that initially contains CARDS.
     * @param cards
     *            the cards.
     */
    Pile(Card... cards) {
        this._cards = new ArrayList<>(Arrays.asList(cards));
    }

    /** A new, empty Pile. */
    Pile() {
        this._cards = new ArrayList<>();
    }

    /** Copy the contents of PILE0 into me, replacing my previous contents. */
    void copyFrom(Pile pile0) {
        this._cards.clear();
        this._cards.addAll(pile0._cards);
    }

    /** Return my top card, or null if I am empty. */
    Card top() {
        if (this.isEmpty()) {
            return null;
        } else {
            return this.get(0);
        }
    }

    /** Return my bottom card, or null if I am empty. */
    Card bottom() {
        if (this.isEmpty()) {
            return null;
        } else {
            return this.get(this.size() - 1);
        }
    }

    /**
     * Return my Kth from top card (0 <= K < size()). Causes
     * IllegalArgumentException if K is out of range.
     */
    Card get(int k) {
        try {
            return this._cards.get(this.size() - 1 - k);
        } catch (IndexOutOfBoundsException excp) {
            return null;
        }
    }

    /** Return my current number of cards. */
    int size() {
        return this._cards.size();
    }

    /** Return true iff I am empty. */
    boolean isEmpty() {
        return this._cards.isEmpty();
    }

    /**
     * @param card
     *            the card to check.
     * @return if the pile contains CARD
     */
    boolean contains(Card card) {
        return this._cards.contains(card);
    }

    /**
     * Return and remove my top card. Returns null and has no effect if I am
     * empty.
     */
    Card dealTop() {
        return this.isEmpty() ? null
                : this._cards.remove(this._cards.size() - 1);
    }

    /**
     * Add CARD to me as my top card. Has no effect if CARD is null.
     * @param card
     *            the card.
     */
    void add(Card card) {
        if (card != null) {
            this._cards.add(card);
        }
    }

    /**
     * Place all of the cards in PILE on top of my cards, so that PILE's former
     * top card (if any) is now mine as well. Remove the cards from PILE.
     */
    void move(Pile pile) {
        this.move(pile, pile.size());
    }

    /**
     * Place the top K cards in PILE on top of my cards, so that PILE's former
     * top card (if any) is now mine as well. If there are fewer than K cards
     * in PILE, move all of them. Removes the cards from PILE.
     */
    void move(Pile pile, int k) {
        List<Card> L =
                pile._cards.subList(Math.max(0, pile.size() - k), pile.size());
        this._cards.addAll(L);
        L.clear();
    }

    /** Remove all my cards. */
    void clear() {
        this._cards.clear();
    }

    /** Turn me over, so that my bottom card becomes my top. */
    void turnOver() {
        Collections.reverse(this._cards);
    }

    /** Shuffle me, using RANDOM to determine the order. */
    void shuffle(Random random) {
        Collections.shuffle(this._cards, random);
    }

    @Override
    public String toString() {
        return this._cards.toString();
    }

    /** The cards in this pile. The top card is last. */
    private final ArrayList<Card> _cards;
}

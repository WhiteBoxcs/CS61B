package canfield;

import java.util.ArrayList;
import java.util.Random;

import static canfield.Utils.*;

/** Represents the state of a game of Canfield.
 *  @author P. N. Hilfinger
 */
class Game {

    /** Number of cards dealt to the reserve. */
    static final int RESERVE_SIZE = 13;
    /** Number of tableau piles. */
    static final int TABLEAU_SIZE = 4;
    /** Score for each foundation card. */
    static final int POINTS_PER_CARD = 5;

    /** A new Game, as yet undealt. */
    Game() {
        _stock = new Pile();
        _waste = new Pile();
        _reserve = new Pile();
        for (int i = 0; i < TABLEAU_SIZE; i += 1) {
            _tableau.add(new Pile());
        }
        for (int i = 0; i < Card.NUM_SUITS; i += 1) {
            _foundation.add(new Pile());
        }
    }

    /** A new Game, copied from GAME0. No state is shared with GAME0. */
    Game(Game game0) {
        this();
        copyFrom(game0);
    }

    /** Copy my state from GAME0. No state in the result is shared with
     *  GAME0. */
    void copyFrom(Game game0) {
        _stock.copyFrom(game0._stock);
        _waste.copyFrom(game0._waste);
        _reserve.copyFrom(game0._reserve);
        for (int i = 0; i < TABLEAU_SIZE; i += 1) {
            _tableau.get(i).copyFrom(game0._tableau.get(i));
        }
        for (int i = 0; i < Card.NUM_SUITS; i += 1) {
            _foundation.get(i).copyFrom(game0._foundation.get(i));
        }
    }

    /** Seed the random-number generator with SEED. */
    void seed(long seed) {
        _random.setSeed(seed);
    }

    /** Clear the current layout and deal a new one. */
    void deal() {
        Pile deck = new Pile(Card.values());
        deck.shuffle(_random);

        _reserve.clear();
        _reserve.move(deck, RESERVE_SIZE);

        for (Pile p : _foundation) {
            p.clear();
        }
        foundation(1).move(deck, 1);
        _base = foundation(1).top();

        for (Pile p : _tableau) {
            p.clear();
            p.move(deck, 1);
        }

        _stock.clear();
        _stock.move(deck);
        _waste.clear();
    }

    /** Return true iff the game is won. */
    boolean isWon() {
        return getScore() == Card.NUM_SUITS * Card.NUM_RANKS * POINTS_PER_CARD;
    }

    /** Return the current score. */
    int getScore() {
        int n;
        n = 0;
        for (Pile p : _foundation) {
            n += p.size();
        }
        return n * POINTS_PER_CARD;
    }

    /** Return true iff the stock is empty. */
    boolean stockEmpty() {
        return _stock.isEmpty();
    }

    /** Return the top card of the waste, or null if the waste is empty. */
    Card topWaste() {
        return _waste.top();
    }

    /** Return the top card of the reserve, or null if the reserve is empty. */
    Card topReserve() {
        return _reserve.top();
    }

    /** Return the #J card from the top of tableau pile #K, where
     *  1 <= K <= TABLEAU_SIZE, 0 <= J, or null if there is no such card.
     *  Throws IllegalArgumentException iff K is out of range. */
    Card getTableau(int k, int j) {
        try {
            return tableau(k).get(j);
        } catch (IndexOutOfBoundsException excp) {
            throw err("no such tableau pile");
        }
    }

    /** Return the top card of tableau pile #K, where 1 <= K <= TABLEAU_SIZE.
     *  Returns null if there is no such card.  Throws IllegalArgumentException
     *  iff K is out of range. */
    Card topTableau(int k) {
        return getTableau(k, 0);
    }

    /** Return the number of cards in tableau pile #K, where
     *  1 <= K <= Card.TABLEAU_SIZE.  Throws IllegalArgumentException iff K is
     *  out of range. */
    int tableauSize(int k) {
        return tableau(k).size();
    }

    /** Return the number of cards in foundation pile #K, where
     *  1 <= K <= Card.NUM_SUITS.  Throws IllegalArgumentException iff K is
     *  out of range. */
    int foundationSize(int k) {
        return foundation(k).size();
    }

    /** Return the top card of #K, where 1 <= K <= Card.NUM_SUITS.
     *  Returns null if pile is empty.
     *  Throws IllegalArgumentException iff K is out of range. */
    Card topFoundation(int k) {
        try {
            return foundation(k).top();
        } catch (IndexOutOfBoundsException excp) {
            throw err("no such foundation pile");
        }
    }

    /* === Methods that implement possible moves. === */

    /** Turn up to 3 cards over from the stock to the waste.  If the stock
     *  is empty, turn over the waste to form a new stock, leaving the waste
     *  empty. */
    void stockToWaste() {
        int num = Math.min(_stock.size(), 3);
        if (num == 0) {
            _stock.move(_waste);
            _stock.turnOver();
        } else {
            for (int i = 0; i < num; i += 1) {
                _waste.move(_stock, 1);
            }
        }
    }

    /** Move the top card of the waste to a suitable foundation pile.
     *  Throws IllegalArgumentException if this is not a legal move. */
    void wasteToFoundation() {
        Pile p = findFoundation(topWaste());
        checkFoundationAdd(topWaste(), p);
        p.move(_waste, 1);
    }

    /** Move the top card of the reserve to a suitable foundation pile.
     *  Throws IllegalArgumentException if this is not a legal move. */
    void reserveToFoundation() {
        Pile p = findFoundation(topReserve());
        checkFoundationAdd(topReserve(), p);
        p.move(_reserve, 1);
    }

    /** Move a card from tableau pile #T, 1 <= T <= TABLEAU_SIZE, to
     *  a suitable foundation pile.
     *  Throws IllegalArgumentException if this is not a legal move. */
    void tableauToFoundation(int t) {
        Pile tableau = tableau(t);
        if (tableau.isEmpty()) {
            throw err("No cards in that pile");
        }
        Pile foundation = findFoundation(tableau.top());
        checkFoundationAdd(tableau.top(), foundation);
        foundation.move(tableau, 1);
        fillFromReserve(tableau);
    }

    /** Move tableau pile #K0 to tableau pile #K1,
     *  where K0, K1 in 1 .. TABLEAU_SIZE. */
    void tableauToTableau(int k0, int k1) {
        Pile t0 = tableau(k0);
        Pile t1 = tableau(k1);
        if (t0 == t1) {
            throw err("Can't move a pile onto itself");
        }
        if (t0.isEmpty()) {
            throw err("Can't move an empty pile");
        }
        if (t1.isEmpty()) {
            t1.move(t0);
        } else {
            checkTableauAdd(t0.bottom(), t1);
            t1.move(t0);
        }
        fillFromReserve(t0);
    }

    /** Move a card from foundation pile #F, 1 <= F <= Card.NUM_SUITS, to
     *  tableau pile #T, 1 <= T <= TABLEAU_SIZE. */
    void foundationToTableau(int f, int t) {
        Pile foundation = foundation(f);
        Pile tableau = tableau(t);
        if (foundation.isEmpty()) {
            throw err("Cannot move from empty pile");
        } else if (tableau.isEmpty()) {
            throw err("Cannot move card to empty tableau");
        }
        checkTableauAdd(foundation.top(), tableau);
        tableau.move(foundation, 1);
    }

    /** Move the top card of the waste to tableau pile #K,
     *  1 <= K <= TABLEAU_SIZE.  Throws IllegalArgumentException if K is
     *  is out of bounds, there is no such card, or the move is illegal */
    void wasteToTableau(int k) {
        Pile p = tableau(k);

        if (!_reserve.isEmpty() && p.isEmpty()) {
            throw err("Still cards in reserve");
        }
        checkTableauAdd(topWaste(), p);
        p.move(_waste, 1);
    }

    /** Move the top card of the waste to tableau pile #K,
     *  1 <= K <= TABLEAU_SIZE.  Throws IllegalArgumentException if K is
     *  is out of bounds, there is no such card, or the move is illegal */
    void reserveToTableau(int k) {
        Pile p = tableau(k);
        checkTableauAdd(topReserve(), p);
        p.move(_reserve, 1);
    }

    /* === Internal methods === */

    /** If P is empty and the reserve is not, move the top card of the reserve
     *  to P. */
    private void fillFromReserve(Pile p) {
        if (p.isEmpty() && !_reserve.isEmpty()) {
            p.move(_reserve, 1);
        }
    }

    /** Return foundation pile #K, 1<=K<=Card.NUM_SUITS. Throws
     *  IllegalArgumentException if K is out of range. */
    private Pile foundation(int k) {
        try {
            return _foundation.get(k - 1);
        } catch (IndexOutOfBoundsException excp) {
            throw err("No such foundation pile: %d", k);
        }
    }

    /** Return the foundation pile whose suit matches that of CARD.  Returns
     *  an empty foundation pile if there is no current foundation pile
     *  with the right suit. */
    private Pile findFoundation(Card card) {
        if (card == null) {
            throw err("No card");
        }
        int suit = card.suit();
        for (int i = 1; i <= Card.NUM_SUITS; i += 1) {
            if (!foundation(i).isEmpty() && suit == foundation(i).top().suit())
            {
                return foundation(i);
            }
        }
        for (int i = 1; i <= Card.NUM_SUITS; i += 1) {
            if (foundation(i).isEmpty()) {
                return foundation(i);
            }
        }
        return null;
    }

    /** Return tableau pile #K, 1<=K<=TABLEAU_SIZE. Throws
     *  IllegalArgumentException if K is out of range. */
    private Pile tableau(int k) {
        try {
            return _tableau.get(k - 1);
        } catch (IndexOutOfBoundsException excp) {
            throw err("No such tableau pile: %d", k);
        }
    }

    /** Assuming P is a foundation pile, checks whether CARD may be placed
     *  on it, throwing an IllegalArgumentException if not. */
    private void checkFoundationAdd(Card card, Pile p) {
        Card f = p.top();
        if (card == null) {
            throw err("no card to add");
        }
        if (f == null) {
            if (card.rank() != _base.rank()) {
                throw err("foundation piles must start at %s",
                          _base.rankName());
            }
        } else if (card.suit() != f.suit()) {
            throw err("foundations build up in suit");
        } else if (f.rank() % Card.NUM_RANKS + 1 != card.rank()) {
            throw err("card does not follow top card of foundation");
        }
    }

    /** Assuming P is a tableau pile, checks whether CARD may be placed
     *  on it, throwing IllegalArgumentException if not. */
    private void checkTableauAdd(Card card, Pile p) {
        Card t = p.top();
        if (card == null) {
            throw err("no card to add");
        }
        if (card.rank() == _base.rank()) {
            throw err("%s must go to the foundation", card);
        } else if (t != null && t.isRed() == card.isRed()) {
            throw err("tableau is built down in alternating colors");
        } else if (t != null
                   && (t.rank() - card.rank() + Card.NUM_RANKS) % Card.NUM_RANKS
                   != 1) {
            throw err("tableau is built down in sequence");
        }
    }

    /** The base card: foundations build up from the rank of this card. */
    private Card _base;

    /** Contents of the stock (or hand). */
    private final Pile _stock;
    /** Contents of the waste. */
    private final Pile _waste;
    /** Contents of the reserve. */
    private final Pile _reserve;
    /** The foundation piles. */
    private final ArrayList<Pile> _foundation = new ArrayList<>();
    /** The tableau piles. */
    private final ArrayList<Pile> _tableau = new ArrayList<>();

    /** Source of random numbers for dealing. */
    private final Random _random = new Random();
}

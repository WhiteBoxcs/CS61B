package canfield;

import static canfield.Utils.err;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import canfield.actions.Action;

/**
 * Represents the state of a game of Canfield.
 * @author P. N. Hilfinger
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
        this.history = new Stack<Action>();
        this.listeners = new ArrayList<GameListener>();

        this._stock = new Pile();
        this._waste = new Pile();
        this._reserve = new Pile();
        for (int i = 0; i < TABLEAU_SIZE; i += 1) {
            this._tableau.add(new Pile());
        }
        for (int i = 0; i < Card.NUM_SUITS; i += 1) {
            this._foundation.add(new Pile());
        }
    }

    /**
     * A new Game, copied from GAME0. No state is shared with GAME0.
     * @param game0
     *            the game.
     */
    Game(Game game0) {
        this();
        this.copyFrom(game0);
    }

    /**
     * Copy my state from GAME0. No state in the result is shared with GAME0.
     */
    void copyFrom(Game game0) {
        this._stock.copyFrom(game0._stock);
        this._waste.copyFrom(game0._waste);
        this._reserve.copyFrom(game0._reserve);
        for (int i = 0; i < TABLEAU_SIZE; i += 1) {
            this._tableau.get(i).copyFrom(game0._tableau.get(i));
        }
        for (int i = 0; i < Card.NUM_SUITS; i += 1) {
            this._foundation.get(i).copyFrom(game0._foundation.get(i));
        }
    }

    /** Seed the random-number generator with SEED. */
    void seed(long seed) {
        this._random.setSeed(seed);
    }

    /** Clear the current layout and deal a new one. */
    void deal() {
        Pile deck = new Pile(Card.values());
        deck.shuffle(this._random);

        this._reserve.clear();
        this._reserve.move(deck, RESERVE_SIZE);

        for (Pile p : this._foundation) {
            p.clear();
        }
        this.foundation(1).move(deck, 1);
        this._base = this.foundation(1).top();

        for (Pile p : this._tableau) {
            p.clear();
            p.move(deck, 1);
        }

        this._stock.clear();
        this._stock.move(deck);
        this._waste.clear();

        for (GameListener listener : this.listeners) {
            listener.onGameChange(this);
        }
    }

    /** Return true iff the game is won. */
    boolean isWon() {
        return this.getScore() == Card.NUM_SUITS * Card.NUM_RANKS
                * POINTS_PER_CARD;
    }

    /** Return the current score. */
    int getScore() {
        int n;
        n = 0;
        for (Pile p : this._foundation) {
            n += p.size();
        }
        return n * POINTS_PER_CARD;
    }

    /** Return true iff the stock is empty. */
    boolean stockEmpty() {
        return this._stock.isEmpty();
    }

    /** Return the top card of the waste, or null if the waste is empty. */
    Card topWaste() {
        return this._waste.top();
    }

    /** Return the top card of the reserve, or null if the reserve is empty. */
    Card topReserve() {
        return this._reserve.top();
    }

    /**
     * Return the #J card from the top of tableau pile #K, where 1 <= K <=
     * TABLEAU_SIZE, 0 <= J, or null if there is no such card. Throws
     * IllegalArgumentException iff K is out of range.
     */
    Card getTableau(int k, int j) {
        try {
            return this.tableau(k).get(j);
        } catch (IndexOutOfBoundsException excp) {
            throw err("no such tableau pile");
        }
    }

    /**
     * Return the top card of tableau pile #K, where 1 <= K <= TABLEAU_SIZE.
     * Returns null if there is no such card. Throws IllegalArgumentException
     * iff K is out of range.
     */
    Card topTableau(int k) {
        return this.getTableau(k, 0);
    }

    /**
     * Return the number of cards in tableau pile #K, where 1 <= K <=
     * Card.TABLEAU_SIZE. Throws IllegalArgumentException iff K is out of
     * range.
     */
    int tableauSize(int k) {
        return this.tableau(k).size();
    }

    /**
     * Return the number of cards in foundation pile #K, where 1 <= K <=
     * Card.NUM_SUITS. Throws IllegalArgumentException iff K is out of range.
     */
    int foundationSize(int k) {
        return this.foundation(k).size();
    }

    /**
     * Return the top card of #K, where 1 <= K <= Card.NUM_SUITS. Returns null
     * if pile is empty. Throws IllegalArgumentException iff K is out of range.
     */
    Card topFoundation(int k) {
        try {
            return this.foundation(k).top();
        } catch (IndexOutOfBoundsException excp) {
            throw err("no such foundation pile");
        }
    }

    /**
     * @param card
     *            the card to check
     * @return the foundation pile of CARD if there is one else -1.
     */
    int foundationPileOf(Card card) {
        for (int i = 1; i <= this._foundation.size(); i++) {
            if (this._foundation.get(i - 1).contains(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param card
     *            the card to check
     * @return the foundation pile of CARD if there is one else -1.
     */
    int tableauPileOf(Card card) {
        for (int i = 1; i <= this._tableau.size(); i++) {
            if (this._tableau.get(i - 1).contains(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the kth card from the top of the waste.
     * @param k
     *            0 <= K < waste.size()
     * @return the return.
     */
    Card getWaste(int k) {
        return this._waste.get(k);
    }

    /**
     * Gives the waste size.
     * @return the size of the waste.
     */
    int wasteSize() {
        return this._waste.size();
    }

    /**
     * Gets the kth card from the top of the reserve.
     * @param k
     *            0 <= K < reserve.size()
     * @return the reserve.
     */
    Card getReserve(int k) {
        return this._reserve.get(k);
    }

    /**
     * Gives the reserve size.
     * @return the size of the reserve.
     */
    int reserveSize() {
        return this._reserve.size();
    }

    /**
     * Gets an index of an empty yableau pile.
     * @return 1 <= K <= TABLEAU_SIZE if there is an empty tableau pile, or -1.
     */
    public int getEmptyTableau() {
        for (int i = 1; i <= TABLEAU_SIZE; i++) {
            if (this.tableauSize(i) == 0) {
                return i;
            }
        }

        return -1;

    }

    /* === Undo Code === */
    /**
     * Applies an action and stores it to the history.
     * @param action
     *            The action to enact.
     * @return Returns the enacted action.
     */
    Action apply(Action action) {
        action.apply();
        this.history.push(action);

        for (GameListener listener : this.listeners) {
            listener.onGameChange(this);
        }

        return action;
    }

    /**
     * Undoes the action on the top of the stack.
     * @return if the undo occured.
     */
    boolean undo() {
        if (this.history.isEmpty()) {
            return false;
        }

        Action lastMove = this.history.pop();
        lastMove.inverseApply();

        for (GameListener listener : this.listeners) {
            listener.onGameChange(this);
        }

        return true;
    }

    /**
     * Adds a listener to the game.
     * @param listener
     *            the lister to add.
     */
    public void addListener(GameListener listener) {
        this.listeners.add(listener);
    }

    /* === Methods that implement possible moves. === */

    /**
     * Turn up to 3 cards over from the stock to the waste. If the stock is
     * empty, turn over the waste to form a new stock, leaving the waste empty.
     * THIS METHOD IS MAINTAINED FOR BACKWARDS COMPATABILITY
     */
    void stockToWaste() {
        this.apply(new Action() {
            private int num = 0;

            @Override
            protected void act() {
                this.num = Math.min(Game.this._stock.size(), 3);
                if (this.num == 0) {
                    Game.this._stock.move(Game.this._waste);
                    Game.this._stock.turnOver();
                } else {
                    for (int i = 0; i < this.num; i += 1) {
                        Game.this._waste.move(Game.this._stock, 1);
                    }
                }

            }

            @Override
            protected void undo() {
                if (this.num == 0) {
                    Game.this._waste.move(Game.this._stock);
                    Game.this._waste.turnOver();
                }

                for (int i = 0; i < this.num; i += 1) {
                    Game.this._stock.move(Game.this._waste, 1);
                }
            }
        });
    }

    /**
     * Move the top card of the waste to a suitable foundation pile. Throws
     * IllegalArgumentException if this is not a legal move.
     */
    void wasteToFoundation() {
        this.apply(new Action() {
            private Pile foundPile;

            @Override
            protected void act() {
                Pile p = Game.this.findFoundation(Game.this.topWaste());
                Game.this.checkFoundationAdd(Game.this.topWaste(), p);
                p.move(Game.this._waste, 1);
                this.foundPile = p;

            }

            @Override
            protected void undo() {
                Game.this._waste.move(this.foundPile, 1);
            }
        });
    }

    /**
     * Move the top card of the reserve to a suitable foundation pile. Throws
     * IllegalArgumentException if this is not a legal move.
     */
    void reserveToFoundation() {
        this.apply(new Action() {
            private Pile foundPile;

            @Override
            protected void act() {
                Pile p = Game.this.findFoundation(Game.this.topReserve());
                Game.this.checkFoundationAdd(Game.this.topReserve(), p);
                p.move(Game.this._reserve, 1);

                this.foundPile = p;
            }

            @Override
            protected void undo() {
                Game.this._reserve.move(this.foundPile, 1);
            }
        });
    }

    /**
     * Move a card from tableau pile #T, 1 <= T <= TABLEAU_SIZE, to a suitable
     * foundation pile. Throws IllegalArgumentException if this is not a legal
     * move.
     */
    void tableauToFoundation(final int t) {
        this.apply(new Action() {
            private Pile tableau;
            private Pile foundation;
            private boolean filled;

            @Override
            protected void act() {
                this.tableau = Game.this.tableau(t);
                if (this.tableau.isEmpty()) {
                    throw err("No cards in that pile");
                }
                this.foundation = Game.this.findFoundation(this.tableau.top());
                Game.this.checkFoundationAdd(this.tableau.top(),
                        this.foundation);
                this.foundation.move(this.tableau, 1);
                this.filled = Game.this.fillFromReserve(this.tableau);
            }

            @Override
            protected void undo() {
                if (this.filled) {
                    Game.this._reserve.move(this.tableau, 1);
                }
                this.tableau.move(this.foundation, 1);

            }
        });
    }

    /**
     * Move tableau pile #K0 to tableau pile #K1, where K0, K1 in 1 ..
     * TABLEAU_SIZE.
     */
    void tableauToTableau(final int k0, final int k1) {
        this.apply(new Action() {
            private boolean filled;
            private int sizeT0;
            private Pile t0;
            private Pile t1;

            @Override
            protected void act() {
                this.t0 = Game.this.tableau(k0);
                this.t1 = Game.this.tableau(k1);
                if (this.t0 == this.t1) {
                    throw err("Can't move a pile onto itself");
                }
                if (this.t0.isEmpty()) {
                    throw err("Can't move an empty pile");
                }

                this.sizeT0 = this.t0.size();
                if (this.t1.isEmpty()) {
                    this.t1.move(this.t0);
                } else {
                    Game.this.checkTableauAdd(this.t0.bottom(), this.t1);
                    this.t1.move(this.t0);

                }
                this.filled = Game.this.fillFromReserve(this.t0);
            }

            @Override
            protected void undo() {
                if (this.filled) {
                    Game.this._reserve.move(this.t0, 1);
                }

                this.t0.move(this.t1, this.sizeT0);

            }
        });
    }

    /**
     * Move a card from foundation pile #F, 1 <= F <= Card.NUM_SUITS, to
     * tableau pile #T, 1 <= T <= TABLEAU_SIZE.
     * @param f the foundation pile.
     * @param t the rtableau pile.
     */
    void foundationToTableau(final int f, final int t) {
        this.apply(new Action() {
            private Pile tableau;
            private Pile foundation;

            @Override
            protected void act() {
                this.foundation = Game.this.foundation(f);
                this.tableau = Game.this.tableau(t);
                if (this.foundation.isEmpty()) {
                    throw err("Cannot move from empty pile");
                } else if (this.tableau.isEmpty()) {
                    throw err("Cannot move card to empty tableau");
                }
                Game.this.checkTableauAdd(this.foundation.top(), this.tableau);
                this.tableau.move(this.foundation, 1);
            }

            @Override
            protected void undo() {
                this.foundation.move(this.tableau, 1);

            }

        });
    }

    /**
     * Move the top card of the waste to tableau pile #K, 1 <= K <=
     * TABLEAU_SIZE. Throws IllegalArgumentException if K is is out of bounds,
     * there is no such card, or the move is illegal.
     * @param kIndex
     *            the kIndex.
     */
    void wasteToTableau(final int kIndex) {
        this.apply(new Action() {

            private Pile tabPile;

            @Override
            protected void act() {
                Pile p = Game.this.tableau(kIndex);

                if (!Game.this._reserve.isEmpty() && p.isEmpty()) {
                    throw err("Still cards in reserve");
                }
                Game.this.checkTableauAdd(Game.this.topWaste(), p);
                p.move(Game.this._waste, 1);

                this.tabPile = p;
            }

            @Override
            protected void undo() {
                Game.this._waste.move(this.tabPile, 1);
            }
        });
    }

    /**
     * Move the top card of the waste to tableau pile #K, 1 <= K <=
     * TABLEAU_SIZE. Throws IllegalArgumentException if K is is out of bounds,
     * there is no such card, or the move is illegal
     * @param kIndex
     *            the tabIndex.
     */
    void reserveToTableau(int kIndex) {
        this.apply(new Action() {

            @Override
            protected void act() {
                Pile p = Game.this.tableau(kIndex);
                Game.this.checkTableauAdd(Game.this.topReserve(), p);
                p.move(Game.this._reserve, 1);
            }

            @Override
            protected void undo() {
                Pile p = Game.this.tableau(kIndex);
                Game.this._reserve.move(p, 1);

            }
        });
    }

    /* === Internal methods === */

    /**
     * If P is empty and the reserve is not, move the top card of the reserve
     * to P.
     * @param p
     *            The pile to fill.
     * @return If the pile was filled.
     */
    private boolean fillFromReserve(Pile p) {
        if (p.isEmpty() && !this._reserve.isEmpty()) {
            p.move(this._reserve, 1);
            return true;
        }
        return false;
    }

    /**
     * Return foundation pile #K, 1<=K<=Card.NUM_SUITS. Throws
     * IllegalArgumentException if K is out of range.
     */
    private Pile foundation(int k) {
        try {
            return this._foundation.get(k - 1);
        } catch (IndexOutOfBoundsException excp) {
            throw err("No such foundation pile: %d", k);
        }
    }

    /**
     * Return the foundation pile whose suit matches that of CARD. Returns an
     * empty foundation pile if there is no current foundation pile with the
     * right suit.
     * @param card the foundation of the card.
     * @return the founcation.
     */
    private Pile findFoundation(Card card) {
        if (card == null) {
            throw err("No card");
        }
        int suit = card.suit();
        for (int i = 1; i <= Card.NUM_SUITS; i += 1) {
            if (!this.foundation(i).isEmpty()
                    && suit == this.foundation(i).top().suit()) {
                return this.foundation(i);
            }
        }
        for (int i = 1; i <= Card.NUM_SUITS; i += 1) {
            if (this.foundation(i).isEmpty()) {
                return this.foundation(i);
            }
        }
        return null;
    }

    /**
     * Return tableau pile #K, 1<=K<=TABLEAU_SIZE. Throws
     * IllegalArgumentException if K is out of range.
     */
    private Pile tableau(int k) {
        try {
            return this._tableau.get(k - 1);
        } catch (IndexOutOfBoundsException excp) {
            throw err("No such tableau pile: %d", k);
        }
    }

    /**
     * Assuming P is a foundation pile, checks whether CARD may be placed on
     * it, throwing an IllegalArgumentException if not.
     */
    private void checkFoundationAdd(Card card, Pile p) {
        Card f = p.top();
        if (card == null) {
            throw err("no card to add");
        }
        if (f == null) {
            if (card.rank() != this._base.rank()) {
                throw err("foundation piles must start at %s",
                        this._base.rankName());
            }
        } else if (card.suit() != f.suit()) {
            throw err("foundations build up in suit");
        } else if (f.rank() % Card.NUM_RANKS + 1 != card.rank()) {
            throw err("card does not follow top card of foundation");
        }
    }

    /**
     * Assuming P is a tableau pile, checks whether CARD may be placed on it,
     * throwing IllegalArgumentException if not.
     */
    private void checkTableauAdd(Card card, Pile p) {
        Card t = p.top();
        if (card == null) {
            throw err("no card to add");
        }
        if (card.rank() == this._base.rank()) {
            throw err("%s must go to the foundation", card);
        } else if (t != null && t.isRed() == card.isRed()) {
            throw err("tableau is built down in alternating colors");
        } else if (t != null && (t.rank() - card.rank() + Card.NUM_RANKS)
                % Card.NUM_RANKS != 1) {
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

    /** The game history. */
    private Stack<Action> history;

    /** The game listeners. */
    private ArrayList<GameListener> listeners;

    /** Source of random numbers for dealing. */
    private final Random _random = new Random();

}

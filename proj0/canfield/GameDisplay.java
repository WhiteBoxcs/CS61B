package canfield;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import ucb.gui.Pad;

/**
 * A widget that displays a Pinball playfield.
 * @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 800, BOARD_HEIGHT = 600;
    /**
     * The origin of the thing.
     */
    private static final Point ORIGIN =
            new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2);

    /**
     * THe reveal distance.
     */
    private static final int CARD_REVEAL = 22;

    /** The positions of cards. **/
    private static final Point RESERVE_POS = cctp(-3, 0.25);

    /**
     * The the stock position.
     */
    private static final Point STOCK_POS = cctp(-3, 1.4);

    /**
     * THe waste position.
     */
    private static final Point WASTE_POS = cctp(-1.75, 1.4);

    /**
     * A graphical representation of GAME.
     * @param game
     *            The game to display.
     */
    public GameDisplay(Game game) {
        this._game = game;
        this.setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
        this.background = GameDisplay.getImage("bg.jpg");

        this.rebuild();

    }

    /**
     * Paints the game.
     */
    @Override
    public synchronized void paintComponent(Graphics2D g) {
        this.cards.sort(new GUICard.LayerComparator());

        Rectangle b = g.getClipBounds();
        g.drawImage(this.background, 0, 0, b.width, b.height, null);

        for (int i = 0; i < this.cards.size(); i++) {
            this.paintCard(g, this.cards.get(i));
        }
    }

    /* ======= REBUILD ======= */

    /**
     * Rebuilds the display using the given game.
     */
    public void rebuild() {
        this.cards.clear();

        this.rebuildReserve();
        this.rebuildStock();
        this.rebuildWaste();
        this.rebuildFoundation();
        this.rebuildTableau();

        this.cards.sort(new GUICard.LayerComparator());

    }

    /**
     * Rebuilds the reserve.
     */
    private void rebuildReserve() {
        Card reserve = this._game.topReserve();
        if (reserve != null) {
            ArrayList<GUIStackedCard> reserveStack =
                    new ArrayList<GUIStackedCard>();

            Point rTop = new Point(
                    (int) RESERVE_POS.getX()
                            + (this._game.reserveSize() - 1) * Card.NUM_RANKS,
                    (int) RESERVE_POS.getY());

            reserveStack.add(
                    new GUIStackedCard(reserve, CardType.RESERVE, rTop, true));

            for (int i = 1; i < this._game.reserveSize(); i++) {
                Point wPos = new Point((int) rTop.getX() - i * Card.NUM_RANKS,
                        (int) rTop.getY());

                GUIStackedCard resCard = new GUIStackedCard(
                        this._game.getReserve(i), CardType.RESERVE, wPos,
                        reserveStack.get(reserveStack.size() - 1), false);
                resCard.flip();

                reserveStack.add(resCard);
            }

            this.cards.addAll(reserveStack);
        }
    }

    /**
     * Rebuilds the stock.
     */
    private void rebuildStock() {
        if (!this._game.stockEmpty()) {
            GUICard stock =
                    new GUICard(Card.C10, CardType.STOCK, STOCK_POS, 0);
            stock.flip();
            this.cards.add(stock);
        } else {
            this.cards.add(new GUIEmptyCard(CardType.STOCK, STOCK_POS));
        }

    }

    /**
     * Rebuilds the waste.
     */
    private void rebuildWaste() {
        Card waste = this._game.topWaste();
        if (waste != null) {
            ArrayList<GUIStackedCard> wasteStack =
                    new ArrayList<GUIStackedCard>();
            wasteStack.add(new GUIStackedCard(waste, CardType.WASTE, WASTE_POS,
                    true));

            for (int i = 1; i < Math.min(this._game.wasteSize(), 3); i++) {
                Point wPos =
                        new Point((int) WASTE_POS.getX() - i * Card.NUM_RANKS,
                                (int) WASTE_POS.getY());

                wasteStack.add(new GUIStackedCard(this._game.getWaste(i),
                        CardType.WASTE, wPos,
                        wasteStack.get(wasteStack.size() - 1), false));
            }

            this.cards.addAll(wasteStack);
        }

    }

    /**
     * Rebuilds the foundation.
     */
    private void rebuildFoundation() {
        for (int x = 1; x <= Card.NUM_SUITS; x++) {
            Card found = this._game.topFoundation(x);
            if (found != null) {
                this.cards.add(new GUIMoveableCard(found, CardType.FOUNDATION,
                        cctp(-1 + x, -1), 0));
            } else {
                this.cards.add(new GUIEmptyCard(CardType.FOUNDATION,
                        cctp(-1 + x, -1)));
            }
        }

    }

    /**
     * Rebuilds the tableau.
     */
    private void rebuildTableau() {

        for (int x = 1; x <= Game.TABLEAU_SIZE; x++) {

            ArrayList<GUIStackedCard> tabPile =
                    new ArrayList<GUIStackedCard>();

            int tabSize = this._game.tableauSize(x);

            Point basis = cctp(-1 + x, 0);
            Point top = cctp(-1 + x, 0 + tabSize
                    * ((double) CARD_REVEAL / (double) GUICard.HEIGHT));

            if (tabSize <= 0) {
                this.cards
                        .add(new GUIEmptyCard(CardType.TABLEAU_EMPTY, basis));
                continue;
            }

            GUIStackedCard head = new GUIStackedCard(
                    this._game.topTableau(x), this._game.tableauSize(x) > 1
                            ? CardType.TABLEAU_HEAD : CardType.TABLEAU_BASE,
                    top, true);
            tabPile.add(head);

            for (int i = 1; i <= this._game.tableauSize(x) - 1; i++) {

                Point cPos = new Point((int) top.getX(),
                        (int) top.getY() - CARD_REVEAL * i);

                boolean base = i == this._game.tableauSize(x) - 1;
                tabPile.add(new GUIStackedCard(this._game.getTableau(x, i),
                        base ? CardType.TABLEAU_BASE : CardType.TABLEAU_NORM,
                        cPos, tabPile.get(tabPile.size() - 1), base));
            }
            if (!tabPile.isEmpty()) {
                this.cards.addAll(tabPile);
            }

        }
    }

    /* ======= HELPERS ====== */

    /**
     * Gets the card at a certain position.
     * @param pos
     *            The test position
     * @param except
     *            But this card.
     * @return The list of cards at a certain position sorted by layer.
     */
    public ArrayList<GUICard> getCardAt(Point pos, GUICard except) {
        ArrayList<GUICard> satisfying = new ArrayList<GUICard>();

        for (GUICard card : this.cards) {
            if (card.getBoundingBox().contains(pos) && card != except) {
                satisfying.add(card);
            }
        }

        satisfying.sort(new GUICard.LayerComparator());

        return satisfying;
    }

    /**
     * Gets the card at a certain position.
     * @param pos
     *            The test position
     * @return The list of cards at a certain position sorted by layer.
     */
    public ArrayList<GUICard> getCardAt(Point pos) {
        return this.getCardAt(pos, null);
    }

    /**
     * Gets the top card at a given position.
     * @param pos
     *            The POS to check.
     * @param except
     *            but this card.
     * @return the top card or NULL if there is no card.
     */
    public GUICard getTopCardAt(Point pos, GUICard except) {
        ArrayList<GUICard> satisfying = this.getCardAt(pos, except);
        if (!satisfying.isEmpty()) {
            return satisfying.get(satisfying.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Gets the top card at a given position.
     * @param pos
     *            The POS to check.
     * @return the top card or NULL if there is no card.
     */
    public GUICard getTopCardAt(Point pos) {
        return this.getTopCardAt(pos, null);
    }

    /**
     * Gets the top card colliding with a given GUI card.
     * @param with
     *            The card with which another card may collide.
     * @return the top card colliding with WITH
     */
    public ArrayList<GUICard> getCollision(GUICard with) {
        ArrayList<GUICard> satisfying = new ArrayList<GUICard>();

        for (GUICard card : this.cards) {
            if (card.getBoundingBox().intersects(with.getBoundingBox())
                    && card != with) {
                satisfying.add(card);
            }
        }
        satisfying.sort((o1, o2) -> {
                double px = with.getCenter().getX();
                double py = with.getCenter().getY();
                return (int) o2.getPos().distance(px, py)
                        - (int) o1.getPos().distance(px, py);
            });

        return satisfying;
    }

    /**
     * Gets the image.
     * @param name The name of the image.
     * @return what image is returned..
     */
    public static Image getImage(String name) {
        InputStream in = GameDisplay.class
                .getResourceAsStream("/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    /** Draw CARD at P on G. */
    private void paintCard(Graphics2D g, GUICard card) {
        g.drawImage(card.getImage(), (int) card.getPos().getX(),
                (int) card.getPos().getY(),
                (int) card.getBoundingBox().getWidth(),
                (int) card.getBoundingBox().getHeight(), null);
    }

    /* ================ Positional attributes ================ */

    /**
     * Converts card coords to pixels.
     * @param x
     *            the grid position in X of a given card
     * @param y
     *            the grid position in Y of a given card
     * @return the point where the cards pos is.
     */
    public static Point cctp(double x, double y) {
        int paddedWidth = GUICard.WIDTH + GUICard.PADDING;
        int paddedHeight = GUICard.HEIGHT + GUICard.PADDING;

        return new Point((int) ((x - 0.5) * paddedWidth) + (int) ORIGIN.getX(),
                (int) ((y - 0.5) * paddedHeight) + (int) ORIGIN.getY());
    }

    /** Game I am displaying. */
    private final Game _game;
    /**
     * ( THe set of cards.
     */
    private ArrayList<GUICard> cards = new ArrayList<GUICard>();

    /**
     * the background.
     */
    private final Image background;

}

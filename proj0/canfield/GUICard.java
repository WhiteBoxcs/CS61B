/**
 * @author MadcowD
 */
package canfield;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;

/**
 * @author MadcowD The card sprite class
 */
class GUICard {

    /** Displayed dimensions of a card image. */
    public static final int HEIGHT = 125, WIDTH = 90;
    public static final int PADDING = 10;

    /**
     * @param repr
     *            The representation of the card.
     * @param pos
     *            The center position of the card.
     * @param image
     *            The image of the card.
     * @param layer
     *            The layer of the card.
     */
    public GUICard(Card repr, CardType type, Point pos, int layer) {
        super();

        this.faceDown = false;
        this.repr = repr;
        this.type = type;

        int x = (int) pos.getX() - WIDTH / 2;
        int y = (int) pos.getY() - HEIGHT / 2;

        this.boundingBox = new Rectangle(x, y, WIDTH, HEIGHT);

        this.image = this.getCardImage(repr);

        this.layer = layer;
    }

    /* ==== Sudo Event Listeners ==== */

    /**
     * Called when the card is selected and the mouse is dragging! Unfortunate
     * side effect of trying to program with component oriented mindset.
     * @param pos
     *            The position of the mouse.
     * @param justified
     *            If the drag was justified (caused a change of gamestate).
     */
    public void onDrag(Point pos) {
    }

    /**
     * Called when the card is selected and the mouse is released! Unfortunate
     * side effect of trying to program with component oriented mindset.
     * @param pos
     *            The position of the release.
     * @param justified
     *            If the release was justified (caused a change of gamestate).
     */
    public void onRelease(Point pos) {
    }

    /**
     * Called when the card is selected! Unfortunate side effect of trying to
     * program with component oriented mindset.
     * @param pos
     *            The position of the click..
     * @param justified
     *            If the click was justified (caused a change of gamestate).
     */
    public void onClick(Point pos) {
    }

    /* ==== Getters/Setters ==== */

    /**
     * @return the layer
     */
    public int getLayer() {
        return this.layer;
    }

    /**
     * @param layer
     *            the layer to set
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     * @return the repr
     */
    public Card getRepr() {
        return this.repr;
    }

    /**
     * @return the boundingBox
     */
    public Rectangle getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * @return the image
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * @return the center of the bounding box.
     */
    public Point getCenter() {
        int x = (int) this.boundingBox.getCenterX();
        int y = (int) this.boundingBox.getCenterY();

        return new Point(x, y);
    }

    /**
     * Sets the position of the GUICard.
     * @param pos
     *            The position
     */
    public void setPos(Point pos) {
        this.boundingBox.setLocation((int) pos.getX(), (int) pos.getY());
    }

    /**
     * @return the position of the GUICard
     */
    public Point getPos() {
        return this.boundingBox.getLocation();
    }

    /* ==== Helpers ==== */

    /** Return an Image read from the resource named NAME. */

    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return GameDisplay.getImage("playing-cards/" + card + ".png");
    }

    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return GameDisplay.getImage("playing-cards/blue-back.png");
    }

    /**
     * @return the type
     */
    public CardType getType() {
        return this.type;
    }

    /**
     * @return the faceDown
     */
    public boolean isFaceDown() {
        return this.faceDown;
    }

    /**
     * @param faceDown
     *            the faceDown to set
     */
    public void flip() {
        this.faceDown = !this.faceDown;
        if (this.faceDown) {
            this.image = this.getBackImage();
        } else {
            this.getCardImage(this.repr);
        }
    }

    /* ==== Comparator ===== */
    public static class LayerComparator implements Comparator<GUICard> {
        /*
         * Uses that 0 > 1. (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object,
         * java.lang.Object)
         */
        @Override
        public int compare(GUICard o1, GUICard o2) {
            return o1.getLayer() - o2.getLayer();
        }

    }

    /* ==== Fields ==== */
    private Card repr;
    private CardType type;
    private Rectangle boundingBox;
    private Image image;
    private int layer;
    private boolean faceDown;
}

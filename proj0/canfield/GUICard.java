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

    /**
     * The padding of a card.
     */
    public static final int PADDING = 10;

    /**
     * @param repr
     *            The representation of the card.
     * @param type
     *            the type of card.
     * @param pos
     *            The center position of the card.
     * @param layer
     *            The layer of the card.
     */
    public GUICard(Card repr, CardType type, Point pos, int layer) {
        super();

        this._faceDown = false;
        this._repr = repr;
        this._type = type;

        int x = (int) pos.getX() - WIDTH / 2;
        int y = (int) pos.getY() - HEIGHT / 2;

        this._boundingBox = new Rectangle(x, y, WIDTH, HEIGHT);

        this._image = this.getCardImage(repr);

        this._layer = layer;
    }

    /* ==== Sudo Event Listeners ==== */

    /**
     * Called when the card is selected and the mouse is dragging! Unfortunate
     * side effect of trying to program with component oriented mindset.
     * @param pos
     *            The position of the mouse.
     */
    public void onDrag(Point pos) {
    }

    /**
     * Called when the card is selected and the mouse is released! Unfortunate
     * side effect of trying to program with component oriented mindset.
     * @param pos
     *            The position of the release.
     */
    public void onRelease(Point pos) {
    }

    /**
     * Called when the card is selected! Unfortunate side effect of trying to
     * program with component oriented mindset.
     * @param pos
     *            The position of the click.
     */
    public void onClick(Point pos) {
    }

    /* ==== Getters/Setters ==== */

    /**
     * @return the layer
     */
    public int getLayer() {
        return this._layer;
    }

    /**
     * @param layer
     *            the layer to set
     */
    public void setLayer(int layer) {
        this._layer = layer;
    }

    /**
     * @return the repr
     */
    public Card getCard() {
        return this._repr;
    }

    /**
     * @return the boundingBox
     */
    public Rectangle getBoundingBox() {
        return this._boundingBox;
    }

    /**
     * @return the image
     */
    public Image getImage() {
        return this._image;
    }

    /**
     * @return the center of the bounding box.
     */
    public Point getCenter() {
        int x = (int) this._boundingBox.getCenterX();
        int y = (int) this._boundingBox.getCenterY();

        return new Point(x, y);
    }

    /**
     * Sets the position of the GUICard.
     * @param pos
     *            The position
     */
    public void setPos(Point pos) {
        this._boundingBox.setLocation((int) pos.getX(), (int) pos.getY());
    }

    /**
     * @return the position of the GUICard
     */
    public Point getPos() {
        return this._boundingBox.getLocation();
    }

    /* ==== Helpers ==== */

    /** Return an Image read from the resource named NAME. */

    /**
     * Return an Image of CARD.
     * @param card
     *            the card.
     * @return the image,.
     */
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
        return this._type;
    }

    /**
     * @return the faceDown
     */
    public boolean isFaceDown() {
        return this._faceDown;
    }

    /**
     * flipts the cards.
     */
    public void flip() {
        this._faceDown = !this._faceDown;
        if (this._faceDown) {
            this._image = this.getBackImage();
        } else {
            this.getCardImage(this._repr);
        }
    }

    /* ==== Comparator ===== */
    /**
     * THe standard layer comparator.
     * @author William
     */
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
    /**
     * the card which is being represented.
     */
    private Card _repr;

    /**
     * The type of the card.
     */
    private CardType _type;

    /**
     * the bvounding box of the card.
     */
    private Rectangle _boundingBox;

    /**
     * The image of the card.
     */
    private Image _image;

    /**
     * The drawing layer of the card.
     */
    private int _layer;

    /**
     * If the card is facedown or not.
     */
    private boolean _faceDown;
}

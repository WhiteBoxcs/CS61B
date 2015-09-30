/**
 * @author MadcowD
 */
package canfield;

import java.awt.Point;

/**
 * @author MadcowD Generates a new stacked GUI card. These horrible naming
 *         convention is namely because there aren't sub packages.
 */
public class GUIStackedCard extends GUIMoveableCard {

    /**
     * Full fledged constructor specifying further cards on the stack.
     * @param repr
     *            The repr.
     * @param type
     *            The type.
     * @param pos
     *            The pos.
     * @param nextCard
     *            The next card.
     * @param base
     *            If it is a bvase.
     */
    public GUIStackedCard(Card repr, CardType type, Point pos,
            GUIStackedCard nextCard, boolean base) {
        super(repr, type, pos,
                nextCard != null ? nextCard.getLayer() - 1 : Card.NUM_RANKS);
        this._base = base;
        this._nextCard = nextCard;
    }

    /***
     * Alternative constructor for last card on stack (or single stacks).
     * @param repr
     *            The repor.
     * @param type
     *            The type.
     * @param pos
     *            The position,.
     * @param base
     *            if its abase.
     */
    public GUIStackedCard(Card repr, CardType type, Point pos, boolean base) {
        this(repr, type, pos, null, base);
        this._nextCard = null;
    }

    /* ==== Movement of Stacked Cards ==== */

    @Override
    public void onClick(Point pos) {
        super.onClick(pos);
        if (this._nextCard != null) {
            this._nextCard.onClick(pos);
        }
    }

    @Override
    public void onDrag(Point pos) {
        if (this._base) {
            this.pileDrag(pos);
        }
    };

    @Override
    public void onRelease(Point pos) {
        super.onRelease(pos);
        if (this._nextCard != null) {
            this._nextCard.onRelease(pos);
        }
    };

    /**
     * Drags a pile card recursively.
     * @param pos
     *            the position of the mouse
     */
    private void pileDrag(Point pos) {
        super.onDrag(pos);
        if (this._nextCard != null) {
            this._nextCard.pileDrag(pos);
        }

    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
        if (this._nextCard != null) {
            this._nextCard.setLayer(layer + 1);
        }
    };

    /* ==== Fields ==== */

    /**
     * If the card is a base.
     */
    private boolean _base;

    /**
     * The next card.
     */
    private GUIStackedCard _nextCard;

}

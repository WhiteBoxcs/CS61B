package canfield;

import java.awt.Rectangle;

public class GUICard extends Point {

	private Card card;
	private Rectangle boundingBox;

	/**
	 * Constructs a GUI card with card coordinate position X,Y.
	 * @param c 
	 * @param x
	 * @param y
	 */
	GUICard(Card card, int layer, int x, int y) {
		super(x, y);
		this.setCard(card);
		
		Point pixelPos = GameDisplay.cctp(x, y);
		
		this.setBoundingBox(
				new Rectangle((int)pixelPos.getX(),
						(int)pixelPos.getY(),
						HEIGHT,WIDTH));
		
		
		
	} 


	/* ================ SETTER/GETTERS ===============*/
	
	/**
	 * @return the card
	 */
	public Card getCard() {
		return card;
	}

	/**
	 * @param card the card to set
	 */
	private void setCard(Card card) {
		this.card = card;
	}
	
    /**
	 * @return the boundingBox
	 */
	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	/**
	 * @param boundingBox the boundingBox to set
	 */
	public void setBoundingBox(Rectangle boundingBox) {
		this.boundingBox = boundingBox;
	}

	/** Displayed dimensions of a card image. */
    public static final int HEIGHT = 125, WIDTH = 90;
	
	

}

package canfield;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import ucb.gui.Pad;

/**
 * A widget that displays a Pinball playfield.
 *
 * @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 800, BOARD_HEIGHT = 600;

    /** Displayed dimensions of a card image. */
    private static final int CARD_HEIGHT = 125, CARD_WIDTH = 90;
    
    private static final int CARD_PADDING = 10;
    private static final int CARD_REVEAL = 30;
    
    /** The positions of cards **/
    private static final Point RESERVE_POS = cctp(-3,0);
	private static final Point STOCK_POS = cctp(-3,1);
	private static final Point WASTE_POS = cctp(-2,1);

    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        this._game = game;
        this.setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
        background = this.getImage("bg.jpg");
        
        
    }

    /** Return an Image read from the resource named NAME. */
    private Image getImage(String name) {
        InputStream in = this.getClass().getResourceAsStream(
                "/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return this.getImage("playing-cards/" + card + ".png");
    }

    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return this.getImage("playing-cards/blue-back.png");
    }

    /** Draw CARD at X, Y on G. */
    private void paintCard(Graphics2D g, Card card, int x, int y) {
        if (card != null) {
            g.drawImage(this.getCardImage(card), x, y, CARD_WIDTH, CARD_HEIGHT,
                    null);
        }
    }
    
    /** Draw CARD at P on G. */
    private void paintCard(Graphics2D g, Card card, Point p) {
        this.paintCard(g,card,p.x,p.y);
    }

    /** Draw card back at X, Y on G. */
    private void paintBack(Graphics2D g, int x, int y) {
        g.drawImage(this.getBackImage(), x,y, CARD_WIDTH, CARD_HEIGHT, null);
    }
    
    /** Draw card back at P on G. */
    private void paintBack(Graphics2D g, Point p){
    	this.paintBack(g,p.x,p.y);
    }

    
    /**
     * Paints the game.
     */
    @Override
    public synchronized void paintComponent(Graphics2D g) {
    	/*paint the background */
    	 Rectangle b = g.getClipBounds();
    	 g.drawImage(background,0,0,b.width,b.height,null);

        
    	 
        // Spaids Hearts Diamonds Clubs
    	 
    	 /* RESERVE */
    	 Card reserve = _game.topReserve();
    	 if(reserve != null)
    		 this.paintCard(g, reserve, RESERVE_POS);
    	 
    	 /* STOCK */
    	 if(!_game.stockEmpty())
    		 this.paintBack(g, STOCK_POS);
    	 
    	 /* WASTE */
    	 Card waste = _game.topWaste();
    	 if(waste != null)
    		 this.paintCard(g,waste, WASTE_POS);

    	 /* TABLEAU */
    	 for(int x = 1; x <= 4; x++){
    		 Point basis = cctp(-1+x,0);
    		 
    		 for(int i = 0; i < _game.tableauSize(x); i++){
    			 
    			 this.paintCard(g, _game.getTableau(x, i), 
    					 new Point((int)basis.getX(),
							 (int)basis.getY()+CARD_REVEAL*i) );
    		 }
    		 
    	 }
    	 
    	 /* FOUNDATION */
    	 for(int x = 1; x <= 4; x++){
    		 Card found = _game.topFoundation(x);
    		 if(found != null)
    			 this.paintCard(g, found, cctp(-1+x,-1));
    	 }
        
    }
    
    
    /* ================ Positional attributes ================ */
    
    private static Point ORIGIN = new Point(BOARD_WIDTH/2, BOARD_HEIGHT/2);
    
    /**
     * Converts card coords to pixels
     * @param x the grid position in X of a given card
     * @param y the grid position in Y of a given card
     * @return the point where the cards pos is.
     */
    public static Point cctp(double x,double y){
    	int paddedWidth = CARD_WIDTH + CARD_PADDING;
    	int paddedHeight = CARD_HEIGHT + CARD_PADDING;
    	
    	return new Point((int)((x - 0.5)*paddedWidth)+(int)ORIGIN.getX(),
    			(int)((y-0.5)*paddedHeight) + (int)ORIGIN.getY());
    }
    
    

    /** Game I am displaying. */
    private final Game _game;
    private final Image background;

}

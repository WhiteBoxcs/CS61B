package canfield;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
    
    public static Image getImage(String name) {
        InputStream in = GameDisplay.class.getResourceAsStream(
                "/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    
    /** Draw CARD at P on G. */
    private void paintCard(Graphics2D g, GUICard card) {
        g.drawImage(card.getImage(),
                (int)card.getPos().getX(),
                (int)card.getPos().getY(),
                (int)card.getBoundingBox().getWidth(),
                (int)card.getBoundingBox().getHeight(),
                null);
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

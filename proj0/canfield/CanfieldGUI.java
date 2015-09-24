package canfield;

import java.awt.event.MouseEvent;

import ucb.gui.LayoutSpec;
import ucb.gui.TopLevel;

/**
 * A top-level GUI for Canfield solitaire.
 *
 * @author
 */
class CanfieldGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        this._game = game;
        
        this.addMenuButton("Game->New Game", "newGame");
        this.addMenuButton("Game->Undo", "undo");
        this.addMenuButton("Game->Quit", "quit");
        


        
        this.addLabel("New game started!","messageLabel", new LayoutSpec("y",
                1, "x", 0));
        
        this._display = new GameDisplay(game);
        this.add(this._display, new LayoutSpec("y", 0, "width", 2));
        this._display.setMouseHandler("click", this, "mouseClicked");
        this._display.setMouseHandler("release", this, "mouseReleased");
        this._display.setMouseHandler("drag", this, "mouseDragged");
        
        

        this.display(true);
    }

    /**
     * Creates a new game
     * @param dummy
     */
    public void newGame(String dummy){
    	//TODO: IMOPLEMENRT
    }
    
    /**
     * Undoes a move if there is one to undo.
     * @param dummy
     */
    public void undo(String dummy){
    	try{
    		_game.undo();
    	}
    	catch(IllegalArgumentException exp){
    		this.error(exp);
    	}
    	
    	this._display.repaint();
    }
    
    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        // FIXME
        this._display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        // FIXME
        this._display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        this._display.repaint();
    }

    
    /* ================ MESSAGE STUFF ===================*/
    /**
     * Writes an error message to the label.
     * @param exp
     */
    private void error(Exception exp){
    	String errorMsg = (String.format(exp.getMessage()));
    	this.message( "Error" + errorMsg);
    	this.showMessage(errorMsg, "Error", "Error");
    	
    }
    
    /**
     * Writes a simple message to the label.
     * @param message
     */
    private void message(String message){
    	this.setLabel("messageLabel", message);
    }
    
    
    /*===================================================*/
    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

}

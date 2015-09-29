package canfield;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import ucb.gui.LayoutSpec;
import ucb.gui.TopLevel;

/**
 * A top-level GUI for Canfield solitaire.
 * 
 * @author Paul N. Hilfiger
 * @author William Guss
 */
class CanfieldGUI extends TopLevel implements GameListener {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        this._game = game;
        game.addListener(this);

        this.addMenuButton("Game->New Game", "newGame");
        this.addMenuButton("Game->Undo", "undo");
        this.addMenuButton("Game->Quit", "quit");

        this.addLabel("New game started!", "messageLabel", new LayoutSpec("y", 1, "x", 0));

        this._display = new GameDisplay(game);
        this.add(this._display, new LayoutSpec("y", 0, "width", 2));
        this._display.setMouseHandler("click", this, "mouseClicked");
        this._display.setMouseHandler("release", this, "mouseReleased");
        this._display.setMouseHandler("drag", this, "mouseDragged");

        this.display(true);
    }

    /**
     * Creates a new game.
     * 
     * @param dummy
     */
    public void newGame(String dummy) {
        this._game.deal();
    }

    /**
     * Undoes a move if there is one to undo.
     * 
     * @param dummy
     */
    public void undo(String dummy) {
        try {
            this._game.undo();
        } catch (IllegalArgumentException exp) {
            this.error(exp);
        }

        this._display.repaint();
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }

    /* =========== GAME LISTENER =============== */
    @Override
    public void onGameChange(Game changedGame) {
        this._display.rebuild();
        this._display.repaint();
        this.message("Score: " + this._game.getScore());

        if (this._game.isWon()) {
            this.victory();
        }
    }

    /* =========== INPUT LISTENER ============== */

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        GUICard top = this._display.getTopCardAt(event.getPoint());
        if (top != null) {

            if (top.getType() == CardType.STOCK) {
                this._game.stockToWaste();
            }

            top.onClick(event.getPoint());
        }

        this._display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        if (this.selectedCard == null) {
            this.selectedCard = this._display.getTopCardAt(event.getPoint());
            if (this.selectedCard != null) {

                this.selectedLayer = this.selectedCard.getLayer();
                this.selectedCard.setLayer(100);
            }
        }

        if (this.selectedCard != null) {
            this.selectedCard.onDrag(event.getPoint());
        }

        this._display.repaint();
    }

    /**
     * Action in response to mouse-released event EVENT. Occurs only after drag.
     */
    public synchronized void mouseReleased(MouseEvent event) {

        if (this.selectedCard != null) {

            try {
                this.processInput();

                this.selectedCard.onRelease(event.getPoint());
                this.selectedCard.setLayer(this.selectedLayer);
                this.selectedCard = null;
            } catch (NullPointerException exp) {
                this.error(exp);
            }
        }

        this._display.repaint();
    }

    /**
     * Performs all input logic between two cards.
     */
    private void processInput() {
        /* see if there was another card. */
        ArrayList<GUICard> colliding = this._display.getCollision(this.selectedCard);
        if (!colliding.isEmpty()) {

            GUICard other = colliding.get(0);

            try {

                switch (this.selectedCard.getType()) {

                case WASTE:

                    switch (other.getType()) {
                    case FOUNDATION:
                        this._game.wasteToFoundation();
                        break;
                    case TABLEAU_BASE:
                    case TABLEAU_HEAD:
                    case TABLEAU_NORM:
                        this._game.wasteToTableau(this._game.tableauPileOf(other.getRepr()));
                        break;
                    case TABLEAU_EMPTY:
                        this._game.wasteToTableau(this._game.getEmptyTableau());
                    default:
                        break;
                    }
                    break;

                case TABLEAU_HEAD:

                    if (other.getType() == CardType.FOUNDATION) {
                        this._game.tableauToFoundation(
                                this._game.tableauPileOf(this.selectedCard.getRepr()));
                    }
                    break;

                case TABLEAU_BASE:
                    int tabPile = this._game.tableauPileOf(this.selectedCard.getRepr());
                    boolean satisfied = false;

                    for (int i = 0; i < colliding.size() && !satisfied; i++) {
                        GUICard colCard = colliding.get(i);
                        switch (colCard.getType()) {

                        case TABLEAU_BASE:
                        case TABLEAU_HEAD:

                            int otherTabPile = this._game.tableauPileOf(colCard.getRepr());
                            if (otherTabPile != tabPile) {
                                this._game.tableauToTableau(tabPile,
                                        this._game.tableauPileOf(colCard.getRepr()));
                                satisfied = true;
                            }

                            break;

                        case FOUNDATION:

                            if (this._game.tableauSize(tabPile) == 1) {
                                this._game.tableauToFoundation(tabPile);
                                satisfied = true;
                            }
                            break;
                        default:
                            break;
                        }
                    }

                    break;

                case FOUNDATION:
                    switch (other.getType()) {
                    case TABLEAU_BASE:
                    case TABLEAU_HEAD:
                    case TABLEAU_NORM:
                        this._game.foundationToTableau(
                                this._game.foundationPileOf(this.selectedCard.getRepr()),
                                this._game.tableauPileOf(other.getRepr()));
                        break;
                    default:
                        break;
                    }
                    break;

                case RESERVE:
                    switch (other.getType()) {
                    case TABLEAU_BASE:
                    case TABLEAU_HEAD:
                    case TABLEAU_NORM:
                        this._game.reserveToTableau(this._game.tableauPileOf(other.getRepr()));
                        break;

                    case FOUNDATION:
                        this._game.reserveToFoundation();
                        break;
                    default:
                        break;
                    }

                    break;
                default:
                    break;
                }
            } catch (IllegalArgumentException exp) {
                this.error(exp);
            }

        }

    }

    /* ================ MESSAGE STUFF =================== */
    /**
     * Writes an error message to the label.
     * 
     * @param exp
     */
    private void error(Exception exp) {
        String errorMsg = String.format(exp.getMessage());
        this.message("Error: " + errorMsg);
        this.showMessage(errorMsg, "Error", "Error");

    }

    /**
     * Writes a simple message to the label.
     * 
     * @param message
     */
    private void message(String message) {
        this.setLabel("messageLabel", message);
    }

    private void victory() {
        int result = this.showOptions("You won with score " + this._game.getScore() + "!",
                "Victory!", "information", null, "New Game", "Quit");
        if (result == 0) {
            this.newGame(null);
        } else {
            this.quit(null);
        }
    }

    /* =================================================== */
    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

    /** The card I am selectiong **/
    private GUICard selectedCard = null;
    private int selectedLayer = 0;

}

package canfield;

/** A type of player that gets input from the mouse, and reports
 *  game positions and reports errors on a GUI.
 *  @author
 */
class GUIPlayer extends Player {

    /** A GUIPlayer that makes moves on GAME. */
    GUIPlayer(Game game) {
        super(game);
    }

    @Override
    void play() {
        _game.deal();
        _display = new CanfieldGUI("Canfield", _game);
    }

    /** Displays the playing surface. */
    private CanfieldGUI _display;

}

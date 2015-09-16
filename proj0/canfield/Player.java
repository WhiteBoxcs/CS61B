package canfield;

/** A player provides moves and commands to a Game and may also
 *  display the status of the game.
 *  @author P. N. Hilfinger
 */
abstract class Player {

    /** A Player that makes moves on GAME. */
    protected Player(Game game) {
        _game = game;
    }

    /** Play one or more games of solitaire. */
    abstract void play();

    /** The Game being played. */
    protected Game _game;

}


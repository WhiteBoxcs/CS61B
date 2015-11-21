/**
 *
 */
package loa;

import java.util.ArrayList;
import java.util.Random;

import loa.exceptions.GameException;
import loa.exceptions.GameNotStartedException;
import loa.exceptions.GameVictoryException;
import loa.exceptions.InvalidMoveException;
import loa.exceptions.UnknownPlayerException;
import loa.players.DensityMachinePlayer;
import loa.players.HumanPlayer;
import loa.players.Player;
import loa.util.Logger;

/**
 * @author William Hebgen Guss Represents an actual game of league of legends.
 */
public class Game extends Logger {
    /**
     * The board.
     */
    private Board _board;

    /**
     * If the game is platying.
     */
    private boolean _playing;

    /**
     * Whomever is playing the game.
     */
    private ArrayList<Player> _players;

    /**
     * The index of the player playing the game.
     */
    private int playerIndex = 0;

    /**
     * The winner of the game.
     */
    private Player winner;

    /**
     * The version umber of the game.
     */
    public static final String VERSION = "1.0";

    /**
     * The first random float.
     */
    public static final Random RANDOM = new Random();

    /**
     * Initializes with a new board.
     * @param name
     *            the name of the game. lol.
     */
    public Game(String name) {
        super(name);

        this._board = new Board(this);
        this._playing = false;
        this._players = new ArrayList<Player>();

        this._players.add(new HumanPlayer(Piece.BP, 0));
        this._players.add(new DensityMachinePlayer(Piece.WP, 0, this));
    }

    /**
     * Plays a move in the game.
     * @param input
     *            The input from a view.
     * @throws GameException
     *             Throws an exception iff either the move is invalid or the
     *             game is not started.
     * @returns Whether or not a move is expected.
     */
    public void play(Move input) throws GameException {
        if (this.currentPlayer() != null) {
            if (input == null && (this.inputExpected() || !this.playing())) {
                return;
            } else if (input != null && input.isInvalid()) {
                throw new InvalidMoveException(input);
            } else if (!this.playing()) {
                throw new GameNotStartedException();
            }

            this.checkVictory();

            this.logMove(
                    this._board.performMove(this.currentPlayer().turn(input)));

            this.checkVictory();

            this.playerIndex = (this.playerIndex + 1) % this._players.size();

        }
    }

    /**
     * Plays without giving input.
     * @param move
     * @throws GameException
     *             Throws an exception iff either the move is invalid or the
     *             game is not started.
     * @returns Whether or not a move is expected.
     */
    public void play() throws GameException {
        this.play(null);
    }

    /**
     * Starts a game.
     * @returns Whether or not a move is expected
     */
    public boolean start() {
        this.setPlaying(true);
        return this.inputExpected();
    }

    /**
     * Clears the game board and stops playign the game.
     */
    public void clear() {
        this.setPlaying(false);
        this._board.clear();
        this.winner = null;
        this.playerIndex = 0;
        for (Player p : this._players) {
            p.setScore(0);
        }
        this.log("Board cleared.", LogLevel.GAME_STATE);
    }

    /**
     * @param playing
     *            the _playing to set.
     */
    protected void setPlaying(boolean playing) {
        if (this._playing != playing) {
            this._playing = playing;
            if (this._playing) {
                this.log("Game started.", LogLevel.GAME_STATE);
            } else {
                this.log("Game stopped.", LogLevel.GAME_STATE);
            }
        }
    }

    /**
     * @return whether or not a game is being played.
     */
    public boolean playing() {
        return this._playing;
    }

    /**
     * @return whether or not a move is expected.
     */
    public boolean inputExpected() {

        return !this.playing() || this.currentPlayer() != null
                && this.currentPlayer().inputExpected();
    }

    /**
     * Gets the current player.
     * @return the current player.
     */
    public Player currentPlayer() {
        if (!this._players.isEmpty()) {
            return this._players.get(this.playerIndex);
        }
        return null;
    }

    /**
     * Gets the board on which the game is being played.
     * @return The board.
     */
    public Board getBoard() {
        return this._board;
    }

    /**
     * Sets a given player to a specified plah styel.
     * @param player
     *            The player to set.
     * @param newPlayer
     *            the new player.
     */
    public void setPlayer(Piece player, Player newPlayer)
            throws UnknownPlayerException {

        for (int i = 0; i < this._players.size(); i++) {
            if (this._players.get(i).team() == player) {
                this._players.set(i, newPlayer);
                this.setPlaying(false);
                return;
            }
        }

        throw new UnknownPlayerException(player);
    }

    /**
     * Sets a piece within the game.
     * @param row
     *            The row to set.
     * @param col
     *            The collumn to set.
     * @param piece
     *            The piece type to set.
     */
    public void setPiece(Piece piece, int row, int col) {
        this.setPlaying(false);
        this._board.set(row, col, piece);
        if (this.winner != null
                && this._board.contiguityScore(this.winner.team()) != 1) {
            this.winner.setScore(0);
            this.winner = null;
        }
    }

    /**
     * Checks the game for victory.
     * @throws GameVictoryException
     *             Throws a game victory exception if victory has been reached.
     */
    private void checkVictory() throws GameVictoryException {
        if (this.winner != null) {
            this.log(this.currentPlayer().team().fullName() + " wins.",
                    LogLevel.GAME_STATE);
            this.setPlaying(false);
            throw new GameVictoryException(this.winner);
        }

        double contScore =
                this._board.contiguityScore(this.currentPlayer().team());
        if (this.currentPlayer().getScore() == 1 || contScore == 1) {
            this.currentPlayer().setScore(contScore);

            this.log(this.currentPlayer().team().fullName() + " wins.",
                    LogLevel.GAME_STATE);
            this.setPlaying(false);
            this.winner = this.currentPlayer();
            this.playerIndex = (this.playerIndex + 1) % this._players.size();
            throw new GameVictoryException(this.winner);

        }

        this.currentPlayer().setScore(contScore);
    }

    /**
     * Logs a move.
     * @param finalMove
     *            The move to log.
     */
    private void logMove(Move finalMove) {
        this.log(
                this.currentPlayer().team().abbrev().toUpperCase() + "::"
                        + finalMove.toString(),
                this.currentPlayer().verbose());
    }

    /**
     * Logs a message with a log level.
     * @param message
     *            Them essage to log.
     * @param level
     *            the log level.
     */
    private void log(String message, LogLevel level) {
        this.log(message, level.getLevel());
    }

    /**
     * Contains the log levels for the game.
     * @author William
     */
    public enum LogLevel {
        DEBUG(0), GAME_STATE(1), MOVES(2), MOVES_AI(3);

        /**
         * The level.
         */
        private final int _level;

        /**
         * Creates an enum type.
         * @param level
         *            The level of the type.
         */
        private LogLevel(int level) {
            this._level = level;
        }

        /**
         * Gets the level of the LOgLEVEL.
         * @return the level.
         */
        public int getLevel() {
            return this._level;
        }
    }
}

package org.IgorNorbert.lista4;

/**
 * Interface for Chinese checker game manager.
 * Classes implementing this interface should enforce game rules
 * and check move correctness
 */
public interface GameMaster {
    /**
     * Add player.
     * @return Color of the added player
     * @throws AllSeatsTakenException thrown iff there is no room
     * for another player
     */
    Color addPlayer() throws AllSeatsTakenException;

    /**
     * Add player and assign them to the chosen seat.
     * @param seat seat to whom the player should be assigned
     * @return Color of the assigned player
     * @throws SeatTakenException iff The chosen seat has already been taken
     */
    Color addPlayer(Seat seat) throws SeatTakenException;

    /**
     * Removes all checkers from the board.
     */
    void clearBoard();

    /**
     * Starts the game.
     * @return Color of the starting player
     * @throws IncorrectNumberOfPlayersException
     * iff there is incorrect number of players
     */
    Color startGame() throws IncorrectNumberOfPlayersException;

    /**
     * Check whether the game has finished.
     * @return true iff the game has finished
     */
    boolean isFinished();

    /**
     * Get the winner array.
     * @return Array with sorted by the order in
     * which the players finished the game (where index = 0 means the winner)
     */
    Color[] getWinner();

    /**
     * Color of the player currently making move.
     * @return Color of the player whose turn is currently played
     */
    Color getCurrentPlayer();

    /**
     * Moves checker from old position to new one.
     * @param oldX x coordinate of checker
     * @param oldY y coordinate of checker
     * @param newX x coordinate of move
     * @param newY y coordinate of move
     * @param color color of the player who moves the checker
     * @return true iff another move of the same player can be made
     * @throws IncorrectMoveException iff The chose move is incorrect
     * @throws NotThisPlayerTurnException iff the color parameter
     * doesn't match color of the current player @see getCurrentPlayer
     */
    boolean moveChecker(int oldX, int oldY, int newX, int newY, Color color)
            throws IncorrectMoveException, NotThisPlayerTurnException;

    /**
     * Skips the turn of the chosen player.
     * @param color color of the player whose turn should be skipped
     * @throws NotThisPlayerTurnException Iff given color doesn't match
     * the color of the player currently making move @see getCurrentPlayer
     */
    void skipTurn(Color color) throws NotThisPlayerTurnException;

    /**
     * Surrender the game.
     * @param color color of the surrendering player
     * @throws NotThisPlayerTurnException thrown iff the color
     * does not match the color of player currently making
     * their move @see getCurrentPlayer
     */
    void forfeit(Color color) throws NotThisPlayerTurnException;

    /**
     * Returns an array representing the state of the game.
     * @return array of Colors, where each color corresponds to player's color
     */
    Color[][] getCheckerArray();

    /**
     * Returns array of boolean representing the board's size and shape
     * @return a field is false iff it's permanently off limits
     */
    boolean[][] boardMask();
}

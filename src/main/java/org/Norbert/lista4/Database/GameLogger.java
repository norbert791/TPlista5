package org.Norbert.lista4.Database;

import org.Norbert.lista4.Game.Color;

public interface GameLogger {
    /**
     * Records a checkerMove.
     * @param oldX X coordinate of the old position
     * @param oldY Y coordinate of the old position
     * @param newX X coordinate of the new position
     * @param newY Y coordinate of the new position
     * @param color Color of the checker
     */
    void insertCheckerMove(int oldX, int oldY, int newX, int newY, Color color);

    /**
     * Records a player's turn skip.
     * @param color Color of the player that's skipped their turn
     */
    void insertSkip(Color color);

    /**
     * Records player's surrender
     * @param color Color of the player that skipped their turn
     */
    void insertForfeit(Color color);

    /**
     * Updates the dataBase with this game's results.
     */
    void commitGame();

    /**
     * Clear's this game's record.
     */
    void clear();

    /**
     * Stores the shape of the board for reference.
     */
    void storeBoardShape(boolean[][] board);

    /**
     * Adds player with their name.
     * @param playerName name of the player
     * @param playerColor color of the player
     */
    void addPlayer(String playerName, Color playerColor);
}

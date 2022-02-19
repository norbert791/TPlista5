package org.Norbert.lista5;

import org.Norbert.lista5.Database.GameLogger;
import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Game.Seat;

public class MockLogger implements GameLogger {
    /**
     * Records a checkerMove.
     *
     * @param oldX  X coordinate of the old position
     * @param oldY  Y coordinate of the old position
     * @param newX  X coordinate of the new position
     * @param newY  Y coordinate of the new position
     * @param color Color of the checker
     */
    @Override
    public void insertCheckerMove(int oldX, int oldY, int newX, int newY, Color color) {

    }

    /**
     * Records a player's turn skip.
     *
     * @param color Color of the player that's skipped their turn
     */
    @Override
    public void insertSkip(Color color) {

    }

    /**
     * Records player's surrender
     *
     * @param color Color of the player that skipped their turn
     */
    @Override
    public void insertForfeit(Color color) {

    }

    /**
     * Updates the dataBase with this game's results.
     */
    @Override
    public void commitGame() {

    }

    /**
     * Clear's this game's record.
     */
    @Override
    public void clear() {

    }

    /**
     * Stores the shape of the board for reference.
     *
     * @param gameType Name of the GameMaster interface implementation used
     */
    @Override
    public void insertGameType(String gameType) {

    }

    /**
     * Adds player with their name.
     *
     * @param playerName  name of the player
     * @param playerColor color of the player
     * @param playerSeat  seat assigned to the player
     */
    @Override
    public void addPlayer(String playerName, Color playerColor, Seat playerSeat) {

    }
}

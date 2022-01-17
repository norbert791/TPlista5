package org.IgorNorbert.lista4;

/**
 * Interface used for managing UI.
 */
public interface UserInterface {
    /**
     * Prints the board onto the interface.
     * @param board Color array representing current state of the game
     */
    void printBoard(Color[][] board);

    /**
     * Sets the color of player currently making their move.
     * @param color color representing player currently making their move
     */
    void setCurrentPlayer(Color color);

    /**
     * Sets color the color of client.
     * @param color color representing the client's color
     */
    void setPlayerColor(Color color);

    /**
     * Prints error message onto the interface.
     * @param errorMessage Message that is to be displayed to client
     */
    void printError(String errorMessage);

    /**
     * Display information whether player may make another move.
     * @param nextMove insert true iff client may make another move
     */
    void nextMove(boolean nextMove);
}

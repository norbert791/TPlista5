package org.IgorNorbert.lista4;

public interface UserInterface {
    void printBoard(Color[][] board);
    void setCurrentPlayer(Color color);
    void setPlayerColor(Color color);
    void printError(String errorMessage);
    void nextMove(boolean nextMove);
}

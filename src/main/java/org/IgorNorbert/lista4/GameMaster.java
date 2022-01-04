package org.IgorNorbert.lista4;

public interface GameMaster {
    Color addPlayer();
    Color addPlayer(Seat seat);
    void clearBoard();
    Color startGame();
    Color isFinished();
    boolean moveChecker(int oldX, int oldY, int newX, int newY, Color color);
    Color[][] getCheckerArray();
}

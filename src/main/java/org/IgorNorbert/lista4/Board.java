package org.IgorNorbert.lista4;

public interface Board {
    boolean moveChecker(int OldX, int OldY, int newX, int newY) throws IncorrectMoveException;
    Color getCheckerColor(int X, int Y) throws IncorrectPositionException;
    void addChecker(int X, int Y, Color color) throws IncorrectPositionException;
    void removeChecker(int X, int Y) throws IncorrectPositionException;
    Color[][] getCheckerColorArray();
}

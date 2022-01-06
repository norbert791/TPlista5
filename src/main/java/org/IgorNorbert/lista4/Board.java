package org.IgorNorbert.lista4;

public interface Board {
    boolean moveChecker(int oldX, int oldY, int newX, int newY) throws IncorrectMoveException;
    Color getCheckerColor(int X, int Y) throws IncorrectPositionException;
    void addChecker(int X, int Y, Color color) throws IncorrectPositionException;
    void removeChecker(int X, int Y) throws IncorrectPositionException;
    void setCorner(Seat seat, Color color);
    boolean checkCorner(Seat seat, Color color);
    Color[][] getCheckerColorArray();
}

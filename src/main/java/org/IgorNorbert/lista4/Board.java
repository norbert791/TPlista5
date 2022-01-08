package org.IgorNorbert.lista4;

import org.IgorNorbert.lista4.Color;
import org.IgorNorbert.lista4.IncorrectMoveException;
import org.IgorNorbert.lista4.IncorrectPositionException;
import org.IgorNorbert.lista4.Seat;

public interface Board {
    boolean moveChecker(int oldX, int oldY, int newX, int newY) throws IncorrectMoveException;
    Color getCheckerColor(int X, int Y) throws IncorrectPositionException;
    void addChecker(int X, int Y, Color color) throws IncorrectPositionException;
    void removeChecker(int X, int Y) throws IncorrectPositionException;
    void setCorner(Seat seat, Color color);
    boolean checkCorner(Seat seat, Color color);
    Color[][] getCheckerColorArray();
}

package org.Norbert.lista4.Database;

import org.Norbert.lista4.Game.Color;

import java.io.Serializable;

public record PlayerMove(MoveType moveType, Color playerColor,
                         CheckerMove checkerMove) implements Serializable {

    public enum MoveType implements Serializable{
        SKIP,
        SURRENDER,
        CHECKER_MOVE;
    }

    public record CheckerMove(
            int oldX, int oldY, int newX, int newY) implements Serializable{

    }
}

package org.Norbert.lista4.Database;

import org.Norbert.lista4.Game.Color;

public record PlayerMove(MoveType moveType, Color playerColor, CheckerMove checkerMove) {

    protected enum MoveType {
        SKIP,
        SURRENDER,
        CHECKER_MOVE;
    }

    protected record CheckerMove(int oldX, int oldY, int newX, int newY) {

    }
}

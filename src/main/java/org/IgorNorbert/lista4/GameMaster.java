package org.IgorNorbert.lista4;

import org.IgorNorbert.lista4.*;

public interface GameMaster { //TODO: An idea to reduce dependency on Seat / Color: Consider padding f
    // TODO: (cont) factory methods that'd return set of possible seats / colors. Make Color and Seat
    //  TODO: (cont 2) abstract and extend from them.
    Color addPlayer() throws AllSeatsTakenException;
    Color addPlayer(Seat seat) throws SeatTakenException;
    void clearBoard();
    Color startGame() throws IncorrectNumberOfPlayersException;
    boolean isFinished();
    Color[] getWinner();
    Color getCurrentPlayer();
    boolean moveChecker(int oldX, int oldY, int newX, int newY, Color color)
            throws IncorrectMoveException, NotThisPlayerTurnException;
    void skipTurn(Color color) throws NotThisPlayerTurnException;
    void forfeit(Color color) throws NotThisPlayerTurnException;
    Color[][] getCheckerArray();
}

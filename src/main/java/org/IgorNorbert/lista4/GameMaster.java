package org.IgorNorbert.lista4;

public interface GameMaster {
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
    void forfeit(Color color);
    Color[][] getCheckerArray();
}

package org.IgorNorbert.lista4;

public class SimpleMaster implements GameMaster{

    @Override
    public Color addPlayer() {
        return null;
    }

    @Override
    public Color addPlayer(Seat seat) {
        return null;
    }

    @Override
    public void clearBoard() {

    }

    @Override
    public Color startGame() {
        return null;
    }

    @Override
    public Color isFinished() {
        return null;
    }

    @Override
    public boolean moveChecker(int oldX, int oldY, int newX, int newY, Color color) {
        return false;
    }

    @Override
    public Color[][] getCheckerArray() {
        return new Color[0][];
    }
}

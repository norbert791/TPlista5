package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.GameLogger;
import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Game.Seat;

import java.util.Collection;
import java.util.LinkedList;

public class HibernateLogger implements GameLogger {
    private final Collection<PlayerMove> moves = new LinkedList<>();
    @Override
    public void insertCheckerMove(int oldX, int oldY, int newX, int newY, Color color) {

    }

    @Override
    public void insertSkip(Color color) {

    }

    @Override
    public void insertForfeit(Color color) {

    }

    @Override
    public void commitGame() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void insertGameType(String gameType) {

    }

    @Override
    public void addPlayer(String playerName, Color playerColor, Seat playerSeat) {

    }
}

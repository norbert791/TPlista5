package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.GameLogger;
import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Game.Seat;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.*;

public class HibernateLogger implements GameLogger {
    private final Collection<PlayerMove> moves = new LinkedList<>();
    private final Collection<LobbyPlayer> players = new LinkedList<>();
    private final Map<Color, LobbyPlayer> playerDict = new EnumMap<Color, LobbyPlayer>(Color.class);
    private String gameMode;
    private SessionFactory sessionFactory;
    private DataSource dataSource;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void insertCheckerMove(int oldX, int oldY, int newX, int newY, Color color) {
        PlayerMove temp = new PlayerMove();
        temp.setMoveType(MoveType.CHECKER_MOVE);
        temp.setPlayer(playerDict.get(color));
        temp.setCheckerMove(new CheckerMove(oldX, oldY, newX, newY));
        moves.add(temp);
    }

    @Override
    public void insertSkip(Color color) {
        PlayerMove temp = new PlayerMove();
        temp.setPlayer(playerDict.get(color));
        temp.setMoveType(MoveType.SKIP);
        moves.add(temp);
    }

    @Override
    public void insertForfeit(Color color) {
        PlayerMove temp = new PlayerMove();
        temp.setPlayer(playerDict.get(color));
        temp.setMoveType(MoveType.SURRENDER);
        moves.add(temp);
    }

    @Override
    public void commitGame() {
        Game toCommit = new Game();
        toCommit.setGameType(gameMode);
        toCommit.setTimestamp(new Timestamp(System.currentTimeMillis()));
        for (LobbyPlayer temp : players) {
            temp.setGame(toCommit);
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx;
            tx = session.beginTransaction();
            session.save(toCommit);
            for (LobbyPlayer temp : players) {
                session.save(temp);
            }
            for (PlayerMove temp : moves) {
                session.save(temp);
            }
            tx.commit();
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        moves.clear();
        players.clear();
        playerDict.clear();
    }

    @Override
    public void insertGameType(String gameType) {
        this.gameMode = gameType;
    }

    @Override
    public void addPlayer(String playerName, Color playerColor, Seat playerSeat) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Player> cr = builder.createQuery(Player.class);
        Root<Player> root = cr.from(Player.class);
        Predicate[] predicates = new Predicate[1];
        predicates[0] = builder.like(root.get("nickName"), playerName);
        cr.select(root).where(predicates);
        TypedQuery<Player> query = session.createQuery(cr);
        try {
            List<Player> results = query.getResultList();
            session.close();
            if (results.size() != 1) {
                return;
            }
            else {
                LobbyPlayer temp = new LobbyPlayer();
                temp.setPlayerId(results.get(0).getId());
                temp.setColor(playerColor);
                temp.setSeat(playerSeat);
                temp.setGame(null);
                this.players.add(temp);
                this.playerDict.put(playerColor, temp);
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}

package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Game.Seat;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class LobbyPlayer implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
    private int playerId;
    @Enumerated(EnumType.STRING)
    private Color color;
    @Enumerated(EnumType.STRING)
    private Seat seat;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

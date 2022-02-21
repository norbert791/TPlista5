package org.Norbert.lista5.Database.HibernateImplementation;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table (name = "Game")
public class Game {
    @Id
    @GeneratedValue
    private int id;
    private String gameType;
    private Timestamp timestamp;

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

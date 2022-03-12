package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Game.Color;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "move")
public class PlayerMove implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private LobbyPlayer player;
    @Enumerated(EnumType.STRING)
    private MoveType moveType;
    @OneToOne(targetEntity = CheckerMove.class, cascade = CascadeType.ALL)
    private CheckerMove checkerMove;
    private int orderingNumber;

    public int getOrderingNumber() {
        return orderingNumber;
    }

    public void setOrderingNumber(int orderingNumber) {
        this.orderingNumber = orderingNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LobbyPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LobbyPlayer player) {
        this.player = player;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public CheckerMove getCheckerMove() {
        return checkerMove;
    }

    public void setCheckerMove(CheckerMove checkerMove) {
        this.checkerMove = checkerMove;
    }
}

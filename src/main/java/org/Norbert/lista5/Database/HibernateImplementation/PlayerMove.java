package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Game.Color;

import javax.persistence.*;

@Entity
@Table(name = "move")
public class PlayerMove {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private LobbyPlayer player;
    @Enumerated(EnumType.STRING)
    private MoveType moveType;
    @OneToOne(targetEntity = CheckerMove.class, cascade = CascadeType.ALL)
    private CheckerMove checkerMove;

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

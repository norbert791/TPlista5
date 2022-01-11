package org.IgorNorbert.lista4;

import java.io.Serializable;

public enum Package implements Serializable {
    MOVE,
    CONNECT,
    JOIN,
    LEAVE,
    READY,
    BOARD,
    PLAYERINT,
    RETURN,
    ERROR,
    DISCONNECT,
    LOBBIES,
    FORFEIT, SKIP, CURRENT;
    private Serializable argument = null;
    public void setArgument(Serializable argument){
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }
}

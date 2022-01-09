package org.IgorNorbert.lista4;

import java.io.Serializable;

public enum Package implements Serializable {
    MOVE,
    CONNECT,
    JOIN,
    LEAVE,
    READY,
    BOARD,
    RETURN,
    ERROR,
    DISCONNECT,
    FORFEIT;
    private Serializable argument = null;
    public void setArgument(Serializable argument){
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }
}

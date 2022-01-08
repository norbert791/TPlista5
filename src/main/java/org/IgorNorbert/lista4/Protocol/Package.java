package org.IgorNorbert.lista4.Protocol;

import java.io.Serializable;

public enum Package implements Serializable {
    MOVE,
    CONNECT,
    JOIN,
    READY,
    COLOR,
    BOARD,
    FORFEIT;
    private Serializable argument;
    public void setArgument(Serializable argument){
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }
}

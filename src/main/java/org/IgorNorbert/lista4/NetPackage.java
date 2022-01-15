package org.IgorNorbert.lista4;

import java.io.Serializable;

public class NetPackage implements Serializable {
    public enum Type implements Serializable{
        MOVE,
        CONNECT,
        JOIN,
        LEAVE,
        READY,
        BOARD,
        PLAYERCOLOR,
        RETURN,
        ERROR,
        DISCONNECT,
        LOBBIES,
        FORFEIT,
        SKIP,
        CURRENTPLAYER;
    }
    public Type type;
    private Serializable argument = null;
    public void setArgument(Serializable argument){
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }
}

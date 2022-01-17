package org.IgorNorbert.lista4;

import java.io.Serializable;

/**
 * The class used for encapsulation of messages sent to server
 */
public class NetPackage implements Serializable {
    /**
     * Enum class used for message type distinction
     */
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
    /**
     * Argument for command to used for managing the command
     */
    private Serializable argument = null;

    /**
     * Sets the argument for the command
     * @param argument Argument for command
     */
    public void setArgument(Serializable argument){
        this.argument = argument;
    }

    /**
     * Retrieves argument from the command
     * @return
     */
    public Object getArgument() {
        return argument;
    }
}

package org.Norbert.lista4.Protocol;

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
        BOARDMASK,
        ERROR,
        DISCONNECT,
        LOBBIES,
        FORFEIT,
        SKIP,
        CURRENTPLAYER,
        PlAYERLIST,
        WINORDER;
    }

    /**
     * This package is purely to save a line per package creation.
     */
    public NetPackage(NetPackage.Type type, Serializable argument){
        this.type = type;
        this.argument = argument;
    }
    /**
     * This constructor is purely to save a line per package creation.
     * @param type
     */
    public NetPackage(NetPackage.Type type) {
        this.type = type;
        this.argument = null;
    }

    /**
     * Default constructor
     */
    public NetPackage() {
        this.type = null;
        this.argument = null;
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

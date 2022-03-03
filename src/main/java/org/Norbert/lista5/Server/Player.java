package org.Norbert.lista5.Server;

import org.Norbert.lista5.Database.AuthorizationFailed;
import org.Norbert.lista5.Database.UserManager;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;
import org.Norbert.lista5.Database.HistoryRetriever;
import org.Norbert.lista5.Game.Exceptions.IncorrectMoveException;
import org.Norbert.lista5.Game.Exceptions.NotThisPlayerTurnException;
import org.Norbert.lista5.Protocol.NetPackage;
import org.Norbert.lista5.Protocol.NetProtocolServer;
import org.Norbert.lista5.Protocol.SimpleNetProtocolFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class representing player receiving requests from client.
 */
public class Player implements Runnable {
    /**
     * Nickname of player instance. For now randomized as placeholder.
     */
    private String nickName;
    /**
     * Lobby the player is currently connected to.
     */
    private Lobby lobby = null;
    /**
     * Array containing list of all lobbies.
     */
    private Lobby[] lobbyArray = null;
    /**
     * Reference to Server class.
     */
    private final Server parent;
    /**
     * socket used by this instance for communication with Client.
     */
    private final Socket socket;
    /**
     * Used as variable for loop in Runnable method.
     */
    private boolean connected = true;
    /**
     * Server-side implementation of protocol.
     */
    private NetProtocolServer protocol = null;

    private final UserManager userManager;

    private HistoryRetriever retriever;

    private boolean signedIn = false;

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    /**
     * Constructor used for spawning connection thread.
     * @param parent reference to server that spawns the thread
     * @param socket reference to socket used for exchanging data with client
     */
    public Player(final Server parent, final Socket socket, HistoryRetriever retriever, final UserManager userManager) {
        this.lobbyArray = parent.getLobbyArray();
        this.parent = parent;
        this.socket = socket;
        this.retriever = retriever;
        this.userManager = userManager;
        this.nickName = null;
    }

    /**
     * Default constructor, used mainly in tests.
     */
    public Player() {
        this.parent = null;
        socket = null;
        retriever = null;
        this.userManager = null;
        this.nickName = "test"
                + ThreadLocalRandom.current().nextInt(1000);
    }

    /**
     * Private method used for handling move request.
     * @param oldX old x coordinate
     * @param oldY old y coordinate
     * @param newX new x coordinate
     * @param newY new y coordinate
     * @return NetPackage with reply to the client
     */
    private NetPackage moveChecker(
            final int oldX, final int oldY, final int newX, final int newY) {
        NetPackage returnInfo;
        //If player is not in lobby, send him error,
        // else make lobby handle the request and return appropriate reply
        if (lobby != null) {
             returnInfo = new NetPackage();
             returnInfo.type = NetPackage.Type.MOVE;
            try {
               returnInfo.setArgument(
                       lobby.moveChecker(oldX, oldY, newX, newY, this));
            } catch (IncorrectMoveException | NotThisLobbyException
                    | NotThisPlayerTurnException e) {
               returnInfo = new NetPackage();
               returnInfo.type = NetPackage.Type.ERROR;
               returnInfo.setArgument(e.getMessage());
            }
        } else {
            returnInfo = new NetPackage();
            returnInfo.type = NetPackage.Type.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }

    /**
     * Handle setReady request.
     * @param value true iff player should be flaged as ready
     * @return NetPackage with appropriate reply
     */
    private NetPackage setReady(final boolean value) {
        NetPackage returnInfo = new NetPackage();
        returnInfo.type = NetPackage.Type.READY;
        //Send error iff player is not in lobby, else set
        if (lobby != null) {
                lobby.setReady(this, value);
                returnInfo.setArgument("Set ready");
        } else {
            returnInfo = new NetPackage();
            returnInfo.type = NetPackage.Type.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }

    /**
     * Handle leave request.
     * @return NetPackage containing the reply
     */
    private NetPackage leave() {
        NetPackage returnInfo;
        //If player is in lobby, remove them
        if (lobby != null) {
            lobby.removePlayer(this);
            lobby = null;
            returnInfo = new NetPackage();
            returnInfo.type = NetPackage.Type.LEAVE;
            returnInfo.setArgument("Left successfully");
        } else { //Else send back error
            returnInfo = new NetPackage();
            returnInfo.type = NetPackage.Type.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }

    /**
     * Handle Join Lobby request.
     * @param number id of the lobby (their array index)
     * @return NetPackage containing the reply
     */
    private NetPackage joinLobby(final int number) {
        //If player already is in lobby, send them error
        NetPackage result;
        if (lobby != null) {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You already are in a game");
        }
        //else join the lobby, if it is not full
        try {
            lobbyArray[number].addPlayer(this);
            lobby = lobbyArray[number];
            result = new NetPackage();
            result.type = NetPackage.Type.JOIN;
            result.setArgument("Joined successfully");
        } catch (LobbyFullException | NotThisLobbyException
                | IndexOutOfBoundsException e) {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument(e.getMessage());
        }
        return result;
    }

    /**
     * Get array of colors, representing state of the game board.
     * @return On success NetPackage containing
     * Color array representing the board
     */
    private NetPackage getColorArray() {
        NetPackage result;
        if (lobby != null) {
            result = new NetPackage();
            result.type = NetPackage.Type.BOARD;
            result.setArgument(lobby.getCheckerArray());
        } else {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }

    /**
     * Retrieve color representing this player on board.
     * @return NetPackage containing their color
     */
    private NetPackage getPlayerColor() {
        NetPackage result;
        if (lobby != null) {
            result = new NetPackage();
            result.type = NetPackage.Type.PLAYERCOLOR;
            result.setArgument(lobby.getPlayerColor(this));
        } else {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }

    /**
     * Fetch list of lobbies from server and cache it.
     * @return NetPackage with Array of lobbies on success or null otherwise.
     */
    private NetPackage updateLobbyArray() {
        lobbyArray = parent == null ? null : parent.getLobbyArray();
        NetPackage result = new NetPackage();
        result.type = NetPackage.Type.LOBBIES;
        int[] arg = new int[lobbyArray.length];
        for (int i = 0; i < arg.length; i++) {
            arg[i] = lobbyArray[i].getPlayerArray().length;
        }
        result.setArgument(arg);
        return result;
    }

    /**
     * Handles Skip turn request.
     * @return NetPackage with Type equal to SKIP on return
     */
    private NetPackage skipTurn() {
        NetPackage result;
        if (lobby == null) {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You are not on lobby");
            return result;
        }
        try {
            lobby.skipTurn(this);
            result = new NetPackage();
            result.type = NetPackage.Type.SKIP;
        } catch (NotThisLobbyException e) {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You are not in lobby");
        } catch (NotThisPlayerTurnException e) {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You may only skip during your turn");
        }
        return result;
    }

    /**
     * Handles request for array of players sorted by
     * their place in game (Winner index == 0).
     * @return NetPackage containing order in which the players have one.
     * Null if no player has yet won, all player is not in lobby
     */
    private NetPackage winOrder() {
        Player[] temp = this.lobby.getWinnerLine();
        if (this.lobby != null && temp != null) {
            String[] order = new String[temp.length];
            for (int i = 0; i < temp.length; i++) {
                order[i] = temp[i].getNickName();
            }
            return new NetPackage(NetPackage.Type.WINORDER, order);
        }
        return new NetPackage(NetPackage.Type.WINORDER, null);
    }

    /**
     * Handles request for list of names of players
     * that are in lobby with client.
     * @return NetPackage containing Mapping from their name to their color
     */
    private NetPackage playerList() {
        try {
            if (lobby != null) {
                return new NetPackage(NetPackage.Type.PlAYERLIST,
                        (Serializable) lobby.getPlayerMap());
            }
        } catch (ClassCastException e) {
            e.printStackTrace();

        }
        return new NetPackage(NetPackage.Type.PlAYERLIST, null);
    }

    private NetPackage getMask() {
        if(lobby == null) {
            return new NetPackage(NetPackage.Type.ERROR, "You are not in lobby");
        } else {
            return new NetPackage(NetPackage.Type.BOARDMASK, lobby.getMask());
        }
    }

    private NetPackage fetchHistory() {
        GameDescriptionRecord[] temp = retriever.fetchGameList(nickName);
        return new NetPackage(NetPackage.Type.FETCH_HISTORY, temp);
    }

    private NetPackage fetchRecord(int id) {
        GameRecord temp = retriever.fetchHistory(id);
        return new NetPackage(NetPackage.Type.FETCH_GAME_RECORD, temp);
    }

    /**
     * NetPackage containing the color of the current player.
     * @return On success, NetPackage with color assigned to the player or
     * null if the game has not yet begun
     */
    private NetPackage currentPlayer() {
        NetPackage result;
        if (lobby == null) {
            result = new NetPackage();
            result.type = NetPackage.Type.ERROR;
            result.setArgument("You are not in lobby");
        } else {
            result = new NetPackage();
            result.type = NetPackage.Type.CURRENTPLAYER;
            result.setArgument(lobby.getPlayerColor(lobby.getCurrentPlayer()));
        }
        return result;
    }
    private NetPackage register(NetPackage message) {
        try {
            String[] args = (String[])message.getArgument();
            if(userManager.register(args[0], args[1], args[2])) {
                return new NetPackage(NetPackage.Type.REGISTER, "Registered correctly");
            }
            else {
                return new NetPackage(NetPackage.Type.SIGN, "Registration failed");
            }
        } catch (ClassCastException | IndexOutOfBoundsException e) {
            return new NetPackage(NetPackage.Type.ERROR, "Incorrect request");
        }
    }
    private NetPackage sign(NetPackage message) {
        try {
            String[] args = (String[]) message.getArgument();
            if (userManager.logIn(args[0], args[1])) {
                try {
                    this.nickName = userManager.getName();
                    this.signedIn = true;
                } catch (AuthorizationFailed e) {
                    e.printStackTrace();
                }
                return new NetPackage(NetPackage.Type.SIGN, "Signed correctly");
            }
            else {
                return new NetPackage(NetPackage.Type.ERROR, "Signing in failed");
            }
        } catch (IndexOutOfBoundsException | ClassCastException e) {
            return new NetPackage(NetPackage.Type.ERROR, "Incorrect request");
        }
    }
    private NetPackage disconnect() {
        System.out.println("Disconnect received");
        connected = false;
        if (lobby != null) {
            lobby.removePlayer(this);
        }
        return new NetPackage(NetPackage.Type.DISCONNECT);
    }

    /**
     * Choose appropriate method for handling Client request.
     * @param message request from client
     * @return message Feed-back for client
     */
    protected NetPackage parseCommand(final NetPackage message) {
         NetPackage result = new NetPackage();
         result.type = NetPackage.Type.ERROR;
         result.setArgument("Command not recognized");
         try {
             result = switch (message.type) {
                 case JOIN -> joinLobby((int) message.getArgument());
                 case PLAYERCOLOR -> getPlayerColor();
                 case READY -> setReady((boolean) message.getArgument());
                 case LEAVE, FORFEIT -> leave();
                 case BOARD -> getColorArray();
                 case MOVE -> {
                     int[] temp = (int[]) message.getArgument();
                     yield moveChecker(temp[0], temp[1], temp[2], temp[3]);
                 }
                 case DISCONNECT -> disconnect();
                 case LOBBIES -> updateLobbyArray();
                 case SKIP -> skipTurn();
                 case CURRENTPLAYER -> currentPlayer();
                 case WINORDER -> winOrder();
                 case PlAYERLIST -> playerList();
                 case BOARDMASK -> getMask();
                 case FETCH_HISTORY -> fetchHistory();
                 case FETCH_GAME_RECORD -> fetchRecord((int) message.getArgument());
                 case ERROR, CONNECT -> {
                     NetPackage temp = new NetPackage();
                     temp.type = NetPackage.Type.ERROR;
                     temp.setArgument("This command is reserved for server");
                     yield temp;
                 }
                 case REGISTER -> register(message);
                 case SIGN -> sign(message);
             };
         } catch (IndexOutOfBoundsException e) {
             result.setArgument("The int array should be size of 4");
         } catch (ClassCastException e) {
             result.setArgument("This command has incorrect parameter");
         }
        return result;
    }

    /**
     * Override used for maintaining the connection with client
     * and handling their request / sending replies.
     */
    @Override
    public void run() {
        protocol = new SimpleNetProtocolFactory().getServerSide();
        try {
            protocol.setSocket(socket);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception");
            return;
        }
        while(!signedIn && connected) {
            try {
                if (protocol.waitForPackage()) {
                    NetPackage temp = protocol.retrievePackage();
                    if (temp.type == NetPackage.Type.REGISTER || temp.type == NetPackage.Type.SIGN) {
                        temp = parseCommand(temp);
                        protocol.sendPackage(temp);
                    }
                }
            } catch (IOException e) {
                return;
                }
            }
        while (connected) {
            try {
                if (protocol.waitForPackage()) {
                    NetPackage temp = protocol.retrievePackage();
                    temp = parseCommand(temp);
                    protocol.sendPackage(temp);
                }
            } catch (IOException e) {
                if (lobby != null) {
                    lobby.removePlayer(this);
                }
                return;
            }
        }
        try {
            if(lobby != null) {
                lobby.removePlayer(this);
            }
            protocol.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Retrievs the nickName of this player.
     * @return String with instance's nickname
     */
    public String getNickName() {
        return this.nickName;
    }
}

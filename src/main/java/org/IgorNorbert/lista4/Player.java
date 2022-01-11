package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.prefs.PreferenceChangeEvent;

public class Player implements Runnable{
    private Lobby lobby = null;
    private Lobby[] lobbyArray = null;
    private final Server parent;
    private final Socket socket;
    private boolean connected = true;
    private boolean boardReady = false;
    public Player( Server parent, Socket socket){
        this.lobbyArray = parent.getLobbyArray();
        this.parent = parent;
        this.socket = socket;
    }
    public Player(){
        this.parent = null;
        socket = null;
    }

    private Package moveChecker(int oldX, int oldY, int newX, int newY) {
        Package returnInfo;
        if(lobby != null){
             returnInfo = Package.MOVE;
            try {
               returnInfo.setArgument(lobby.moveChecker(oldX, oldY, newX, newY, this));
            } catch (IncorrectMoveException | NotThisLobbyException | NotThisPlayerTurnException e) {
               returnInfo = Package.ERROR;
               returnInfo.setArgument(e.getMessage());
            }
        }
        else{
            returnInfo = Package.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private Package setReady(boolean value){
        Package returnInfo = Package.READY;
        if(lobby != null){
                lobby.setReady(this, value);
                returnInfo.setArgument("Set ready");
        }
        else{
            returnInfo = Package.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private Package leave(){
        Package returnInfo;
        if(lobby != null){
            lobby.removePlayer(this);
            lobby = null;
            returnInfo = Package.LEAVE;
            returnInfo.setArgument("Left successfully");
        }
        else{
            returnInfo = Package.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private Package joinLobby(int number) {
        Package result;
        if(lobby != null){
            result = Package.ERROR;
            result.setArgument("You already are in a game");
        }
        try{
            lobbyArray[number].addPlayer(this);
            lobby = lobbyArray[number];
            result = Package.JOIN;
            result.setArgument("Joined successfully");
        } catch (LobbyFullException | NotThisLobbyException | IndexOutOfBoundsException e) {
            result = Package.ERROR;
            result.setArgument(e.getMessage());
        }
        return result;
    }
    private Package getPlayerArray() {
        Package result;
        if(lobby != null){
            result = Package.BOARD;
            result.setArgument(lobby.getCheckerArray());
        }
        else {
            result = Package.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private Package getPlayerInt(){
        Package result;
        if(lobby != null){
            result = Package.PLAYERINT;
            result.setArgument(lobby.getPlayerInt(this));
        }
        else {
            result = Package.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private Package updateLobbyArray(){
        lobbyArray = parent == null ? null : parent.getLobbyArray();
        Package result = Package.LOBBIES;
        result.setArgument(lobbyArray == null ? null : lobbyArray.length);
        return result;
    }
    private Package skipTurn(){
        Package result;
        if(lobby == null){
            result = Package.ERROR;
            result.setArgument("You are not on lobby");
            return result;
        }
        try{
            lobby.skipTurn(this);
            result = Package.SKIP;
        } catch (NotThisLobbyException e) {
            result = Package.ERROR;
            result.setArgument("You are not in lobby");
        } catch (NotThisPlayerTurnException e) {
            result = Package.ERROR;
            result.setArgument("You may only skip during your turn");
        }
        return result;
    }
    private Package currentPlayer(){
        Package result;
        if (lobby == null) {
            result = Package.ERROR;
            result.setArgument("You are not in lobby");
        }
        else {
            result = Package.CURRENT;
            result.setArgument(lobby.getPlayerInt(lobby.getCurrentPlayer()));
        }
        return result;
    }
    private void disconnect(){
        connected = false;
    }
    protected Package parseCommand(Package message){
         Package result = Package.ERROR;
         result.setArgument("Command not recognized");
         try{
             result = switch (message){
                 case JOIN -> joinLobby((int) message.getArgument());
                 case PLAYERINT -> getPlayerInt();
                 case READY -> setReady((boolean) message.getArgument());
                 case LEAVE, FORFEIT -> leave();
                 case BOARD -> getPlayerArray();
                 case MOVE -> {
                     int[] temp = (int[])message.getArgument();
                     yield moveChecker(temp[0], temp[1], temp[2], temp[3]);
                 }
                 case DISCONNECT -> {disconnect(); yield null;}
                 case LOBBIES -> updateLobbyArray();
                 case SKIP -> skipTurn();
                 case CURRENT -> currentPlayer();
                 case ERROR, CONNECT, RETURN -> {
                     Package temp = Package.ERROR;
                     temp.setArgument("This command is reserved for server");
                     yield temp;
                 }
             };
         } catch (IndexOutOfBoundsException e) {
             result.setArgument("The int array should be size of 4");
         } catch (ClassCastException e){
             result.setArgument("This command has incorrect parameter");
         }
         return result;
    }
    public void fetchBoard(){
        boardReady = true;
     }

    @Override
    public void run() {
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;
        Package message;
        if (socket == null){
            return;
        }
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            }
         catch (IOException e) {
            return;
        }
        while(connected){
            try{
                if(boardReady){
                    message = parseCommand(Package.BOARD);
                    outputStream.writeObject(message);
                    boardReady = false;
                }
                else if(inputStream.available() > 0){
                    message = (Package) inputStream.readObject();
                    message = parseCommand(message);
                    outputStream.writeObject(message);
                }
                wait(500);
            }catch (ClassNotFoundException | InterruptedException | IOException e) {
                e.printStackTrace();
            } catch (ClassCastException e){
              message = Package.ERROR;
              message.setArgument("Object not recognized");
                try {
                    outputStream.writeObject(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

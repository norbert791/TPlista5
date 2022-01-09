package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable{
    private Lobby lobby = null;
    private Lobby[] lobbyArray = null;
    private final Server parent;
    private final Socket socket;
    private boolean connected = true;
    private boolean boardReady = false;
    public Player(Lobby[] lobbyArray, Server parent, Socket socket){
        this.lobbyArray = lobbyArray;
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
             returnInfo = Package.RETURN;
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
    private Package setRead(boolean value){
        Package returnInfo = Package.RETURN;
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
            returnInfo = Package.RETURN;
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
            result = Package.RETURN;
            result.setArgument("Joined successfully");
        } catch (LobbyFullException | NotThisLobbyException e) {
            result = Package.ERROR;
            result.setArgument(e.getMessage());
        }
        return result;
    }
    private Package getPlayerArray() {
        Package result;
        if(lobby != null){
            result = Package.RETURN;
            result.setArgument(lobby.getCheckerArray());
        }
        else {
            result = Package.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private Package updateLobbyList(){
        lobbyArray = parent == null ? null : parent.getLobbyList();
        Package result = Package.RETURN;
        result.setArgument(lobbyArray);
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
                 case READY -> setRead((boolean) message.getArgument());
                 case LEAVE, FORFEIT -> leave();
                 case BOARD -> getPlayerArray();
                 case MOVE -> {
                     int[] temp = (int[])message.getArgument();
                     yield moveChecker(temp[0], temp[1], temp[2], temp[3]);
                 }
                 case DISCONNECT -> {disconnect(); yield null;}
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
    public void FetchBoard(){
        boardReady = true;
     }

    @Override
    public void run() {
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        Package message = null;
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
    }
}

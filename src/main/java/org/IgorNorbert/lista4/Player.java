package org.IgorNorbert.lista4;

import java.io.IOException;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Player implements Runnable {
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

    private NetPackage moveChecker(int oldX, int oldY, int newX, int newY) {
        NetPackage returnInfo;
        if(lobby != null){
             returnInfo = NetPackage.MOVE;
            try {
               returnInfo.setArgument(lobby.moveChecker(oldX, oldY, newX, newY, this));
            } catch (IncorrectMoveException | NotThisLobbyException | NotThisPlayerTurnException e) {
               returnInfo = NetPackage.ERROR;
               returnInfo.setArgument(e.getMessage());
            }
        }
        else{
            returnInfo = NetPackage.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private NetPackage setReady(boolean value){
        NetPackage returnInfo = NetPackage.READY;
        if(lobby != null){
                lobby.setReady(this, value);
                returnInfo.setArgument("Set ready");
        }
        else{
            returnInfo = NetPackage.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private NetPackage leave(){
        NetPackage returnInfo;
        if(lobby != null){
            lobby.removePlayer(this);
            lobby = null;
            returnInfo = NetPackage.LEAVE;
            returnInfo.setArgument("Left successfully");
        }
        else{
            returnInfo = NetPackage.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private NetPackage joinLobby(int number) {
        NetPackage result;
        if(lobby != null){
            result = NetPackage.ERROR;
            result.setArgument("You already are in a game");
        }
        try{
            lobbyArray[number].addPlayer(this);
            lobby = lobbyArray[number];
            result = NetPackage.JOIN;
            result.setArgument("Joined successfully");
        } catch (LobbyFullException | NotThisLobbyException | IndexOutOfBoundsException e) {
            result = NetPackage.ERROR;
            result.setArgument(e.getMessage());
        }
        return result;
    }
    private NetPackage getPlayerArray() {
        NetPackage result;
        if(lobby != null){
            result = NetPackage.BOARD;
            result.setArgument(lobby.getCheckerArray());
        }
        else {
            result = NetPackage.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private NetPackage getColorArray(){
        NetPackage result;
        if(lobby != null){
            result = NetPackage.BOARD;
            result.setArgument(lobby.getColorArray());
        }
        else{
            result = NetPackage.ERROR;
            result.setArgument("Your are not in a lobby");
        }
        return result;
    }
    private NetPackage getPlayerInt(){
        NetPackage result;
        if(lobby != null){
            result = NetPackage.PLAYERINT;
            result.setArgument(lobby.getPlayerInt(this));
        }
        else {
            result = NetPackage.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private NetPackage updateLobbyArray(){
        lobbyArray = parent == null ? null : parent.getLobbyArray();
        NetPackage result = NetPackage.LOBBIES;
        result.setArgument(lobbyArray == null ? null : lobbyArray.length);
        return result;
    }
    private NetPackage skipTurn(){
        NetPackage result;
        if(lobby == null){
            result = NetPackage.ERROR;
            result.setArgument("You are not on lobby");
            return result;
        }
        try{
            lobby.skipTurn(this);
            result = NetPackage.SKIP;
        } catch (NotThisLobbyException e) {
            result = NetPackage.ERROR;
            result.setArgument("You are not in lobby");
        } catch (NotThisPlayerTurnException e) {
            result = NetPackage.ERROR;
            result.setArgument("You may only skip during your turn");
        }
        return result;
    }
    private NetPackage currentPlayer(){
        NetPackage result;
        if (lobby == null) {
            result = NetPackage.ERROR;
            result.setArgument("You are not in lobby");
        }
        else {
            result = NetPackage.CURRENT;
            result.setArgument(lobby.getPlayerInt(lobby.getCurrentPlayer()));
        }
        return result;
    }
    private void disconnect(){
        connected = false;
    }
    protected NetPackage parseCommand(NetPackage message){
         NetPackage result = NetPackage.ERROR;
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
                     NetPackage temp = NetPackage.ERROR;
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
        NetProtocolServer protocol = new SimpleNetProtocolFactory().getServerSide();
        try {
            System.out.println("User run loop started");
            protocol.setSocket(socket);
            System.out.println("User run loop started");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Exception");
            return;
        }

        while (connected){
            System.out.println("User still connected");
            try{
                if(boardReady) {
                    protocol.sendPackage(getColorArray());
                    boardReady = false;
                }
                if(protocol.refresh()){
                    NetPackage temp = protocol.retrievePackage();
                    temp = parseCommand(temp);
                    protocol.sendPackage(temp);
                }
                sleep(500);
            } catch (IOException | InterruptedException  /*InterruptedException*/ e) {
                e.printStackTrace();
            }

        }
        try{
            protocol.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

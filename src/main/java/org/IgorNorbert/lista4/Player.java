package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

public class Player implements Runnable {
    private String nickName = "default " + ThreadLocalRandom.current().nextInt(100);
    private Lobby lobby = null;
    private Lobby[] lobbyArray = null;
    private final Server parent;
    private final Socket socket;
    private boolean connected = true;
    NetProtocolServer protocol = null;
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
             returnInfo = new NetPackage();
             returnInfo.type = NetPackage.Type.MOVE;
            try {
               returnInfo.setArgument(lobby.moveChecker(oldX, oldY, newX, newY, this));
            } catch (IncorrectMoveException | NotThisLobbyException | NotThisPlayerTurnException e) {
               returnInfo = new NetPackage();
               returnInfo.type = NetPackage.Type.ERROR;
               returnInfo.setArgument(e.getMessage());
            }
        }
        else{
            returnInfo = new NetPackage();
            returnInfo.type = NetPackage.Type.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private NetPackage setReady(boolean value){
        NetPackage returnInfo = new NetPackage();
        returnInfo.type = NetPackage.Type.READY;
        if(lobby != null){
                lobby.setReady(this, value);
                returnInfo.setArgument("Set ready");
        }
        else{
            returnInfo = new NetPackage();
            returnInfo.type =NetPackage.Type.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private NetPackage leave(){
        NetPackage returnInfo;
        if(lobby != null){
            lobby.removePlayer(this);
            lobby = null;
            returnInfo = new NetPackage();
            returnInfo.type = NetPackage.Type.LEAVE;
            returnInfo.setArgument("Left successfully");
        }
        else{
            returnInfo = new NetPackage();
            returnInfo.type =NetPackage.Type.ERROR;
            returnInfo.setArgument("You are not in lobby");
        }
        return returnInfo;
    }
    private NetPackage joinLobby(int number) {
        NetPackage result;
        if(lobby != null){
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You already are in a game");
        }
        try{
            lobbyArray[number].addPlayer(this);
            lobby = lobbyArray[number];
            result = new NetPackage();
            result.type = NetPackage.Type.JOIN;
            result.setArgument("Joined successfully");
        } catch (LobbyFullException | NotThisLobbyException | IndexOutOfBoundsException e) {
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument(e.getMessage());
        }
        return result;
    }
    private NetPackage getColorArray() {
        NetPackage result;
        if(lobby != null){
            result = new NetPackage();
            result.type = NetPackage.Type.BOARD;
            result.setArgument(lobby.getColorArray());
        }
        else {
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private NetPackage getPlayerColor(){
        NetPackage result;
        if(lobby != null){
            result = new NetPackage();
            result.type = NetPackage.Type.PLAYERCOLOR;
            result.setArgument(lobby.getPlayerColor(this));
        }
        else {
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You are not in a lobby");
        }
        return result;
    }
    private NetPackage updateLobbyArray(){
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
    private NetPackage skipTurn(){
        NetPackage result;
        if(lobby == null){
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You are not on lobby");
            return result;
        }
        try{
            lobby.skipTurn(this);
            result = new NetPackage();
            result.type = NetPackage.Type.SKIP;
        } catch (NotThisLobbyException e) {
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You are not in lobby");
        } catch (NotThisPlayerTurnException e) {
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You may only skip during your turn");
        }
        return result;
    }
    private NetPackage winOrder() {
        Player[] temp = this.lobby.getWinnerLine();
        if (this.lobby != null && temp != null ) {
            String[] order = new String[temp.length];
            for (int i = 0; i < temp.length; i++) {
                order[i] = temp[i].getNickName();
            }
            return new NetPackage(NetPackage.Type.WINORDER, order);
        }
        return new NetPackage(NetPackage.Type.WINORDER, null);
    }
    private NetPackage playerList() {
        try {
            if (lobby != null) {
                return new NetPackage(NetPackage.Type.PlAYERLIST, (Serializable) lobby.getMap());
            }
        } catch (ClassCastException e) {
            e.printStackTrace();

        }
        return new NetPackage(NetPackage.Type.PlAYERLIST, null);
    }
    private NetPackage currentPlayer(){
        NetPackage result;
        if (lobby == null) {
            result = new NetPackage();
            result.type =NetPackage.Type.ERROR;
            result.setArgument("You are not in lobby");
        }
        else {
            result = new NetPackage();
            result.type = NetPackage.Type.CURRENTPLAYER;
            result.setArgument(lobby.getPlayerColor(lobby.getCurrentPlayer()));
        }
        return result;
    }
    private void disconnect(){
        connected = false;
        if(lobby != null){
            lobby.removePlayer(this);
        }
    }
    protected NetPackage parseCommand(NetPackage message){
         NetPackage result = new NetPackage();
         result.type =NetPackage.Type.ERROR;
         result.setArgument("Command not recognized");
         try{
             result = switch (message.type){
                 case JOIN -> joinLobby((int) message.getArgument());
                 case PLAYERCOLOR -> getPlayerColor();
                 case READY -> setReady((boolean) message.getArgument());
                 case LEAVE, FORFEIT -> leave();
                 case BOARD -> getColorArray();
                 case MOVE -> {
                     int[] temp = (int[])message.getArgument();
                     yield moveChecker(temp[0], temp[1], temp[2], temp[3]);
                 }
                 case DISCONNECT -> {
                     NetPackage temp = new NetPackage();
                     temp.type = NetPackage.Type.DISCONNECT;
                     temp.setArgument("Close correctly");
                     protocol.sendPackage(temp);
                     connected = false;
                     yield null;
                 }
                 case LOBBIES -> updateLobbyArray();
                 case SKIP -> skipTurn();
                 case CURRENTPLAYER -> currentPlayer();
                 case WINORDER -> winOrder();
                 case PlAYERLIST -> playerList();
                 case ERROR, CONNECT, RETURN -> {
                     NetPackage temp = new NetPackage();
                     temp.type = NetPackage.Type.ERROR;
                     temp.setArgument("This command is reserved for server");
                     yield temp;
                 }
             };
         } catch (IndexOutOfBoundsException e) {
             result.setArgument("The int array should be size of 4");
         } catch (ClassCastException e){
             result.setArgument("This command has incorrect parameter");
         } catch (IOException e) {
             e.printStackTrace();
         }
        return result;
    }

    @Override
    public void run() {
        protocol = new SimpleNetProtocolFactory().getServerSide();
        try {
            protocol.setSocket(socket);
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Exception");
            return;
        }

        while (connected) {
            try {
                if(protocol.waitForPackage()) {
                    NetPackage temp = protocol.retrievePackage();
      //              System.out.println(temp.type);
                    temp = parseCommand(temp);
                    if(!connected){break;} // This is stupid, refactor it
                    protocol.sendPackage(temp);
                }
              //  sleep(500);
            } catch (IOException e) {
                if(lobby != null){
                    lobby.removePlayer(this);
                }
                return;
            } /*catch (InterruptedException e) {
                connected = false;
                if(lobby != null){
                    lobby.removePlayer(this);
                }
            }*/
        }
        try{
            protocol.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getNickName() {
        return this.nickName;
    }
}

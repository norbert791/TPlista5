package org.IgorNorbert.lista4;
//TODO:
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
//TODO: apply state pattern to make managing NetPackages easier
public class ClientLogic {
    private Socket socket;
    NetProtocolClient protocol = null;
    private final Queue<NetPackage> nextCommand = new ConcurrentLinkedQueue<>();
    private volatile boolean inLobby = false;
    private volatile boolean gameStarted = false;
    private volatile boolean connected = false;
    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    private final UserInterface userInterface;
    public ClientLogic(){
        this.userInterface = new MainFrame(this);
    }
    public void connect(String address){
        if(connected){
            userInterface.printError("Already Connected");
            return;
        }
        try {
            int portNumber = 7777;
            socket = new Socket(address, portNumber);
            pool.execute(new ConnectionThread());
        } catch (IOException e) {
            userInterface.printError("Failed at connecting to a server");
            socket = null;
        }
    }
    private class ConnectionThread implements Runnable {
        @Override
        public void run() {

            protocol = new SimpleNetProtocolFactory().getClientSide();
            try {
                protocol.setSocket(socket);
                connected = true;
            } catch (IOException e){
                e.printStackTrace();
                return;
            }

            while(connected) {
        //        System.out.println("Thread working");
                try{
                    if(protocol.isReady()){
                        NetPackage temp = protocol.retrievePackage();
                        decodePackage(temp);
                    }
                    if(!nextCommand.isEmpty()){
                        protocol.sendPackage(nextCommand.remove());
                    }
                    else{
                        if(inLobby){
                            if(gameStarted) {
                                NetPackage temp = new NetPackage();
                                temp.type = NetPackage.Type.CURRENTPLAYER;
                                nextCommand.add(temp);
                                temp = new NetPackage();
                                temp.type = NetPackage.Type.BOARD;
                                nextCommand.add(temp);
                                temp = new NetPackage();
                                temp.type = NetPackage.Type.PLAYERCOLOR;
                                nextCommand.add(temp);
                            }
                            else{
                                NetPackage temp = new NetPackage();
                                temp.type = NetPackage.Type.CURRENTPLAYER;
                                nextCommand.add(temp);
                            }
                        }
                    }
                    sleep(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    connected = false;
                    userInterface.printError("Connection error occurred");
                }
            }
            NetPackage temp = new NetPackage();
            temp.type = NetPackage.Type.DISCONNECT;
            try {
                protocol.sendPackage(temp);
                protocol.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private synchronized void decodePackage(NetPackage result){
        try {
            switch (result.type) {
                case ERROR ->
                    userInterface.printError((String) result.getArgument());
                case CURRENTPLAYER -> {
                    if(result.getArgument() == null){
                        gameStarted = false;
                    }
                    else{
                        gameStarted = true;
                        userInterface.setCurrentPlayer((Color) result.getArgument());
                    }
                }
                case MOVE -> userInterface.nextMove((boolean) result.getArgument());
                case BOARD -> userInterface.printBoard((Color[][]) result.getArgument());
                case PLAYERCOLOR -> userInterface.setPlayerColor((Color) result.getArgument());
                case JOIN -> this.inLobby = true;
                case LEAVE -> this.inLobby = false;
            }
        } catch (ClassCastException e) { //This exception shouldn't occur
            e.printStackTrace();
        }
    }

    public void moveChecker(int oldX, int oldY, int newX, int newY){
        if (!connected) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.MOVE;
        temp.setArgument(new int[]{oldX, oldY, newX, newY});
        nextCommand.add(temp);
    }
    public void setReady(boolean value){
        if (!connected) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.READY;
        temp.setArgument(value);
        nextCommand.add(temp);
       // System.out.println("Client queued ready");
    }
    public void leave(){
        if (!connected) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.LEAVE;
        nextCommand.add(temp);;
    }
    public void join(int number){
        if (!connected) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.JOIN;
        temp.setArgument(number);
        nextCommand.add(temp);
    }
    public void skip(){
        if (!connected) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.SKIP;
        nextCommand.add(temp);
    }
    public void disconnect(){
        if (!connected) {
            return;
        }
        this.connected = false;
    }

    /**
     * Private enum class used for managing state of the connection
     */
    private enum State{
        DISCONNECTED,
        CONNECTED,
        IN_LOBBY,
        GAME_ONGOING,
        GAME_FINISHED;

        /**
         * Used to send requests for data relevant for the current state of the client.
         */
        public void fetch_status(){

        }
    }
    public static void main(String[] args){
        final ClientLogic temp = new ClientLogic();
    }
}

package org.IgorNorbert.lista4;
//TODO: The protocol could improved by queueing server requests, each request would be uniquely identified,
//TODO: and then handling each response independently
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ClientLogic {
    private final int portNumber = 7777;
    private Socket socket;
    private volatile Integer playerInt = null;
    private volatile Queue<NetPackage> nextCommand = new LinkedList<>();
    private boolean inLobby = false;
    private boolean gameStarted = false;
    private boolean connected = true;
    private String errorMessage;
    private volatile Integer playerMove;
    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    private final UserInterface userInterface;
    public ClientLogic(){
        this.userInterface = new MainFrame(this);
    }
    public void connect(String address){
        try {
            socket = new Socket(address, portNumber);
            pool.execute(new ConnectionThread());
        } catch (IOException e) {
            errorMessage = "Connection failed";
            e.printStackTrace();
            socket = null;
        }
    }
    private class ConnectionThread implements Runnable {
        @Override
        public void run() {

            final NetProtocolClient protocol = new SimpleNetProtocolFactory().getClientSide();
            try {
                protocol.setSocket(socket);
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
                  //              System.out.println("GAME ISN FUCKING ");
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
                }
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
                case READY -> System.out.println((String) result.getArgument());
            }
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public synchronized void moveChecker(int oldX, int oldY, int newX, int newY){
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.MOVE;
        temp.setArgument(new int[]{oldX, oldY, newX, newY});
        nextCommand.add(temp);
    }
    public synchronized void setReady(boolean value){
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.READY;
        temp.setArgument(value);
        nextCommand.add(temp);
       // System.out.println("Client queued ready");
    }
    public synchronized void leave(){
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.LEAVE;
        nextCommand.add(temp);;
    }
    public synchronized void join(int number){
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.JOIN;
        temp.setArgument(number);
        nextCommand.add(temp);
    }
    public Integer getPlayerInt() {
        return playerInt;
    }
    public void skip(){
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.SKIP;
        nextCommand.add(temp);
    }
    public void disconnect(){
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.DISCONNECT;
        nextCommand.clear();
        nextCommand.add(temp); //TODO: this requires improvement -> not thread safe
        this.connected = false;
    }
    public static void main(String[] args){
        final ClientLogic temp = new ClientLogic();
    }
}

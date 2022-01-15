package org.IgorNorbert.lista4;
//TODO: The protocol could improved by queueing server requests, each request would be uniquely identified,
//TODO: and then handling each response independently
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientLogic {
    private final int portNumber = 7777;
    private Socket socket;
    private volatile Integer[][] arrayBuffer;
    private volatile Integer playerInt = null;
    private volatile Queue<NetPackage> nextCommand = new LinkedList<>();
    private String errorMessage = null;
    private Object returnValue = null;
    private boolean connected = true;
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
            System.out.println("Thread working");
            while(connected) {
                try{

                    if(protocol.refresh()){
                        NetPackage temp = protocol.retrievePackage();
                        decodePackage(temp);
                    }
                    if(nextCommand.size() > 0){
                        protocol.sendPackage(nextCommand.remove());
                    }
                    wait(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void decodePackage(NetPackage result){
        try {
            switch (result) {
                case ERROR ->
                    userInterface.printError((String) result.getArgument());
                case CURRENT -> userInterface.setCurrentPlayer((Color) result.getArgument());
                case MOVE -> userInterface.nextMove((boolean) result.getArgument());
                case BOARD -> userInterface.printBoard((Color[][]) result.getArgument());
                case PLAYERINT -> userInterface.setPlayerColor((Color) result.getArgument());
            }
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public synchronized void moveChecker(int oldX, int oldY, int newX, int newY){
        NetPackage temp = NetPackage.MOVE;
        temp.setArgument(new int[]{oldX, oldY, newX, newY});
        nextCommand.add(temp);
    }
    public synchronized void setReady(boolean value){
        NetPackage temp = NetPackage.READY;
        temp.setArgument(value);
        nextCommand.add(temp);
    }
    public synchronized void leave(){
        nextCommand.add(NetPackage.LEAVE);;
    }
    public synchronized void join(int number){
        NetPackage temp = NetPackage.JOIN;
        temp.setArgument(number);
        nextCommand.add(temp);
    }

    public Integer getPlayerInt() {
        return playerInt;
    }
    public Integer[][] getBoard(){
        return arrayBuffer;
    }
    public void skip(){
        nextCommand.add(NetPackage.SKIP);
    }
    public void disconnect(){
        nextCommand.clear();
        nextCommand.add(NetPackage.DISCONNECT); //TODO: this requires improvement -> not thread safe
        this.connected = false;
    }
    public static void main(String[] args){
        final ClientLogic temp = new ClientLogic();
    }
}

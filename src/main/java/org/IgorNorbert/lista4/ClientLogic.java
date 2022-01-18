package org.IgorNorbert.lista4;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
public class ClientLogic {
    private Socket socket;
    NetProtocolClient protocol = null;
    private State state = State.DISCONNECTED;
    private final Queue<NetPackage> nextCommand = new ConcurrentLinkedQueue<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    private final UserInterface userInterface;
    public ClientLogic(){
        this.userInterface = new MainFrame(this);
    }
    public void connect(String address){
        if(state != State.DISCONNECTED){
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
                transform(State.CONNECTED);
            } catch (IOException e){
                e.printStackTrace();
                return;
            }

            while (state != State.DISCONNECTED) {
               // System.out.println(state);
                try{
                    if (protocol.isReady()) {
                        NetPackage temp = protocol.retrievePackage();
                        decodePackage(temp);
                    }
                    if (!nextCommand.isEmpty()) {
                        protocol.sendPackage(nextCommand.remove());
                    }
                    else {
                        fetch_data();
                    }
                    sleep(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    transform(State.DISCONNECTED);
                    userInterface.printError("Connection error occurred");
                }
            }

            NetPackage temp = new NetPackage(NetPackage.Type.DISCONNECT);
            try {
                protocol.sendPackage(temp);
                protocol.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            userInterface.printStartScreen();
        }
    }

    private synchronized void decodePackage(NetPackage result){
        try {
            switch (result.type) {
                case ERROR ->
                    userInterface.printError((String) result.getArgument());
                case CURRENTPLAYER -> {
                    if(result.getArgument() != null){
                        transform(State.IN_GAME);
                        userInterface.setCurrentPlayer((Color) result.getArgument());
                    }
                }
                case MOVE -> userInterface.nextMove((boolean) result.getArgument());
                case BOARD -> userInterface.printBoard((Color[][]) result.getArgument());
                case PLAYERCOLOR -> userInterface.setPlayerColor((Color) result.getArgument());
                case JOIN -> transform(State.IN_LOBBY);
                case LEAVE -> transform(State.CONNECTED);
                case LOBBIES -> userInterface.printLobbyList((int[]) result.getArgument());
                case PlAYERLIST -> userInterface.printPlayers((Map<String, Color>)result.getArgument());
                case WINORDER -> userInterface.updateVictors((String[]) result.getArgument());
            }
        } catch (ClassCastException e) { //These exceptions shouldn't occur
            e.printStackTrace();
        }
    }

    public void moveChecker(int oldX, int oldY, int newX, int newY){
        if (state != State.IN_GAME) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.MOVE;
        temp.setArgument(new int[]{oldX, oldY, newX, newY});
        nextCommand.add(temp);
    }
    public void setReady(boolean value){
        if (state != State.IN_LOBBY) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.READY;
        temp.setArgument(value);
        nextCommand.add(temp);
       // System.out.println("Client queued ready");
    }
    public void leave(){
        if (state != State.IN_GAME) {
            return;
        }
        nextCommand.clear();
        nextCommand.add(new NetPackage(NetPackage.Type.LEAVE));
        transform(State.CONNECTED);
    }
    public void join(int number) {
        if (state != State.CONNECTED) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.JOIN;
        temp.setArgument(number);
        nextCommand.add(temp);
    }
    public void skip(){
        if (state != State.IN_GAME) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.SKIP;
        nextCommand.add(temp);
    }
    public void disconnect() {
        if (state != State.DISCONNECTED) {
            return;
        }
        transform(State.DISCONNECTED);
    }

    /**
     * Private enum class used for managing state of the connection
     */
    private enum State {
        DISCONNECTED,
        CONNECTED,
        IN_LOBBY,
        IN_GAME,
    }
    /**
     * Used to send continual requests for data relevant for the current state of the client.
     */
    private void fetch_data() throws IOException{
        switch (state) {
            case CONNECTED -> nextCommand.add(new NetPackage(NetPackage.Type.LOBBIES));
            case IN_LOBBY -> {
                nextCommand.add(new NetPackage(NetPackage.Type.CURRENTPLAYER));
                nextCommand.add(new NetPackage(NetPackage.Type.PlAYERLIST));
            }
            case IN_GAME -> {
                nextCommand.add(new NetPackage(NetPackage.Type.CURRENTPLAYER));
                nextCommand.add(new NetPackage(NetPackage.Type.BOARD));
                nextCommand.add(new NetPackage(NetPackage.Type.WINORDER));
            }
            case DISCONNECTED -> {}
        }
    }
    /**
     * Certain actions request need to be made only once AFTER state shift.
     * This method is meant to manage both state and sending those requests.
     * @param newState The next state of the client
     */
    private void transform(final State newState) {
        switch (this.state) {
            case DISCONNECTED:
            case IN_GAME:
                if (newState == State.CONNECTED) {
                    nextCommand.add(new NetPackage(NetPackage.Type.LOBBIES));
                }
                break;
            case IN_LOBBY:
                switch (newState) {
                    case IN_GAME -> {
                        nextCommand.add(new NetPackage(NetPackage.Type.PLAYERCOLOR));
                        nextCommand.add(new NetPackage(NetPackage.Type.PlAYERLIST));
                    }
                    case CONNECTED -> nextCommand.add(new NetPackage(NetPackage.Type.LOBBIES));
                }
                break;
            case CONNECTED:
                if(newState == State.IN_LOBBY) {
                    userInterface.printLobby();
                    nextCommand.clear();
                }
                break;
                default:
                break;
        }
        this.state = newState;
    }
    public static void main(String[] args){
        final ClientLogic temp = new ClientLogic();
    }
}

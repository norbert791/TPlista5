package org.Norbert.lista5.ClientLogic;
import org.Norbert.lista5.ClientUI.MainFrame;
import org.Norbert.lista5.ClientUI.UserInterface;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;
import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Protocol.NetPackage;
import org.Norbert.lista5.Protocol.NetProtocolClient;
import org.Norbert.lista5.Protocol.SimpleNetProtocolFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
public class ClientLogic {
    /**
     * Socket used for exchanging data with server.
     */
    private Socket socket;
    /**
     * Interface used for communication with server.
     */
    private NetProtocolClient protocol = null;
    /**
     * field describing current state of the client.
     */
    private State state = State.DISCONNECTED;
    /**
     * Queue interface used for storing commands,
     * this way any user action will reach the server
     * unless the Queue is emptied explicitly.
     */
    private final Queue<NetPackage> nextCommand = new ConcurrentLinkedQueue<>();
    /**
     * Executor service used for managing client threads.
     */
    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    /**
     * User interface. Used for sending information to user.
     */
    private final UserInterface userInterface;
    /**
     * Default constructor.
     */
    public ClientLogic() {
        this.userInterface = new MainFrame(this);
    }
    /**
     * Connects to server at address.
     * @param address of server the client should connect to
     */
    public void connect(final String address) {
        if (state != State.DISCONNECTED) {
            userInterface.printError("Already Connected");
            return;
        }
        try {
            final int portNumber = 7777;
            socket = new Socket(address, portNumber);
            pool.execute(new ConnectionThread());
        } catch (IOException e) {
            userInterface.printError("Failed at connecting to a server");
            socket = null;
        }
    }

    /**
     * private class used for sending requests and processing replies.
     */
    private class ConnectionThread implements Runnable {
        @Override
        public void run() {
            //Creating client-side implementation of protocol
            protocol = new SimpleNetProtocolFactory().getClientSide();
            try {
                protocol.setSocket(socket);
                transform(State.CONNECTED);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            //While client is connected process replies and send requests
            while (state != State.DISCONNECTED) {
                try {
                    System.out.println(state);
                    if (protocol.isReady()) {
                        NetPackage temp = protocol.retrievePackage();
                        decodePackage(temp);
                    }
                    if (!nextCommand.isEmpty()) {
                        protocol.sendPackage(nextCommand.remove());
                    } else {
                        fetchData();
                    }
                    //Used to regulate intervals at which
                    // client will send request for server state
                    //TODO this may cause some bottle-neck. Better refactor it.
                    sleep(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    transform(State.DISCONNECTED);
                    userInterface.printError("Connection error occurred");
                }
            }
            //Send info to server that we are leaving & close connection
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

    /**
     * Private method used for responding to reply from server.
     * @param result NetPackage containing feed back from server
     */
    private synchronized void decodePackage(final NetPackage result) {
        try {
            switch (result.type) {
                case ERROR ->
                    userInterface.printError((String) result.getArgument());
                case CURRENTPLAYER -> {
                    if (result.getArgument() != null) {
                        transform(State.IN_GAME);
                        userInterface.setCurrentPlayer(
                                (Color) result.getArgument());
                    }
                }
                case MOVE -> userInterface.nextMove(
                        (boolean) result.getArgument());
                case BOARD -> userInterface.printBoard(
                        (Color[][]) result.getArgument());
                case PLAYERCOLOR -> userInterface.setPlayerColor(
                        (Color) result.getArgument());
                case JOIN -> transform(State.IN_LOBBY);
                case LEAVE -> transform(State.CONNECTED);
                case LOBBIES -> userInterface.printLobbyList(
                        (int[]) result.getArgument());
                case PlAYERLIST -> userInterface.printPlayers(
                        (Map<String, Color>) result.getArgument());
                case WINORDER -> userInterface.updateVictors(
                        (String[]) result.getArgument());
                case BOARDMASK -> {
                    userInterface.setMask((boolean[][]) result.getArgument());
                    userInterface.printLobby();
                }
                case FETCH_HISTORY -> userInterface.printHistory(
                        (GameDescriptionRecord[]) result.getArgument());
                case FETCH_GAME_RECORD -> userInterface.printGameRecord(
                        (GameRecord) result.getArgument());
                //TODO: printError should not be used this way
                case REGISTER -> userInterface.printError("Registered successfully");
                case SIGN -> transform(State.SIGNED_IN);
            }
        } catch (ClassCastException e) { //These exceptions shouldn't occur
            e.printStackTrace();
        }
    }

    /**
     * Send request to move checker from old to new position.
     * @param oldX old x coordinate
     * @param oldY old y coordinate
     * @param newX old x coordinate
     * @param newY old y coordinate
     */
    public void moveChecker(
            final int oldX, final int oldY, final int newX, final int newY) {
        if (state != State.IN_GAME) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.MOVE;
        temp.setArgument(new int[]{oldX, oldY, newX, newY});
        nextCommand.add(temp);
    }

    /**
     * Notifies server that client is ready to play.
     * @param value true iff player is ready
     */
    public void setReady(final boolean value) {
        //Can be sent only if player is in lobby
        // (otherwise server should ignore it or return ERROR
        // so it is pointless to send it in the first place)
        if (state != State.IN_LOBBY) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.READY;
        temp.setArgument(value);
        nextCommand.add(temp);
    }

    public void register(String email, String user, String password) {
        nextCommand.add(new NetPackage(NetPackage.Type.REGISTER, new String[]{email, user, password}));

    }
    public void logIn(String email, String password) {
        nextCommand.add(new NetPackage(NetPackage.Type.SIGN, new String[]{email, password}));
    }

    /**
     * Send Leave request.
     */
    public void leave() {
        if (state != State.IN_GAME) {
            return;
        }
        nextCommand.clear();
        nextCommand.add(new NetPackage(NetPackage.Type.LEAVE));
        transform(State.CONNECTED);
    }

    /**
     * Joins to lobby whose id is equal to number.
     * @param number id of lobby the client should connect to
     */
    public void join(final int number) {
        if (state != State.CONNECTED) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.JOIN;
        temp.setArgument(number);
        nextCommand.add(temp);
    }

    /**
     * Sends requests to server that player wants to skip their turn.
     */
    public void skip() {
        if (state != State.IN_GAME) {
            return;
        }
        NetPackage temp = new NetPackage();
        temp.type = NetPackage.Type.SKIP;
        nextCommand.add(temp);
    }

    /**
     * Sends requests to server that players
     * wants to safely disconnect.
     */
    public void disconnect() {
        if (state == State.DISCONNECTED) {
            return;
        }
        transform(State.DISCONNECTED);
    }

    public void fetchHistory() {
        if (state == State.CONNECTED) {
            NetPackage temp = new NetPackage();
            temp.type = NetPackage.Type.FETCH_HISTORY;
            nextCommand.add(temp);
        }
    }

    public void fetchGame(int gameId) {
        if (state == State.CONNECTED) {
            nextCommand.add(new NetPackage(NetPackage.Type.FETCH_GAME_RECORD, gameId));
        }
    }

    /**
     * Private enum class used for managing state of the connection.
     */
    private enum State {
        /**
         * Client is disconnected.
         */
        DISCONNECTED,
        /**
         * Client is connected.
         */
        CONNECTED,
        /**
         * The user has been authorized
         */
        SIGNED_IN,
        /**
         * Client is in lobby.
         */
        IN_LOBBY,
        /**
         * Client is in ongoing game.
         */
        IN_GAME,
    }
    /**
     * Used to send continual requests for data
     * relevant for the current state of the client.
     */
    private void fetchData() throws IOException {
        switch (state) {
            case SIGNED_IN -> nextCommand.add(
                    new NetPackage(NetPackage.Type.LOBBIES));
            case IN_LOBBY -> {
                nextCommand.add(new NetPackage(NetPackage.Type.CURRENTPLAYER));
                nextCommand.add(new NetPackage(NetPackage.Type.PlAYERLIST));
            }
            case IN_GAME -> {
                nextCommand.add(new NetPackage(NetPackage.Type.CURRENTPLAYER));
                nextCommand.add(new NetPackage(NetPackage.Type.BOARD));
                nextCommand.add(new NetPackage(NetPackage.Type.WINORDER));
            }
            default -> { }
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
                if (newState == State.CONNECTED) {
                    userInterface.askForCredentials();
                }
                break;
            case IN_GAME:
                if (newState == State.CONNECTED) {
                    nextCommand.add(new NetPackage(NetPackage.Type.LOBBIES));
                }
                break;
            case IN_LOBBY:
                switch (newState) {
                    case IN_GAME -> {
                        nextCommand.add(new NetPackage(
                                NetPackage.Type.PLAYERCOLOR));
                        nextCommand.add(new NetPackage(
                                NetPackage.Type.PlAYERLIST));
                    }
                    case CONNECTED -> nextCommand.add(
                            new NetPackage(NetPackage.Type.LOBBIES));
                }
                break;
            case CONNECTED:
                if (newState == State.SIGNED_IN) {
                    nextCommand.clear();
                }
                break;
            case SIGNED_IN:
                if (newState == State.IN_LOBBY) {
                    nextCommand.add(new NetPackage(NetPackage.Type.BOARDMASK));
                }
                break;
                default:
                break;
        }
        this.state = newState;
    }

    /**
     * Initializes client.
     * @param args do nothing
     */
    public static void main(final String[] args) {
        final ClientLogic temp = new ClientLogic();
    }
}

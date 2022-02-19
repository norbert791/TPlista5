package org.Norbert.lista5.Server;
//TODO More advanced management
// can be implemented e.g. kicking players, removing lobbies, ...
//TODO some values should be customizable rather than hardcoded
//TODO Server is locking when waiting for connection.
import org.Norbert.lista5.Database.GameLogger;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.SimpleRetriever;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class used for server initialization and thread management.
 */
public class Server {
    private final ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

    /**
     * Collection of Lobbies.
     */
    private final Collection<Lobby> lobbyList = new ArrayList<>();
    /**
     * Collection of Players.
     */
    private final Collection<Player> playersList = new ArrayList<>();
    /**
     * Max number of players at the given time.
     */
    private final int maxNumberOfConnections = 36;
    /**
     * Executor service for efficient thread management.
     */
    private final ExecutorService pool =
            Executors.newFixedThreadPool(maxNumberOfConnections);
    /**
     * Port number of connections.
     */
    private final int portNumber;
    /**
     * Status of the server.
     */
    private boolean running = true;

    /**
     * Constructor.
     * @param portNumber port used for connection
     */
    public Server(final int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * Get array of all lobbies.
     * @return Array of references to Lobbies
     */
    public Lobby[] getLobbyArray() {
        return lobbyList.toArray(new Lobby[0]);
    }

    /**
     * Starts the server main thread.
     * @throws IOException if unpredicted io occurred during start
     */
    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(portNumber);
        while (running) {
           Socket connection = socket.accept();
           if (playersList.size() < maxNumberOfConnections) {
               Player temp = new Player(this, connection,
                       (SimpleRetriever) context.getBean("SimpleRetriever"));
               pool.execute(temp);
               System.out.println("User connected");
           }
        }
    }

    /**
     * Ads a number of lobbies.
     * @param numberOfLobbies number of lobbies to add
     * @throws IllegalArgumentException if given number is below 1
     */
    public void addLobby(final int numberOfLobbies)
            throws IllegalArgumentException {
        if (numberOfLobbies < 0) {
            throw new IllegalArgumentException(
                    "numberOfLobbies has to be greater than 0, given: "
                            + numberOfLobbies);
        } else {
            for (int i = 0; i < numberOfLobbies; i++) {
                lobbyList.add(new Lobby((GameLogger) context.getBean("SimpleLogger")));
            }
        }
    }

    /**
     * Used for starting the server.
     * @param args do nothing
     */
    public static void main(final String[] args) {
        final Server server = new Server(7777);
        server.addLobby(6);
        System.out.println("Server is running");
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server has finished");
    }
}

package org.IgorNorbert.lista4;
//TODO More advanced management can be implemented e.g. kicking players, removing lobbies, ...
//TODO some values should be customizable rather than hardcoded
//TODO Server is locking when waiting for connection.
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Collection<Lobby> lobbyList = new ArrayList<>();
    private final Collection<Player> playersList = new ArrayList<>();
    private final int maxNumberOfConnections = 36;
    private final ExecutorService pool = Executors.newFixedThreadPool(maxNumberOfConnections);
    private final int portNumber ;
    public Server(int portNumber){
        this.portNumber = portNumber;
    }
    public Lobby[] getLobbyArray(){
        return lobbyList.toArray(new Lobby[0]);
    }
    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(portNumber);
        boolean running = true;
        while(true){
           Socket connection = socket.accept();
           if(playersList.size() < maxNumberOfConnections){
               Player temp = new Player( this, connection);
               pool.execute(temp);
           }
        }
    }
    public void addLobby(int numberOfLobbies) throws IllegalArgumentException{
        if(numberOfLobbies < 0){
            throw new IllegalArgumentException("numberOfLobbies has to be greater than 0, given: " + numberOfLobbies);
        }
        else {
            for (int i = 0; i < numberOfLobbies; i++){
                lobbyList.add(new Lobby());
            }
        }
    }
    public static void main(String[] args){
        final Server server = new Server(7777);
        System.out.println("Server is running");
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server has finished");
    }
}

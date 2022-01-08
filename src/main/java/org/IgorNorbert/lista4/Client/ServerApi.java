package org.IgorNorbert.lista4.Client;


/**
 * Set of commands that are used for communication with server
 */
public interface ServerApi {
    /**
     * Connect to a server using address, port and name
     * @param address Address of the server you are connecting to
     * @param port Port number
     * @param name Your nickname
     * @return true if and only if connection was established correctly
     */
    boolean connect(String address, int port, String name);

    /**
     * Join the room (lobby) given by number:
     * @param roomNumber The number of lobby
     * @return true iff Lobby was joined successfully
     */
    boolean joinGame(int roomNumber);

    /**
     * Get the array of lobbies
     * @return 2d array containing lobby numbers and number of players inside
     */
    int[][] getLobbyList();

    /**
     * Sets your ready status in lobby
     * @throws NotInLobbyException iff you are not in a lobby
     */
    void setReady() throws NotInLobbyException;

    /**
     * Moves checker on
     * @return true iff next move is possible
     * @throws NotYourTurnException
     */
    boolean moveChecker() throws NotYourTurnException;

    /**
     * Return the number you were assigned in a game (your in-match id)
     * @return The number that marks the positions of your checker on board @see getBoard
     * @throws NotInLobbyException
     */
    int getNumber() throws NotInLobbyException;

    /**
     * Get the 2d array representing the board
     * @return 2d array where each field is a positive number iff a player's checker has been put there
     * @throws NotInLobbyException iff you are not in lobby
     */
    int[][] getBoard() throws NotInLobbyException;

    /**
     * Surrender the game
     * @throws NotYourTurnException iff it is not your game
     */
    void forfeit() throws NotYourTurnException;

    /**
     * Skips your turn
     * @throws NotYourTurnException if you didn't make a turn
     */
    void skip() throws NotYourTurnException;

    /**
     * Disconnects you from the current server
     */
    void disconnect();

    /**
     * Make you leave your current lobby
     */
    void leaveLobby();
}

package org.IgorNorbert.lista4;

/**
 * Exception indicating that there is no room for another player in lobby.
 */
public class LobbyFullException extends Exception {
    /**
     * Constructor.
     * @param message Message of the exception
     */
    public LobbyFullException(final String message) {
        super(message);
    }
}

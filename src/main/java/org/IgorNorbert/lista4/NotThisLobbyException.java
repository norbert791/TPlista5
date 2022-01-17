package org.IgorNorbert.lista4;

/**
 * Exception used for indicating that player is not in a lobby or
 * is in another one.
 */
public class NotThisLobbyException extends Throwable {
    /**
     * Constructor.
     * @param message message of the exception
     */
    public NotThisLobbyException(final String message) {
        super(message);
    }
}

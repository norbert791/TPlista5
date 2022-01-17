package org.IgorNorbert.lista4;

/**
 * Exception used for indicating certain action may
 * not be done in given player's turn.
 */
public class NotThisPlayerTurnException extends Exception {
    /**
     * Constructor.
     * @param message message of the exception
     */
    public NotThisPlayerTurnException(final String message) {
        super(message);
    }
}

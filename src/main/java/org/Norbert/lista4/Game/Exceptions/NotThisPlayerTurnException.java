package org.Norbert.lista4.Game.Exceptions;

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

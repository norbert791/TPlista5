package org.Norbert.lista4.Game.Exceptions;

/**
 * Exception indicating forbidden move.
 */
public class IncorrectMoveException extends Exception {
    /**
     * Constructor.
     * @param message the message of this exception
     */
    public IncorrectMoveException(final String message) {
        super(message);
    }
}

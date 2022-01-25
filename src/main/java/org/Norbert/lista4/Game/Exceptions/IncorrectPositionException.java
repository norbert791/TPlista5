package org.Norbert.lista4.Game.Exceptions;

/**
 * Exception indicating improper checker moving or placing.
 */
public class IncorrectPositionException extends Exception {
    /**
     * Constructor.
     * @param message Message of the exception
     */
    public IncorrectPositionException(final String message) {
        super(message);
    }
}

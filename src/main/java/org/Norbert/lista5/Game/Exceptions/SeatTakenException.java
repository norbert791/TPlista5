package org.Norbert.lista5.Game.Exceptions;

/**
 * The exception indicating that chosen seat is occupied.
 */
public class SeatTakenException extends Exception {
    /**
     * Constructor.
     * @param message message of exception
     */
    public SeatTakenException(final String message) {
        super(message);
    }
}

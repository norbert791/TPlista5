package org.IgorNorbert.lista4;

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

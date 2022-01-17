package org.IgorNorbert.lista4;

/**
 * The exception indicating that the number of players is against the rules.
 */
public class IncorrectNumberOfPlayersException extends Exception {
    /**
     * Constructor.
     * @param message Message of the exception
     */
    public IncorrectNumberOfPlayersException(final String message) {
        super(message);
    }
}

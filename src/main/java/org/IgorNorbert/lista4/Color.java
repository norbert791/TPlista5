package org.IgorNorbert.lista4;

/**
 * Enum class used for players and checkers distinction
 */
public enum Color {
    RED,
    GREEN,
    BLUE,
    CYAN,
    MAGENTA,
    YELLOW;

    /**
     * Maps colors to integer values
     * @param color color whose value we want to retrieve
     * @return the Integer of chosen color or null if parameter was null
     */
    public static Integer toInteger(Color color){
        if(color == null){
            return null;
        }
        return switch (color){
            case RED -> 1;
            case GREEN -> 2;
            case BLUE -> 3;
            case CYAN -> 4;
            case MAGENTA -> 5;
            case YELLOW -> 6;
        };
    }
}

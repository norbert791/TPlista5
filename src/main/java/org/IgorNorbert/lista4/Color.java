package org.IgorNorbert.lista4;

public enum Color {
    RED,
    GREEN,
    BLUE,
    CYAN,
    MAGENTA,
    YELLOW;
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

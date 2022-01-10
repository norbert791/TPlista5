package org.IgorNorbert.lista4;

import java.lang.Math;

/**
 * Implementation of class Board.
 */
public class ArrayBoard implements Board {
    private final int height = 17;
    private final int length = 13;
    private final int midHeight = height / 2;
    private final int midLength = length / 2;
    private final int starSize = 4;
    private final OptionalColor[][] fields;
    public ArrayBoard() {
        fields = new OptionalColor[height][length * 2 - 1];
        for (int i = 0; i < starSize; i++) {
            for (int j = 0; j <= i * 2; j += 2) {
                fields[i][length - 1 - i + j] = new OptionalColor();
                fields[i][length - 1 - i + j].color = null;
            }
        }
        for (int i = 4; i <= midHeight; i++) {
            for (int j = i - starSize;
                j < length * 2 - 1 - (i - starSize); j += 2) {
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
        for (int i = midHeight + 1; i < length; i++) {
            for (int j = starSize - 1 - (i -  midHeight - 1);
                j < length * 2 - midLength + 1 + (i - midHeight + 1); j += 2){
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
        for (int i = length; i < height; i++) {
            for (int j = midHeight + 1 + (i - length);
                 j < height - 1 - (i - length); j += 2) {
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
    }
    @Override
    public boolean moveChecker(final int oldX, final int oldY,
                               final int newX, final int newY)
            throws IncorrectMoveException {
        final int deltaX = newX - oldX;
        final int deltaY = newY - oldY;
        final int absDeltaX = Math.abs(deltaX);
        final int absDeltaY = Math.abs(deltaY);
        boolean result = false;
        try {
             if (fields[newY][newX] == null || fields[newY][newX].color != null
                    || fields[oldY][oldX] == null || fields[oldY][oldX].color == null ){
                throw new IncorrectMoveException("The move is not possible");
            }
            else if(absDeltaX == absDeltaY && absDeltaY == 1){
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX].color = null;
             }
            else if(absDeltaX == absDeltaY && absDeltaY == 2 && fields[oldY + deltaY / 2][oldX + deltaX / 2].color != null){
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX].color = null;
                result = isAnotherJumpPossible(oldX, oldY, newX, newY);
             }
            else if(absDeltaY == 0 && absDeltaX == 2){
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX] = null;
             }
            else if(absDeltaY == 0 && absDeltaX == 4 && fields[newY][oldX + deltaX / 2].color !=null){
                fields[newY][newX] = fields[oldY][oldX];
                fields[oldY][oldX] = null;
                result = isAnotherJumpPossible(oldX, oldY, newX, newY);
             }
            else{
                throw new IncorrectMoveException("Incorrect coordinates");
             }
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectMoveException("Index is out of bound");
        }
        return result;
    }

    @Override
    public Color getCheckerColor(int x, int y) throws IncorrectPositionException {
        try {
            return fields[y][x] == null ? null : fields[y][x].color;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectPositionException("Coordinates out of board");
        }
    }

    @Override
    public void addChecker(int x, int y, Color color) throws IncorrectPositionException {
        try{
            if(fields[y][x] == null || fields[y][x].color != null){
                throw new IncorrectPositionException("The field is not available");
            }
            fields[y][x].color = color;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectPositionException("This field is out of the board");
        }
    }

    @Override
    public void removeChecker(int x, int y) throws IncorrectPositionException {
        try{
            if(fields[y][x] == null || fields[y][x].color == null){
                throw new IncorrectPositionException("No color found at the given position");
            }
            fields[y][x].color = null;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectPositionException("This field is out of the board");
        }
    }

    @Override
    public void setCorner(Seat seat, Color color) {
        switch (seat) {
            case NORTH:
                for (int i = 0; i < starSize; i++){
                    for( int j = 0; j <= i * 2; j+=2 ){
                        fields[i][length - 1 - i + j].color = color;
                    }
                }
                break;
            case NORTHEAST:
                for (int i = 4; i < 8; i++){
                    for (int j = 18 + (i - 4); j < 25 - (i - 4); j+= 2){
                        fields[i][j].color = color;
                    }
                }
                break;
            case NORTHWEST:
                for (int i = 4; i < 9; i++){
                    for (int j = i - 4; j < 7 - (i - 4); j+= 2){
                        fields[i][j].color = color;
                    }
                }
                break;
            case SOUTH:
                for (int i = length; i < height; i++) {
                    for (int j = midHeight + 1 + (i - length); j < height - 1 - (i - length); j += 2) {
                        fields[i][j].color = color;
                    }
                }
                break;
            case SOUTHEAST:
                for (int i = 9; i < 13; i++){
                    for (int j = 21 - (i - 9); j < 22 + (i - 9); j+=2){
                        fields[i][j].color = color;
                    }
                }
                break;
            case SOUTHWEST:
                for (int i = 9; i < 13; i++){
                    for (int j = 3 - (i - 9) ; j < 4 + (i - 9); j+= 2){
                        fields[i][j].color = color;
                    }
                }
                break;
        }
    }
    @Override
    public boolean checkCorner(Seat seat, Color color){
        switch (seat) {
            case NORTH:
                for (int i = 0; i < starSize; i++){
                    for( int j = 0; j <= i * 2; j+=2 ){
                        if(fields[i][length - 1 - i + j].color != color){
                            return false;
                        }
                    }
                }
                break;
            case NORTHEAST:
                for (int i = 4; i < 8; i++){
                    for (int j = 18 + (i - 4); j < 25 - (i - 4); j+= 2){
                        if(fields[i][j].color != color){
                            return false;
                        }
                    }
                }
                break;
            case NORTHWEST:
                for (int i = 4; i < 8; i++){
                    for (int j = i - 4; j < 7 - (i - 4); j+= 2){
                        if(fields[i][j].color != color){
                            return false;
                        }
                    }
                }
                break;
            case SOUTH:
                for (int i = length; i < height; i++) {
                    for (int j = midHeight + 1 + (i - length); j < height - 1 - (i - length); j += 2) {
                        if(fields[i][j].color != color){
                            return false;
                        }
                    }
                }
                break;
            case SOUTHEAST:
                for (int i = 9; i < 13; i++){
                    for (int j = 21 - (i - 9); j < 22 + (i - 9); j+= 2){
                        if(fields[i][j].color != color){
                            return false;
                        }
                    }
                }
                break;
            case SOUTHWEST:
                for (int i = 9; i < 13; i++){
                    for (int j = 3 - (i - 9) ; j < 4 + (i - 9); j+= 2){
                        if(fields[i][j].color != color){
                            return false;
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public Color[][] getCheckerColorArray() {
        Color[][] result = new Color[height][length * 2 - 1];
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length * 2 - 1; j++){
                result[i][j] = fields[i][j] == null ? null : fields[i][j].color;
            }
        }
        return result;
    }
    private class OptionalColor{
        public Color color;
    }

    public OptionalColor[][] getFields(){
        return fields;
    }

    private boolean isAnotherJumpPossible(int oldX, int oldY, int currentX, int currentY){
        if (currentX -4 >= 0 && fields[currentY][currentX - 4] != null &&
                fields[currentY][currentX - 4].color == null &&
                fields[currentY][currentX - 2].color != null &&
                oldX != currentX - 4 && oldY != currentY){
            return true;
        }
        else if (currentX + 4 <= length * 2 - 1 && fields[currentX + 4][currentY] != null &&
                fields[currentY][currentX + 4].color == null &&
                fields[currentY][currentX + 2].color != null &&
                oldX != currentX + 4 && oldY != currentY){
            return true;
        }
        else if(currentX - 2 >= 0 && currentY - 2 >= 0 && fields[currentY - 2][currentX - 2] != null &&
                fields[currentY - 2][currentX - 2].color == null && fields[currentY - 1][currentX - 1].color != null &&
                oldX != currentX - 2 && oldY != currentY - 2){
                return true;
        }
        else if(currentX - 2 >= 0 && currentY + 2 < height && fields[currentY + 2][currentX - 2] != null &&
                fields[currentY + 2][currentX - 2].color == null && fields[currentY + 1][currentX - 1].color!= null &&
                oldX != currentX - 2 && oldY != currentY + 2){
            return true;
        }
        else if(currentX + 2 < length * 2 - 1 && currentY - 2 >= 0 && fields[currentY - 2][currentX + 2] != null &&
                fields[currentY - 2][currentX + 2].color == null && fields[currentY - 1][currentX + 1].color != null &&
                oldX != currentX + 2 && oldY != currentY - 2){
            return true;
        }
        else return currentX + 2 < length * 2 - 1 && currentY + 2 < height && fields[currentY + 2][currentX + 2] != null &&
                    fields[currentY + 2][currentX + 2].color == null && fields[currentY + 1][currentX + 1].color != null &&
                    oldX != currentX + 2 && oldY != currentY + 2;
    }

  /*  public static void main(String args[]){
        ArrayBoard temp = new ArrayBoard();
        for (Seat seat : Seat.values()
             ) {
            temp.setCorner(seat, Color.CYAN);
        }
        for (OptionalColor[] array : temp.getFields()
             ) {
            for (OptionalColor field : array
                 ) {
                if (field == null){
                    System.out.print("#");
                }
                else if (field.color != null){
                    System.out.print("*");
                }
                else{
                    System.out.print("o");
                }
            }
            System.out.print("\n");
        }
    }*/
}

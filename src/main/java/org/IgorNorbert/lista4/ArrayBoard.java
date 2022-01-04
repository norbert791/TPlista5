package org.IgorNorbert.lista4;

import java.lang.Math;

public class ArrayBoard implements Board{
    private final int height = 17;
    private final int length = 13;
    private final int midHeight = height / 2;
    private final int midLength = length / 2;
    private final int starSize = 4;
    private final OptionalColor[][] fields;
    public ArrayBoard(){
        fields = new OptionalColor[height][length * 2 - 1];
        for(int i = 0; i < starSize; i++){
            for( int j = 0; j <= i * 2; j+=2 ){
                fields[i][length - 1 - i + j] = new OptionalColor();
                fields[i][length - 1 - i + j].color = null;
            }
        }
        for(int i = 4; i <= midHeight; i++){
            for(int j = i - starSize; j < length * 2 - 1 - (i - starSize); j += 2){
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
        for(int i = midHeight + 1; i < length; i++){
            for(int j = starSize - 1 - (i -  midHeight - 1); j < length * 2 - midLength + 1 + (i - midHeight + 1); j+=2){
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
        for(int i = length; i < height; i++ ) {
            for( int j = midHeight + 1 + (i - length); j < height - 1 - (i - length); j += 2){
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
    }
    @Override
    public boolean moveChecker(int oldX, int oldY, int newX, int newY) throws IncorrectMoveException {
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
                result = true;
             }
            else if(absDeltaY == 0 && absDeltaX == 2){
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX] = null;
             }
            else if(absDeltaY == 0 && absDeltaX == 4 && fields[newY][oldX + deltaX / 2].color !=null){
                fields[newY][newX] = fields[oldY][oldX];
                fields[oldY][oldX] = null;
                result = true;
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
    public Color getCheckerColor(int X, int Y) throws IncorrectPositionException {
        try {
            if(fields[Y][X] == null){
                throw new IncorrectPositionException("No color found at the given position");
            }
            return fields[Y][X].color;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectPositionException("Coordinates out of board");
        }
    }

    @Override
    public void addChecker(int X, int Y, Color color) throws IncorrectPositionException {
        try{
            if(fields[Y][X] == null || fields[Y][X].color != null){
                throw new IncorrectPositionException("The field is not available");
            }
            fields[Y][X].color = color;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectPositionException("This field is out of the board");
        }
    }

    @Override
    public void removeChecker(int X, int Y) throws IncorrectPositionException {
        try{
            if(fields[Y][X] == null || fields[Y][X].color == null){
                throw new IncorrectPositionException("No color found at the given position");
            }
            fields[Y][X].color = null;
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IncorrectPositionException("This field is out of the board");
        }
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

/*    public static void main(String args[]){
        ArrayBoard temp = new ArrayBoard();
        for (OptionalColor[] array : temp.getFields()
             ) {
            for (OptionalColor field : array
                 ) {
                if (field == null){
                    System.out.print("#");
                }
                else{
                    System.out.print("*");
                }
            }
            System.out.print("\n");
        }
    }*/
}

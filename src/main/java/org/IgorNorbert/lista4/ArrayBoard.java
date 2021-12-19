package org.IgorNorbert.lista4;

public class ArrayBoard implements Board{
    private final int height = 17;
    private final Color[][] colorArray;
    public ArrayBoard(){
       colorArray = new Color[height][];
       for (int i = 0; i < 4; i++){
           colorArray[i] = new Color[1 + i];
       }
       for (int i = 4; i < 9; i++){
            colorArray[i] = new Color[13 + 4 - i];
       }
       for (int i = 9; i < 13; i++){
           colorArray[i] = new Color[10 + i - 9];
       }
       for (int i = 13; i < height; i++) {
           colorArray[i] = new Color[4 + 13 - i];
       }
    }

    @Override
    public boolean moveChecker(int OldX, int OldY, int newX, int newY){
        return false;
    }

    @Override
    public Color getCheckerColor(int X, int Y) throws IncorrectPositionException {
        try{
            return colorArray[Y][X];
        }
        catch (IndexOutOfBoundsException e){
            throw new IncorrectPositionException();
        }
    }

    @Override
    public void addChecker(int X, int Y, Color color) throws IncorrectPositionException {
        try {
            colorArray[Y][X] = color;
        }
        catch (IndexOutOfBoundsException e){
            throw new IncorrectPositionException();
        }
    }

    @Override
    public void removeChecker(int X, int Y) throws IncorrectPositionException {
        try {
            colorArray[Y][X] = null;
        }
        catch (IndexOutOfBoundsException e){
            throw new IncorrectPositionException();
        }
    }

    @Override
    public Color[][] getCheckerColorArray(){
        return colorArray;
    }
}

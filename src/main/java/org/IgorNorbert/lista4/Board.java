package org.IgorNorbert.lista4;

/**
 * The interface for interacting with the board. The board is a rectangle with
 * a horizontal illegal space of size 1 between each two fields
 */
public interface Board {

    /**
     * Moves checker from point a to point b
     * @param oldX the X coordinate of the chosen checker
     * @param oldY the Y coordinate of the chosen checker
     * @param newX the X coordinate of the new checker
     * @param newY the Y coordinate of the new checker
     * @return true if another move is possible to make (e.g. jump was made)
     * @throws IncorrectMoveException thrown if there is no checker at the given position,
     * new position is occupied or illegal.
     */
    boolean moveChecker(int oldX, int oldY, int newX, int newY) throws IncorrectMoveException;

    /**
     * Gets the color of the checker at the given position
     * @param X x coordinate
     * @param Y y coordinate
     * @return The color of checker at the given position
     * @throws IncorrectPositionException if position is of the board size
     */
    Color getCheckerColor(int X, int Y) throws IncorrectPositionException;

    /**
     * Add checker at the chosen position
     * @param X x coordinate
     * @param Y y coordinate
     * @param color chosen color
     * @throws IncorrectPositionException if chose position is illegal or out of bound
     */
    void addChecker(int X, int Y, Color color) throws IncorrectPositionException;

    /**
     * Removes checker from given position
     * @param X x coordinate
     * @param Y y coordinate
     * @throws IncorrectPositionException if position is out of the bound or
     * there was no checker at the given position
     */
    void removeChecker(int X, int Y) throws IncorrectPositionException;

    /**
     * puts the appropriate number of checkers at the chosen corner of the board
     * @param seat the corner where checkers should be placed
     * @param color
     */
    void setCorner(Seat seat, Color color);

    /**
     * Checks whether all the corner's positions are occupied by the checkers of chosen color
     * @param seat Side (corner) of the board which is to be to check
     * @param color The color we use for check
     * @return true if chosen corner of the board is occupied by checkers of color param
     */
    boolean checkCorner(Seat seat, Color color);

    /**
     * Return the array containing the checkers of chosen color
     * @return array containing colors at the positions where are checkers of respective colors
     * and null fields where there are no checkers or the field is illegal.
     */
    Color[][] getCheckerColorArray();
}

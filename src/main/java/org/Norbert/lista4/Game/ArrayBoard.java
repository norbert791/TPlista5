package org.Norbert.lista4.Game;

import org.Norbert.lista4.Game.Exceptions.IncorrectMoveException;
import org.Norbert.lista4.Game.Exceptions.IncorrectPositionException;

import java.lang.Math;

/**
 * Implementation of class Board.
 */
public class ArrayBoard implements Board {
    /**
     * height of the board.
     */
    private final int height = 17;
    /**
     * length of the board (maximum number of checkers in the longest row).
     */
    private final int length = 13;
    /**
     * half of the height.
     */
    private final int midHeight = height / 2;
    /**
     * half of the length.
     */
    private final int midLength = length / 2;
    /**
     * Size of the star's arm.
     */
    private final int starSize = 4;
    /**
     * Array of the board's fields.
     */
    private final OptionalColor[][] fields;

    /**
     * Constructs empty ArrayBoard.
     */
    public ArrayBoard() {
        fields = new OptionalColor[height][length * 2 - 1];
        for (int i = 0; i < starSize; i++) {
            for (int j = 0; j <= i * 2; j += 2) {
                fields[i][length - 1 - i + j] = new OptionalColor();
                fields[i][length - 1 - i + j].color = null;
            }
        }
        for (int i = starSize; i <= midHeight; i++) {
            for (int j = i - starSize;
                j < length * 2 - 1 - (i - starSize); j += 2) {
                fields[i][j] = new OptionalColor();
                fields[i][j].color = null;
            }
        }
        for (int i = midHeight + 1; i < length; i++) {
            for (int j = starSize - 1 - (i -  midHeight - 1);
                j < length * 2 - midLength + 1 + (i - midHeight + 1); j += 2) {
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

    /**
     * Moves checker from old position to the new one.
     * @param oldX the X coordinate of the chosen checker
     * @param oldY the Y coordinate of the chosen checker
     * @param newX the X coordinate of the new checker
     * @param newY the Y coordinate of the new checker
     * @return true iff another move is possible
     * @throws IncorrectMoveException if at least one of the following is true:
     * there is no checker at the chosen field,
     * the new position is occupied or illegal
     */
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
                    || fields[oldY][oldX] == null || fields[oldY][oldX].
                     color == null) {
                throw new IncorrectMoveException("The move is not possible");
            } else if (absDeltaX == absDeltaY && absDeltaY == 1) {
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX].color = null;
            } else if (absDeltaX == absDeltaY && absDeltaY == 2
                     && fields[oldY + deltaY / 2]
                     [oldX + deltaX / 2].color != null) {
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX].color = null;
                result = isAnotherJumpPossible(oldX, oldY, newX, newY);
            } else if (absDeltaY == 0 && absDeltaX == 2) {
                fields[newY][newX].color = fields[oldY][oldX].color;
                fields[oldY][oldX] = null;
             } else if (absDeltaY == 0 && absDeltaX == starSize
                     && fields[newY][oldX + deltaX / 2]
                     .color != null) {
                fields[newY][newX] = fields[oldY][oldX];
                fields[oldY][oldX] = null;
                result = isAnotherJumpPossible(oldX, oldY, newX, newY);
             } else {
                throw new IncorrectMoveException("Incorrect coordinates");
             }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new IncorrectMoveException("Index is out of bound");
        }
        return result;
    }

    /**
     * retrieves the color of the checker at the chosen position.
     * @param x x coordinate
     * @param y y coordinate
     * @return color of the checker at the chosen position
     * @throws IncorrectPositionException thrown iff chosen position
     * was out of the board boundary
     */
    @Override
    public Color getCheckerColor(final int x, final int y)
            throws IncorrectPositionException {
        try {
            return fields[y][x] == null ? null : fields[y][x].color;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IncorrectPositionException("Coordinates out of board");
        }
    }

    /**
     * Adds a checker at the chosen position.
     * @param x x of the chosen position
     * @param y y of the chosen position
     * @param color chosen color
     * @throws IncorrectPositionException
     */
    @Override
    public void addChecker(final int x, final int y, final Color color)
            throws IncorrectPositionException {
        try {
            if (fields[y][x] == null || fields[y][x].color != null) {
                throw new IncorrectPositionException(
                        "The field is not available");
            }
            fields[y][x].color = color;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IncorrectPositionException(
                    "This field is out of the board");
        }
    }

    /**
     * removes checker from the chosen position.
     * @param x x coordinate
     * @param y y coordinate
     * @throws IncorrectPositionException
     * thrown if there is no checker at the chosen position
     * or the field was out of bounds
     */
    @Override
    public void removeChecker(final int x, final int y)
            throws IncorrectPositionException {
        try {
            if (fields[y][x] == null || fields[y][x].color == null) {
                throw new IncorrectPositionException(
                        "No color found at the given position");
            }
            fields[y][x].color = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IncorrectPositionException(
                    "This field is out of the board");
        }
    }

    /**
     * Puts appropriate number of checkers of given color
     * at the corner corresponding to given seat.
     * @param seat The corner where checkers should be placed
     * @param color The color of checkers
     */
    @Override
    public void setCorner(final Seat seat, final Color color) {
        switch (seat) {
            case NORTH:
                for (int i = 0; i < starSize; i++) {
                    for (int j = 0; j <= i * 2; j += 2) {
                        fields[i][length - 1 - i + j].color = color;
                    }
                }
                break;
            case NORTHEAST:
                for (int i = starSize; i < midHeight; i++) {
                    for (int j = height + 1 + (i - starSize);
                         j < length * 2 - 1 - (i - starSize); j += 2) {
                        fields[i][j].color = color;
                    }
                }
                break;
            case NORTHWEST:
                for (int i = starSize; i < starSize * 2 + 1; i++) {
                    for (int j = i - starSize;
                         j < midLength + 1 - (i - starSize); j += 2) {
                        fields[i][j].color = color;
                    }
                }
                break;
            case SOUTH:
                for (int i = length; i < height; i++) {
                    for (int j = midHeight + 1 + (i - length);
                         j < height - 1 - (i - length); j += 2) {
                        fields[i][j].color = color;
                    }
                }
                break;
            case SOUTHEAST:
                for (int i = midHeight + 1; i < length; i++) {
                    for (int j = height + starSize - (i - midHeight - 1);
                         j < height + starSize + 1 + (i - midHeight - 1);
                         j += 2) {
                        fields[i][j].color = color;
                    }
                }
                break;
            case SOUTHWEST:
                for (int i = midHeight + 1; i < length; i++) {
                    for (int j = starSize - 1 - (i - length + starSize);
                         j < starSize + (i - length + starSize); j += 2) {
                        fields[i][j].color = color;
                    }
                }
                break;
            default:
        }
    }

    /**
     * Checks whether all the fields in given corner of the boards
     * are occupied by checkers of chosen color.
     * @param seat Side (corner) of the board which is to be to check
     * @param color The color we use for check
     * @return true iff all the corner's fields
     * are occupied by checkers of chosen color
     */
    @Override
    public boolean checkCorner(final Seat seat, final Color color) {
        switch (seat) {
            case NORTH:
                for (int i = 0; i < starSize; i++) {
                    for (int j = 0; j <= i * 2; j += 2) {
                        if (fields[i][length - 1 - i + j].color != color) {
                            return false;
                        }
                    }
                }
                break;
            case NORTHEAST:
                for (int i = starSize; i < length - starSize - 1; i++) {
                    for (int j = height + 1 + (i - starSize);
                         j < length * 2 - 1 - (i - starSize); j += 2) {
                        if (fields[i][j].color != color) {
                            return false;
                        }
                    }
                }
                break;
            case NORTHWEST:
                for (int i = starSize; i < length - starSize; i++) {
                    for (int j = i - starSize;
                         j < starSize * 2 - 1 - (i - starSize); j += 2) {
                        if (fields[i][j].color != color) {
                            return false;
                        }
                    }
                }
                break;
            case SOUTH:
                for (int i = length; i < height; i++) {
                    for (int j = midHeight + 1 + (i - length);
                         j < height - 1 - (i - length); j += 2) {
                        if (fields[i][j].color != color) {
                            return false;
                        }
                    }
                }
                break;
            case SOUTHEAST:
                for (int i = midHeight + starSize; i < length; i++) {
                    for (int j = length * 2 - starSize
                            - 1 - (i - length + starSize);
                         j < length * 2 - starSize + (i - length + starSize);
                         j += 2) {
                        if (fields[i][j].color != color) {
                            return false;
                        }
                    }
                }
                break;
            case SOUTHWEST:
                for (int i = length - starSize; i < length; i++) {
                    for (int j = starSize - 1 - (i - length + starSize);
                         j < starSize + (i - length + starSize); j += 2) {
                        if (fields[i][j].color != color) {
                            return false;
                        }
                    }
                }
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Returns the Array representing the board, where fields are either null.
     * or equal to color representing the checker at the corresponding position
     * @return Array of colors
     */
    @Override
    public Color[][] getCheckerColorArray() {
        Color[][] result = new Color[height][length * 2 - 1];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length * 2 - 1; j++) {
                result[i][j] = fields[i][j] == null ? null : fields[i][j].color;
            }
        }
        return result;
    }

    @Override
    public boolean[][] getMask() {
        boolean[][] result = new boolean[height][length * 2 - 1];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length * 2 - 1; j++) {
                result[i][j] = fields[i][j] != null;
            }
        }
        return result;
    }

    /**
     * Class representing the board's fields.
     * Null if the field is off-limits, non-null if
     * the fields corresponds to a board
     */
    private class OptionalColor {
        /**
         * Color of the optionalColor.
         */
        public Color color;
    }

    /**
     * Return the internal representation of the board.
     * @return Array containing inner representation of this board
     */
    public OptionalColor[][] getFields() {
        return fields;
    }

    private boolean isAnotherJumpPossible(
            final int oldX, final int oldY, final int currentX,
            final int currentY) {
        final int longJump = 4;
        if (currentX - longJump >= 0
                && fields[currentY][currentX - longJump] != null
                && fields[currentY][currentX - longJump].color == null
                && fields[currentY][currentX - 2].color != null
                && oldX != currentX - longJump && oldY != currentY) {
            return true;
        } else if (currentX + longJump < length * 2 - 1
                && fields[currentY][currentX + longJump] != null
                && fields[currentY][currentX + longJump].color == null
                && fields[currentY][currentX + 2].color != null
                && oldX != currentX + longJump && oldY != currentY) {
            return true;
        } else if (currentX - 2 >= 0 && currentY - 2 >= 0
                && fields[currentY - 2][currentX - 2] != null
                && fields[currentY - 2][currentX - 2].color == null
                && fields[currentY - 1][currentX - 1].color != null
                && oldX != currentX - 2 && oldY != currentY - 2) {
            return true;
        } else if (currentX - 2 >= 0 && currentY + 2 < height
                && fields[currentY + 2][currentX - 2] != null
                && fields[currentY + 2][currentX - 2].color == null
                && fields[currentY + 1][currentX - 1].color != null
                && oldX != currentX - 2 && oldY != currentY + 2) {
            return true;
        } else if (currentX + 2 < length * 2 - 1 && currentY - 2 >= 0
                && fields[currentY - 2][currentX + 2] != null
                && fields[currentY - 2][currentX + 2].color == null
                && fields[currentY - 1][currentX + 1].color != null
                && oldX != currentX + 2 && oldY != currentY - 2) {
            return true;
        } else {
            return currentX + 2 < length * 2 - 1 && currentY + 2 < height
                    && fields[currentY + 2][currentX + 2] != null
                    && fields[currentY + 2][currentX + 2].color == null
                    && fields[currentY + 1][currentX + 1].color != null
                    && oldX != currentX + 2 && oldY != currentY + 2;

        }
    }
}

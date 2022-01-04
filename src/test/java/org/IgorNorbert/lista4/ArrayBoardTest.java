package org.IgorNorbert.lista4;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayBoardTest {
    @Test
    public void putAndGetCheckerTest(){
        ArrayBoard board = new ArrayBoard();

        int[] rightCoordinates = new int[2];
        rightCoordinates[0] = 2;
        rightCoordinates[1] = 12;
        int[] wrongCoordinates = new int[2];
        wrongCoordinates[0] = 2;
        wrongCoordinates[1] = 11;

        assertDoesNotThrow(() -> board.addChecker(rightCoordinates[0],rightCoordinates[1],Color.GREEN),
                "Adding checker failed");
        assertDoesNotThrow(() -> Assertions.assertEquals(board.getCheckerColor(rightCoordinates[0], rightCoordinates[1]),
                Color.GREEN, "Checkers' colors don't match"), "Comparing Checkers failed");
        assertThrowsExactly(IncorrectPositionException.class, () ->
                board.addChecker(wrongCoordinates[0],wrongCoordinates[1], Color.CYAN),
                "This field should not be available");
        assertThrowsExactly(IncorrectPositionException.class, () ->
                board.addChecker(0,0,Color.BLUE), "This field is outside of board!");
        assertThrowsExactly(IncorrectPositionException.class, () ->
                board.addChecker(rightCoordinates[0], rightCoordinates[1],Color.RED),
                "This field should be occupied");
        assertThrowsExactly(IncorrectPositionException.class , () ->
                board.getCheckerColor(wrongCoordinates[0], wrongCoordinates[1]),
                "This field should not be available");
        assertThrowsExactly(IncorrectPositionException.class , () ->
                        board.getCheckerColor(0, 0),
                "This field should not be available");
    }
    @Test
    public void moveCheckerTest(){
        final ArrayBoard board = new ArrayBoard();
        //Adding and moving a checker. Then adding another one and jumping with him over the previous one
        assertDoesNotThrow(() -> board.addChecker(
                10, 12, Color.CYAN), "Adding a checker should work");
        assertDoesNotThrow(() -> board.moveChecker(
                10,12,11,13));
        assertDoesNotThrow(()-> board.addChecker(
                10,12, Color.RED), "Adding a checker should work");
        assertDoesNotThrow(() -> board.moveChecker(
                10, 12, 12,14), "This move should be legal");
        //Adding and trying to move checker on an either occupied or incorrect field
        assertDoesNotThrow(() -> board.addChecker(8,4, Color.MAGENTA),"This move should be legal");
        assertThrowsExactly(IncorrectMoveException.class, () ->
                board.moveChecker(8,4, 7, 3),"This move should be illegal");
        assertThrowsExactly(IncorrectMoveException.class, () ->
                board.moveChecker(8, 4, 10, 6), "This move should be illegal");
        assertDoesNotThrow(() -> board.addChecker(6,4, Color.MAGENTA),"This move should be legal");
        assertThrowsExactly(IncorrectMoveException.class, () ->
                board.moveChecker(8, 4, 6, 4));
        assertDoesNotThrow(() -> board.addChecker(4,4, Color.BLUE),"This move should be legal");
        assertThrowsExactly(IncorrectMoveException.class, () ->
                board.moveChecker(8,4, 4,4), "This move should be illegal");
        //Attempting to move checker that does not exist
        assertThrowsExactly(IncorrectMoveException.class, () ->
                board.moveChecker(14, 0, 13, 1), "This move should be illegal");
        //Checking return values of moveChecker function
        try{
            board.addChecker(0,4, Color.YELLOW);
            board.addChecker(3,5, Color.RED);
            assertFalse(board.moveChecker(0, 4, 2, 4), "False should be returned");
            Assertions.assertTrue(board.moveChecker(2,4,4,6), "True should be returned");
        }
        catch (IncorrectPositionException | IncorrectMoveException e){
            fail();
        }
    }
    @Test
    public void removeCheckerTest(){
        final ArrayBoard board = new ArrayBoard();
        assertThrowsExactly(IncorrectPositionException.class, () -> board.removeChecker(0,12));
        try{
            board.addChecker(12, 4, Color.RED);
            board.removeChecker(12, 4);
            assertNull(board.getCheckerColor(12, 4));
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }
    @Test
    public void getCheckerArrayTest(){
        final ArrayBoard board = new ArrayBoard();
        try{
            board.addChecker(12,4, Color.YELLOW);
            assertEquals(board.getCheckerColorArray()[4][12], Color.YELLOW);
        }
        catch (IncorrectPositionException e){
            fail(e.getMessage());
        }
    }
}

package org.IgorNorbert.lista4;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleMasterTest {
    @Test
    public void PlayerAddingTest(){
        final SimpleMaster master = new SimpleMaster();
        for(int i = 0; i < 6; i++){
            try{
                master.addPlayer();
            }
            catch (AllSeatsTakenException e){
                fail("Those seats should be empty");
            }
        }
        assertThrowsExactly(AllSeatsTakenException.class, master::addPlayer);
        assertThrowsExactly(SeatTakenException.class, () -> master.addPlayer(Seat.SOUTHWEST));
        final SimpleMaster master2 = new SimpleMaster();
        assertDoesNotThrow(() -> master2.addPlayer(Seat.NORTH));
        assertThrowsExactly(SeatTakenException.class, () -> master.addPlayer(Seat.NORTH));
    }
    @Test
    public void GameInitializationTest(){
        final SimpleMaster[] master = new SimpleMaster[6];
        Set<Color> players = new HashSet<>();
        try{
           master[0] = new SimpleMaster();
           players.add(master[0].addPlayer());
           players.add(master[0].addPlayer());
           assertDoesNotThrow(master[0]::startGame);
           Color[][] array = master[0].getCheckerArray();
           assertTrue( players.contains(array[3][9]));
           assertTrue(players.contains(array[14][10]));
           master[1] = new SimpleMaster();
           players.clear();
           for(int i = 0; i < 3 ; i++){
               players.add(master[1].addPlayer());
           }
           assertDoesNotThrow(master[1]::startGame);
           array = master[1].getCheckerArray();
           assertTrue(players.contains(array[4][20]));
           assertTrue(players.contains(array[4][2]));
           assertTrue(players.contains(array[14][10]));
           players.clear();
           master[2] = new SimpleMaster();
           for(int i = 0; i < 4; i++){
               players.add(master[2].addPlayer());
           }
           assertDoesNotThrow(master[2]::startGame);
           array = master[2].getCheckerArray();
           assertTrue(players.contains(array[5][21]));
           assertTrue(players.contains(array[5][3]));
           assertTrue(players.contains(array[12][0]));
           assertTrue(players.contains(array[11][19]));
           players.clear();
           master[3] = new SimpleMaster();
           for(int i = 0; i < 5 ; i++){
               players.add(master[3].addPlayer());
           }
           assertThrowsExactly(IncorrectNumberOfPlayersException.class, master[3]::startGame);
           players.clear();
           master[4] = new SimpleMaster();
           for (int i = 0; i < 6; i++){
               players.add(master[4].addPlayer());
           }
           assertTrue(players.contains(array[6][22]));
           assertTrue(players.contains(array[6][4]));
           assertTrue(players.contains(array[12][4]));
           assertTrue(players.contains(array[12][20]));
           players.clear();
           master[5] = new SimpleMaster();
           assertThrowsExactly(IncorrectNumberOfPlayersException.class, master[5]::startGame);
           master[5].addPlayer();
           assertThrowsExactly(IncorrectNumberOfPlayersException.class, master[5]::startGame);
        }
        catch (AllSeatsTakenException e) {
            fail("These places should be unoccupied");
        }
    }
    @Test
    public void clearBoardTest(){
        final SimpleMaster temp = new SimpleMaster();
        try{
            for(Seat seat : Seat.values()){
                temp.addPlayer(seat);
            }
            temp.startGame();
            temp.clearBoard();
            Color[][] array = temp.getCheckerArray();
            for (Color[] row : array){
                for (Color field : row){
                    assertNull(field);
                }
            }
        }
        catch (SeatTakenException e){
            fail("Adding player is not working");
        }
        catch (IncorrectNumberOfPlayersException e){
            fail("This game should initialize");
        }
    }
    @Test
    public void checkSkip(){
        final SimpleMaster master = new SimpleMaster();
        Color first;
        try {
            master.addPlayer();
            master.addPlayer();
            first = master.startGame();
        }
        catch (AllSeatsTakenException | IncorrectNumberOfPlayersException e){
            fail("There should be right number of players");
            return;
        }
        assertDoesNotThrow(() -> master.skipTurn(first));
        assertNotEquals(first, master.getCurrentPlayer());
    }
    @Test
    public void checkMove(){
        final SimpleMaster master = new SimpleMaster();
        try {
            Color player1 = master.addPlayer();
            Color player2 = master.addPlayer();
            Color currentPlayer = master.startGame();
            assertThrowsExactly(NotThisPlayerTurnException.class, () ->
                    master.moveChecker(9,3,10,4, currentPlayer == player1 ? player2 : player1));
            if(master.getCheckerArray()[3][9] == currentPlayer){
                assertDoesNotThrow(() -> master.moveChecker(
                        9, 3, 10, 4, currentPlayer));
            }
            else{
                assertDoesNotThrow(() -> master.moveChecker(
                        9, 13, 10, 12, currentPlayer));
            }
            assertNotSame(currentPlayer, master.getCurrentPlayer());
           final Color currentPlayer2 = master.getCurrentPlayer();
            assertDoesNotThrow(() -> master.skipTurn(currentPlayer2));
        }
        catch (AllSeatsTakenException e){
            fail("There should be unoccupied seats");
        }
        catch (IncorrectNumberOfPlayersException e){
            fail("The number of players should be correct");
        }
    }
    @Test
    public void checkMoveReturnValue(){
        final SimpleMaster master = new SimpleMaster();
        try {
            Color player1 = master.addPlayer();
            Color player2 = master.addPlayer();
            Color firstPlayer = master.startGame();
            final Color bottomPlayer =
                    master.getCheckerArray()[13][9] == player1 ? player1 : player2;
            final Color topPlayer = bottomPlayer == player1 ? player2 : player1;
            if (firstPlayer != bottomPlayer){
                master.skipTurn(firstPlayer);
            }
            //A sequence of moves that are meant to moving checkers and jumping over other checkers
            assertFalse(master.moveChecker(9,13,10,12, bottomPlayer),
                    "First move should be false");
            master.skipTurn(topPlayer);
            assertFalse(master.moveChecker(11, 13, 9, 11, bottomPlayer),
                    "Second move should be false");
            master.skipTurn(topPlayer);
            assertFalse(master.moveChecker(9,11,8,10, bottomPlayer),
                    "Third move should be false");
            master.skipTurn(topPlayer);
            assertFalse(master.moveChecker(12,14, 11, 13, bottomPlayer),
                    "Fourth move should be false");
            master.skipTurn(topPlayer);
            assertTrue(master.moveChecker(11,13,9,11, bottomPlayer),
                    "Fifth move should be true");
        }
        catch (AllSeatsTakenException | IncorrectNumberOfPlayersException e){
            fail("Adding players and starting game should work properly, " +
                    "testing theses function is outside of this test scope");
        }
        catch (NotThisPlayerTurnException e){
            fail("The order of the players should be appropriate -> " + e.getMessage());
        }
        catch (IncorrectMoveException e){
            fail("The moves should be legal");
        }
    }
    @Test
    public void checkForfeit(){
        final SimpleMaster master = new SimpleMaster();
        Color first, second;
        try{
            Color p1 = master.addPlayer();
            Color p2 = master.addPlayer();
            first = master.startGame();
            second = first == p1 ? p2 : p1;
            assertThrowsExactly(NotThisPlayerTurnException.class, () -> master.forfeit(second));
            assertDoesNotThrow(() -> master.forfeit(first));
            assertTrue(master.isFinished());
            assertEquals(master.getWinner()[0], second);
        }
        catch (AllSeatsTakenException | IncorrectNumberOfPlayersException e){
            fail("These exceptions should not occur, refer to respective tests");
        }
    }
    @Test
    public void detectsWin(){
        final SimpleMaster master;
        Color[] players = new Color[3];
        Color bottom = null;
        try {
            Field board = SimpleMaster.class.getDeclaredField("board");
            master = new SimpleMaster();
            players[0] = master.addPlayer();
            players[1] = master.addPlayer();
            players[2] = master.addPlayer();
            master.startGame();
            board.setAccessible(true);
            Board temp = (Board) board.get(master);
            for( Color player : players ){
                if (temp.getCheckerColor(11, 13) == player){
                    bottom = player;
                    break;
                }
            }
            temp.setCorner(Seat.NORTH, bottom);
            temp.removeChecker(9,3);
            temp.addChecker(10,4, bottom);
            Color currentPlayer = master.getCurrentPlayer();
            while(currentPlayer != bottom){
                master.skipTurn(currentPlayer);
                currentPlayer = master.getCurrentPlayer();
            }
            master.moveChecker(10,4,9,3,currentPlayer);
            assertFalse(master.isFinished());
            assertEquals(master.getWinner()[0], currentPlayer);
            assertEquals(master.getWinner().length,1);
        }
        catch (NoSuchFieldException e){
            fail("This field should be present");
        }
        catch (AllSeatsTakenException | IncorrectNumberOfPlayersException
                | IncorrectPositionException | NotThisPlayerTurnException
                | IncorrectMoveException e){
            fail("The game should be prepared correctly");
        }
        catch (IllegalAccessException e){
            fail("Access was supposed to be set public within the test scope");
        }
    }
}





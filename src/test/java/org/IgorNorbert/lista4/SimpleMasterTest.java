package org.IgorNorbert.lista4;

import org.junit.internal.runners.statements.Fail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
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
        catch (AllSeatsTakenException e){
            fail("These places should be unoccupied");
        }

    }
}

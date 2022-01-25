package org.Norbert.lista4;

import org.Norbert.lista4.Game.Color;
import org.Norbert.lista4.Game.Exceptions.NotThisPlayerTurnException;
import org.Norbert.lista4.Server.Lobby;
import org.Norbert.lista4.Server.LobbyFullException;
import org.Norbert.lista4.Server.NotThisLobbyException;
import org.Norbert.lista4.Server.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {
    @Test
    public void addPlayerTest(){
        final Lobby test = new Lobby();
        Player[] players = new Player[7];
        for ( int i = 0; i < players.length; i++ ){
            players[i] = new Player();
        }
        assertThrowsExactly(IllegalArgumentException.class, () -> test.addPlayer(null));
        for(int i = 0; i < 6; i++){
            int finalI = i;
            assertDoesNotThrow(() -> test.addPlayer(players[finalI]));
        }
        assertThrowsExactly(NotThisLobbyException.class, ()-> test.addPlayer(players[0]));
        assertThrowsExactly(LobbyFullException.class, ()-> test.addPlayer(players[6]));
    }
    @Test
    public void removePlayerTest(){
        try {
            Field playerMap = Lobby.class.getDeclaredField("playerMap");
            Field readinessMap = Lobby.class.getDeclaredField("readinessMap");
            Field line = Lobby.class.getDeclaredField("forfeitLine");
            Lobby test = new Lobby();
            Player[] players = new Player[3];
            for (int i = 0; i < 3; i++){
                players[i] = new Player();
            }
            for (Player temp : players){
                test.addPlayer(temp);
            }
            playerMap.setAccessible(true);
            readinessMap.setAccessible(true);
            line.setAccessible(true);
            Map<Player, Color> temp1 = (Map<Player, Color>) playerMap.get(test);
            Map<Player, Boolean> temp2 = (Map<Player, Boolean>) readinessMap.get(test);
            Collection<Color> temp3 = (Collection<Color>) line.get(test);
            assertEquals(temp1.size(), 3);
            assertEquals(temp2.size(), 3);
            assertEquals(temp3.size(), 0);
            test.removePlayer(players[0]);
            assertEquals(temp1.size(),2);
            assertEquals(temp2.size(), 2);
            assertEquals(temp3.size(),0);
            test.addPlayer(players[0]);
            for( Player temp : players){
                test.setReady(temp, true);
            }
            Player surrenders = test.getCurrentPlayer() == players[2] ? players[0] : players[2];
            test.removePlayer(surrenders);
            assertEquals(temp1.size(),2);
            assertEquals(temp2.size(), 2);
            assertEquals(temp3.size(),1);
            test.skipTurn(test.getCurrentPlayer());
            test.skipTurn(test.getCurrentPlayer());
            assertEquals(temp3.size(), 0);
        }
        catch (NoSuchFieldException | LobbyFullException | NotThisLobbyException
                | IllegalAccessException | NotThisPlayerTurnException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void moveCheckerTest() {
        Player player1 = new Player();
        Player player2 = new Player();
        Lobby test = new Lobby();

        try {
            assertThrowsExactly(NotThisLobbyException.class, () ->
                    test.moveChecker(0, 0, 0, 0, new Player()));
            test.addPlayer(player1);
            test.setReady(player1, true);
            test.addPlayer(player2);
            test.setReady(player2, true);
        }
        catch (LobbyFullException | NotThisLobbyException e){
            e.printStackTrace();
        }
        Color[][] array = test.getCheckerArray();
        boolean [] booleanArray = {false, false, false, false , false, false};
        for(Color[] tempArray : array){
            for(Color temp : tempArray){
                if(temp != null){
                    booleanArray[Color.toInteger(temp)] = true;
                }
            }
        }
        int counter = 0;
        for (boolean temp : booleanArray){
            if(temp){
                counter ++;
            }
        }
        assertEquals(counter, 2);
        Player currentPlayer = test.getCurrentPlayer();
        if(Objects.equals(array[3][9], test.getPlayerColor(currentPlayer))){
           Assertions.assertDoesNotThrow(() -> test.moveChecker(9, 3,10,4,currentPlayer));
        }
        else{
            Assertions.assertDoesNotThrow(() -> test.moveChecker(9,13,8,12, currentPlayer));
        }
        assertDoesNotThrow(() -> test.removePlayer(test.getCurrentPlayer()));
        assertEquals(test.getWinnerLine()[0], currentPlayer);
    }
}

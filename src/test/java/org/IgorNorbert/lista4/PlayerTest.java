package org.IgorNorbert.lista4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class PlayerTest {

    @Test
    public void joinLobbyTest(){
        final Lobby[] lobby = {new Lobby()};
        final Player player1 = new Player();
        final Player player2 = new Player();
        Field field = null;
        Method join = null;
        try {
            field = Player.class.getDeclaredField("lobbyArray");
            join = Player.class.getDeclaredMethod("joinLobby", int.class);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            fail();
        }

        field.setAccessible(true);
        join.setAccessible(true);
        try {
            field.set(player1, lobby);
            field.set(player2, lobby);
            NetPackage temp = (NetPackage) join.invoke(player1,0);
            NetPackage temp2 = (NetPackage) join.invoke(player2, 1);
        //    assertEquals(temp, NetPackage.JOIN);
         //   assertEquals(temp2, NetPackage.ERROR);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void setReadyTest() {
        final Lobby[] lobby = {new Lobby()};
        final Player player1 = new Player();
        final Player player2 = new Player();
        Field field = null;
        Method setReady = null;
        Method join = null;
        try {
            field = Player.class.getDeclaredField("lobbyArray");
            setReady = Player.class.getDeclaredMethod("setReady", boolean.class);
            join = Player.class.getDeclaredMethod("joinLobby", int.class);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            fail();
        }

        field.setAccessible(true);
        setReady.setAccessible(true);
        join.setAccessible(true);
        try {
            field.set(player1, lobby);
            field.set(player2, lobby);
            NetPackage temp0 = (NetPackage) setReady.invoke(player1, true);
            //    assertEquals(temp0, NetPackage.ERROR);
            join.invoke(player1,0);
            join.invoke(player2,0);
            NetPackage temp = (NetPackage) setReady.invoke(player1,true);
            NetPackage temp2 = (NetPackage) setReady.invoke(player1, false);
         //   assertEquals(temp, NetPackage.READY);
         //   assertEquals(temp2, NetPackage.READY);
        }catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void leaveTest(){
        final Lobby lobby = new Lobby();
        final Player player1 = new Player();
        Field field = null;
        Method leave = null;
        try {
            field = Player.class.getDeclaredField("lobby");
            leave = Player.class.getDeclaredMethod("leave");
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            fail();
        }
        try{
            field.setAccessible(true);
            leave.setAccessible(true);
            NetPackage temp = (NetPackage) leave.invoke(player1);
            field.set(player1, lobby);
          //  assertEquals(temp, NetPackage.ERROR);
            temp = (NetPackage) leave.invoke(player1);
           // assertEquals(temp, NetPackage.LEAVE);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Test
    void testGetPlayerArray(){
        final Lobby lobby = new Lobby();
        final Player player1 = new Player();
        final Player player2 = new Player();
        Field field;
        Method ready;
        Method getBoard;
        Method getColor;
        try {
            field = Player.class.getDeclaredField("lobby");
            ready = Player.class.getDeclaredMethod("setReady", boolean.class);
            getBoard = Player.class.getDeclaredMethod("getColorArray");
            getColor = Player.class.getDeclaredMethod("getPlayerColor");
            field.setAccessible(true);
            ready.setAccessible(true);
            getBoard.setAccessible(true);
            getColor.setAccessible(true);
            field.set(player1, lobby);
            field.set(player2, lobby);
            lobby.addPlayer(player1);
            lobby.addPlayer(player2);
            ready.invoke(player1, true);
            ready.invoke(player2, true);
            NetPackage temp = (NetPackage) getBoard.invoke(player1);
            assertEquals(temp.getArgument().getClass(), Color[][].class);
            Color[][] temp2 = (Color [][]) temp.getArgument();
            assertNotNull(temp2[0][12]);
            assertNotNull(((Color) ((NetPackage) getColor.invoke(player1)).getArgument()));
        } catch (NoSuchFieldException | NoSuchMethodException |
                IllegalAccessException | LobbyFullException |
                NotThisLobbyException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Test
    void updateLobbyList(){
        Server server = new Server(7777);
        Player player = new Player(server, new Socket());
        try {
            Field field = Player.class.getDeclaredField("lobbyArray");
            Method method = Player.class.getDeclaredMethod("updateLobbyArray");
            field.setAccessible(true);
            method.setAccessible(true);
            assertEquals(((Lobby[])field.get(player)).length, 0);
            server.addLobby(5);
            assertEquals(((Lobby[])field.get(player)).length, 0);
            method.invoke(player);
            assertEquals(((Lobby[])field.get(player)).length,  5);
        } catch (NoSuchFieldException | InvocationTargetException |
                IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

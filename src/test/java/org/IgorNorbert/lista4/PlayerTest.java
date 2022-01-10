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
            Package temp = (Package) join.invoke(player1,0);
            Package temp2 = (Package) join.invoke(player2, 1);
            assertEquals(temp, Package.RETURN);
            assertEquals(temp2, Package.ERROR);
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
            Package temp0 = (Package) setReady.invoke(player1, true);
            assertEquals(temp0, Package.ERROR);
            join.invoke(player1,0);
            join.invoke(player2,0);
            Package temp = (Package) setReady.invoke(player1,true);
            Package temp2 = (Package) setReady.invoke(player1, false);
            assertEquals(temp, Package.RETURN);
            assertEquals(temp2, Package.RETURN);
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
            Package temp = (Package) leave.invoke(player1);
            field.set(player1, lobby);
            assertEquals(temp, Package.ERROR);
            temp = (Package) leave.invoke(player1);
            assertEquals(temp, Package.RETURN);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Test
    void testGetPlayerArrayAndInt(){
        final Lobby lobby = new Lobby();
        final Player player1 = new Player();
        final Player player2 = new Player();
        Field field;
        Method ready;
        Method getBoard;
        Method getInt;
        try {
            field = Player.class.getDeclaredField("lobby");
            ready = Player.class.getDeclaredMethod("setReady", boolean.class);
            getBoard = Player.class.getDeclaredMethod("getPlayerArray");
            getInt = Player.class.getDeclaredMethod("getPlayerInt");
            field.setAccessible(true);
            ready.setAccessible(true);
            getBoard.setAccessible(true);
            getInt.setAccessible(true);
            field.set(player1, lobby);
            field.set(player2, lobby);
            lobby.addPlayer(player1);
            lobby.addPlayer(player2);
            ready.invoke(player1, true);
            ready.invoke(player2, true);
            Package temp = (Package) getBoard.invoke(player1);
            assertEquals(temp.getArgument().getClass(), Integer[][].class);
            Integer[][] temp2 = (Integer [][]) temp.getArgument();
            assertTrue(temp2[0][12] > 0);
            assertTrue(((Integer)((Package)getInt.invoke(player1)).getArgument()) > 0);
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

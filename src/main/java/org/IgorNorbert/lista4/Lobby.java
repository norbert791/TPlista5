package org.IgorNorbert.lista4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Lobby {
    private GameMaster game;
    private final Map<Player, Color> playerMap = new HashMap<>();
    private final int maxNumberOfPlayers = 6;
    private final Map<Player, Boolean> readinessMap = new HashMap<>();
    private final Collection<Color> forfeitLine = new ArrayList<>();
    public synchronized void addPlayer(Player player) throws LobbyFullException, AlreadyInLobbyException {
        if(playerMap.size() > maxNumberOfPlayers){
            throw new LobbyFullException("The lobby is already full");
        }
        if (playerMap.containsKey(player))  {
            throw new AlreadyInLobbyException("You already are in this lobby");
        }
        if(game != null){
            throw new LobbyFullException("The game has already started");
        }
        playerMap.put(player, null);
        readinessMap.put(player, false);
    }
    public synchronized void removePlayer(Player player){
        if(!playerMap.containsKey(player)){
            return;
        }
        else if(game != null){
            forfeitLine.add(playerMap.get(player));
        }
        playerMap.remove(player);
        readinessMap.remove(player);
        updatePlayerLine();
    }
    private synchronized void updatePlayerLine(){
        try{
            if(forfeitLine.contains(game.getCurrentPlayer())){
                final Color color = game.getCurrentPlayer();
                game.forfeit(color);
            }
        }
        catch (NotThisPlayerTurnException e){
            System.out.println("The forfeit should be possible -> " + e.getMessage());
        }
    }
    private synchronized Color getCurrentPlayer(){
        return game == null ? null : game.getCurrentPlayer();
    }
    public synchronized boolean moveChecker(int oldX, int oldY, int newX, int newY, Player player){
        try{
            return game.moveChecker(oldX, oldY, newX, newY, playerMap.get(player));
        }
        catch (IncorrectMoveException e) {
            return true;
        }
        catch (NotThisPlayerTurnException e) {
            return false;
        }
    }
    public synchronized Integer[][] getCheckerArray(){
        final Color[][] colorArray = game.getCheckerArray();
        Integer[][] result = new Integer[colorArray.length][];
        for(int i = 0; i < colorArray.length; i++){
            result[i] = new Integer[colorArray[i].length];
            for(int j = 0; j < colorArray[i].length; j++){
                result[i][j] = switch (colorArray[i][j]){
                    case RED -> 1;
                    case GREEN -> 2;
                    case BLUE -> 3;
                    case CYAN -> 4;
                    case MAGENTA -> 5;
                    case YELLOW -> 6;
                };
            }
        }
        return result;
    }
    public synchronized void setReady(Player player, boolean value) {
        if(readinessMap.containsValue(player)){
            readinessMap.replace(player, value);
        }
    }
}

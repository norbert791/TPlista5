package org.IgorNorbert.lista4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the lobby
 */
public class Lobby {
    private volatile GameMaster game;
    private final int maxNumberOfPlayers = 6;
    private final Map<Player, Color> playerMap = new HashMap<>();
    private final Map<Player, Boolean> readinessMap = new HashMap<>();
    private final Collection<Color> forfeitLine = new ArrayList<>();
    private final Collection<Player> winnersLine = new ArrayList<>();
    public synchronized void addPlayer(Player player)
            throws LobbyFullException, NotThisLobbyException, IllegalArgumentException {
        if(player == null){
            throw new IllegalArgumentException("The player must not be null");
        }
        if (playerMap.containsKey(player))  {
            throw new NotThisLobbyException("You already are in this lobby");
        }
        else if(playerMap.size() >= maxNumberOfPlayers){
            throw new LobbyFullException("The lobby is already full");
        }
        else if(game != null){
            throw new LobbyFullException("The game has already started");
        }
        playerMap.put(player, null);
        readinessMap.put(player, false);
        System.out.println("Player joined");
    }
    public synchronized void removePlayer(Player player){
        if(!playerMap.containsKey(player)){
            return;
        }
        else if(game != null){
            forfeitLine.add(playerMap.get(player));
            updatePlayerLine();
            updateGameStatus();
        }
        playerMap.remove(player);
        readinessMap.remove(player);
    }
    private void updatePlayerLine(){
        try{
            if(forfeitLine.contains(game.getCurrentPlayer())){
                final Color color = game.getCurrentPlayer();
                game.forfeit(color);
                forfeitLine.remove(color);
            }
        }
        catch (NotThisPlayerTurnException e){
            System.out.println("The forfeit should be possible -> " + e.getMessage());
        }
    }
    private void updateGameStatus(){
        if (game.isFinished()){
            for (Color temp : game.getWinner()){
                for (Player temp2 : playerMap.keySet()){
                    if(playerMap.get(temp2) == temp){
                        winnersLine.add(temp2);
                    }
                }
            }
            game = null;
        }
    }
    public Player[] getWinnerLine(){
        return winnersLine.toArray(new Player[0]);
    } //TODO Consider removing it
    public synchronized Color getPlayerColor(Player player){
        return playerMap.get(player);
    }
    public synchronized Player getCurrentPlayer(){
        if(game == null){
            return null;
        }
        Player result = null;
        for (Player temp : playerMap.keySet()){
            if (playerMap.get(temp) == game.getCurrentPlayer()){
                result = temp;
            }
        }
        return result;
    }
    public synchronized boolean moveChecker(int oldX, int oldY, int newX, int newY, Player player)
            throws NotThisPlayerTurnException, NotThisLobbyException, IncorrectMoveException{
        if(!playerMap.containsKey(player)){
            throw new NotThisLobbyException("You are not in this lobby");
        }
        boolean result = game.moveChecker(oldX, oldY, newX, newY, playerMap.get(player));
        updatePlayerLine();
        updateGameStatus();
        return result;
    }
    public synchronized void skipTurn(Player player) throws NotThisLobbyException, NotThisPlayerTurnException {
        if(!playerMap.containsKey(player)){
            throw new NotThisLobbyException("You are not in this lobby");
        }
        game.skipTurn(playerMap.get(player));
        updatePlayerLine();
        updateGameStatus();
    }
    public Color[][] getCheckerArray(){
        if(game == null) {
            return null;
        }
        Color[][] result = game.getCheckerArray();
        return result;
    }
    public Color[][] getColorArray(){
        return this.game == null ? null : this.game.getCheckerArray();
    }
    public synchronized void setReady(Player player, boolean value) {
        if(readinessMap.containsKey(player)) {
            readinessMap.replace(player, value);
            // Start game
            if(!readinessMap.containsValue(false) && readinessMap.size() > 1 &&
                    readinessMap.size() < maxNumberOfPlayers){
                try {
                    game = new SimpleMaster();
                    for (Player temp : playerMap.keySet()) {
                        playerMap.replace(temp, game.addPlayer());
                        System.out.println("Player ready");
                    }
                    game.startGame();
                    System.out.println("Game has started");
                } catch (AllSeatsTakenException | IncorrectNumberOfPlayersException e) {
                    for (Player temp : playerMap.keySet()){
                        playerMap.replace(temp, null);
                    }
                    game = null;
                    if (e.getClass() == AllSeatsTakenException.class){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String[] getPlayerArray() {
        ArrayList<String> temp = new ArrayList<>();
        for ( Player player : playerMap.keySet()) {
            temp.add(player.getNickName());
        }
        return temp.toArray(new String[0]);
    }
    public Map<String, Color> getMap() {
        Map<String, Color> result = new HashMap<>();
        for (Player temp : playerMap.keySet()) {
            result.put(temp.getNickName(), playerMap.get(temp));
        }
        return result;
    }
}

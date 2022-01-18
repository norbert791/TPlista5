package org.IgorNorbert.lista4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the lobby.
 */
public class Lobby {
    /**
     * Used for generating board.
     */
    private final boolean[][] mask = new SimpleMaster().boardMask();
    /**
     * interface of the game.
     */
    private volatile GameMaster game;
    /**
     * Maximum number of players in this lobby.
     */
    private final int maxNumberOfPlayers = 6;
    /**
     * Map holding players and their respective colors.
     */
    private final Map<Player, Color> playerMap = new HashMap<>();
    /**
     * Map holding players and their readiness status.
     */
    private final Map<Player, Boolean> readinessMap = new HashMap<>();
    /**
     * Collection storing colors of players that have surrendered.
     */
    private final Collection<Color> forfeitLine = new ArrayList<>();
    /**
     * Collection storing players that have won.
     */
    private final Collection<Player> winnersLine = new ArrayList<>();

    /**
     * Adds player to the lobby.
     * @param player player that should be added
     * @throws LobbyFullException thrown if lobby is full
     * @throws NotThisLobbyException thrown if the player
     * has already been assigned to this lobby
     * @throws IllegalArgumentException thrown if player == null
     */
    public synchronized void addPlayer(final Player player)
            throws LobbyFullException, NotThisLobbyException,
            IllegalArgumentException {
        if (player == null) {
            throw new IllegalArgumentException("The player must not be null");
        }
        if (playerMap.containsKey(player)) {
            throw new NotThisLobbyException("You already are in this lobby");
        } else if (playerMap.size() >= maxNumberOfPlayers) {
            throw new LobbyFullException("The lobby is already full");
        } else if (game != null) {
            throw new LobbyFullException("The game has already started");
        }
        //Colors will be assigned after the game has started
        playerMap.put(player, null);
        readinessMap.put(player, false);
    }

    /**
     * Removes player from this lobby. If the game is ongoing,
     * then he will automatically surrender
     * @param player Player that is to surrender
     */
    public synchronized void removePlayer(final Player player) {
        if (!playerMap.containsKey(player)) {
            return;
        } else if (game != null) {
            forfeitLine.add(playerMap.get(player));
            updatePlayerLine();
            updateGameStatus();
        }
        playerMap.remove(player);
        readinessMap.remove(player);
    }

    /**
     * Calls forfeit method at the turn of a player that has surrendered.
     */
    private void updatePlayerLine() {
        try {
            if (forfeitLine.contains(game.getCurrentPlayer())) {
                final Color color = game.getCurrentPlayer();
                game.forfeit(color);
                forfeitLine.remove(color);
            }
        } catch (NotThisPlayerTurnException e) {
            System.out.println("The forfeit should be possible -> "
                    + e.getMessage());
        }
    }

    /**
     * Checks if the game is finished and appends winnersLine.
     */
    private void updateGameStatus() {
        if (game.isFinished()) {
            for (Color temp : game.getWinner()) {
                for (Player temp2 : playerMap.keySet()) {
                    if (playerMap.get(temp2) == temp) {
                        winnersLine.add(temp2);
                    }
                }
            }
            game = null;
        }
    }

    /**
     * Get array representing the order in which the players have won the game.
     * @return Array sorted so that the winner is indexed by 0
     */
    public Player[] getWinnerLine() {
        return winnersLine.toArray(new Player[0]);
    }

    /**
     * Get Color representing target player.
     * @param player Player whose color is to be returned
     * @return Color representing given player
     */
    public Color getPlayerColor(final Player player) {
        return playerMap.get(player);
    }

    /**
     * Gets reference to player currently making their move.
     * @return reference of the player currently making their turn.
     */
    public Player getCurrentPlayer() {
        if (game == null) {
            return null;
        }
        Player result = null;
        for (Player temp : playerMap.keySet()) {
            if (playerMap.get(temp) == game.getCurrentPlayer()) {
                result = temp;
            }
        }
        return result;
    }

    /**
     * Move checker from one position on board to another.
     * @param oldX old x coordinate
     * @param oldY old y coordinate
     * @param newX new x coordinate
     * @param newY new y coordinate
     * @param player used for color matching
     * @return true iff another move can be made
     * @throws NotThisPlayerTurnException Thrown if Player's color does
     * not match the color of player making their move
     * @throws NotThisLobbyException thrown if player is not in this lobby
     * @throws IncorrectMoveException thrown if the move is illegal
     */
    public synchronized boolean moveChecker(final int oldX, final int oldY,
                                             final int newX, final int newY,
                                            final Player player)
            throws NotThisPlayerTurnException, NotThisLobbyException,
            IncorrectMoveException {
        if (!playerMap.containsKey(player)) {
            throw new NotThisLobbyException("You are not in this lobby");
        }
        boolean result = game.moveChecker(oldX, oldY,
                newX, newY, playerMap.get(player));
        updatePlayerLine();
        updateGameStatus();
        return result;
    }

    /**
     * Skips the turn of player currently making their move.
     * @param player used for player matching
     * @throws NotThisLobbyException
     * thrown if the player is not in this lobby
     * @throws NotThisPlayerTurnException
     * thrown if it is not given player's turn
     */
    public synchronized void skipTurn(final Player player)
            throws NotThisLobbyException, NotThisPlayerTurnException {
        if (!playerMap.containsKey(player)) {
            throw new NotThisLobbyException("You are not in this lobby");
        }
        game.skipTurn(playerMap.get(player));
        updatePlayerLine();
        updateGameStatus();
    }

    /**
     * Get Color array representing current state of the game.
     * @return Color array representing current state of the game.
     */
    public Color[][] getCheckerArray() {
        return this.game == null ? null : game.getCheckerArray();
    }

    /**
     * get Mask of the GameMaster implementation.
     * @return bitMask of the board
     */
    public boolean[][] getMask() {
        return mask;
    }

    /**
     * Sets the ready status for target player
     * and checks if all the players are ready.
     * @param player player whose status is to be set
     * @param value value of the player's ready status
     */
    public synchronized void setReady(final Player player,
                                      final boolean value) {
        if (readinessMap.containsKey(player)) {
            readinessMap.replace(player, value);
            // Start game
            if (!readinessMap.containsValue(false) && readinessMap.size() > 1
                    && readinessMap.size() < maxNumberOfPlayers) {
                try {
                    game = new SimpleMaster();
                    for (Player temp : playerMap.keySet()) {
                        playerMap.replace(temp, game.addPlayer());
                    }
                    game.startGame();
                } catch (AllSeatsTakenException
                        | IncorrectNumberOfPlayersException e) {
                    for (Player temp : playerMap.keySet()) {
                        playerMap.replace(temp, null);
                    }
                    game = null;
                    if (e.getClass() == AllSeatsTakenException.class) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Get array with nicknames of players in the lobby.
     * @return String array with players' names
     */
    public String[] getPlayerArray() {
        ArrayList<String> temp = new ArrayList<>();
        for (Player player : playerMap.keySet()) {
            temp.add(player.getNickName());
        }
        return temp.toArray(new String[0]);
    }

    /**
     * Get Map containing nicknames of players and their respective colors.
     * @return Map containing nicknames of players and their respective colors
     */
    public Map<String, Color> getPlayerMap() {
        Map<String, Color> result = new HashMap<>();
        for (Player temp : playerMap.keySet()) {
            result.put(temp.getNickName(), playerMap.get(temp));
        }
        return result;
    }
}

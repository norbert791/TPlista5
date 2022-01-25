package org.Norbert.lista4.Game;

import org.Norbert.lista4.Game.Exceptions.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * The implementation of GameMaster.
 */
public class SimpleMaster implements GameMaster {
    /**
     * Board for our game.
     */
    private final Board board = new ArrayBoard();
    /**
     * Mapping from seat to color representing players.
     */
    private final Map<Seat, Color> map = new EnumMap<>(Seat.class);
    /**
     * player currently making move.
     */
    private Color currentPlayer = null;
    /**
     * Order in which players make their moves.
     */
    private Color[] order;
    /**
     * Collection containing colors representing
     * the order in which the players have won the game.
     */
    private final Collection<Color> winOrder = new ArrayList<>();
    /**
     * true iff the game was finished.
     */
    private boolean isFinished = false;
    /**
     * Counter for managing the order of players.
     * The field may be manipulated to effectively manage the game,
     * so it shouldn't be used for stat-tracing
     */
    private int turn = 0;

    /**
     * Adds a player to the gameTable and returns
     * color representing that player.
     * @return Color representing the added player
     * @throws AllSeatsTakenException thrown if all placed are occupied
     */
    @Override
    public Color addPlayer() throws AllSeatsTakenException {
        for (Color temp : Color.values()) {
            if (!map.containsValue(temp)) {
                for (Seat temp2 : Seat.values()) {
                    if (!map.containsKey(temp2)) {
                        map.put(temp2, temp);
                        return temp;
                    }
                }
            }
        }
        throw new AllSeatsTakenException("There is no room for another player");
    }

    /**
     * Ads player to chosen seat.
     * @param seat seat to whom the player should be assigned
     * @return color representing the assigned player
     * @throws SeatTakenException thrown if the seat has
     * already a player assigned to it
     */
    @Override
    public Color addPlayer(final Seat seat) throws SeatTakenException {
        if (map.containsKey(seat)) {
            throw new SeatTakenException("This seat is already occupied");
        }
        for (Color temp : Color.values()) {
            if (!map.containsValue(temp)) {
                map.put(seat, temp);
                return temp;
            }
        }
        return null;
    }

    /**
     * Removes all checkers from the board.
     */
    @Override
    public void clearBoard() {
        Color[][] array = board.getCheckerColorArray();
        try {
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    if (board.getCheckerColor(j, i) != null) {
                        board.removeChecker(j, i);
                    }
                }
            }
        } catch (IncorrectPositionException e) {
            System.out.println("This exception should never occur");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Make any necessary preparations and start the game.
     * @return Color of the player who is the first one in order
     * @throws IncorrectNumberOfPlayersException if the number added players
     * is lesser than one or equal to five
     */
    @Override
    public Color startGame() throws IncorrectNumberOfPlayersException {
        if (map.keySet().size() < 2 || map.keySet().size() == 5) {
            throw new IncorrectNumberOfPlayersException(
                    "There must be 2, 3, 4 or 6 players");
        }
        fixPositions();
        order = generateOrder();
        currentPlayer = order[0];
        for (Seat temp : map.keySet()) {
            board.setCorner(temp, map.get(temp));
        }
        return currentPlayer;
    }

    /**
     * Check if the game is still going.
     * @return true iff the game is finished
     */
    @Override
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Get the color of player who is currently making their move.
     * @return Color representing the player currently making their move
     */
    @Override
    public Color getCurrentPlayer() {

        return currentPlayer;
    }

    /**
     * Get array of Colors representing the order in
     * which the players have won the game.
     * @return Array of colors representing win order
     */
    @Override
    public Color[] getWinner() {
        return winOrder.toArray(new Color[0]);
    }

    /**
     * Move the checker from old position to the new one.
     * @param oldX x coordinate of checker
     * @param oldY y coordinate of checker
     * @param newX x coordinate of move
     * @param newY y coordinate of move
     * @param color color of the player who moves the checker
     * @return true iff the player represented by color
     * may make another move (jump)
     * @throws NotThisPlayerTurnException thrown if the color parameter
     * does not match the color of player currently making their move
     * @throws IncorrectMoveException thrown if the move violates the game rules
     */
    @Override
    public boolean moveChecker(
            final int oldX, final int oldY, final int newX,
            final int newY, final Color color)
            throws NotThisPlayerTurnException, IncorrectMoveException {
        boolean nextTurn;
        if (currentPlayer != color || color == null) {
            throw new NotThisPlayerTurnException(
                    "This is not the given player's turn");
        }
        try {
            if (board.getCheckerColor(oldX, oldY) == null) {
                throw new IncorrectMoveException(
                        "There is no checker at the given position");
            } else if (board.getCheckerColor(oldX, oldY) == color) {
                nextTurn = board.moveChecker(oldX, oldY, newX, newY);
            } else {
                throw new IncorrectMoveException(
                        "Selected checker color doesn't match player color");
            }
        } catch (IncorrectPositionException | IncorrectMoveException e) {
            throw new IncorrectMoveException(
                    "This move is illegal -> " + e.getMessage());
        }
        //Checking win condition after player's turn
        for (Seat temp : map.keySet()) {
            if (map.get(temp) == currentPlayer) {
                if (board.checkCorner(
                        switch (temp) {
                    case NORTH -> Seat.SOUTH;
                    case SOUTH -> Seat.NORTH;
                    case NORTHEAST -> Seat.SOUTHWEST;
                    case NORTHWEST -> Seat.SOUTHEAST;
                    case SOUTHEAST -> Seat.NORTHWEST;
                    case SOUTHWEST -> Seat.NORTHEAST;
                },
                        currentPlayer)) {
                    winOrder.add(currentPlayer);
                    removePlayer(currentPlayer);
                    currentPlayer = order[turn % order.length];
                    if (order.length == 1) {
                        winOrder.add(currentPlayer);
                        removePlayer(currentPlayer);
                        isFinished = true;
                        currentPlayer = null;
                    }
                    return false;
                }
                break;
            }
        }
        if (!nextTurn) {
            final int maxNumberOfTurns = 6000;
            turn++;
            currentPlayer = order[turn % order.length];
            if (turn > maxNumberOfTurns) {
                currentPlayer = null;
                return false;
            }
        }
        return nextTurn;
    }

    /**
     * Skip the turn of player represented by color.
     * @param color color of the player whose turn should be skipped
     * @throws NotThisPlayerTurnException
     * thrown if it isn't the chosen player's turn
     */
    @Override
    public void skipTurn(final Color color) throws NotThisPlayerTurnException {
        if (currentPlayer == color) {
            turn++;
            currentPlayer = order[turn % order.length];
        }
    }

    /**
     * Removes the player represented by color from the game.
     * @param color color of the surrendering player
     * @throws NotThisPlayerTurnException thrown if the player is not
     * surrendering in their turn
     */
    @Override
    public void forfeit(final Color color) throws NotThisPlayerTurnException {
        if (currentPlayer != color) {
            throw new NotThisPlayerTurnException(
                    "The player may not surrender if it isn't their turn");
        }
        removePlayer(color);
        currentPlayer = order[turn % order.length];
        if (order.length == 5) {
            currentPlayer = null;
            isFinished = true;
        } else if(order.length == 1) {
            winOrder.add(currentPlayer);
            isFinished = true;
            removePlayer(currentPlayer);
            currentPlayer = null;
        }
    }

    /**
     * Get the representation of current state of the board.
     * @return Array representing the current state of the board
     */
    @Override
    public Color[][] getCheckerArray() {
        return board.getCheckerColorArray();
    }

    @Override
    public boolean[][] boardMask() {
        return this.board.getMask();
    }

    /**
     * Private method specific to this implementation.
     * It forces players into fixed positions on board
     */
    private void fixPositions() {
        Color[] players = map.values().toArray(new Color[0]);
        switch (players.length) {
            case 2 -> {
                map.clear();
                map.put(Seat.NORTH, players[0]);
                map.put(Seat.SOUTH, players[1]);
            }
            case 3 -> {
                map.clear();
                map.put(Seat.SOUTH, players[0]);
                map.put(Seat.NORTHEAST, players[1]);
                map.put(Seat.NORTHWEST, players[2]);
            }
            case 4 -> {
                map.clear();
                map.put(Seat.SOUTHEAST, players[0]);
                map.put(Seat.SOUTHWEST, players[1]);
                map.put(Seat.NORTHEAST, players[2]);
                map.put(Seat.NORTHWEST, players[3]);
            }
        }
    }

    /**
     * Pick the starting player on random and
     * creates the order in which players will make their moves.
     * @return Array representing the order in which players will play.
     */
    private Color[] generateOrder() {
        Seat[] temp = map.keySet().toArray(new Seat[0]);
        // Sorting the array of seats in 'clockwise' order
        Arrays.sort(temp, (one, two) -> {
            final Function<Seat, Integer> eval = (k) -> switch (k) {
                case NORTH -> 1;
                case NORTHEAST -> 2;
                case SOUTHEAST -> 3;
                case SOUTH -> 4;
                case SOUTHWEST -> 5;
                case NORTHWEST -> 6;
            };
            return eval.apply(one) - eval.apply(two);
        });
        Color[] result = new Color[temp.length];
        int length = result.length;
        //Picking first player (offset)
        int firstPlayer = ThreadLocalRandom.current().nextInt(length);
        //Sorting colors of players clockwise with offset
        // (firstPlayer is first in array,
        // the following colors are ordered clockwise)
        for (int i = 0; i < length; i++) {
            result[i] = map.get(temp[(i + firstPlayer) % length]);
        }
        return result;
    }

    /**
     * Method used for safely removing players from the game,
     * without ruining the order of players.
     * @param color Color of the player that should be removed
     */
    private void removePlayer(final Color color) {
        if (order != null) {
            ArrayList<Color> temp = new ArrayList<>(List.of(order));
            temp.remove(color);
            order = temp.toArray(new Color[0]);
        }
        for (Seat temp : map.keySet()) {
            if (map.get(temp) == color) {
                map.remove(temp, color);
            }
        }
    }
}

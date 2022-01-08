package org.IgorNorbert.lista4;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class SimpleMaster implements GameMaster {
    private final Board board = new ArrayBoard();
    private final Map<Seat, Color> map = new EnumMap<>(Seat.class);
    private Color currentPlayer = null;
    private Color[] order;
    private final Collection<Color> winOrder = new ArrayList<>();
    private boolean isFinished = false;
    private int turn = 0;

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

    @Override
    public Color addPlayer(Seat seat) throws SeatTakenException {
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

    @Override
    public Color startGame() throws IncorrectNumberOfPlayersException {
        if(map.keySet().size() < 2 || map.keySet().size() == 5){
            throw new IncorrectNumberOfPlayersException("There must be 2, 3, 4 or 6 players");
        }
        fixPositions();
        order = generateOrder();
        currentPlayer = order[0];
        for (Seat temp : map.keySet()) {
            board.setCorner(temp, map.get(temp));
        }
        return currentPlayer;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public Color getCurrentPlayer(){

        return currentPlayer;
    }

    @Override
    public Color[] getWinner() {
        return winOrder.toArray(new Color[0]);
    }

    @Override
    public boolean moveChecker(int oldX, int oldY, int newX, int newY, Color color) throws NotThisPlayerTurnException, IncorrectMoveException {
        boolean nextTurn;
        if (currentPlayer != color || color == null){
            throw new NotThisPlayerTurnException("This is not the given player's turn");
        }
        try{
            if (board.getCheckerColor(oldX, oldY) == null){
                throw new IncorrectMoveException("There is no checker at the given position");
            }
            else if (board.getCheckerColor(oldX, oldY) == color){
                nextTurn = board.moveChecker(oldX, oldY, newX, newY);
            }
            else{
                throw new IncorrectMoveException("Selected checker color doesn't match player color");
            }
        }
        catch (IncorrectPositionException | IncorrectMoveException e) {
            throw new IncorrectMoveException("This move is illegal -> " + e.getMessage());
        }
        //Checking win condition after player's turn
        for(Seat temp : map.keySet()){
            if(map.get(temp) == currentPlayer){
                if (board.checkCorner(switch (temp){
                    case NORTH -> Seat.SOUTH;
                    case SOUTH -> Seat.NORTH;
                    case NORTHEAST -> Seat.SOUTHWEST;
                    case NORTHWEST -> Seat.SOUTHEAST;
                    case SOUTHEAST -> Seat.NORTHWEST;
                    case SOUTHWEST -> Seat.NORTHEAST;
                },currentPlayer)){
                    winOrder.add(currentPlayer);
                    removePlayer(currentPlayer);
                    currentPlayer = order[turn % order.length];
                    if(order.length == 1){
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
        if (!nextTurn){
            turn++;
            currentPlayer = order[turn % order.length];
            int maxNumberOfTurns = 60000;
            if(turn > maxNumberOfTurns){
                currentPlayer = null;
                return false;
            }
        }
        return nextTurn;
    }

    @Override
    public void skipTurn(Color color) throws NotThisPlayerTurnException {
        if (currentPlayer == color){
            turn++;
            currentPlayer = order[turn % order.length];
        }
    }

    @Override
    public void forfeit(Color color) throws NotThisPlayerTurnException{
        if(currentPlayer != color){
            throw new NotThisPlayerTurnException("The player may not surrender if it isn't their turn");
        }
        removePlayer(color);
        currentPlayer = order[turn % order.length];
        if (order.length == 5){
            currentPlayer = null;
            isFinished = true;
        }
        else if(order.length == 1){
            winOrder.add(currentPlayer);
            isFinished = true;
            removePlayer(currentPlayer);
            currentPlayer = null;
        }
    }

    @Override
    public Color[][] getCheckerArray() {
        return board.getCheckerColorArray();
    }

    private void fixPositions(){
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
    private Color[] generateOrder(){
        Seat[] temp = map.keySet().toArray(new Seat[0]);
        // Sorting the array of seats in 'clockwise' order
        Arrays.sort(temp, (one, two) -> {
            final Function<Seat, Integer> eval = (k) -> switch (k){
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
        //Sorting colors of players clockwise with offset (firstPlayer is first in array,
        // the following colors are ordered clockwise)
        for(int i = 0; i < length; i++){
            result[i] = map.get(temp[(i + firstPlayer) % length]);
        }
        return result;
    }
    private void removePlayer(Color color) {
        if(order != null){
            ArrayList<Color> temp = new ArrayList<>(List.of(order));
            temp.remove(color);
            order = temp.toArray(new Color[0]);
        }
        for (Seat temp : map.keySet()){
            if(map.get(temp) == color){
                map.remove(temp, color);
            }
        }
    }
}
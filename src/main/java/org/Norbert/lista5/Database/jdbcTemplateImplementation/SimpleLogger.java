package org.Norbert.lista5.Database.jdbcTemplateImplementation;

import org.Norbert.lista5.Database.GameLogger;
import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Game.Seat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleLogger implements GameLogger {
    private final List<PlayerMove> playerMoves = new ArrayList<>();
    private String gameType;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    private final Map<String, Color> playerMap = new HashMap<>();
    private final Map<String, Seat> playerSeat = new HashMap<>();

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }
    /**
     * Records a checkerMove.
     *
     * @param oldX  X coordinate of the old position
     * @param oldY  Y coordinate of the old position
     * @param newX  X coordinate of the new position
     * @param newY  Y coordinate of the new position
     * @param color Color of the checker
     */
    @Override
    public void insertCheckerMove(int oldX, int oldY, int newX, int newY, Color color) {
        playerMoves.add(new PlayerMove(PlayerMove.MoveType.CHECKER_MOVE, color,
                new PlayerMove.CheckerMove(oldX, oldY, newX, newY)));
    }

    /**
     * Records a player's turn skip.
     *
     * @param color Color of the player that's skipped their turn
     */
    @Override
    public void insertSkip(Color color) {
        playerMoves.add(new PlayerMove(PlayerMove.MoveType.SKIP, color, null));
    }

    /**
     * Records player's surrender
     *
     * @param color Color of the player that skipped their turn
     */
    @Override
    public void insertForfeit(Color color) {
        playerMoves.add(new PlayerMove(
                PlayerMove.MoveType.SURRENDER, color, null));
    }

    /**
     * Updates the dataBase with this game's results.
     */
    @Override
    public void commitGame() {
        if (gameType == null) {//TODO consider throwing exception
            return;
        }
        Integer gameId;
        Map<String, Object> out;
        Map<Color, Integer> colorIntegerMap = new HashMap<>();
        SimpleJdbcCall temp = new SimpleJdbcCall(jdbcTemplateObject)
                .withProcedureName("create_game").declareParameters(
                        new SqlParameter("game_type", Types.VARCHAR),
                        new SqlOutParameter("gameId", Types.INTEGER));
        out = temp.execute(gameType);
        gameId = (Integer) out.get("gameId");
        temp = new SimpleJdbcCall(jdbcTemplateObject)
                .withProcedureName("insert_player").declareParameters(
                        new SqlParameter("PlayerColor", Types.VARCHAR),
                        new SqlParameter("playerSeat", Types.VARCHAR),
                        new SqlParameter("nickname", Types.VARCHAR),
                        new SqlParameter("game_id", Types.INTEGER),
                        new SqlOutParameter("player_id", Types.INTEGER)
                );
        for (String tempString : playerMap.keySet()) {
            out = temp.execute(colorToString(playerMap.get(tempString)),
                    playerSeatToString(playerSeat.get(tempString)), tempString, gameId);
            colorIntegerMap.put(playerMap.get(tempString), (Integer) out.get("player_id"));
        }
        temp = new SimpleJdbcCall(jdbcTemplateObject).
                withProcedureName("insert_move").declareParameters(
                        new SqlParameter("player_id", Types.INTEGER),
                        new SqlParameter("orderingNum", Types.INTEGER),
                        new SqlParameter("game_id", Types.INTEGER),
                        new SqlParameter("type", Types.VARCHAR),
                        new SqlParameter("old_x", Types.INTEGER),
                        new SqlParameter("old_y", Types.INTEGER),
                        new SqlParameter("new_x", Types.INTEGER),
                        new SqlParameter("new_y", Types.INTEGER)
                );
        int counter = 0;
        for (PlayerMove move : playerMoves) {
            if(move.moveType() == PlayerMove.MoveType.CHECKER_MOVE){
                out = temp.execute(colorIntegerMap.get(move.playerColor()), ++counter,
                        gameId, playerMoveTypeToString(move.moveType()),
                        move.checkerMove().oldX(),
                        move.checkerMove().oldY(),
                        move.checkerMove().newX(),
                        move.checkerMove().newY());
            }
            else {
                out = temp.execute(colorIntegerMap.get(move.playerColor()), ++counter,
                        gameId, playerMoveTypeToString(move.moveType()),
                        null, null, null, null);
            }
        }
    }

    /**
     * Clear's this game's record.
     */
    @Override
    public void clear() {
        playerMoves.clear();
        playerSeat.clear();
        playerMap.clear();
        gameType = null;
    }

    /**
     * Stores the shape of the board for reference.
     *
     * @param gameType array representing the board's shape
     */
    @Override
    public void insertGameType(String gameType) {
        this.gameType = gameType;
    }

    /**
     * Adds player with their name.
     *
     * @param playerName  name of the player
     * @param playerColor color of the player
     * @param playerSeat seat assigned to the player
     */
    @Override
    public void addPlayer(String playerName, Color playerColor, Seat playerSeat) {
        playerMap.put(playerName, playerColor);
        this.playerSeat.put(playerName, playerSeat);
    }

    /**
     * Translates Color to String recognized for mariaDB
     * @param color color to be translated
     * @return String representing enum type in mariaDB
     */
    private String colorToString(Color color) {
       return switch (color) {
           case RED -> "RED";
           case GREEN -> "GREEN";
           case BLUE -> "BLUE";
           case CYAN -> "CYAN";
           case MAGENTA -> "MAGENTA";
           case YELLOW -> "YELLOW";
       };
    }

    /**
     * Translates MoveType to enum type for mariaDB
     * @param type type to be translated
     * @return String representing the mariaDB enum type
     */
    private String playerMoveTypeToString (PlayerMove.MoveType type) {
        return switch (type) {
            case SKIP -> "skip";
            case SURRENDER -> "surrender";
            case CHECKER_MOVE -> "checker";
        };
    }

    /**
     * Translates Seat type to enum type for mariaDB
     * @return String representing maria enum type
     */
    private String playerSeatToString(Seat seat) {
        return switch (seat) {
            case NORTH -> "NORTH";
            case SOUTH -> "SOUTH";
            case NORTHEAST -> "NORTH_EAST";
            case NORTHWEST -> "NORTH_WEST";
            case SOUTHEAST -> "SOUTH_EAST";
            case SOUTHWEST -> "SOUTH_WEST";
        };
    }
}

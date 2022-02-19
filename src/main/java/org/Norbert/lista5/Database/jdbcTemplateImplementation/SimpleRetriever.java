package org.Norbert.lista5.Database.jdbcTemplateImplementation;

import org.Norbert.lista5.Database.HistoryRetriever;
import org.Norbert.lista5.Game.Color;
import org.Norbert.lista5.Game.GameMaster;
import org.Norbert.lista5.Game.Seat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Types;
import java.util.*;

public class SimpleRetriever implements HistoryRetriever {
    private final List<PlayerMove> playerMoves = new ArrayList<>();
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    private final Map<String, Color> playerMap = new HashMap<>();

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    /**
     * Get list of games
     *
     * @param nick_name nick of the player whose games should be fetched
     * @return Array of games available for retrieval
     */
    @Override
    public GameDescriptionRecord[] fetchGameList(String nick_name) {
        List<GameDescriptionRecord> temp = new ArrayList<>();
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplateObject)
                .withProcedureName("list_games").declareParameters(
                        new SqlParameter("player_nick", Types.VARCHAR)
                );
        Map<String, Object> out = call.execute(nick_name);
        List<Map<String, Object>> results =
                (List<Map<String,Object>>) out.get("#result-set-1");
        results.forEach(game -> {
            GameDescriptionRecord tempGame = new GameDescriptionRecord(
                    (Integer) game.get("id"), (String) game.get("gameType"),
                    game.get("creationTime").toString());
            temp.add(tempGame);
        });
        return temp.toArray(new GameDescriptionRecord[0]);
    }

    /**
     * Fetched moves of selected game
     *
     * @param game_id id of games whose moves should be retrieved
     * @return Array of Player moves for reconstructing game
     */
    @Override
    public GameRecord fetchHistory(int game_id) {
        //Getting list of players from database
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplateObject)
                .withProcedureName("list_players").declareParameters(
                        new SqlParameter("game_id", Types.INTEGER)
                );
        Map<String, Object> out = call.execute(game_id);
        List<Map<String, Object>> results =
                (List<Map<String,Object>>) out.get("#result-set-1");
        Map<String, Color> nameColorMap = new HashMap<>();
        Map<Seat, Color> seatColorHashMap = new HashMap<>();
        results.forEach(r -> {
            nameColorMap.put((String) r.get("nickname"), translateStringColor((String) r.get("color")));
            seatColorHashMap.put(translateStringSeat((String) r.get("seat")), translateStringColor((String) r.get("color")));
        });
        //Getting game type
        call = new SimpleJdbcCall(jdbcTemplateObject)
                .withFunctionName("fetch_type")
                .declareParameters(new SqlParameter("game_id", Types.INTEGER));
        String gameType = call.executeFunction(
                String.class, new MapSqlParameterSource("game_id", game_id));
        //Getting board shape and initial state directly from class
        boolean[][] boardMask;
        Color[][] initialBoard;
        try {
            Class tempClass = Class.forName( "org.Norbert.lista4.Game." + gameType); //TODO: change to injection
            GameMaster gameInfo = (GameMaster) tempClass.getDeclaredConstructor().newInstance();
            boardMask = gameInfo.boardMask();
            initialBoard = gameInfo.initialBoard(seatColorHashMap);
        } catch (ClassNotFoundException | InvocationTargetException
                | InstantiationException | IllegalAccessException
                | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        //Getting move list
        call = new SimpleJdbcCall(jdbcTemplateObject)
                .withProcedureName("list_moves").declareParameters(
                        new SqlParameter("game_id", Types.INTEGER)
                );
        out = call.execute(game_id);
        results = (List<Map<String,Object>>) out.get("#result-set-1");
        results.forEach(r -> {
            if (Objects.equals(r.get("moveType"), "checker")) {
                playerMoves.add(new PlayerMove(
                        PlayerMove.MoveType.CHECKER_MOVE,
                        translateStringColor((String) r.get("color")), new PlayerMove.CheckerMove(
                                (int) r.get("oldX"), (int) r.get("oldY"), (int) r.get("newX"), (int) r.get("newY")
                )));
            }
            else {
                playerMoves.add(new PlayerMove(
                        Objects.equals(r.get("moveType"), "skip") ? PlayerMove.MoveType.SKIP : PlayerMove.MoveType.SURRENDER,
                        translateStringColor((String) r.get("color")),
                        null
                ));
            }
        });
        //Constructing record
        return new GameRecord(initialBoard, boardMask, nameColorMap, playerMoves.toArray(new PlayerMove[0]));
    }

    private Color translateStringColor(String colorName) {
        return switch (colorName) {
            case "RED" -> Color.RED;
            case "GREEN" -> Color.GREEN;
            case "BLUE" -> Color.BLUE;
            case "CYAN" -> Color.CYAN;
            case "MAGENTA" -> Color.MAGENTA;
            case "YELLOW" -> Color.YELLOW;
            default -> null;
        };
    }

    private Seat translateStringSeat(String seatName) {
        return switch (seatName) {
            case "NORTH" -> Seat.NORTH;
            case "SOUTH" -> Seat.SOUTH;
            case "NORTH_EAST" -> Seat.NORTHEAST;
            case "NORTH_WEST" -> Seat.NORTHWEST;
            case "SOUTH_EAST" -> Seat.SOUTHEAST;
            case "SOUTH_WEST" -> Seat.SOUTHWEST;
            default -> null;
        };
    }
}

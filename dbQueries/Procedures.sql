USE `techprog.lista5`;

DELIMITER $$

CREATE PROCEDURE create_game(IN game_type VARCHAR(100), OUT gameId INTEGER)
BEGIN
    DECLARE temp BINARY(16);
    SET temp = UNHEX(REPLACE(UUID(),'-',''));
    INSERT INTO game (game.creationTime, gameType, game.uid) VALUE (CURRENT_TIMESTAMP, game_type, temp);
    SET gameId = (SELECT id FROM game WHERE uid = temp);
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE insert_player(IN PlayerColor
    ENUM('RED', 'GREEN', 'BLUE', 'CYAN', 'MAGENTA', 'YELLOW'),
    IN playerSeat ENUM('NORTH', 'SOUTH','NORTH_EAST', 'NORTH_WEST',
        'SOUTH_EAST', 'SOUTH_WEST'),
IN nick_name VARCHAR(50), IN game_id INTEGER, OUT player_id INTEGER)
BEGIN
    DECLARE player INTEGER;
    SET player = (SELECT id FROM player WHERE nickname = nick_name);
    IF player IS NULL
    THEN
        INSERT INTO player (nickname) VALUE (nick_name);
        SET player = (SELECT id FROM player WHERE nickname = nick_name);
    END IF;
    INSERT INTO lobbyPlayer(playerId, gameId, color, seat) VALUE (player, game_id, PlayerColor, playerSeat);
    SET player_id = player;
END $$

DELIMITER ;

DELIMITER $$
CREATE PROCEDURE insert_move(IN player_id INTEGER, IN orderingNum INTEGER,
IN game_id INTEGER, IN type ENUM('skip', 'surrender', 'checker'), IN old_x INTEGER, IN old_y INTEGER,
IN new_x INTEGER, IN new_y INTEGER)
    BEGIN
        DECLARE temp BINARY(16);
        DECLARE temp2 INTEGER;
        SET AUTOCOMMIT = 0;
        START TRANSACTION;
        IF type = 'skip' or type = 'surrender'
            THEN
            INSERT INTO move (moveType, orderingNumber, gameId, playerId, checkerMove) VALUE(
                type, orderingNum, game_id, player_id, NULL);
        ELSE
            SET temp2 = (SELECT id FROM checkerMove
            WHERE newX = new_x AND newY = new_y AND oldX = old_x AND oldY = old_y);
            IF temp2 IS NULL
            THEN
                SET temp = UNHEX(REPLACE(UUID(),'-',''));
                INSERT INTO checkerMove (uid, oldX, oldY, newX, newY)
                VALUE (temp, old_x, old_y, new_x, new_y);
                SET temp2 = (SELECT id FROM checkerMove
            WHERE newX = new_x AND newY = new_y AND oldX = old_x AND oldY = old_y);
            END IF;
            INSERT INTO move (moveType, orderingNumber, gameId, playerId, checkerMove) VALUE (
                type, orderingNum, game_id, player_id, temp2);
            END IF;
        COMMIT ;
    END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE list_games(IN player_nick VARCHAR(50))
    BEGIN
        SELECT game.id, game.gameType, game.creationTime FROM game
            INNER JOIN lobbyPlayer ON game.id = lobbyPlayer.gameId
            INNER JOIN player ON lobbyPlayer.playerId = player.id
            WHERE player.nickname LIKE player_nick;
    END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE list_players(IN game_id INTEGER)
    BEGIN
        SELECT lobbyPlayer.color, lobbyPlayer.seat, player.nickname
        FROM lobbyPlayer INNER JOIN player ON lobbyPlayer.playerId = player.id
        WHERE lobbyPlayer.gameId = game_id;
    END ;
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE list_moves(IN game_id INTEGER)
    BEGIN
        SELECT DISTINCT lobbyPlayer.color, move.moveType, move.orderingNumber,
               checkerMove.oldX, checkerMove.oldY, checkerMove.newX, checkerMove.newY
        FROM lobbyPlayer
        INNER JOIN move ON lobbyPlayer.playerId = move.playerId
        LEFT JOIN checkerMove ON move.checkerMove = checkerMove.id
        WHERE move.gameId = game_id
        ORDER BY move.orderingNumber;
    END ;
DELIMITER ;

DELIMITER $$
CREATE FUNCTION fetch_type(game_id INTEGER) RETURNS VARCHAR(100)
    BEGIN
        DECLARE temp VARCHAR(100);
        SET temp = (SELECT gameType FROM game WHERE id = game_id);
        RETURN temp;
    END ;
DELIMITER ;

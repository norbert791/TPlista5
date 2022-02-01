USE `techprog.lista5`;
CREATE TABLE IF NOT EXISTS player (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR (50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS game (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    gameType VARCHAR(100) NOT NULL,
    creationTime timestamp,
    uid BINARY(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS lobbyPlayer (
    playerId INTEGER NOT NULL REFERENCES player(id),
    gameId INTEGER NOT NULL REFERENCES game(id),
    color ENUM('RED', 'GREEN', 'BLUE', 'CYAN', 'MAGENTA', 'YELLOW') NOT NULL,
    seat  ENUM('NORTH', 'SOUTH', 'NORTH_EAST',
        'NORTH_WEST', 'SOUTH_EAST', 'SOUTH_WEST') NOT NULL
);

CREATE TABLE IF NOT EXISTS move(
    moveType ENUM('skip', 'surrender', 'checker') NOT NULL,
    orderingNumber INTEGER NOT NULL,
    gameId INTEGER NOT NULL REFERENCES game(id),
    playerId INTEGER NOT NULL REFERENCES player(id),
    checkerMove INTEGER,
    PRIMARY KEY (orderingNumber, gameId)
);

CREATE TABLE IF NOT EXISTS checkerMove(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    uid BINARY(16) NOT NULL,
    oldX INTEGER NOT NULL,
    oldY INTEGER NOT NULL,
    newX INTEGER NOT NULL,
    newY INTEGER NOT NULL
);
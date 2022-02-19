package org.Norbert.lista5.Database.jdbcTemplateImplementation;

import org.Norbert.lista5.Database.jdbcTemplateImplementation.PlayerMove;
import org.Norbert.lista5.Game.Color;

import java.io.Serializable;
import java.util.Map;

public record GameRecord (Color[][] initialBoard, boolean[][] boardShape,
                          Map<String, Color> playerMap, PlayerMove[] moves) implements Serializable {
}

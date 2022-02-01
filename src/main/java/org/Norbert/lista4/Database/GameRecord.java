package org.Norbert.lista4.Database;

import org.Norbert.lista4.Game.Color;

import java.io.Serializable;
import java.util.Map;

public record GameRecord (Color[][] initialBoard, boolean[][] boardShape,
                          Map<String, Color> playerMap, PlayerMove[] moves) implements Serializable {
}

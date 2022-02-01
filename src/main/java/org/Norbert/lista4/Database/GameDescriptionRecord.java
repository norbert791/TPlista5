package org.Norbert.lista4.Database;

import java.io.Serializable;

public record GameDescriptionRecord(Integer gameId, String gameType, String date) implements Serializable {
}

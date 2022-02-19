package org.Norbert.lista5.Database.jdbcTemplateImplementation;

import java.io.Serializable;

public record GameDescriptionRecord(Integer gameId, String gameType, String date) implements Serializable {
}

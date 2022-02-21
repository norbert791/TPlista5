package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.HistoryRetriever;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;

public class HibernateRetriever implements HistoryRetriever {
    @Override
    public GameDescriptionRecord[] fetchGameList(String nick_name) {
        return new GameDescriptionRecord[0];
    }

    @Override
    public GameRecord fetchHistory(int game_id) {
        return null;
    }
}

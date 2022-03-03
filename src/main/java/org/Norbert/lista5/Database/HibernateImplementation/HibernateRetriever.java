package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.HistoryRetriever;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;

import javax.sql.DataSource;

public class HibernateRetriever implements HistoryRetriever {
    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public GameDescriptionRecord[] fetchGameList(String nick_name) {
        return new GameDescriptionRecord[0];
    }

    @Override
    public GameRecord fetchHistory(int game_id) {
        return null;
    }
}

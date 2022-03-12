package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.HistoryRetriever;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.util.List;

public class HibernateRetriever implements HistoryRetriever {
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public GameDescriptionRecord[] fetchGameList(String nick_name) {
        return null;
    }

    @Override
    public GameRecord fetchHistory(int game_id) {
        return null;
    }
}

package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.AuthorizationFailed;
import org.Norbert.lista5.Database.UserManager;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework
        .security
        .crypto
        .bcrypt
        .BCrypt;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class HibernateUserManager implements UserManager {
    private SessionFactory sessionFactory;
    private String playerName = null;
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean logIn(String email, String password){
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Player> cr = builder.createQuery(Player.class);
        Root<Player> root = cr.from(Player.class);
        Predicate[] predicates = new Predicate[1];
        predicates[0] = builder.like(root.get("email"), email);
        cr.select(root).where(predicates);
        TypedQuery<Player> query = session.createQuery(cr);
        try {
            List<Player> results = query.getResultList();
            if (results.size() != 1) {
                session.close();
                return false;
            }
            else {
                 String salt = results.get(0).getSalt();
                 if (BCrypt.checkpw(password, results.get(0).getHashedPassword())) {
                    session.close();
                    playerName = results.get(0).getNickName();
                    return true;
                }
                else {
                    return false;
                }
            }
        } catch (SessionException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public boolean register(String email, String name, String password) {
        Session session = sessionFactory.openSession();
        Player player = new Player();
        player.setEmail(email);
        player.setSalt(BCrypt.gensalt());
        player.setHashedPassword(BCrypt.hashpw(password, player.getSalt()));
        player.setNickName(name);
        Transaction tx;
        try {
            tx = session.beginTransaction();
            session.save(player);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            return false;
        } finally {
          session.close();
        }
    }

    @Override
    public String getName() throws AuthorizationFailed {
        if (playerName == null) {
            throw new AuthorizationFailed("This user did not sign in");
        }
        else {
            return playerName;
        }
    }
}

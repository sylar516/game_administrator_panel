package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;
    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "350609sylarR");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        Query<Player> query = session.createNativeQuery("select * from player", Player.class);
        query.setMaxResults(pageSize);
        query.setFirstResult(pageNumber * pageSize);
        return query.list();
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        Query<Integer> query = session.createNamedQuery("Player_getAllCount", Integer.class);
        return query.uniqueResult();
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        session.persist(player);
        transaction.commit();
        return player;
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Player playerMerge = (Player) session.merge(player);
        transaction.commit();
        return playerMerge;
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        Player player = session.get(Player.class, id);
        Optional<Player> optionalPlayer = Optional.of(player);
        return optionalPlayer;
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.remove(player);
        transaction.commit();
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}
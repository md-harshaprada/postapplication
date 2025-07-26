package secondwebapp;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraUserDAO implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CassandraUserDAO.class);
    private final Session session;

    public CassandraUserDAO() {
        Cluster cluster = null;
        Session tempSession = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoints("127.0.0.1")
                    .withPort(9042)
                    .build();
            tempSession = cluster.connect("user_keyspace");
        } catch (DriverException e) {
            logger.error("Failed to connect to Cassandra cluster", e);
            if (cluster != null) {
                cluster.close();
            }
            throw e;
        }
        this.session = tempSession;
    }

    public boolean userExists(String name) {
        String query = "SELECT COUNT(*) FROM userdetails WHERE name = ?";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(name);
            ResultSet resultSet = session.execute(boundStatement);
            return resultSet.one().getLong(0) > 0;
        } catch (DriverException e) {
            logger.error("Error checking if user exists", e);
            throw e;
        }
    }

    public void registerUser(String name, String password) {
        if (userExists(name)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        String query = "INSERT INTO userdetails (name, password) VALUES (?, ?)";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(name, password);
            session.execute(boundStatement);
        } catch (DriverException e) {
            logger.error("Error registering user", e);
            throw e;
        }
    }

    public boolean loginUser(String name, String password) {
        String query = "SELECT * FROM userdetails WHERE name = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(name, password);
            ResultSet resultSet = session.execute(boundStatement);
            return resultSet.one() != null;
        } catch (DriverException e) {
            logger.error("Error logging in user", e);
            throw e;
        }
    }
    
    public List<String> getAllUsers()  throws SQLException{
        List<String> users = new ArrayList<>();
        String query = "SELECT name FROM userdetails";

        try {
            ResultSet resultSet = session.execute(query);

            for (Row row : resultSet) {
                users.add(row.getString("name"));
            }

        } catch (DriverException e) {
            logger.error("Error retrieving users from Cassandra", e);
        }

        return users;
    }

    @Override
    public void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}

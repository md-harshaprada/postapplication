package group;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.DriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class CassandraGroupDAO implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CassandraGroupDAO.class);
    private final Session session;

    public CassandraGroupDAO() {
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

    public UUID createGroup(String name) {
        UUID groupId = UUID.randomUUID();
        String query = "INSERT INTO groupdetails (group_id, group_name) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(groupId, name);
            session.execute(boundStatement);
        } catch (DriverException e) {
            logger.error("Error creating group", e);
            throw e;
        }
        return groupId;
    }

    public void addUsersToGroup(UUID groupId, List<String> users) {
        String query = "INSERT INTO group_users (group_id, user_name) VALUES (?, ?)";
        try {
            PreparedStatement preparedStatement = session.prepare(query);

            for (String user : users) {
                BoundStatement boundStatement = preparedStatement.bind(groupId, user);
                session.execute(boundStatement);
            }
        } catch (DriverException e) {
            logger.error("Error adding users to group", e);
            throw e;
        }
    }
    
    @Override
    public void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}

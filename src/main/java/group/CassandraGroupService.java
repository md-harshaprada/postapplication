package group;

import java.util.List;
import java.util.UUID;

public class CassandraGroupService {
    private CassandraGroupDAO cassandraGroupDAO;

    public CassandraGroupService() {
        cassandraGroupDAO = new CassandraGroupDAO();
    }

    public UUID createGroup(String name) {
        return cassandraGroupDAO.createGroup(name);
    }

    public void addUsersToGroup(UUID groupId, List<String> usernames) {
    	cassandraGroupDAO.addUsersToGroup(groupId, usernames);
    }
}
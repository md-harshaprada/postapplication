package secondwebapp;

import java.sql.SQLException;
import java.util.List;

public class CassandraUserService {

    private CassandraUserDAO cassandraUserDAO;
    public CassandraUserService() {
        cassandraUserDAO = new CassandraUserDAO();
    }
    
    public List<String> getAllUsers() {
        try {
            return cassandraUserDAO.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import secondwebapp.Post;

public class GroupDAO {
    private Connection connection;


    public GroupDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "tiger");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer createGroup(String name) throws SQLException {
        String sql = "INSERT INTO user.groups (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating group failed, no ID obtained.");
            }
        }
    }

    public void addUsersToGroup(Integer groupId, List<String> usernames) throws SQLException {
        String sql = "INSERT INTO group_members(group_id, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String username : usernames) {
                statement.setInt(1, groupId);
                statement.setString(2, username);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public List<Integer> getUserGroups(String username) throws SQLException {
        String sql = "SELECT group_id FROM group_members WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            List<Integer> groups = new ArrayList<>();
            while (rs.next()) {
                groups.add(rs.getInt("group_id"));
            }
            return groups;
        }
    }
    
    public boolean isUserInGroup(String username, int groupId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM group_members WHERE username = ? AND group_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setInt(2, groupId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Post> getPostsByGroup(int groupId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, name, content, shared, comments_enabled FROM posts WHERE group_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, groupId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String content = resultSet.getString("content");
                String shared = resultSet.getString("shared");
                boolean commentsEnabled = resultSet.getBoolean("comments_enabled");
                posts.add(new Post(id, name, content, shared, commentsEnabled, groupId));
            }
        }
        return posts;
    }

    public String getGroupNameById(Integer groupId) throws SQLException {
        String sql = "SELECT name FROM user.groups WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, groupId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        }
        return null;
    }
    
    public List<String> getGroupMembersByGroupId(Integer groupId) {
        List<String> members = new ArrayList<>();
        String sql = "SELECT username FROM group_members WHERE group_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    members.add(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    
}


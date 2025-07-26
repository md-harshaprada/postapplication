package secondwebapp;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class PostDAO {
    private Connection connection;

    public PostDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "tiger");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public int postContent(String username, String content, String shared, List<String> specificUsers, boolean commentsEnabled, Integer groupId, String filename, String filetype, InputStream fileData) throws SQLException {
        String sql = "INSERT INTO posts(name, content, shared, comments_enabled, group_id, attachment_filename, attachment_filetype, attachment_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, content);
            statement.setString(3, shared);
            statement.setBoolean(4, commentsEnabled);
            if (groupId == null) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, groupId);
            }
            statement.setString(6, filename);
            statement.setString(7, filetype);
            if (fileData != null) {
                statement.setBlob(8, fileData);
            } else {
                statement.setNull(8, Types.BLOB);
            }

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int postId = 0;
            if (generatedKeys.next()) {
                postId = generatedKeys.getInt(1);
            }

            if ("specific".equals(shared) && specificUsers != null && !specificUsers.isEmpty()) {
                insertSpecificUsers(postId, specificUsers);
            }

            return postId;
        }
    }

    
    private void insertSpecificUsers(int postId, List<String> specificUsers) throws SQLException {
        String sql = "INSERT INTO post_specific_users(post_id, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String user : specificUsers) {
                statement.setInt(1, postId);
                statement.setString(2, user);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public int addReply(int postId, String username, String content, Integer parentReplyId, Integer groupId) throws SQLException {
        String sql = "INSERT INTO replies(post_id, username, content, parent_reply_id, group_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, postId);
            statement.setString(2, username);
            statement.setString(3, content);
            if (parentReplyId != null) {
                statement.setInt(4, parentReplyId);
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            if (groupId != null) {
                statement.setInt(5, groupId);
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int replyId = 0;
            if (generatedKeys.next()) {
                replyId = generatedKeys.getInt(1);
            }

            return replyId;
        }
    }


    public List<Post> getSpecificPostsForUser(String username) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.content, p.shared, p.comments_enabled, p.group_id, p.attachment_filename, p.attachment_filetype, p.attachment_data FROM posts p " +
                     "JOIN post_specific_users psu ON p.id = psu.post_id " +
                     "WHERE psu.username = ? " +
                     "ORDER BY p.id DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String content = resultSet.getString("content");
                String shared = resultSet.getString("shared");
                boolean commentsEnabled = resultSet.getBoolean("comments_enabled");
                int groupId = resultSet.getInt("group_id");
                String attachmentFilename = resultSet.getString("attachment_filename");
                String attachmentFiletype = resultSet.getString("attachment_filetype");
                InputStream attachmentData = resultSet.getBinaryStream("attachment_data");

                posts.add(new Post(id, name, content, shared, commentsEnabled, groupId, attachmentFilename, attachmentFiletype, attachmentData)); 
            }
        }
        return posts;
    }

    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, name, content, shared, comments_enabled, group_id, attachment_filename, attachment_filetype, attachment_data FROM posts ORDER BY id DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String content = resultSet.getString("content");
                String postShared = resultSet.getString("shared");
                boolean commentsEnabled = resultSet.getBoolean("comments_enabled");
                int groupId = resultSet.getInt("group_id");
                String attachmentFilename = resultSet.getString("attachment_filename");
                String attachmentFiletype = resultSet.getString("attachment_filetype");
                InputStream attachmentData = resultSet.getBinaryStream("attachment_data");

                posts.add(new Post(id, name, content, postShared, commentsEnabled, groupId, attachmentFilename, attachmentFiletype, attachmentData)); 
            }
        }
        return posts;
    }

    public Post getPostById(int postId) throws SQLException {
        Post post = null;
        String sql = "SELECT id, name, content, shared, comments_enabled, group_id, attachment_filename, attachment_filetype, attachment_data FROM posts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String content = resultSet.getString("content");
                    String postShared = resultSet.getString("shared");
                    boolean commentsEnabled = resultSet.getBoolean("comments_enabled");
                    int groupId = resultSet.getInt("group_id");
                    String attachmentFilename = resultSet.getString("attachment_filename");
                    String attachmentFiletype = resultSet.getString("attachment_filetype");
                    InputStream attachmentData = resultSet.getBinaryStream("attachment_data");

                    post = new Post(id, name, content, postShared, commentsEnabled, groupId, attachmentFilename, attachmentFiletype, attachmentData);
                }
            }
        }
        return post;
    }

    
    public List<Reply> getRepliesForPost(int postId) throws SQLException {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT id, username, content, parent_reply_id, group_id FROM replies WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String content = resultSet.getString("content");
                Integer parentReplyId = resultSet.getObject("parent_reply_id") != null ? resultSet.getInt("parent_reply_id") : null;
                Integer groupId = resultSet.getObject("group_id") != null ? resultSet.getInt("group_id") : null;
                replies.add(new Reply(id, postId, username, content, parentReplyId, groupId));
            }
        }
        return replies;
    }
    
    public void likePost(int postId, String username) throws SQLException {
        String sql = "INSERT INTO post_likes (post_id, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    public void likeReply(int replyId, String username) throws SQLException {
        String sql = "INSERT INTO reply_likes (reply_id, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, replyId);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    public int getPostLikesCount(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public int getReplyLikesCount(int replyId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reply_likes WHERE reply_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, replyId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }
    
    public void insertPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts(name, content, shared, comments_enabled, group_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getContent());
            statement.setString(3, post.getPostShared());
            statement.setBoolean(4, post.isCommentsEnabled());
            
            if (post.getGroupId() == null) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, post.getGroupId());
            }
            
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setId(generatedKeys.getInt(1));
            }
        }
    }

    public List<Post> getPostsByGroup(int groupId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, name, content, shared, comments_enabled, group_id FROM posts WHERE group_id = ? ORDER BY id DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, groupId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String content = resultSet.getString("content");
                String shared = resultSet.getString("shared");
                boolean commentsEnabled = resultSet.getBoolean("comments_enabled");
                Integer groupIdResult = resultSet.getObject("group_id") != null ? resultSet.getInt("group_id") : null;

                List<String> attachments = new ArrayList<>();
                String attachmentSql = "SELECT attachment_filename FROM posts WHERE id = ?";
                
                try (PreparedStatement attachmentStmt = connection.prepareStatement(attachmentSql)) {
                    attachmentStmt.setInt(1, id);
                    ResultSet attachmentResultSet = attachmentStmt.executeQuery();
                    
                    while (attachmentResultSet.next()) {
                        String attachmentFilename = attachmentResultSet.getString("attachment_filename");
                        if (attachmentFilename != null && !attachmentFilename.isEmpty()) {
                            attachments.add(attachmentFilename);
                        }
                    }
                }

                Post post = new Post(id, name, content, shared, commentsEnabled, groupIdResult);
                post.setAttachments(attachments);
                posts.add(post);
            }
        }
        
        return posts;
    }


    
    public List<Reply> getRepliesByPost(int postId) throws SQLException {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT id, post_id, username, content, parent_reply_id, group_id FROM replies WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int postIdResult = resultSet.getInt("post_id");
                String username = resultSet.getString("username");
                String content = resultSet.getString("content");
                Integer parentReplyId = resultSet.getObject("parent_reply_id") != null ? resultSet.getInt("parent_reply_id") : null;
                Integer groupId = resultSet.getObject("group_id") != null ? resultSet.getInt("group_id") : null;
                replies.add(new Reply(id, postIdResult, username, content, parentReplyId, groupId));
            }
        }
        return replies;
    }
    
    public void deletePostAndReplies(int postId) throws SQLException {
        String deleteRepliesSql = "DELETE FROM replies WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteRepliesSql)) {
            statement.setInt(1, postId);
            statement.executeUpdate();
        }

        String deletePostSql = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deletePostSql)) {
            statement.setInt(1, postId);
            statement.executeUpdate();
        }
    }

    public void deleteReply(int replyId) throws SQLException {
    	String deleteChildReplySql = "DELETE FROM replies WHERE parent_reply_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteChildReplySql)) {
            statement.setInt(1, replyId);
            statement.executeUpdate();
        }
    	
        String deleteReplySql = "DELETE FROM replies WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteReplySql)) {
            statement.setInt(1, replyId);
            statement.executeUpdate();
        }
    }

    public List<Post> getSearchPosts(String search_content) throws SQLException {
    	String searchPostQuery = "SELECT * FROM posts WHERE content LIKE ?";
    	List<Post> searchPosts = new ArrayList<>();
    	try(PreparedStatement statement = connection.prepareStatement(searchPostQuery)){
    		statement.setString(1, "%" + search_content + "%");
    		ResultSet resultSet = statement.executeQuery();
    		while (resultSet.next()) {
    			 int id = resultSet.getInt("id");
                 String name = resultSet.getString("name");
                 String content = resultSet.getString("content");
                 String postShared = resultSet.getString("shared");
                 boolean commentsEnabled = resultSet.getBoolean("comments_enabled");
                 int groupId = resultSet.getInt("group_id");
                 String attachmentFilename = resultSet.getString("attachment_filename");
                 String attachmentFiletype = resultSet.getString("attachment_filetype");
                 InputStream attachmentData = resultSet.getBinaryStream("attachment_data");

                 searchPosts.add(new Post(id, name, content, postShared, commentsEnabled, groupId, attachmentFilename, attachmentFiletype, attachmentData)); 
            }
    	}
    	return searchPosts;
    }

}

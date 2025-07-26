package secondwebapp;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CassandraPostDAO implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CassandraPostDAO.class);
    private final Session session;

    public CassandraPostDAO() {
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

    public void cqlPostContent(int postId, String username, String content, String shared, List<String> specificUsers, boolean commentsEnabled, Integer groupId, String filename, String filetype, ByteBuffer fileData) {
        String query = "INSERT INTO posts (id, name, content, post_shared, comments_enabled, group_id, attachment_filename, attachment_filetype, attachment_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(postId, username, content, shared, commentsEnabled, groupId, filename, filetype, fileData);

            session.execute(boundStatement);

            if ("specific".equals(shared) && specificUsers != null && !specificUsers.isEmpty()) {
                insertSpecificUsers(postId, specificUsers);
            }
        } catch (DriverException e) {
            logger.error("Error inserting post content", e);
            throw e;
        }
    }

    private void insertSpecificUsers(int postId, List<String> specificUsers) {
        String query = "INSERT INTO specific_users (post_id, user_name) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = session.prepare(query);

            for (String user : specificUsers) {
                BoundStatement boundStatement = preparedStatement.bind(postId, user);
                session.execute(boundStatement);
            }

        } catch (DriverException e) {
            logger.error("Error inserting specific users", e);
            throw e;
        }
    }

    public void addReply(int replyId,int postId, String username, String content, Integer parentReplyId, Integer groupId) {
        String query = "INSERT INTO replies (id, post_id, username, content, parent_reply_id, group_id) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(replyId, postId, username, content, parentReplyId, groupId);

            session.execute(boundStatement);

        } catch (DriverException e) {
            logger.error("Error inserting reply", e);
            throw e;
        }
    }


    public List<PostCql> getAllPosts() {
        List<PostCql> posts = new ArrayList<>();
        String query = "SELECT id, name, content, post_shared, comments_enabled, group_id, attachment_filename, attachment_filetype, attachment_data FROM posts";

        try {
            ResultSet resultSet = session.execute(query);

            for (Row row : resultSet) {
                int id = row.getInt("id");
                String name = row.getString("name");
                String content = row.getString("content");
                String postShared = row.getString("post_shared");
                boolean commentsEnabled = row.getBool("comments_enabled");
                Integer groupId = row.getInt("group_id");
                String filename = row.getString("attachment_filename");
                String filetype = row.getString("attachment_filetype");
                ByteBuffer attachmentData = row.getBytes("attachment_data");

                posts.add(new PostCql(id, name, content, postShared, commentsEnabled, groupId, filename, filetype, attachmentData));
            }
            Collections.sort(posts, new Comparator<PostCql>() {
                @Override
                public int compare(PostCql p1, PostCql p2) {
                    return Integer.compare(p2.getId(), p1.getId());
                }
            });

        } catch (DriverException e) {
            logger.error("Error retrieving all posts", e);
            throw e;
        }

        return posts;
    }


    public List<PostCql> getPostsByIds(List<Integer> postIds) {
        List<PostCql> posts = new ArrayList<>();

        if (postIds.isEmpty()) {
            return posts;
        }

        String query = "SELECT id, name, content, post_shared, comments_enabled, group_id, attachment_filename, attachment_filetype, attachment_data FROM posts WHERE id IN ?";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(postIds);
            ResultSet resultSet = session.execute(boundStatement);

            for (Row row : resultSet) {
                int id = row.getInt("id");
                String name = row.getString("name");
                String content = row.getString("content");
                String postShared = row.getString("post_shared");
                boolean commentsEnabled = row.getBool("comments_enabled");
                Integer groupId = row.getInt("group_id");
                String filename = row.getString("attachment_filename");
                String filetype = row.getString("attachment_filetype");
                ByteBuffer attachmentData = row.getBytes("attachment_data");

                posts.add(new PostCql(id, name, content, postShared, commentsEnabled, groupId, filename, filetype, attachmentData));
            }
            Collections.sort(posts, new Comparator<PostCql>() {
                @Override
                public int compare(PostCql p1, PostCql p2) {
                    return Integer.compare(p2.getId(), p1.getId());
                }
            });

        } catch (DriverException e) {
            logger.error("Error retrieving posts by IDs", e);
            throw e;
        }

        return posts;
    }


    
    public List<Integer> getPostIdsForUser(String username) {
        List<Integer> postIds = new ArrayList<>();
        String query = "SELECT post_id FROM specific_users WHERE user_name = ? allow filtering";

        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(username);
            ResultSet resultSet = session.execute(boundStatement);

            for (Row row : resultSet) {
                postIds.add(row.getInt("post_id"));
            }

        } catch (DriverException e) {
            logger.error("Error retrieving post IDs for user", e);
            throw e;
        }

        return postIds;
    }
    
    public List<Reply> getRepliesForPost(int postId) {
        List<Reply> replies = new ArrayList<>();
        String query = "SELECT id, username, content, parent_reply_id, group_id FROM replies WHERE post_id = ? allow filtering";

        try {
            ResultSet resultSet = session.execute(session.prepare(query).bind(postId));

            for (Row row : resultSet) {
                int id = row.getInt("id");
                String username = row.getString("username");
                String content = row.getString("content");
                Integer parentReplyId = row.getInt("parent_reply_id") != 0 ? row.getInt("parent_reply_id") : null;
                Integer groupId = row.getInt("group_id") != 0 ? row.getInt("group_id") : null;

                replies.add(new Reply(id, postId, username, content, parentReplyId, groupId));
            }

        } catch (Exception e) {
            System.err.println("Error retrieving replies for post ID " + postId + ": " + e.getMessage());
        }

        return replies;
    }
    
    public void deletePost(int postId) {
        String checkQuery = "SELECT COUNT(*) FROM posts WHERE id = ?";
        String deleteQuery = "DELETE FROM posts WHERE id = ?";

        try {
            PreparedStatement checkStatement = session.prepare(checkQuery);
            BoundStatement checkBoundStatement = checkStatement.bind(postId);
            ResultSet resultSet = session.execute(checkBoundStatement);
            Row row = resultSet.one();
            long count = row.getLong("count");

            if (count > 0) {
                PreparedStatement deleteStatement = session.prepare(deleteQuery);
                BoundStatement deleteBoundStatement = deleteStatement.bind(postId);
                session.execute(deleteBoundStatement);
            } else {
                logger.info("Post with ID " + postId + " does not exist, no deletion performed.");
            }

        } catch (DriverException e) {
            logger.error("Error checking or deleting post", e);
            throw e;
        }
    }


    public void deleteRepliesForPost(int postId) {
        String countQuery = "SELECT COUNT(*) FROM replies WHERE post_id = ? ALLOW FILTERING";
        String findRepliesQuery = "SELECT id FROM replies WHERE post_id = ? ALLOW FILTERING";
        String deleteQuery = "DELETE FROM replies WHERE id = ? AND post_id = ?";

        try {
            PreparedStatement countStatement = session.prepare(countQuery);
            BoundStatement countBoundStatement = countStatement.bind(postId);
            ResultSet countResultSet = session.execute(countBoundStatement);
            Row countRow = countResultSet.one();
            long count = countRow.getLong(0);

            if (count > 0) {
                PreparedStatement findStatement = session.prepare(findRepliesQuery);
                BoundStatement findBoundStatement = findStatement.bind(postId);
                ResultSet resultSet = session.execute(findBoundStatement);

                for (Row row : resultSet) {
                    int replyId = row.getInt("id");

                    PreparedStatement deleteStatement = session.prepare(deleteQuery);
                    BoundStatement deleteBoundStatement = deleteStatement.bind(replyId, postId);
                    session.execute(deleteBoundStatement);
                }

            } else {
                logger.info("No replies found for post");
            }

        } catch (DriverException e) {
            logger.error("Error deleting replies", e);
            throw e;
        }
    }



    public void deleteReply(int replyId) {
        String findRepliesQuery = "SELECT id, post_id FROM replies WHERE parent_reply_id = ? ALLOW FILTERING";
        String deleteQuery = "DELETE FROM replies WHERE id = ? AND post_id = ?";
        String deleteQueryForReply = "DELETE FROM replies WHERE id = ? ";
        String checkQuery = "SELECT COUNT(*) FROM replies WHERE id = ?";

        try {
            PreparedStatement checkStatement = session.prepare(checkQuery);
            BoundStatement checkBoundStatement = checkStatement.bind(replyId);
            ResultSet resultSet = session.execute(checkBoundStatement);
            Row row = resultSet.one();
            long count = row.getLong("count");

            if (count > 0) {
                PreparedStatement findStatement = session.prepare(findRepliesQuery);
                BoundStatement findBoundStatement = findStatement.bind(replyId);
                ResultSet repliesResultSet = session.execute(findBoundStatement);

                for (Row replyRow : repliesResultSet) {
                    int id = replyRow.getInt("id");
                    int postId = replyRow.getInt("post_id");

                    PreparedStatement deleteStatement = session.prepare(deleteQuery);
                    BoundStatement deleteBoundStatement = deleteStatement.bind(id, postId);
                    session.execute(deleteBoundStatement);
                }

                PreparedStatement deleteSpecificStatement = session.prepare(deleteQueryForReply);
                BoundStatement deleteSpecificBoundStatement = deleteSpecificStatement.bind(replyId); 
                session.execute(deleteSpecificBoundStatement);

            } else {
                logger.info("Reply does not exist.");
            }

        } catch (DriverException e) {
            logger.error("Error deleting reply", e);
            throw e;
        }
    }

    public List<PostCql> getPostsByGroupFromCassandra(int groupId) {
        List<PostCql> posts = new ArrayList<>();
        
        String postQuery = "SELECT id, name, content, post_shared, comments_enabled, group_id FROM posts WHERE group_id = ? allow filtering";
        
        try {
            PreparedStatement postPreparedStatement = session.prepare(postQuery);
            BoundStatement postBoundStatement = postPreparedStatement.bind(groupId);
            
            ResultSet postResultSet = session.execute(postBoundStatement);
            for (Row postRow : postResultSet) {
                int id = postRow.getInt("id");
                String name = postRow.getString("name");
                String content = postRow.getString("content");
                String shared = postRow.getString("post_shared");
                boolean commentsEnabled = postRow.getBool("comments_enabled");
                Integer groupIdResult = postRow.getInt("group_id");
                
                List<String> attachments = new ArrayList<>();
                String attachmentQuery = "SELECT attachment_filename FROM posts WHERE id = ?";
                PreparedStatement attachmentPreparedStatement = session.prepare(attachmentQuery);
                BoundStatement attachmentBoundStatement = attachmentPreparedStatement.bind(id);
                ResultSet attachmentResultSet = session.execute(attachmentBoundStatement);
                
                for (Row attachmentRow : attachmentResultSet) {
                    String attachmentFilename = attachmentRow.getString("attachment_filename");
                    if (attachmentFilename != null && !attachmentFilename.isEmpty()) {
                        attachments.add(attachmentFilename);
                    }
                }
                
                PostCql post = new PostCql(id, name, content, shared, commentsEnabled, groupIdResult);
                post.setAttachments(attachments);
                posts.add(post);
            }
        } catch (DriverException e) {
            logger.error("Error retrieving posts by group from Cassandra", e);
        }
        
        return posts;
    }


    @Override
    public void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}

package secondwebapp;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public class CassandraPostService {
    private final CassandraPostDAO cassandraPostDAO = new CassandraPostDAO();
    
    public void postContent(int postId, String username, String content, String shared, List<String> specificUsers, boolean commentsEnabled, Integer groupId, String filename, String filetype, ByteBuffer fileData) {
        try {
            cassandraPostDAO.cqlPostContent(postId, username, content, shared, specificUsers, commentsEnabled, groupId, filename, filetype, fileData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addReply(int replyId, int postId, String username, String content, Integer parentReplyId, Integer groupId) {
        try {
        	cassandraPostDAO.addReply(replyId, postId, username, content, parentReplyId, groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PostCql> getSpecificPostsForUser(String username) {
        try {
        	List<Integer> postIds = cassandraPostDAO.getPostIdsForUser(username);
            return cassandraPostDAO.getPostsByIds(postIds);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<PostCql> getAllPosts() {
        try {
            return cassandraPostDAO.getAllPosts();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public List<Reply> getRepliesForPost(int postId) {
        try {
            return cassandraPostDAO.getRepliesForPost(postId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public void deletePostAndReplies(int postId) {
        try {
            cassandraPostDAO.deleteRepliesForPost(postId);
            cassandraPostDAO.deletePost(postId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PostCql> getPostsByGroup(int groupId) {
            return cassandraPostDAO.getPostsByGroupFromCassandra(groupId);
    }
    
    public void deleteReply(int replyId) {
        try {
            cassandraPostDAO.deleteReply(replyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

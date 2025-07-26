package secondwebapp;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PostService {
    private PostDAO postDAO;
    
    public PostService() {
        postDAO = new PostDAO();
    }

    public int postContent(String username, String content, String shared, List<String> specificUsers, boolean commentsEnabled, Integer groupId, String filename, String filetype, InputStream fileData) {
        try {
            return postDAO.postContent(username, content, shared, specificUsers, commentsEnabled, groupId, filename, filetype, fileData);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<Post> getSpecificPostsForUser(String username) {
        try {
            return postDAO.getSpecificPostsForUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Post> getAllPosts() {
        try {
            return postDAO.getAllPosts();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public int addReply(int postId, String username, String content, Integer parentReplyId, Integer groupId) {
        try {
            return postDAO.addReply(postId, username, content, parentReplyId, groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Reply> getRepliesForPost(int postId) {
        try {
            return postDAO.getRepliesForPost(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public void likePost(int postId, String username) {
        try {
            postDAO.likePost(postId, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void likeReply(int replyId, String username) {
        try {
            postDAO.likeReply(replyId, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPostLikesCount(int postId) {
        try {
            return postDAO.getPostLikesCount(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getReplyLikesCount(int replyId) {
        try {
            return postDAO.getReplyLikesCount(replyId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<Post> getPostsByGroup(int groupId) {
        try {
            return postDAO.getPostsByGroup(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public Post getPostById(int postId) {
        try {
            return postDAO.getPostById(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public List<Reply> getRepliesByPost(int postId) {
        try {
            return postDAO.getRepliesByPost(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public void deletePostAndReplies(int postId) {
    	 try {
    	        postDAO.deletePostAndReplies(postId);
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }

    public void deleteReply(int replyId) {
    	 try {
    	        postDAO.deleteReply(replyId);
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }
    
    public List<Post> getSearchPosts(String search_content) {
        try {
            return postDAO.getSearchPosts(search_content);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
}

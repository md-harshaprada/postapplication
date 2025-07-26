package groupsessions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {
	private Connection connection;


    public SessionDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "tiger");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int createSession(String owner, String sessionName, Timestamp startTime, Timestamp endTime, int groupId, String adminType, boolean onlyAdminsCanAnswer) throws SQLException {
        String query = "INSERT INTO group_sessions (owner, session_name, start_time, end_time, groupId, admin_type, only_admins_can_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, owner);
            stmt.setString(2, sessionName);
            stmt.setTimestamp(3, startTime);
            stmt.setTimestamp(4, endTime);
            stmt.setInt(5, groupId);
            stmt.setString(6, adminType);
            stmt.setBoolean(7, onlyAdminsCanAnswer);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating session failed, no ID obtained.");
                }
            }
        }
    }

    public void addGroupAdmin(int sessionId, String username) throws SQLException {
        String query = "INSERT INTO group_admins (session_id, username) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    public List<Session> getActiveSessions() throws SQLException {
        String query = "SELECT * FROM group_sessions WHERE end_time > CURRENT_TIMESTAMP";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<Session> sessions = new ArrayList<>();
            while (rs.next()) {
                Session session = new Session(
                        rs.getInt("id"),
                        rs.getString("owner"),
                        rs.getString("session_name"),
                        rs.getTimestamp("start_time"),
                        rs.getTimestamp("end_time"),
                        rs.getInt("groupId"),
                        rs.getString("admin_type"), 
                        rs.getBoolean("only_admins_can_answer")
                );
                sessions.add(session);
            }
            return sessions;
        }
    }

    public Session getSessionById(int sessionId) throws SQLException {
        String query = "SELECT * FROM group_sessions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Session(
                            rs.getInt("id"),
                            rs.getString("owner"),
                            rs.getString("session_name"),
                            rs.getTimestamp("start_time"),
                            rs.getTimestamp("end_time"),
                            rs.getInt("groupId"),
                            rs.getString("admin_type"), 
                            rs.getBoolean("only_admins_can_answer") 
                    );
                }
            }
        }
        return null;
    }

    public boolean isOnlyAdminsCanAnswer(int sessionId) throws SQLException {
        String query = "SELECT only_admins_can_answer FROM group_sessions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("only_admins_can_answer");
                }
            }
        }
        return false;
    }

    public List<String> getAdminsForSession(int sessionId) throws SQLException {
        String query = "SELECT username FROM group_admins WHERE session_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> admins = new ArrayList<>();
                while (rs.next()) {
                    admins.add(rs.getString("username"));
                }
                return admins;
            }
        }
    }

    
    public int insertQuestion(int sessionId, String ownerName, String question, String username) throws SQLException {
        String sql = "INSERT INTO questions (sessionid, ownername, question, username) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, sessionId);
            statement.setString(2, ownerName);
            statement.setString(3, question);
            statement.setString(4, username);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int questionId = 0;
            if (generatedKeys.next()) {
                questionId = generatedKeys.getInt(1);
            }

            return questionId; 
        }
    }

    
    public List<SessionPost> getQuestionsBySessionId(int sessionId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE sessionid = ? ORDER BY questionid DESC";
        List<SessionPost> questions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    SessionPost post = new SessionPost();
                    post.setQuestionId(rs.getInt("questionid"));
                    post.setSessionId(rs.getInt("sessionid"));
                    post.setOwnerName(rs.getString("ownername"));
                    post.setQuestion(rs.getString("question"));
                    post.setUserName(rs.getString("username"));
                    questions.add(post);
                }
            }
        }
        return questions;
    }
    
    public int insertAnswer(SessionAnswer answer) throws SQLException {
        String query = "INSERT INTO answers (session_id, question_id, user_name, answer) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, answer.getSessionId());
            stmt.setInt(2, answer.getQuestionId());
            stmt.setString(3, answer.getUserName());
            stmt.setString(4, answer.getAnswer());
            
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int answerId = 0;
            if (generatedKeys.next()) {
                answerId = generatedKeys.getInt(1);
            }

            return answerId;
        }
    }

    
    public void addUpvote(int answerId, int sessionId, String userName) {
    	String query = "INSERT INTO answers_upvote (answer_id, session_id, username) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, answerId);
            stmt.setInt(2, sessionId);
            stmt.setString(3, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAnswerUpvoteCount(int answerId) {
        String query = "UPDATE answers SET upvote_count = (SELECT COUNT(*) FROM answers_upvote WHERE answer_id = ?) WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, answerId);
            stmt.setInt(2, answerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SessionAnswer> getAnswersByQuestionId(int questionId) {
        List<SessionAnswer> answers = new ArrayList<>();
        String query = "SELECT * FROM answers WHERE question_id = ? ORDER BY upvote_count DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SessionAnswer answer = new SessionAnswer();
                    answer.setId(rs.getInt("id"));
                    answer.setSessionId(rs.getInt("session_id"));
                    answer.setQuestionId(rs.getInt("question_id"));
                    answer.setUserName(rs.getString("user_name"));
                    answer.setAnswer(rs.getString("answer"));
                    answer.setAnswerTime(rs.getTimestamp("answer_time"));
                    answer.setUpvoteCount(rs.getInt("upvote_count"));
                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }
    
    public int getAnswerUpvoteCount(int answerId) {
        String query = "SELECT COUNT(*) FROM answers_upvote WHERE answer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, answerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


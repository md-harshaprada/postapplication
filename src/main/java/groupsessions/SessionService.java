package groupsessions;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class SessionService {
    private SessionDAO sessionDAO;

    private final CassandraSessionDAO cassandraPostDAO = new CassandraSessionDAO();
    public SessionService() {
    	sessionDAO = new SessionDAO();
    }

    public void createSession(String owner, String sessionName, Timestamp startTime, Timestamp endTime, int groupId, String adminType, boolean onlyAdminsCanAnswer, List<String> specificAdmins) throws SQLException {
        int sessionId = sessionDAO.createSession(owner, sessionName, startTime, endTime, groupId, adminType, onlyAdminsCanAnswer);

        if ("specific".equals(adminType)) {
            for (String username : specificAdmins) {
                sessionDAO.addGroupAdmin(sessionId, username);
            }
        }
    }


    public List<Session> getActiveSessions() throws SQLException {
        return sessionDAO.getActiveSessions();
    }

    public Session getSessionById(int sessionId) throws SQLException {
        return sessionDAO.getSessionById(sessionId);
    }
    
    public boolean isOnlyAdminsCanAnswer(int sessionId) throws SQLException {
        return sessionDAO.isOnlyAdminsCanAnswer(sessionId);
    }

    public List<String> getAdminsForSession(int sessionId) throws SQLException {
        return sessionDAO.getAdminsForSession(sessionId);
    }

    
    public int addQuestion(int sessionId, String ownerName, String question, String username) throws SQLException {
        return sessionDAO.insertQuestion(sessionId, ownerName, question,username);
    }
    
    public List<SessionPost> getQuestionsBySessionId(int sessionId) throws SQLException {
        return sessionDAO.getQuestionsBySessionId(sessionId);
    }
    
    public int addAnswer(SessionAnswer answer) throws SQLException {
        return sessionDAO.insertAnswer(answer);
    }

    public List<SessionAnswer> getAnswersByQuestionId(int questionId) throws SQLException {
        return sessionDAO.getAnswersByQuestionId(questionId);
    }
    
    public void upvoteAnswer(int answerId, int sessionId, String userName,int questionId) {
        sessionDAO.addUpvote(answerId, sessionId, userName);
        sessionDAO.updateAnswerUpvoteCount(answerId);

        cassandraPostDAO.addUpvote(answerId, sessionId, userName);
        cassandraPostDAO.updateAnswerUpvoteCount(answerId,questionId);
    }
    
    public int getAnswerUpvoteCount(int answerId) {
        return sessionDAO.getAnswerUpvoteCount(answerId);
    }
}

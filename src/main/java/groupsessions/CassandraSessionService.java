package groupsessions;

import java.sql.SQLException;
import java.util.List;

public class CassandraSessionService {
	  private final CassandraSessionDAO cassandraPostDAO = new CassandraSessionDAO();
	  public void addQuestion(int questionId,int sessionId, String ownerName, String question, String username) throws SQLException {
	        cassandraPostDAO.cqlInsertQuestions(questionId, sessionId,  ownerName,  question,  username);
	    }
	  public void addAnswer(SessionAnswer answer,int answerId) throws SQLException {
	        cassandraPostDAO.cqlInsertAnswers(answer,answerId);
	    }
	  public List<SessionPost> getQuestionsBySessionId(int sessionId) throws SQLException {
	        return cassandraPostDAO.getQuestionsBySessionId(sessionId);
	    }
	  
	  public List<SessionAnswer> getAnswersByQuestionId(int questionId) throws SQLException {
	        return cassandraPostDAO.getAnswersByQuestionId(questionId);
	    }
}

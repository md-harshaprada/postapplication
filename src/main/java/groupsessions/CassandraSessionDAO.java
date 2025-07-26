package groupsessions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;

public class CassandraSessionDAO implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CassandraSessionDAO.class);
    private final Session session;

    public CassandraSessionDAO() {
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

    public void cqlInsertQuestions(int questionId, int sessionId, String ownerName, String question, String username)  {
        String query = "INSERT INTO questions (questionid, sessionid, ownername, question, username) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(questionId, sessionId, ownerName, question, username);
            session.execute(boundStatement);
        } catch (DriverException e) {
            logger.error("Error inserting question", e);
            throw e;
        }
    }

    public void cqlInsertAnswers(SessionAnswer answer,int answerId) {
        String query = "INSERT INTO answers (answer_id, session_id, question_id, user_name, answer, upvote_count) VALUES (?, ?, ?, ?, ?,?)";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(answerId, answer.getSessionId(), answer.getQuestionId(), answer.getUserName(), answer.getAnswer(),0);
            session.execute(boundStatement);
        } catch (DriverException e) {
            logger.error("Error inserting answer", e);
            throw e;
        }
    }
    
    public void addUpvote(int answerId, int sessionId, String userName) {
        String query = "INSERT INTO answers_upvote (answer_id, session_id, username) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = session.prepare(query);
            BoundStatement boundStatement = preparedStatement.bind(answerId, sessionId, userName);
            session.execute(boundStatement);
        } catch (DriverException e) {
            logger.error("Error adding upvote", e);
            throw e;
        }
    }

    public void updateAnswerUpvoteCount(int answerId, int questionId) {
        try {
            String countQuery = "SELECT COUNT(*) FROM answers_upvote WHERE answer_id = ?";
            ResultSet resultSet = session.execute(countQuery, answerId);
            Row row = resultSet.one();

            if (row != null) {
                long upvoteCountLong = row.getLong(0);

                if (upvoteCountLong > Integer.MAX_VALUE) {
                    throw new ArithmeticException("Upvote count exceeds the maximum limit for int.");
                }

                int upvoteCount = (int) upvoteCountLong;

                String updateQuery = "UPDATE answers SET upvote_count = ? WHERE question_id = ? AND answer_id = ?";
                session.execute(updateQuery, upvoteCount, questionId, answerId);
            } else {
                System.err.println("No upvote count found for answerId: " + answerId);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }



    public List<SessionPost> getQuestionsBySessionId(int sessionId) {
        String query = "SELECT questionid, sessionid, ownername, question, username FROM questions WHERE sessionid = ? ORDER BY questionid DESC";
        List<SessionPost> questions = new ArrayList<>();

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(sessionId);
        ResultSet resultSet = session.execute(boundStatement);

        for (Row row : resultSet) {
            SessionPost post = new SessionPost();
            post.setQuestionId(row.getInt("questionid"));
            post.setSessionId(row.getInt("sessionid"));
            post.setOwnerName(row.getString("ownername"));
            post.setQuestion(row.getString("question"));
            post.setUserName(row.getString("username"));
            questions.add(post);
        }

        return questions;
    }
    
    public List<SessionAnswer> getAnswersByQuestionId(int questionId) {
        String query = "SELECT answer_id, session_id, question_id, user_name, answer, upvote_count FROM answers WHERE question_id = ?";
        List<SessionAnswer> answers = new ArrayList<>();

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(questionId);

        ResultSet resultSet = session.execute(boundStatement);

        for (Row row : resultSet) {
            SessionAnswer answer = new SessionAnswer();
            answer.setId(row.getInt("answer_id"));
            answer.setSessionId(row.getInt("session_id"));
            answer.setQuestionId(row.getInt("question_id"));
            answer.setUserName(row.getString("user_name"));
            answer.setAnswer(row.getString("answer"));
            answer.setUpvoteCount(row.getInt("upvote_count"));
            answers.add(answer);
        }

        return answers;
    }
    
    @Override
    public void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}

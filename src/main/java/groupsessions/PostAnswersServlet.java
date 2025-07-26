package groupsessions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/postanswers")
public class PostAnswersServlet extends HttpServlet {

	   private SessionService sessionService;
	    private CassandraSessionService cassandraSessionService; 
	    @Override
	    public void init() throws ServletException {
	        sessionService = new SessionService();
	        cassandraSessionService = new CassandraSessionService();
	    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int sessionId = Integer.parseInt(request.getParameter("sessionId"));
        int questionId = Integer.parseInt(request.getParameter("questionId"));
        String username = (String) request.getSession().getAttribute("username");
        String answerText = request.getParameter("answer");

        SessionAnswer answer = new SessionAnswer();
        answer.setSessionId(sessionId);
        answer.setQuestionId(questionId);
        answer.setUserName(username);
        answer.setAnswer(answerText);
        

        String isCassandraParam = request.getParameter("isCassandra");
        boolean isCassandra = isCassandraParam != null && Boolean.parseBoolean(isCassandraParam);
        try {
            int answerId = sessionService.addAnswer(answer);
            cassandraSessionService.addAnswer(answer, answerId);
            response.sendRedirect("groupsessions.jsp?sessionId=" + sessionId + "&isCassandra="+isCassandra);
        } catch (SQLException e) {
            throw new ServletException("Error posting answer", e);
        }
    }
}

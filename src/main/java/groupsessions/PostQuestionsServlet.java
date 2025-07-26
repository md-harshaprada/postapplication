package groupsessions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/postquestions")
public class PostQuestionsServlet extends HttpServlet {
    private SessionService sessionService;
    private CassandraSessionService cassandraSessionService; 
    @Override
    public void init() throws ServletException {
        sessionService = new SessionService();
        cassandraSessionService = new CassandraSessionService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionIdStr = request.getParameter("sessionId");
        String ownerName = request.getParameter("ownerName");
        String question = request.getParameter("question");
        String username = (String) request.getSession().getAttribute("username");

        if (sessionIdStr != null && ownerName != null && question != null) {
            try {
                int sessionId = Integer.parseInt(sessionIdStr);
                int questionId = sessionService.addQuestion(sessionId, ownerName, question,username);
                cassandraSessionService.addQuestion(questionId,sessionId, ownerName, question,username);
                response.sendRedirect("groupsessions.jsp?sessionId=" + sessionId);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to post question.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters.");
        }
    }
}

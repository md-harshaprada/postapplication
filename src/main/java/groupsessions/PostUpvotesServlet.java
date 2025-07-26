package groupsessions;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/postupvotes")
public class PostUpvotesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int answerId = Integer.parseInt(req.getParameter("answerId"));
        int sessionId = Integer.parseInt(req.getParameter("sessionId"));
        int questionId = Integer.parseInt(req.getParameter("questionId"));
        String userName = (String) req.getSession().getAttribute("username");

        SessionService sessionService = new SessionService();
        sessionService.upvoteAnswer(answerId, sessionId, userName,questionId);
        int upvoteCount = sessionService.getAnswerUpvoteCount(answerId);

        resp.setContentType("text/plain");
        resp.getWriter().write(String.valueOf(upvoteCount));
    }
}

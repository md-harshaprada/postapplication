package secondwebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/likeReply")
public class LikeReplyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        int replyId = Integer.parseInt(request.getParameter("replyId"));

        PostService postService = new PostService();
        postService.likeReply(replyId, username);
        int likeCount = postService.getReplyLikesCount(replyId);

        response.getWriter().write(String.valueOf(likeCount));
    }
}
package secondwebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/likePost")
public class LikePostServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        int postId = Integer.parseInt(request.getParameter("postId"));

        PostService postService = new PostService();
        postService.likePost(postId, username);
        int likeCount = postService.getPostLikesCount(postId);

        response.getWriter().write(String.valueOf(likeCount));
    }
}

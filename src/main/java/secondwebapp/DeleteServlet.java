package secondwebapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String postIdParam = request.getParameter("postId");
        String replyIdParam = request.getParameter("replyId");

        PostService postService = new PostService();
        CassandraPostService cassandraPostService= new CassandraPostService();
        
        Integer groupId = null;
        String groupIdParam = request.getParameter("groupId");
        if (groupIdParam != null && !groupIdParam.isEmpty()) {
            try {
                groupId = Integer.parseInt(groupIdParam);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String groupName = null;
        String groupNameParam = request.getParameter("groupName");
        if (groupNameParam != null && !groupNameParam.isEmpty()) {
            groupName = groupNameParam;
        }

        String isCassandraParam = request.getParameter("isCassandra");
        boolean isCassandra = isCassandraParam != null && Boolean.parseBoolean(isCassandraParam);

        String redirectDashboardPage = "dashboard.jsp?isCassandra=" + isCassandra;
        
        try {
            if (postIdParam != null) {
                int postId = Integer.parseInt(postIdParam);
                postService.deletePostAndReplies(postId);
                cassandraPostService.deletePostAndReplies(postId);
            } else if (replyIdParam != null) {
                int replyId = Integer.parseInt(replyIdParam);
                postService.deleteReply(replyId);
                cassandraPostService.deleteReply(replyId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String redirectPage = (groupId != null) ? "group.jsp?groupId=" + groupId + "&groupName=" + groupName + "&isCassandra=" +  isCassandra: redirectDashboardPage;
        response.sendRedirect(redirectPage);
    }
}

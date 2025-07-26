package secondwebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/post")
@MultipartConfig
public class PostServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        String content = request.getParameter("content");
        String postShared = request.getParameter("share");
        boolean commentsEnabled = request.getParameter("commentsEnabled") != null;

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

        PostService postService = new PostService();
        CassandraPostService cassandraPostService = new CassandraPostService();
        
        String redirectDashboardPage = "dashboard.jsp?isCassandra=" + isCassandra;

        if (request.getParameter("replyContent") != null) {
            int postId = Integer.parseInt(request.getParameter("postId"));
            String replyContent = request.getParameter("replyContent");
            Integer parentReplyId = request.getParameter("parentReplyId") != null ? Integer.parseInt(request.getParameter("parentReplyId")) : null;

            int replyId = postService.addReply(postId, username, replyContent, parentReplyId, groupId);
            cassandraPostService.addReply(replyId, postId, username, replyContent, parentReplyId, groupId);
            String redirectPage = (groupId != null) ? "group.jsp?groupId=" + groupId + "&groupName=" + groupName + "&isCassandra=" +  isCassandra: redirectDashboardPage;
            response.sendRedirect(redirectPage);

        } else {
            Part filePart = request.getPart("attachment");
            String filename = null;
            String filetype = null;
            InputStream fileData = null;

            if (filePart != null && filePart.getSize() > 0) {
                filename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                filetype = filePart.getContentType();
                fileData = filePart.getInputStream();
            }

            List<String> specificUsers = null;
            if ("specific".equals(postShared)) {
                String[] selectedUsers = request.getParameterValues("specificUsers");
                if (selectedUsers != null) {
                    specificUsers = new ArrayList<>();
                    Collections.addAll(specificUsers, selectedUsers);
                }
            }

            int postId = postService.postContent(username, content, postShared, specificUsers, commentsEnabled, groupId, filename, filetype, fileData);

            if (fileData != null) {
                byte[] fileBytes = readInputStreamToByteArray(fileData);
                ByteBuffer fileBuffer = ByteBuffer.wrap(fileBytes);
                cassandraPostService.postContent(postId, username, content, postShared, specificUsers, commentsEnabled, groupId, filename, filetype, fileBuffer);
            } else {
                cassandraPostService.postContent(postId, username, content, postShared, specificUsers, commentsEnabled, groupId, null, null, null);
            }

            String redirectPage = (groupId != null) ? "group.jsp?groupId=" + groupId + "&groupName=" + groupName + "&isCassandra=" +  isCassandra: redirectDashboardPage;
            response.sendRedirect(redirectPage);
        }
    }
    
    private byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PostService postService = new PostService();
        
        String searchPost = request.getParameter("searchposts");
        String sessionUsername = request.getParameter("username");
        
        List<Post> searchedPosts = new ArrayList<>();
        
        List<Post> specificPosts = new ArrayList<>();
        
        if (searchPost != null) {
            try {
                searchedPosts = postService.getSearchPosts(searchPost);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        if (sessionUsername != null) {
            try {
                specificPosts = postService.getSpecificPostsForUser(sessionUsername);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("searchedPosts", searchedPosts);
        result.put("specificPosts", specificPosts);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(result);
        out.print(json);
        out.flush();
    }

}

package group;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet("/createGroup")
public class CreateGroupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String groupName = request.getParameter("groupName");
        String[] groupUsers = request.getParameterValues("groupUsers");

        GroupService groupService = new GroupService();
        CassandraGroupService cassandraGroupService = new CassandraGroupService();
        try {
            Integer groupId = groupService.createGroup(groupName);
            UUID grpID = cassandraGroupService.createGroup(groupName);
            List<String> userList = Arrays.asList(groupUsers);
            groupService.addUsersToGroup(groupId, userList);
            cassandraGroupService.addUsersToGroup(grpID, userList);
            response.sendRedirect("group.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
}




package groupsessions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import group.GroupService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/createSession")
public class CreateSessionServlet extends HttpServlet {
    private SessionService sessionService;
    private GroupService groupService = new GroupService();

    @Override
    public void init() throws ServletException {
        sessionService = new SessionService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String owner = request.getParameter("owner");
        String sessionName = request.getParameter("sessionName");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        int groupId = Integer.parseInt(request.getParameter("groupId"));
        String adminType = request.getParameter("admin");
        boolean onlyAdminsCanAnswer = request.getParameter("onlyAdmins") != null;

        List<String> specificAdmins = new ArrayList<>();
        if ("specific".equals(adminType)) {
            String[] selectedAdmins = request.getParameterValues("specificUsers");
            if (selectedAdmins != null) {
                specificAdmins.addAll(Arrays.asList(selectedAdmins));
            }
        }

        try {
            Timestamp startTime = convertToTimestamp(startTimeStr);
            Timestamp endTime = convertToTimestamp(endTimeStr);

            sessionService.createSession(owner, sessionName, startTime, endTime, groupId, adminType, onlyAdminsCanAnswer, specificAdmins);
            response.sendRedirect("groupsessions.jsp");
        } catch (SQLException | ParseException e) {
            throw new ServletException("Error creating session", e);
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String groupIdStr = request.getParameter("groupId");
        List<String> groupMembers = new ArrayList<>();

        if (groupIdStr != null) {
            try {
                int groupId = Integer.parseInt(groupIdStr);
                groupMembers = groupService.getGroupMembersByGroupId(groupId);
            } catch (NumberFormatException e) {
                e.printStackTrace(); 
            }
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(groupMembers);
        out.print(json);
        out.flush();
    }

    private Timestamp convertToTimestamp(String datetimeLocal) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date parsedDate = inputFormat.parse(datetimeLocal);
        String formattedDate = outputFormat.format(parsedDate);

        return Timestamp.valueOf(formattedDate);
    }
}

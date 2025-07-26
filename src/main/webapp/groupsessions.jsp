<%@ page import="java.util.List" %>
<%@ page import="groupsessions.Session" %>
<%@ page import="groupsessions.SessionService" %>
<%@ page import="groupsessions.CassandraSessionService" %>
<%@ page import="java.lang.Math" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="groupsessions.SessionService" %>
<%@ page import="groupsessions.SessionPost" %>
<%@ page import="groupsessions.SessionAnswer" %>
<%@ page import="group.GroupService"%>
<%@ page import="group.GroupDAO"%>

<%
    SessionService sessionService = new SessionService();
CassandraSessionService cassandraSessionService = new CassandraSessionService();
List<Session> filteredSessions = new ArrayList<>();
    List<Session> activeSessions = sessionService.getActiveSessions();
    GroupService groupService = new GroupService();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group Sessions</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css">
    <link rel="stylesheet" href="static/css/groupsessions-style.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
     <script src="static/js/groupsessions-script.js"></script>
 <% 
 String sessionIdStr = request.getParameter("sessionId");
   Boolean isCassandra = Boolean.parseBoolean(request.getParameter("isCassandra"));
%>
</head>
<body>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Post Forum</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse justify-content-between" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" aria-current="page" href="dashboard.jsp">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="group.jsp">Group</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="groupsessions.jsp">Sessions</a>
                </li>
            </ul>
            <div class="d-flex align-items-center">
                <input type="checkbox" id="toggle-two" data-toggle="toggle" data-on="Cassandra" data-off="MySQL">
                <a href="index.jsp" class="btn btn-secondary ms-3">Logout</a>
            </div>
        </div>
    </div>
</nav>

    <div class="wrapper">
        <div class="sidebar">
            <nav>
                <a id="createSessionLink" class="nav-link">Create Session</a>
                <a id="activeSessionsLink" class="nav-link">Active Sessions</a>
            </nav>
        </div>

        <div class="content">
            <div id="createSessionContent" class="container mt-4">
    <h2 align="center">Create New Session</h2>
    <div class="row justify-content-center">
        <div class="col-md-6">
            <form action="createSession" method="post">
                <div class="mb-3">
                    <label for="owner" class="form-label">Owner:</label>
                    <input type="text" id="owner" name="owner" class="form-control" placeholder="Owner Name" required>
                </div>
                <div class="mb-3">
                    <label for="sessionName" class="form-label">Session Name:</label>
                    <input type="text" id="sessionName" name="sessionName" class="form-control" placeholder="Session Name" required>
                </div>
                <div class="mb-3">
                    <label for="startTime" class="form-label">Start Time:</label>
                    <input type="datetime-local" id="startTime" name="startTime" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label for="endTime" class="form-label">End Time:</label>
                    <input type="datetime-local" id="endTime" name="endTime" class="form-control" required>
                </div>
                <div class="mb-3">
        <label for="group" class="form-label">Group:</label>
        <select id="group" name="groupId" class="form-control" required onchange="updateGroupMembers()">
          <option value="">Please select a group</option>
            <%
                List<Integer> userGroups = groupService.getUserGroups((String) session.getAttribute("username"));
                if (!userGroups.isEmpty()) {
                    for (Integer groupId : userGroups) {
                        GroupDAO groupDAO = new GroupDAO();
                        String groupName = groupDAO.getGroupNameById(groupId);
            %>
            <option value="<%= groupId %>"><%= groupName %></option>
            <% 
                    }
                } else {
            %>
            <option value="">No groups available</option>
            <% 
                }
            %>
        </select>
    </div>

                 <div class="mb-3">
        <label for="admin">Select Admins:</label>
        <select name="admin" id="admin" onchange="toggleSpecificUsers()">
            <option value="me">Only Me</option>
            <option value="specific">Specific Users</option>
        </select>
    </div>

 <div id="specific-users" style="display: none;">
        <div class="user-checkboxes" id="user-checkboxes">
        </div>
    </div>


<div class="mb-3">
    <label for="onlyAdmins" class="form-label">Only Admins Can Answer:</label>
    <input type="checkbox" id="onlyAdmins" name="onlyAdmins">
</div>
                
                <button type="submit" class="btn btn-primary">Create Session</button>
            </form>
        </div>
    </div>
</div>


            <div id="activeSessionsContent">
    <h2>Active Sessions</h2>
    <%
        String username = (String) session.getAttribute("username");

        if (userGroups.isEmpty()) {
    %>
        <p>No active sessions available.</p>
    <%
        } else {
            for (Session eachSession : activeSessions) {
                if (userGroups.contains(eachSession.getGroupId())) {
                    filteredSessions.add(eachSession);
                }
            }
            if (filteredSessions.isEmpty()) {
    %>
                <p>No active sessions available for your groups.</p>
    <%
            } else {
    %>
                <div class="row">
                    <% for (Session eachSession : filteredSessions) { %>
                        <div class="col-md-4">
                            <a href="groupsessions.jsp?sessionId=<%= eachSession.getId() %>&isCassandra=<%= String.valueOf(isCassandra) %>"  class="card-link">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title"><%= eachSession.getSessionName() %></h5>
                                        <p class="card-text">
                                            <strong><i>Owner:</i></strong> <%= eachSession.getOwner() %>
                                            <br><strong><i>Session Ends in:</i></strong> <span id="countdown_<%= eachSession.getId() %>" class="countdown"></span>
                                        </p>
                                    </div>
                                </div>
                            </a>
                        </div>
                    <% } %>
                </div>
    <%
            }
        }
    %>
</div>



            <div id="sessionDetailsContent" data-session-id="<%= sessionIdStr %>">
               <%
    if (sessionIdStr != null) {
    	int sessionId = Integer.parseInt(sessionIdStr);
        Session retrievedSession = sessionService.getSessionById(sessionId);
        if (retrievedSession != null) {
            long endTimeMillis = retrievedSession.getEndTime().getTime();
%>               

<%
List<SessionPost> sessionQuestions = null;
if (isCassandra) {
	sessionQuestions = cassandraSessionService.getQuestionsBySessionId(retrievedSession.getId());
}
else{
	sessionQuestions = sessionService.getQuestionsBySessionId(retrievedSession.getId());
}
boolean onlyAdminsCanAnswer = sessionService.isOnlyAdminsCanAnswer(sessionId);
List<String> adminsForSession = onlyAdminsCanAnswer ? sessionService.getAdminsForSession(sessionId) : null;
%>


<h2>Session : <%= retrievedSession.getSessionName() %></h2>
<div id="questions-list">
    <h4>Questions:</h4>
    <% if (sessionQuestions != null && !sessionQuestions.isEmpty()) { %>
        <ul class="list-group mb-3">
            <% for (SessionPost question : sessionQuestions) { %>
                <li class="list-group-item">
                    <strong><%= question.getUserName() %>:</strong> <%= question.getQuestion() %>
                    <a href="#" class="btn btn-info btn-sm show-answers" data-question-id="<%= question.getQuestionId() %>">Show Answers</a>
                    <% if (!onlyAdminsCanAnswer || adminsForSession.contains((String) session.getAttribute("username"))) { %>
                        <a href="#" class="btn btn-info btn-sm reply-toggle" data-question-id="<%= question.getQuestionId() %>">Reply</a>
                    <% } %>
                    
                    <div class="answer-list" id="answer-list-<%= question.getQuestionId() %>" style="display:none;">
                        <ul class="list-group mt-2">
                            <% 
                            List<SessionAnswer> answers =  null;
                            if(isCassandra){
                            	answers = cassandraSessionService.getAnswersByQuestionId(question.getQuestionId());
                            }
                            else{
                                answers = sessionService.getAnswersByQuestionId(question.getQuestionId());
                            }
                                if (answers != null && !answers.isEmpty()) {
                                    answers.sort((a, b) -> Integer.compare(b.getUpvoteCount(), a.getUpvoteCount()));
                                    for (SessionAnswer answer : answers) { %>
                                        <li class="list-group-item">
                                        <%
                                        String content = answer.getAnswer();
                                        content = content.replace("\n", "<br>").replace(" ", "&nbsp;").replace("\"", "&quot;");
                                        %>
                                             <div style='padding: 3px; width: 1150px; word-break: break-all; word-wrap: break-word;'>
                                             <strong><%= answer.getUserName() %>:</strong> <%= content %>
                                           <a href="#" class="btn btn-outline-primary btn-sm upvote-btn" onclick="upvoteAnswer(<%= answer.getId() %>, <%= retrievedSession.getId() %>,<%= question.getQuestionId()%>)">
    <i class="fa fa-arrow-up"></i>
</a>
<span class="upvote-count" id="upvote-count-answer-<%= answer.getId() %>">
    <%= answer.getUpvoteCount() %> Upvotes
</span></div>
                                        </li>
                                    <% } 
                                } else { %>
                                    <li class="list-group-item">No answers yet.</li>
                            <% } %>
                        </ul>
                    </div>

                    <% if (!onlyAdminsCanAnswer || adminsForSession.contains((String) session.getAttribute("username"))) { %>
                        <div class="answer-form" id="answer-form-<%= question.getQuestionId() %>" style="display:none;">
                            <form action="postanswers" method="post" id="answer-form">
                                <input type="hidden" name="sessionId" value="<%= retrievedSession.getId() %>">
                                <input type="hidden" name="questionId" value="<%= question.getQuestionId() %>">
                                <input type="hidden" name="userName" value="<%= session.getAttribute("username") %>">
			<input type="hidden" id="isCassandraField-answer" name="isCassandra" value="">
                                <div class="input-group mt-2">
                                    <textarea name="answer" class="form-control" rows="1" placeholder="Type your answer here..." required></textarea>
                                    <button type="submit" class="btn btn-primary">Submit Answer</button>
                                </div>
                            </form>
                        </div>
                    <% } %>
                </li>
            <% } %>
        </ul>
    <% } else { %>
        <p>No questions have been asked yet.</p>
    <% } %>
</div>

<div class="fixed-bottom-form">
    <form action="postquestions" method="post">
        <input type="hidden" name="sessionId" value="<%= retrievedSession.getId() %>">
        <input type="hidden" name="ownerName" value="<%= retrievedSession.getOwner() %>">
        <div class="input-group">
            <textarea name="question" class="form-control" rows="1" placeholder="Type your question here..." required></textarea>
            <button type="submit" class="btn btn-primary">Add Question</button>
        </div>
    </form>
</div>
                <%
                        }
                    }
                %>
            </div>
        </div>
    </div> 
    
    
    <script>
        $(document).ready(function() {
            <% for (Session eachSession : filteredSessions) { %>
                const endTimeMillis_<%= eachSession.getId() %> = <%= eachSession.getEndTime().getTime() %>;
                updateCountdown('countdown_<%= eachSession.getId() %>', endTimeMillis_<%= eachSession.getId() %>);
            <% } %>
        });

        function updateCountdown(id, endTime) {
            const countdownElement = document.getElementById(id);
            
            function formatTime(ms) {
                const totalSeconds = Math.max(Math.floor(ms / 1000), 0);
                const hours = Math.floor(totalSeconds / 3600);
                const minutes = Math.floor((totalSeconds % 3600) / 60);
                const seconds = totalSeconds % 60;

                return String(hours).padStart(2, '0') + ':' +
                       String(minutes).padStart(2, '0') + ':' +
                       String(seconds).padStart(2, '0');
            }

            function update() {
                const now = new Date().getTime();
                const remainingTime = endTime - now;
                
                if (remainingTime <= 0) {
                    countdownElement.textContent = '00:00:00';
                } else {
                    countdownElement.textContent = formatTime(remainingTime);
                    setTimeout(update, 1000);
                }
            }
            
            update();
        }
    </script>
    
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

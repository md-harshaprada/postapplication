<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="secondwebapp.UserService"%>
<%@ page import="secondwebapp.PostService"%>
<%@ page import="secondwebapp.Post"%>
<%@ page import="secondwebapp.PostCql"%>
<%@ page import="secondwebapp.Reply"%>
<%@ page import="secondwebapp.HtmlUtils"%>
<%@ page import="group.GroupService"%>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="group.GroupDAO"%>
<%@ page import="secondwebapp.CassandraPostService"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Groups</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css">
    <link rel="stylesheet" href="static/css/group-style.css">
     <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
     <script src="https://cdn.ckeditor.com/4.17.2/standard/ckeditor.js"></script>
     <script src="static/js/group-script.js"></script>
     <% 
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
                    <a class="nav-link active" href="group.jsp">Group</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="groupsessions.jsp">Sessions</a>
                </li>
            </ul>
            <div class="d-flex align-items-center">
                <input type="checkbox" id="toggle-two" data-toggle="toggle" data-on="Cassandra" data-off="MySQL">
                <a href="index.jsp" class="btn btn-secondary ms-3">Logout</a>
            </div>
        </div>
    </div>
</nav>
<div class="container mt-4">
     <div class="group-management-header">
            <h1>Group Management</h1>
            <button class="btn btn-primary" onclick="showUserSelection()">Create New Group</button>
        </div>

        <div id="user-selection" style="display: none;">
            <form action="createGroup" method="post">
                <div class="mb-3">
                    <label for="groupName" class="form-label">Group Name:</label>
                    <input type="text" class="form-control" id="groupName" name="groupName" required>
                </div>
                <div class="user-checkboxes">
                    <label>Select Users:</label><br>
                    <%
                        UserService userService = new UserService();
                        List<String> allUsers = userService.getAllUsers();
                        for (String user : allUsers) {
                    %>
                    <div class="form-check form-check-inline">
                    <input type="checkbox" name="groupUsers" value="<%= user %>" class="form-check-input">
                    <%= user %><br><br>
                    </div>
                    <%
                        }
                    %>
                </div>
                <button type="submit" class="btn btn-primary">Create Group</button>
            </form>
        </div>
    
     <div class="available-groups-header">
            <h2>Available Groups</h2>
            <div class="row">
            <%
                GroupService groupService = new GroupService();
                List<Integer> userGroups = groupService.getUserGroups((String) session.getAttribute("username"));
                if (userGroups.isEmpty()) {
                    out.print("<p>No groups available.</p>");
                } else {
                    for (Integer groupId : userGroups) {
                        GroupDAO groupDAO = new GroupDAO();
                        String groupName = groupDAO.getGroupNameById(groupId);
            %>
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title"><%= groupName %></h5>
                      <a href="group.jsp?groupId=<%= groupId %>&groupName=<%= groupName %>&isCassandra=<%= String.valueOf(isCassandra) %>" 
   class="btn btn-primary btn-sm">View Posts</a>
                       
 </div>
                </div>
            </div>
            <%
                    }
                }
            %>
            </div>
        </div>
    
    <%  
    if (request.getParameter("groupId") != null) {
        Integer groupId = Integer.parseInt(request.getParameter("groupId"));
        try {
    String groupName = request.getParameter("groupName");
    if (groupName != null && !groupName.isEmpty()) {
%>
     <h2>Group: <%= groupName %></h2>
     <%
    } else {
%>
    <h2>Group: Unknown</h2>
<%
    }
%>
    <form action="post" method="post" enctype="multipart/form-data">
    <input type="hidden" name="groupId" value="<%= groupId %>">
    <input type="hidden" id="isCassandraField" name="isCassandra" value="">
    <input type="hidden" name="groupName" value="<%= groupName %>">            
            <div class="mb-3">
				<label for="content" class="form-label">Post	 Content:</label>
				<textarea class="form-control" id="content" name="content" rows="3"></textarea>
				 <script>
				 CKEDITOR.replace('content', {
				        filebrowserUploadUrl: '/secondwebapp/upload?type=Files',
				        filebrowserImageUploadUrl: '/secondwebapp/upload?type=Images'
				    });
        </script>
				<label for="share">Share with:</label>
				 <select name="share" id="share">
					<option value="all">All Users</option>
					<option value="me">Only Me</option>
				</select>
			</div>
            
            <div class="mb-3">
        <label for="attachment">Attachment:</label>
        <input type="file" class="form-control" id="attachment" name="attachment">
    </div>
            <div class="mb-3">
                <label for="commentsEnabled">Enable Comments:</label> 
               <input type="checkbox" id="commentsEnabled" name="commentsEnabled" value="true">
            </div>
            <button type="submit" class="btn btn-primary">Post</button>
        </form>
        <br>
<%  
isCassandra = Boolean.parseBoolean(request.getParameter("isCassandra"));
if (isCassandra) {
	PostService postService = new PostService();
	CassandraPostService cassandraPostService = new CassandraPostService();
List<PostCql> posts = cassandraPostService.getPostsByGroup(groupId);

for (PostCql post : posts) { 
    List<String> attachments = post.getAttachments();
%>
<div class="card mb-3">
    <div class="card-body">
         <div class="ms-2 d-flex align-items-center">
    <h5 class="card-title me-3">Posted by: <%= post.getName() %></h5>
    <span class="like-btn" onclick="likePost(<%= post.getId() %>)">
        <i class="fa-solid fa-thumbs-up"></i>
    </span> 
    <span class="like-count ms-2" id="like-count-post-<%= post.getId() %>">
        <%= postService.getPostLikesCount(post.getId()) %> Likes
    </span>
</div>
        <div class="mt-3">
            <% 
                String content = post.getContent();
                int maxLength = 450;
                boolean isLongContent = content.replaceAll("<[^>]*>", "").length() > maxLength;
                String truncatedContent = isLongContent ? HtmlUtils.truncateHtml(content, maxLength) + "..." : content;
            %>
            <div class="d-flex justify-content-between align-items-center">
                  <div style="flex: 1;">
                    <span id="short-post-<%= post.getId() %>" ><%= truncatedContent %></span>
                    <% if (isLongContent) { %>
                        <span id="full-post-<%= post.getId() %>" style="display: none;"><%= content %></span>
                        <a href="javascript:void(0);" onclick="toggleContent('post', <%= post.getId() %>)" id="see-more-post-<%= post.getId() %>">See more</a>
                        <a href="javascript:void(0);" onclick="toggleContent('post', <%= post.getId() %>)" id="see-less-post-<%= post.getId() %>" style="display: none;">See less</a>
                    <% } %>
                </div>
            </div>
        </div>

            <% if (!attachments.isEmpty()) { %>
            
       <p><strong><i>Attachments:</i></strong>
                <% for (String attachment : attachments) { %>
                     <a href="downloadAttachment?postId=<%= post.getId() %>"><%= attachment %></a>
                <% } %>    
            </p>
            <% } %>  
            <h6>Replies:</h6>
 <ul>
                <%
                    if (post.isCommentsEnabled()) {
                        List<Reply> replies = cassandraPostService.getRepliesForPost(post.getId());

                        for (Reply reply : replies) {
                            if (reply.getParentReplyId() == null) { 
                %>
                <li>
                    <strong><%= reply.getUsername() %>:</strong> <%= reply.getContent() %>
                    <span class="like-btn" onclick="likeReply(<%= reply.getId() %>)">
                        <i class="fa-solid fa-thumbs-up"></i>
                    </span>
                    <span class="like-count" id="like-count-reply-<%= reply.getId() %>">
                        <%= postService.getReplyLikesCount(reply.getId()) %> Likes
                    </span>
                    
                    <ul>
                    <% for (Reply nestedReply : replies) {
                            if (nestedReply.getParentReplyId() != null && nestedReply.getParentReplyId().equals(reply.getId())) { %>
                                <li>
                                    <strong><%= nestedReply.getUsername() %>:</strong> <%= nestedReply.getContent() %>
                                    <span class="like-btn" onclick="likeReply(<%= nestedReply.getId() %>)">
                                        <i class="fa-solid fa-thumbs-up"></i>
                                    </span>
                                    <span class="like-count" id="like-count-reply-<%= nestedReply.getId() %>">
                                        <%= postService.getReplyLikesCount(nestedReply.getId()) %> Likes
                                    </span>
                                </li>
                    <% } } %>
                    </ul>
                    <form action="post" method="post" class="mt-2">
    <input type="hidden" name="postId" value="<%= reply.getPostId() %>">
    <input type="hidden" name="groupId" value="<%= groupId %>">
    <input type="hidden" name="groupName" value="<%= groupName %>">
    <input type="hidden" name="parentReplyId" value="<%= reply.getId() %>">
    <textarea class="form-control" name="replyContent" rows="1" placeholder="Write a reply..."></textarea>
    <button type="submit" class="btn btn-secondary btn-sm mt-2">Reply</button>
    <a href="delete?replyId=<%= reply.getId() %>&groupId=<%= groupId %>&groupName=<%= groupName %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete</a>
</form>
                </li>
                <%
                            }
                        }
                    } else {
                %>
                    <li>Comments are disabled for this post. 
<a href="delete?postId=<%= post.getId() %>&groupId=<%= groupId %>&groupName=<%= groupName %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete Post</a></li>
                <%
                    }
                %>
                </ul>

                <% if (post.isCommentsEnabled()) { %>
                <form action="post" method="post">
    <input type="hidden" name="postId" value="<%= post.getId() %>">
    <input type="hidden" name="groupName" value="<%= groupName %>">
    <input type="hidden" name="groupId" value="<%= groupId %>">
    <div class="mb-3">
        <textarea class="form-control" name="replyContent" rows="1" placeholder="Write a reply..."></textarea>
    </div>
    <button type="submit" class="btn btn-secondary btn-sm mt-2">Reply</button>
<a href="delete?postId=<%= post.getId() %>&groupId=<%= groupId %>&groupName=<%= groupName %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete Post</a>
</form>
                <%  }%>             
                <br>
            </div>
        </div>
        <% }
        } 

else{
	PostService postService = new PostService();
List<Post> posts = postService.getPostsByGroup(groupId);

for (Post post : posts) { 
    List<String> attachments = post.getAttachments();
%>
<div class="card mb-3">
    <div class="card-body">
         <div class="ms-2 d-flex align-items-center">
    <h5 class="card-title me-3">Posted by: <%= post.getName() %></h5>
    <span class="like-btn" onclick="likePost(<%= post.getId() %>)">
        <i class="fa-solid fa-thumbs-up"></i>
    </span> 
    <span class="like-count ms-2" id="like-count-post-<%= post.getId() %>">
        <%= postService.getPostLikesCount(post.getId()) %> Likes
    </span>
</div>
        <div class="mt-3">
            <% 
                String content = post.getContent();
                int maxLength = 450;
                boolean isLongContent = content.replaceAll("<[^>]*>", "").length() > maxLength;
                String truncatedContent = isLongContent ? HtmlUtils.truncateHtml(content, maxLength) + "..." : content;
            %>
            <div class="d-flex justify-content-between align-items-center">
                  <div style="flex: 1;">
                    <span id="short-post-<%= post.getId() %>" ><%= truncatedContent %></span>
                    <% if (isLongContent) { %>
                        <span id="full-post-<%= post.getId() %>" style="display: none;"><%= content %></span>
                        <a href="javascript:void(0);" onclick="toggleContent('post', <%= post.getId() %>)" id="see-more-post-<%= post.getId() %>">See more</a>
                        <a href="javascript:void(0);" onclick="toggleContent('post', <%= post.getId() %>)" id="see-less-post-<%= post.getId() %>" style="display: none;">See less</a>
                    <% } %>
                </div>
            </div>
        </div>

            <% if (!attachments.isEmpty()) { %>
            
       <p><strong><i>Attachments:</i></strong>
                <% for (String attachment : attachments) { %>
                     <a href="downloadAttachment?postId=<%= post.getId() %>"><%= attachment %></a>
                <% } %>    
            </p>
            <% } %>  
            <h6>Replies:</h6>
 <ul>
                <%
                    if (post.isCommentsEnabled()) {
                        List<Reply> replies = postService.getRepliesForPost(post.getId());

                        for (Reply reply : replies) {
                            if (reply.getParentReplyId() == null) { 
                %>
                <li>
                    <strong><%= reply.getUsername() %>:</strong> <%= reply.getContent() %>
                    <span class="like-btn" onclick="likeReply(<%= reply.getId() %>)">
                        <i class="fa-solid fa-thumbs-up"></i>
                    </span>
                    <span class="like-count" id="like-count-reply-<%= reply.getId() %>">
                        <%= postService.getReplyLikesCount(reply.getId()) %> Likes
                    </span>
                    
                    <ul>
                    <% for (Reply nestedReply : replies) {
                            if (nestedReply.getParentReplyId() != null && nestedReply.getParentReplyId().equals(reply.getId())) { %>
                                <li>
                                    <strong><%= nestedReply.getUsername() %>:</strong> <%= nestedReply.getContent() %>
                                    <span class="like-btn" onclick="likeReply(<%= nestedReply.getId() %>)">
                                        <i class="fa-solid fa-thumbs-up"></i>
                                    </span>
                                    <span class="like-count" id="like-count-reply-<%= nestedReply.getId() %>">
                                        <%= postService.getReplyLikesCount(nestedReply.getId()) %> Likes
                                    </span>
                                </li>
                    <% } } %>
                    </ul>
                    <form action="post" method="post" class="mt-2">
    <input type="hidden" name="postId" value="<%= reply.getPostId() %>">
    <input type="hidden" name="groupId" value="<%= groupId %>">
    <input type="hidden" name="groupName" value="<%= groupName %>">
    <input type="hidden" name="parentReplyId" value="<%= reply.getId() %>">
    <textarea class="form-control" name="replyContent" rows="1" placeholder="Write a reply..."></textarea>
    <button type="submit" class="btn btn-secondary btn-sm mt-2">Reply</button>
    <a href="delete?replyId=<%= reply.getId() %>&groupId=<%= groupId %>&groupName=<%= groupName %>" class="btn btn-danger btn-sm mt-2">Delete</a>
</form>
                </li>
                <%
                            }
                        }
                    } else {
                %>
                    <li>Comments are disabled for this post. 
<a href="delete?postId=<%= post.getId() %>&groupId=<%= groupId %>&groupName=<%= groupName %>" class="btn btn-danger btn-sm mt-2">Delete Post</a></li>
                <%
                    }
                %>
                </ul>

                <% if (post.isCommentsEnabled()) { %>
                <form action="post" method="post">
    <input type="hidden" name="postId" value="<%= post.getId() %>">
    <input type="hidden" name="groupName" value="<%= groupName %>">
    <input type="hidden" name="groupId" value="<%= groupId %>">
    <div class="mb-3">
        <textarea class="form-control" name="replyContent" rows="1" placeholder="Write a reply..."></textarea>
    </div>
    <button type="submit" class="btn btn-secondary btn-sm mt-2">Reply</button>
<a href="delete?postId=<%= post.getId() %>&groupId=<%= groupId %>&groupName=<%= groupName %>" class="btn btn-danger btn-sm mt-2">Delete Post</a>
</form>
                <%  }%>             
                <br>
            </div>
        </div>
        <% }
        } 
	      } catch (Exception e) {
            e.printStackTrace();
            out.print("Error retrieving posts.");
        }
    }
    %>
</div>
     
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

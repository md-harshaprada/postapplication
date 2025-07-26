<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="secondwebapp.CassandraPostService"%>
<%@ page import="secondwebapp.PostService"%>
<%@ page import="java.util.List"%>
<%@ page import="secondwebapp.PostCql"%>
<%@ page import="secondwebapp.Post"%>
<%@ page import="secondwebapp.Reply"%>
<%@ page import="secondwebapp.UserService"%>
<%@ page import="group.GroupService"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="secondwebapp.HtmlUtils"%>


<%! 

boolean hasNestedReplies(List<Reply> replies, Integer parentReplyId) {
    for (Reply reply : replies) {
        if (parentReplyId != null && parentReplyId.equals(reply.getParentReplyId())) {
            return true;
        }
    }
    return false;
}
    void displayReplies(List<Reply> replies, Integer parentReplyId, boolean isCassandra, HttpSession session, PostService postService, JspWriter out) {
        try {
            for (Reply reply : replies) {
                if ((parentReplyId == null && reply.getParentReplyId() == null) || 
                    (parentReplyId != null && reply.getParentReplyId() != null && reply.getParentReplyId().equals(parentReplyId))) {

                    String replyContent = reply.getContent().replace("\n", "<br>").replace(" ", "&nbsp;").replace("\"", "&quot;");
                    int maxReplyLength = 450;
                    boolean isLongReplyContent = replyContent.length() > maxReplyLength;
                    String truncatedReplyContent = isLongReplyContent ? replyContent.substring(0, maxReplyLength) + "..." : replyContent;

                    out.print("<li class='mb-2'>");
                    out.print("<strong>" + reply.getUsername() + ":</strong>");
                    out.print("<div class='d-flex justify-content-between align-items-center'>");
                    out.print("<div style='flex: 1;'>");
                    out.print("<span id='short-reply-" + reply.getId() + "'>" + truncatedReplyContent + "</span>");

                    if (isLongReplyContent) {
                        out.print("<span id='full-reply-" + reply.getId() + "' style='display: none;'>" + replyContent + "</span>");
                        out.print("<a href='javascript:void(0);' onclick=\"toggleContent('reply', " + reply.getId() + ")\" id='see-more-reply-" + reply.getId() + "'>See more</a>");
                        out.print("<a href='javascript:void(0);' onclick=\"toggleContent('reply', " + reply.getId() + ")\" id='see-less-reply-" + reply.getId() + "' style='display: none;'>See less</a>");
                    }

                    out.print("</div>");
                    out.print("<div class='d-flex align-items-center'>");
                    out.print("<span class='like-btn me-2' onclick='likeReply(" + reply.getId() + ")'>");
                    out.print("<i class='fa-solid fa-thumbs-up'></i>");
                    out.print("</span>");
                    out.print("<span class='like-count' id='like-count-reply-" + reply.getId() + "'>");
                    out.print(postService.getReplyLikesCount(reply.getId()) + " Likes");
                    out.print("</span>");
                    out.print("</div>");
                    out.print("</div>");

                    out.print("<ul class='ps-3' id='reply-list-" + reply.getId() + "' style='display: none;'>");
                    displayReplies(replies, reply.getId(), isCassandra, session, postService, out);
                    out.print("</ul>");

                    out.print("<form action='post' method='post' class='mt-2' id='reply-form-" + reply.getId() + "' style='display: none;'>");
                    out.print("<input type='hidden' name='redirectFrom' value='dashboard'>");
                    out.print("<input type='hidden' name='postId' value='" + reply.getPostId() + "'>");
                    out.print("<input type='hidden' name='parentReplyId' value='" + reply.getId() + "'>");
                    out.print("<textarea class='form-control' name='replyContent' rows='1' placeholder='Write a reply...'></textarea>");
                    out.print("<input type='hidden' id='isCassandraField' name='isCassandra' value='" + isCassandra + "'>");
                    out.print("<button type='submit' class='btn btn-secondary btn-sm mt-2'>Reply</button>");
                    out.print("<a href='delete?replyId=" + reply.getId() + "&isCassandra=" + isCassandra + "' class='btn btn-danger btn-sm mt-2'>Delete</a>");
                    out.print("</form>");
                    
                    if (hasNestedReplies(replies, reply.getId())) {
                        out.print("<a href='javascript:void(0);' onclick=\"toggleReplies(" + reply.getId() + ")\" id='toggle-replies-link-" + reply.getId() + "' style='margin-right: 10px;'>Show Replies</a>");
                    }

                    out.print("<a href='javascript:void(0);' onclick=\"toggleReplyForm(" + reply.getId() + ")\" id='toggle-reply-form-link-" + reply.getId() + "'>Reply</a>");
                    out.print("</li>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>


<!DOCTYPE html>
<html>
<head>
<title>Dashboard</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css">
	 <link rel="stylesheet" href="static/css/dashboard-style.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
 <script src="https://cdn.ckeditor.com/4.17.2/standard/ckeditor.js"></script>
 <script src="static/js/dashboard-script.js"></script>
 <style>
    .post-card {
        padding: 10px;
        border: 1px solid #ddd;
        margin-bottom: 10px;
        border-radius: 5px;
    }

    .post-card h5 {
        margin: 0 0 5px 0;
        font-weight: bold;
    }

    .post-card p {
        margin: 0;
    }
</style>
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
                    <a class="nav-link active" aria-current="page" href="dashboard.jsp">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="group.jsp">Group</a>
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
        <h1 class="mt-4 text-center display-6">Welcome to your Dashboard, <span class="text-primary"><%= session.getAttribute("username") %>!</span></h1>

      <form action="post" method="post" enctype="multipart/form-data">
			<input type="hidden" name="redirectFrom" value="dashboard">
			<div class="mb-3">
				<label for="content" class="form-label">Post	 Content:</label>
				<textarea class="form-control" id="content" name="content" rows="3"></textarea>
				<script>
    CKEDITOR.replace('content', {
        filebrowserUploadUrl: '/secondwebapp/upload?type=Files',
        filebrowserImageUploadUrl: '/secondwebapp/upload?type=Images'
    });
</script>
				<label for="share">Share with:</label> <select name="share"
					id="share" onchange="toggleSpecificUsers()">
					<option value="all">All Users</option>
					<option value="me">Only Me</option>
					<option value="specific">Specific Users</option>
				</select>
			</div>
			<div id="specific-users" style="display: none;">
        <label>Select Users:</label><br>
        <div class="user-checkboxes">
            <%
            UserService userService = new UserService();
            List<String> allUsers = userService.getAllUsers();
            
            String currentUser = (String) session.getAttribute("username");
            for (String user : allUsers) {
                if (!user.equals(currentUser)) {
            %>
            <div class="form-check form-check-inline">
                <input type="checkbox" name="specificUsers" value="<%= user %>" class="form-check-input">
                <label class="form-check-label"><%= user %></label>
            </div>
            <%
                }
            }
            %>
        </div>
    </div>
			<div class="mb-3">
				<label for="attachment">Attachment:</label> <input type="file"
					class="form-control" id="attachment" name="attachment">
			</div>
			<div class="mb-3">
				<label for="commentsEnabled">Enable Comments:</label> <input
					type="checkbox" id="commentsEnabled" name="commentsEnabled"
					value="true">
			</div>
			<input type="hidden" id="isCassandraField" name="isCassandra" value="">
			<button type="submit" class="btn btn-primary">Post</button>
		</form>
		<br>
<script>
function getSearchedPost() {
    var searchPost = document.getElementById("searchposts").value;
    var sessionUsername = document.getElementById("sessionUserName").value;

    fetch('post?searchposts=' + searchPost + '&username=' + sessionUsername)
    .then(response => response.json())
    .then(data => {
        var resultsDiv = document.getElementById("results");
        resultsDiv.innerHTML = '';

        const searchedPosts = data.searchedPosts;
        const specificPosts = data.specificPosts;
        
        console.log(searchedPosts);
        console.log(specificPosts);

        var count = 0;
        var totalPosts = searchedPosts.length;

        if (totalPosts > 0) {
        	resultsDiv.innerHTML += '<a href="dashboard.jsp" class="btn btn-primary btn-sm mt-2">Back</a>';
            searchedPosts.forEach(post => {
                if (post.postShared === "all" || 
                   (post.postShared === "me" && post.name === sessionUsername) ||
                   (post.postShared === "specific" && specificPosts.some(specificPost => specificPost.id === post.id))) {

                    resultsDiv.innerHTML += 
                        '<div class="post-card">' +
                            '<h5>Post By: ' + post.name + '</h5>' +
                            '<p>' + post.content + '</p>' +
                        '</div>' +
                        '<hr>';
                } else {
                    count++;
                }
            });
        }
        const mysqlPostContent = document.getElementById('mysql-posts');
        if (count === totalPosts) {
            resultsDiv.innerHTML = '<p>No posts found.</p>';
        }
        else{
        	mysqlPostContent.style.display = 'none';
        }
    })
    .catch(error => console.error('error:', error));
}

    $(document).ready(function() {
        $('#searchForm').on('submit', function(event) {
            event.preventDefault();
            getSearchedPost();
        });
    });
</script>

<form id="searchForm">
	<input type="hidden" id="sessionUserName" name="sessionUserName" value="<%= session.getAttribute("username") %>">
    <label for="searchposts" class="form-label">Search:</label>
    <input type="text" id="searchposts"  name="searchposts" style="width: 300px" required>
    <button type="submit" >Search</button>
</form>
<br>
<div id="results"></div>

<div id="mysql-posts" style="display: ;">
        <%
        boolean isCassandra = Boolean.parseBoolean(request.getParameter("isCassandra"));
        PostService postService = new PostService();
            if (isCassandra) {
                List<PostCql> allPosts = null;
                List<PostCql> specificPosts = null;
                CassandraPostService cassandraPostService = new CassandraPostService();
                allPosts = cassandraPostService.getAllPosts();
                specificPosts = cassandraPostService.getSpecificPostsForUser((String) session.getAttribute("username"));
                
                if (allPosts != null && !allPosts.isEmpty()) {
                    for (PostCql post : allPosts) {
                        boolean isVisible = post.getPostShared().equals("all") || 
                            (post.getPostShared().equals("me") && post.getName().equals(session.getAttribute("username"))) || 
                            (post.getPostShared().equals("specific") && specificPosts.contains(post));
                        
                        if (isVisible) {
                            %>
                            <div class="card mb-3">
			<div class="card-body">
				 <div class="ms-2 d-flex align-items-center justify-content-between">
            <h5 class="card-title me-3 mb-0">
                Posted by: <span class="text-primary"><%= post.getName() %></span>
            </h5>
            <div class="d-flex align-items-center">
                <span class="like-btn me-2" onclick="likePost(<%= post.getId() %>)">
                    <i class="fa-solid fa-thumbs-up"></i>
                </span>
                <span class="like-count" id="like-count-post-<%= post.getId() %>">
                    <%= postService.getPostLikesCount(post.getId()) %> Likes
                </span>
            </div>
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
				 <% if (post.getAttachmentFilename() != null && (post.getAttachmentFilename().endsWith(".jpg") || post.getAttachmentFilename().endsWith(".png") || post.getAttachmentFilename().endsWith(".jpeg") || post.getAttachmentFilename().endsWith(".jfif"))) { %>
    <div class="mt-3">
        <img src="downloadAttachment?postId=<%= post.getId() %>" alt="Post Image" style="max-width: 40%; max-height: 40%; border-radius: 8px; box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);">
    </div> <br>
<% } %>
        
				<% if (post.getAttachmentFilename() != null && !post.getAttachmentFilename().isEmpty()) { %>
				<p>
					<strong><i>Attachment: </i></strong> <a
						href="downloadAttachment?postId=<%= post.getId() %>"><%= post.getAttachmentFilename() %></a>
				</p>
				<% } %>
				<h6 class="mt-3">Replies:</h6>
         <ul class="ps-3">  
    <% 
        if (post.isCommentsEnabled()) {
            List<Reply> replies = cassandraPostService.getRepliesForPost(post.getId());
            displayReplies(replies, null, isCassandra, session, postService, out);
        } else {
    %>
            <li>Comments are disabled for this post. 
            <a href="delete?postId=<%= post.getId() %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete Post</a></li>
            <% } %>
       </ul>

				<% if (post.isCommentsEnabled()) { %>
				<form action="post" method="post"  id="reply-form">
					<input type="hidden" name="postId" value="<%= post.getId() %>">
					<div class="mb-3">
						<textarea class="form-control" name="replyContent" rows="1"
							placeholder="Write a reply..."></textarea>
					</div>
					
			<input type="hidden" id="isCassandraField1" name="isCassandra" value="">
					<button type="submit" class="btn btn-secondary btn-sm mt-2">Reply</button>
					<a href="delete?postId=<%= post.getId() %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete Post</a>
				</form>
				<% } %>
			</div>
		</div>
                            <%
                        }
                    }
                } 
                else { 
                    %>
                    <p>No posts yet.</p>
                    <% } %>
                <%
            } else {

                List<Post> allPosts = null;
                List<Post> specificPosts = null;
                allPosts = postService.getAllPosts();
                specificPosts = postService.getSpecificPostsForUser((String) session.getAttribute("username"));
                
                if (allPosts != null && !allPosts.isEmpty()) {
                    for (Post post : allPosts) {
                        boolean isVisible = post.getPostShared().equals("all") || 
                            (post.getPostShared().equals("me") && post.getName().equals(session.getAttribute("username"))) || 
                            (post.getPostShared().equals("specific") && specificPosts.contains(post));
                        
                        if (isVisible) {
                            %>
                           <div class="card mb-3">
    <div class="card-body">
        <div class="ms-2 d-flex align-items-center justify-content-between">
            <h5 class="card-title me-3 mb-0">
                Posted by: <span class="text-primary"><%= post.getName() %></span>
            </h5>
            <div class="d-flex align-items-center">
                <span class="like-btn me-2" onclick="likePost(<%= post.getId() %>)">
                    <i class="fa-solid fa-thumbs-up"></i>
                </span>
                <span class="like-count" id="like-count-post-<%= post.getId() %>">
                    <%= postService.getPostLikesCount(post.getId()) %> Likes
                </span>
            </div>
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

        
        <% if (post.getAttachmentFilename() != null && (post.getAttachmentFilename().endsWith(".jpg") || post.getAttachmentFilename().endsWith(".png") || post.getAttachmentFilename().endsWith(".jpeg") || post.getAttachmentFilename().endsWith(".jfif"))) { %>
    <div class="mt-3">
        <img src="downloadAttachment?postId=<%= post.getId() %>" alt="Post Image" style="max-width: 40%; max-height: 40%; border-radius: 8px; box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);">
    </div> <br>
<% } %>
        
        <% if (post.getAttachmentFilename() != null && !post.getAttachmentFilename().isEmpty()) { %>
        <p>
            <strong><i>Attachment: </i></strong> <a href="downloadAttachment?postId=<%= post.getId() %>"><%= post.getAttachmentFilename() %></a>
        </p>
        <% } %>
        <h6 class="mt-3">Replies:</h6>
         
    <ul class="ps-3">  
    <% 
        if (post.isCommentsEnabled()) {
            List<Reply> replies = postService.getRepliesForPost(post.getId());
            displayReplies(replies, null, isCassandra, session, postService, out);
        } else {
    %>
            <li>Comments are disabled for this post. 
            <a href="delete?postId=<%= post.getId() %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete Post</a></li>
            <% } %>
       </ul>
        <% if (post.isCommentsEnabled()) { %>
        <form action="post" method="post" class="mt-3">
            <input type="hidden" name="postId" value="<%= post.getId() %>">
            <div class="mb-3">
                <textarea class="form-control" name="replyContent" rows="1" placeholder="Write a reply..."></textarea>
            </div>
            
			<input type="hidden" id="isCassandraField" name="isCassandra" value="">
            <button type="submit" class="btn btn-secondary btn-sm mt-2">Reply</button>
            <a href="delete?postId=<%= post.getId() %>&isCassandra=<%=isCassandra %>" class="btn btn-danger btn-sm mt-2">Delete Post</a>
        </form>
        <% } %>
    </div>
</div>
                            <%
                        }
                    }
                } 
                else { 
                    %>
                    <p>No posts yet.</p>
                    <% } }%>

</div>
    </div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
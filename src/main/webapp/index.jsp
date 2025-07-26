<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Registration and Login</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
       <link rel="stylesheet" href="static/css/style.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container justify-content-center">
        <strong><a class="navbar-brand" href="#">POST FORUM</a></strong>
    </div>
</nav>
    <div class="card-container">
        <div class="card">
            <h2>Registration</h2>
            <form action="register" method="post">
                <div class="mb-3">
                    <label for="name" class="form-label">Name:</label>
                    <input type="text" class="form-control" id="name" name="name" required>
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label">Password:</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary">Register</button>
            </form>
        </div>
        <div class="card">
            <h2>Login</h2>
            <form action="login" method="post">
                <div class="mb-3">
                    <label for="name" class="form-label">Name:</label>
                    <input type="text" class="form-control" id="name" name="name" required>
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label">Password:</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary">Login</button>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
     <script>
    window.onload = function() {
        var errorMessage = '<%= session.getAttribute("errorMessage") != null ? session.getAttribute("errorMessage") : "" %>';
        if (errorMessage) {
            alert(errorMessage);
            <% session.removeAttribute("errorMessage"); %>
        }
        var registrationMessage = '<%= session.getAttribute("registrationMessage") != null ? session.getAttribute("registrationMessage") : "" %>';
        if (registrationMessage) {
            alert(registrationMessage);
            <% session.removeAttribute("registrationMessage"); %>
        }
    };
</script>

    
</body>
</html>
package secondwebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        UserService userService = new UserService();
        if (userService.loginUser(name, password)) {
            request.getSession().setAttribute("username", name);
            response.sendRedirect("dashboard.jsp");
        } else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h3>Login failed. Please try again.</h3>");
            out.println("<a href='index.jsp'>Return to login page</a>");
            out.println("</body></html>");
        }
    }
}
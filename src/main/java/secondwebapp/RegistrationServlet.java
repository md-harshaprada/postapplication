package secondwebapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String password = request.getParameter("password");

		if (password == null || password.length() < 3 || password.length() > 9) {
			request.getSession().setAttribute("errorMessage", "Password should be between 3 and 9 characters.");
			response.sendRedirect("index.jsp");
			return;
		}

		UserService userService = new UserService();
		try {
			userService.registerUser(name, password); 
			request.getSession().setAttribute("registrationMessage", "Username registered successfully.");
			response.sendRedirect("index.jsp");
		} catch (SQLException e) {
			request.getSession().setAttribute("errorMessage", "Username already exists.");
			response.sendRedirect("index.jsp");
		}
	}
}

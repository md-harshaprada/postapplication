package secondwebapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
	
	private static Connection connection;
    public Test() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "tiger");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String hashMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public boolean checkPassword(String username, String inputPassword) {
        boolean isPasswordCorrect = false;

        try {
            String hashedInputPassword = hashMD5(inputPassword);

            String query = "SELECT password FROM usernameandpassworddemo WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");

                if (storedPasswordHash.equals(hashedInputPassword)) {
                    isPasswordCorrect = true;
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isPasswordCorrect;
    }
    
    public void registerUser(String name, String password) throws SQLException {
    	if (userExists(name)) {
            System.out.println("Username already exists.");
        }
    	else {
        try {
			String hashedInputPassword = hashMD5(password);

	        String sql = "INSERT INTO usernameandpassworddemo (name, password) VALUES (?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, name);
            statement.setString(2, hashedInputPassword);
            statement.executeUpdate();
            System.out.println("User registration successfull");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	}
    }

    private boolean userExists(String name) {
    	String sql = "SELECT COUNT(*) FROM usernameandpassworddemo WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
    	Test test = new Test();
        String username = "ammu";
        String password = "ammu";

        try {
        	test.registerUser(username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        String testUsername = "work";
        String testPassword = "test";
        if (test.checkPassword(testUsername, testPassword)) {
            System.out.println("Password is correct!");
        } else {
            System.out.println("Incorrect password.");
        }
    }
}


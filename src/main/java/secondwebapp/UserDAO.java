package secondwebapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "tiger");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM userdetails WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
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

    public void registerUser(String name, String password) throws SQLException, NoSuchAlgorithmException {
        if (userExists(name)) {
            throw new SQLException("Username already exists.");
        }
        String sql = "INSERT INTO userdetails (name, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
			String hashedInputPassword = hashMD5(password);
            statement.setString(1, name);
            statement.setString(2, hashedInputPassword);
            statement.executeUpdate();
        }
    }
    public boolean loginUser(String name, String password) throws SQLException, NoSuchAlgorithmException {
        String sql = "SELECT * FROM userdetails WHERE name = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
			String hashedInputPassword = hashMD5(password);
            statement.setString(1, name);
            statement.setString(2, hashedInputPassword);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public List<String> getAllUsers() throws SQLException {
        List<String> users = new ArrayList<>();
        String sql = "SELECT name FROM userdetails";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(resultSet.getString("name"));
            }
        }
        return users;
    }
}

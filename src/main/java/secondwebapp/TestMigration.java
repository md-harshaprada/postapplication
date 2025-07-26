package secondwebapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestMigration {

	private static Connection connection;
    public TestMigration() {
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
    
    public void testRunForChangingPSW() {
    	for(int i=1;i<15;i++) {
    		
    		try {
        		String getQuery = "SELECT password FROM userdetails WHERE id = ?";
    		PreparedStatement statement = connection.prepareStatement(getQuery);
    		System.out.println(i);
    		statement.setInt(1, i);
    		ResultSet resultSet = statement.executeQuery();
    		
    		 if (resultSet.next()) {
    			 String password = resultSet.getString(1);
    	    		System.out.println(password);
    	    		String hashedInputPassword = hashMD5(password);
    	    		String sql = "UPDATE userdetails set password = ? where id = ? ";
    	        	PreparedStatement statement1 = connection.prepareStatement(sql);

    	        	statement1.setString(1, hashedInputPassword);
    	        	statement1.setInt(2, i);
    	        	statement1.executeUpdate();
    	            System.out.println("updated successfully");
             }
    		
    	
    		}catch(Exception e) {
    			System.out.println("error");
    		}
    	}
    }
    
    public static void main(String[] args) {
    	TestMigration testMigration = new TestMigration();
    	testMigration.testRunForChangingPSW();
    }
}

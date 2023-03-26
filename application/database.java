package application;

import java.sql.*;

public class database {
	public static Connection connectDb() {
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldata", "root", "");
			System.out.println("cnx reussite");
			return connect;
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}
}

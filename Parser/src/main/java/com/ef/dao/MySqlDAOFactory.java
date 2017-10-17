package com.ef.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlDAOFactory {
	
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	
	public Connection createConnection(Properties prop) {
		try {
			Class.forName(DRIVER);
			String connection = "jdbc:mysql://" + prop.getProperty("host") + ":" + prop.getProperty("port") + "/" + prop.getProperty("database");
			Connection conn = DriverManager.getConnection(connection, prop.getProperty("user"), prop.getProperty("pass"));
			return conn;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

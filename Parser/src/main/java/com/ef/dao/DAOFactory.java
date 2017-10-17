package com.ef.dao;

public abstract class DAOFactory {
	public static final int MYSQL = 1;
	
	public abstract AccessDAO getAccessDAO();
	
	/*
	public static DAOFactory getDAOFactory(int whichFactory, Properties props){
		switch (whichFactory) {
		case MYSQL:
			return new MySqlDAOFactory();
		default:
			return null;
		}
	}*/
}

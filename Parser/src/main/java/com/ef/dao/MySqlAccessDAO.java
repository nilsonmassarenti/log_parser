package com.ef.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.ef.model.Access;
import com.ef.util.Util;

public class MySqlAccessDAO implements AccessDAO {
	
	private Connection connection;
	private Util util;
	
	
	public MySqlAccessDAO(Properties prop){
		MySqlDAOFactory mySqlDAOFactory = new MySqlDAOFactory();
		this.connection = mySqlDAOFactory.createConnection(prop);
		this.util = new Util();
	}

	@Override
	public Boolean add(List<Access> list) {
		String sql = "INSERT INTO tb_access(access_date, "
				+ " access_ip, access_status, "
				+ " access_user_agent) "
				+ " VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			for (Access access : list) {
				ps.setTimestamp(1, this.util.convertLocalDateTimeToSqlDate(access.getDate()));
				ps.setString(2, access.getIp());
				ps.setInt(3, access.getStatus());
				ps.setString(4, access.getUserAgent());
				ps.addBatch();
			}
			int[] results = ps.executeBatch();
			ps.close();
			if (results.length > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Boolean addBlockIp(Set<String> listIps, Integer rule) {
		String sql = "INSERT INTO tb_access_blocked(access_blocked_ip, "
				+ " access_blocked_rule ) "
				+ " VALUES (?, ?)";
		
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			for (String ip : listIps) {
				ps.setString(1, ip);
				ps.setInt(2, rule);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean checkFileImported(LocalDateTime startDate, LocalDateTime endDate) {
		String sql = "SELECT * FROM log.tb_access WHERE access_date between ? and ?";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setTimestamp(1, util.convertLocalDateTimeToSqlDate(startDate));
			ps.setTimestamp(2, util.convertLocalDateTimeToSqlDate(endDate));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Set<String> listIpsToBlock(LocalDateTime startDate, LocalDateTime endDate, Integer threshold) {
		String sql = "SELECT access_ip "
				+ " FROM log.tb_access "
				+ " WHERE access_date between ? and ?"
				+ " GROUP BY access_ip "
				+ " HAVING COUNT(access_ip) >= ?"
				+ " ORDER BY COUNT(access_ip)";
		Set<String> ipsToBlock = new HashSet<>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setTimestamp(1, util.convertLocalDateTimeToSqlDate(startDate));
			ps.setTimestamp(2, util.convertLocalDateTimeToSqlDate(endDate));
			ps.setInt(3, threshold);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				ipsToBlock.add(resultSet.getString("access_ip"));
			}
			resultSet.close();
			ps.close();
			return ipsToBlock;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Integer addRule(LocalDateTime startDate, Integer threshold, Integer durationType) {
		String sql = "INSERT INTO tb_access_rule ("
				+ " access_rule_duration_type, access_rule_threshold, "
				+ " access_rule_start_date) "
				+ " VALUES (? , ?, ?)";
		Integer key = null;
		try {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, durationType);
			ps.setInt(2, threshold);
			ps.setTimestamp(3, util.convertLocalDateTimeToSqlDate(startDate));
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
			    key = rs.getInt(1);
			}
			return key;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}

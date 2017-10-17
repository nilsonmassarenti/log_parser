package com.ef.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ef.model.Access;

public interface AccessDAO {
	public Boolean add(List<Access> list);
	
	public Boolean addBlockIp(Set<String> listIps, Integer rule);
	
	public Boolean checkFileImported(LocalDateTime startDate, LocalDateTime endDate);
	
	public Set<String> listIpsToBlock(LocalDateTime startDate, LocalDateTime endDate, Integer threshold);
	
	public Integer addRule(LocalDateTime startDate, Integer threshold, Integer durationType);
}

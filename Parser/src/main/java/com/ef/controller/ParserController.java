package com.ef.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ef.dao.MySqlAccessDAO;
import com.ef.model.Access;

public class ParserController {

	private Map<String, String> parameters;
	private MySqlAccessDAO daoFactory;
	
	public ParserController(Properties prop){
		
		daoFactory = new MySqlAccessDAO(prop);
	}
	
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void manager() {
		
		Set<String> ipsToBlock = null;
		// check if necessary import file
		if (parameters.get("accesslog") != null) {
			List<Access> listAccess = importFile();
			if (listAccess != null) {
				if (!listAccess.isEmpty()) {
					Access startAccess = listAccess.get(0);
					Access endAccess = listAccess.get(listAccess.size()-1);
					if (!daoFactory.checkFileImported(startAccess.getDate(), endAccess.getDate())) {
						daoFactory.add(listAccess);
						System.out.println("File has been imported");
					} else {
						System.out.println("File has not been imported");
					}
				} else {
					System.out.println("File has not been imported");
				}
			} else {
				System.out.println("File has not been imported");
			}
		}
		if (parameters.get("startDate") != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
			LocalDateTime startDateTime = LocalDateTime.parse(parameters.get("startDate"), formatter);
			String timeToCheck = parameters.get("duration");
			Integer threshold = Integer.parseInt(parameters.get("threshold"));
			Integer durationType = 0;
			if (timeToCheck != null && startDateTime != null) {
				LocalDateTime endDateTime = startDateTime;
				switch (timeToCheck) {
				case "minute":
					endDateTime = endDateTime.plusMinutes(1);
					durationType = 1;
					break;
				case "hourly":
					endDateTime = endDateTime.plusHours(1);
					durationType = 2;
					break;
					
				case "daily":
					endDateTime = endDateTime.plusDays(1);
					durationType = 3;
					break;
				default:
					endDateTime = null;
					break;
				}
				
				if (endDateTime != null) {
					ipsToBlock = daoFactory.listIpsToBlock(startDateTime, endDateTime, threshold);
					Integer rule = daoFactory.addRule(startDateTime, threshold, durationType);
					if (ipsToBlock.size() > 0) {
						daoFactory.addBlockIp(ipsToBlock, rule);
						System.out.println("Ips blocked: ");
						for (String ip : ipsToBlock) {
							System.out.println("IP: " + ip);
						}
					} else {
						System.out.println("No IP blocked");
					}
					
				} else {
					System.out.println("Duration is invalid");
				}
			} else {
				System.out.println("Duration is not filled");
			}
			
		}
	}

	public List<Access> importFile() {
		List<Access> listAccess = new ArrayList<Access>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.parameters.get("accesslog")));
			String contentLine = br.readLine();
			while (contentLine != null) {
				Access access = new Access();
				String[] content = contentLine.split("\\|");
				access.setDate(LocalDateTime.parse(content[0], formatter));
				access.setIp(content[1]);
				access.setStatus(Integer.parseInt(content[3]));
				access.setUserAgent(content[4].replaceAll("\"", ""));
				listAccess.add(access);
				contentLine = br.readLine();
			}
			br.close();
			return listAccess;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}

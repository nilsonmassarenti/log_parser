package com.ef;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ef.controller.ParserController;

public class Parser {

	public static void main(String[] args) {

		Map<String, String> parameters = new HashMap<String, String>();
		for (String arg : args) {
			String[] argSplit = arg.split("=");
			if (argSplit.length == 2) {
				parameters.put(argSplit[0].substring(2), argSplit[1]);
			}
		}
		if (parameters.size() > 0) {
			if (parameters.get("db-config") != null) {
				Properties prop = new Properties();
				InputStream input = null;

				try {
					input = new FileInputStream(parameters.get("db-config"));
					prop.load(input);
					ParserController controller = new ParserController(prop);
					controller.setParameters(parameters);
					controller.manager();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
			}

			
		}
	}

}

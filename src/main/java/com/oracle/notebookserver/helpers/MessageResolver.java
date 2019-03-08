package com.oracle.notebookserver.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
public class MessageResolver {
	public String getMessageValue(String key) {
		Properties prop = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("messages.properties");
		try {
			prop.load(inputStream);
			return prop.getProperty(key);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getMessageValue(String key , Object ... arguments) {
		Properties prop = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("messages.properties");
		try {
			prop.load(inputStream);
			return MessageFormat.format(prop.getProperty(key), arguments);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}

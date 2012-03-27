package com.secondmarket.utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {

	public void setProperties(){
		
		Properties prop = new Properties();
		prop.setProperty("", "");
		try {
			prop.store(new FileOutputStream("config.properties"), null);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getProperties(){
		Properties prop = new Properties();
		
	//	prop.loadFromXML(in);

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
				
	

	}

}

package com.secondmarket.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.StringTokenizer;
/***
 * 
 * @author Danjuan Ye
 *
 */

public class ClasspathPropertyLoader implements PropertyLoader{
	
	public static String DEFAULT_PROPERTY_FILE_NAMES = "properties.xml";
	public static String SYSTEM_PROPERTY_NAME = "properties";
	public static String KS_PROPERTY_PATH = "property_path";
	
	private StringTokenizer getLoadFileNames(){
		String fileNames = System.getProperty(SYSTEM_PROPERTY_NAME);
		if(fileNames == null){
			fileNames = DEFAULT_PROPERTY_FILE_NAMES;
		}
		return getLoadFileNames(fileNames);
	}
	
	private StringTokenizer getLoadFileNames(String fileNames){
		System.out.println("Loading Files: " + fileNames);
		StringTokenizer tokens;
		if(System.getProperty("webstart") != null){
			System.out.println("Running through WebStart.");
			tokens = new StringTokenizer(fileNames,";");
		}
		else{
			System.out.println("File.pathSepartor:" + File.pathSeparator + "");
			tokens = new StringTokenizer(fileNames, File.pathSeparator);
		}
		return tokens;
	}
	
	public void load(PropertyGroup group) throws Exception{
		boolean loaded = false;
		Enumeration enumer = getLoadFileNames();
		while(enumer.hasMoreElements()){
			loaded = load(group, (String)enumer.nextElement()) | loaded;
		}
		if(!loaded){
			throw new Exception("No property files found");
		}
	}
	
	public boolean load(PropertyGroup group, String file) throws Exception{
		InputStream fileStream;
		Reader propReader = null;
		System.out.println("Loader property file: " + file);
		fileStream = getClass().getClassLoader().getResourceAsStream(file);
		if(fileStream == null){
			System.out.println("[e] - file " + file + " not found in classpath");
			//changes for webstart - try to find the file not on the //classpath
			String paths = System.getProperty(KS_PROPERTY_PATH);
			if(paths != null){
				String path = null;
				StringTokenizer token = new StringTokenizer(paths, ";");
				while(token.hasMoreTokens()){
					try{
						path = token.nextToken();
						if(path.lastIndexOf("/") != path.length() - 1){
							path.concat("/");
						}
						propReader = new FileReader(path + file);
						System.out.println("File " + file + "found using path: " + path);
						break;
					}catch(FileNotFoundException fnfe){
						fnfe.printStackTrace(System.err);
						System.out.println("File" + file + " not found using path: " + path);
					}
				}
			}
		}
		else{
			propReader = new InputStreamReader(fileStream);
		}
		if(propReader == null){
			System.out.println("User preferences not found");
		}
		else{
			try{
				getLoader().load(group, propReader);
			}catch(IOException e){
				throw new Exception(e);
			}
		}
		return true;
	}
	
	public void store(PropertyGroup group) throws Exception{
		Writer out = new OutputStreamWriter(System.out);
		try{
			getLoader().store(group, out);
			out.flush();
		}catch(IOException e){
			throw new Exception(e);
		}
	}
	
	protected XMLPropertyLoader getLoader(){
		return XMLPropertyLoader.getInstance();
	}

}

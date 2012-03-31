package com.secondmarket.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/***
 * 
 * @author Danjuan
 *
 */
public class SMProperties {

	private Map<String, PropertyGroup> properties = new TreeMap<String, PropertyGroup>();
	
	public synchronized static SMProperties getInstance(String source) throws Exception{
		SMProperties instance = new SMProperties();
		instance.init(source);
		return instance;
	}
	
	private SMProperties(){
	}
	
	private void init(String source_name) throws Exception{
		PropertyGroup mainGroup = PropertyGroup.getRoot().getGroup(source_name);
		if(mainGroup.exists()){
			Collection<String> c = mainGroup.getGroupNames();
			Iterator<String> i = c.iterator();
			while(i.hasNext()){
				String name = (String)i.next();
				PropertyGroup group = mainGroup.getGroup(name);
				properties.put(name, group);
			}
			
		}
	}
	
	public PropertyGroup getProperty(String key){
		return properties.get(key);
	}
	
	public List<Pattern> getValues(String source, String key){
		List<Pattern> patternList = new ArrayList<Pattern>();
		PropertyGroup group = properties.get(source);
		Collection<String> c = group.getGroupNames();
		Collection<String> collection = null;
		
		Iterator<String> i = c.iterator();
		Iterator<String> iterator = null;
		String subkey = null;
		String value = null;
		Pattern myPattern;
		while(i.hasNext()){
			String name = (String)i.next();
			if(key.equalsIgnoreCase(name)){
				PropertyGroup key_group = group.getGroup(name);
				collection = key_group.getGroupNames();
				iterator = collection.iterator();
				while(iterator.hasNext()){
					subkey = iterator.next();
					value = key_group.getValue(subkey);
					if(value != null){
						value = value.replace("\"", "");
					}
					myPattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
					patternList.add(myPattern);
				}
			}
		}
		return patternList;
	}
	
	public Pattern getValue(String source, String key, String subkey) throws Exception{
		PropertyGroup group = properties.get(source);
		String values = null;
		if(group == null){
			throw new Exception("No property defined - " + source);
		}
		Collection<String> c = group.getGroupNames();
		Iterator<String> i = c.iterator();
		while(i.hasNext()){
			String name = (String)i.next();
			if(key.equalsIgnoreCase(name)){
				PropertyGroup key_group = group.getGroup(name);
				String value = key_group.getValue(subkey);
				if(value != null){
					values = value.replace("\"", "");
				}
			}
		}
		return Pattern.compile(values, Pattern.CASE_INSENSITIVE);
	}
	
	public PropertyGroup getPropertyGroup(String source){
		return (PropertyGroup)properties.get(source);
	}
	
	public Set<String> getSource(){
		return properties.keySet();
	}

	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("Argument missing! Need an argument to specifiy the source tag");
			return;
		}
		
		SMProperties p;
		try {
			p = SMProperties.getInstance(args[0]);
			Set<String> sourceList = p.getSource();
			for(String name:sourceList){
				System.out.println("Start Parsing source: " + name);
				System.out.println(p.getValues(name, "OPTIONS"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

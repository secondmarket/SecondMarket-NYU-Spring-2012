package com.secondmarket.properties;

/***
 * 
 * @author Danjuan
 *
 */
public interface PropertyLoader {
	public void load(PropertyGroup group) throws Exception;
	public void store(PropertyGroup group) throws Exception;
}

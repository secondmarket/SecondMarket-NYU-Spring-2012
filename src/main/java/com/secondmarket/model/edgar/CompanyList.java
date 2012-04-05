package com.secondmarket.model.edgar;

public class CompanyList {
	
	private String companyLink;
	private String companyName;
	private String SICNum;
	private String SICLink;
	private String location;
	private String locationLink;
	
	public CompanyList(){
		
	}
	public void setCompanyLink(String link){
		this.companyLink = link;
	}
	public String getCompanyLink(){
		return this.companyLink;
	}
	
	public void setCompanyName(String name){
		this.companyName = name;
	}
	
	public String getCompanyName(){
		return this.companyName;
	}
	
	public void setSICNum(String num){
		this.SICNum = num;
	}
	
	public String getSICNum(){
		return this.SICNum;
	}
	
	public void setSICLink(String link){
		this.SICLink = link;
	}
	
	public String getSICLink(){
		return this.SICLink;
	}
	
	public void setLocationLink(String link){
		this.locationLink = link;
	}
	
	public String getLocationLink(){
		return this.locationLink;
	}
	
	public void setLocation(String loc){
		this.location = loc;
	}
	
	public String getLocation(){
		return this.location;
	}

}

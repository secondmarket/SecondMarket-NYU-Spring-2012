package com.secondmarket.model.edgar;

public class EdgarDoc {

	private String seq;
	private String description;
	private String docName;
	private String docLink;
	private String type;
	private String size;
	
	public EdgarDoc(){
		
	}
	
	public void setSeq(String seq){
		this.seq = seq;
	}
	
	public String getSeq(){
		return this.seq;
	}
	
	public void setDescr(String descr){
		this.description = descr;
	}
	
	public String getDescr(){
		return this.description;
	}
	
	public void setDocName(String name){
		this.docName = name;
	}
	
	public String getDocName(){
		return this.docName;
	}
	
	public void setDocLink(String link){
		this.docLink = link;
	}
	
	public String getDocLink(){
		return this.docLink;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setSize(String size){
		this.size = size;
	}
	
	public String getSize(){
		return this.size;
	}
}

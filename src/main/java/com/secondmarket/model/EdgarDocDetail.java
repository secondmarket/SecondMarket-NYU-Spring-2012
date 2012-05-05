package com.secondmarket.model;

import java.util.List;

import com.google.code.morphia.annotations.Embedded;

/***
 * 
 * @author Danjuan Ye
 *
 */
@Embedded
public class EdgarDocDetail {

	private String filings;
	private String format;
	private String formatLink;
	private String description;
	private String filingDate;
	private String fileNum;
	private String fileNumLink;
	@Embedded
	private List<EdgarFilingDetail> docList;
	
	public void setDocList(List<EdgarFilingDetail> list){
		docList = list;
	}
	
	public List<EdgarFilingDetail> getDocList(){
		return docList;
	}
	
	public void setFilings(String filings){
		this.filings = filings;
	}
	public String getFilings(){
		return this.filings;
	}
	
	public void setFormat(String format){
		this.format = format;
	}
	
	public String getFormat(){
		return this.format;
	}
	
	public void setFormatLink(String link){
		this.formatLink = link;
	}
	
	public String getFormatLink(){
		return this.formatLink;
	}
	
	public void setDescr(String descr){
		this.description = descr;
	}
	
	public String getDescr(){
		return this.description;
	}
	
	public void setFileDate(String time){
		this.filingDate = time;
	}
	
	public String getFileDate(){
		return this.filingDate;
	}
	
	public void setFileNum(String num){
		this.fileNum = num;
	}
	
	public String getFileNum(){
		return this.fileNum;
	}
	
	public void setFileNumLink(String link){
		this.fileNumLink = link;
	}
	
	public String getFileNumLink(){
		return this.fileNumLink;
	}
}

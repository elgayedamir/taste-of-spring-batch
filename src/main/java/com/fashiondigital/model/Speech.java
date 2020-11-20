package com.fashiondigital.model;

import java.util.Date;

public class Speech {
	
	private String speaker;
	private String theme;
	private Long words;
	private Date date;
	
	public String getSpeaker() {
		return speaker;
	}
	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public Long getWords() {
		return words;
	}
	public void setWords(Long words) {
		this.words = words;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "Speech [speaker=" + speaker + ", theme=" + theme + ", words=" + words + ", date=" + date + "]";
	}
}

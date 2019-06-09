package com.chill.chatapplet.entity;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * 记录用户聊天记录的消息类
 */
public class Chatmsg implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id;
	private Boolean type;// true表示是接收的消息 false表示是发送的消息 ,用于判断消息格式
	private String chatmsg;
	private Timestamp time;
	private String  fileindex;
	private String filename;
	private long filelength;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Boolean getType() {
		return type;
	}
	public void setType(Boolean type) {
		this.type = type;
	}

	public String getChatmsg() {
		return chatmsg;
	}
	public void setChatmsg(String chatmsg) {
		this.chatmsg = chatmsg;
	}
	
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getFileindex() {
		return fileindex;
	}
	public void setFileindex(String fileindex) {
		this.fileindex = fileindex;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public long getFilelength() {
		return filelength;
	}
	public void setFilelength(long filelength) {
		this.filelength = filelength;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
	
		return super.clone();
	}
}

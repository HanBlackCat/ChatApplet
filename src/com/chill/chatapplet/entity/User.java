package com.chill.chatapplet.entity;

import java.net.InetAddress;
import java.sql.Timestamp;


/**
 * User类，定义用户对象
 */
public class User {

	private int userId = 0; // 用户id
	private String name = null; // 用户昵称
	private String salt;
	private String password;
	private InetAddress address = null; // 用户ip地址
	private int port = -1; // 用户udp端口
	private Timestamp time;//注册时间
	public User() {
		super();
	}
	
	public User(int userid) {
		super();
		this.userId = userid;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userid) {
		this.userId = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}

   
}
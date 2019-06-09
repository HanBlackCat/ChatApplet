package com.chill.chatapplet.entity;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 用户操作和服务器响应的消息类*/
public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id=0;
	private String name=null; //昵称
	private String password=null; //哈希加盐后的密码
	private String salt=null; //盐值
	/**
	 * 消息类型 :   M_LOGIN:用户登录     M_REGISTER:用户注册  M_SCHAT:私聊发送  M_PCHAT:公聊发送  M_YES: 接收文件 M_NO:拒绝接收
	 *             M_SELECT:选择好友(初始化聊天记录)  M_ADDF:添加好友   M_DELEF:删除好友   M_QUIT:用户退出 M_FILE:发送文件
	 * */
	private String type=null;
	/*
	private HashMap<Integer, String> friend = new HashMap<Integer, String>() ; //好友列表 key:用户id value:用户名字
	private HashMap<String, User> userList = new HashMap<String, User>(); // 在线用户列表
    private ArrayList<Chatmsg>  chathistory ;//聊天记录
    */   //长度太长了
	private String[] friend;
	private String[] userList;
	private Chatmsg[] chathistory = new Chatmsg[10];//最后十条历史记录
	private InetAddress toAddr=null; //目标用户地址
    private int toPort; //目标用户端口
    private int targetId; //目标用户id
    private String targetName; //目标用户name
    private Chatmsg chatmsg= new Chatmsg(); //发送的消息
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public InetAddress getToAddr() {
		return toAddr;
	}
	public void setToAddr(InetAddress toAddr) {
		this.toAddr = toAddr;
	}
	public int getToPort() {
		return toPort;
	}
	public void setToPort(int toPort) {
		this.toPort = toPort;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	public Chatmsg getChatmsg() {
		return chatmsg;
	}
	public void setChatmsg(Chatmsg chatmsg) throws CloneNotSupportedException {
		this.chatmsg = (Chatmsg)chatmsg.clone();
	}
	public String[] getFriend() {
		return friend;
	}
	public void setFriend(String[] friend) {
		this.friend = friend;
	}
	public String[] getUserList() {
		return userList;
	}
	public void setUserList(String[] userList) {
		this.userList = userList;
	}
	
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public Chatmsg[] getChathistory() {
		return chathistory.clone();
	}
	public void setChathistory(Chatmsg[] chathistory) {
		this.chathistory=(Chatmsg[])chathistory.clone();
	}

    
}
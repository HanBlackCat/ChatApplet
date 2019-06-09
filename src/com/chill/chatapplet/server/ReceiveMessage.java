package com.chill.chatapplet.server;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Map.Entry;

import javax.swing.SwingWorker;
import com.chill.chatapplet.entity.*;
import com.chill.chatapplet.action.*;
import com.chill.chatapplet.client.CellRenderer;
import com.chill.chatapplet.dao.UserDao;

public class ReceiveMessage extends Thread {
	private DatagramSocket serverSocket; // 服务器套接字
	private DatagramPacket packet; // 报文
	private HashMap<String, User> userList = new HashMap<String, User>(); // 在线用户列表
	private byte[] data = new byte[1024 * 8]; // 8K字节数组
	private ServerUI parentUI; // 消息窗口
	private UserDao userDao;

	public ReceiveMessage(DatagramSocket socket, ServerUI parentUI) {
		serverSocket = socket;
		this.parentUI = parentUI;
		parentUI.setUserList(userList);
	}

	@Override
	public void run() {
		while (true) {
			try {
				// 处理接收的报文
				int flag = 1;// 是否在表
				data = new byte[8096];
				packet = new DatagramPacket(data, data.length);
				serverSocket.receive(packet);
				Message msg = (Message) Serialize.ByteToObject(packet.getData());
				userDao = new UserDao();
				System.out.println("服务器收到消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
						+ "目标姓名" + msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
				// 注册
				if (msg.getType().equalsIgnoreCase("M_REGISTER")) {
					Message backmsg = new Message();
					User bean = new User();
					bean.setName(msg.getName());
					bean.setSalt(msg.getSalt());
					bean.setPassword(msg.getPassword());
					bean.setTime(new Timestamp(System.currentTimeMillis()));

					int userid = userDao.doRegister(bean);
					if (userid != 0) {
						backmsg.setType("M_SUCCESS");
						backmsg.setId(userid);
						parentUI.txtArea
								.append(new String(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
										+ "新用户" + bean.getName() + "注册\n"));
					} else {
						backmsg.setType("M_FAILURE");
					}

					byte[] buf = Serialize.ObjectToByte(backmsg);
					DatagramPacket backpacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
							packet.getPort());
					serverSocket.send(backpacket);
					System.out.println("服务器发送消息: " + backmsg.getType() + " 用户: " + backmsg.getId() + "姓名"
							+ backmsg.getName() + " " + "目标姓名" + backmsg.getTargetName() + backpacket.getAddress() + " "
							+ backpacket.getPort());
				}
				// 登录
				else if (msg.getType().equalsIgnoreCase("M_LOGIN")) {
					// backmsg回复报文
					Message backmsg = new Message();
					User bean = new User();
					bean.setName(msg.getName());
					// 查看是否已经在登录表中
					Iterator<User> iterator = userList.values().iterator();
					while (iterator.hasNext()) {
						User usertemp = iterator.next();
						if (msg.getName().equals(usertemp.getName())) {
							flag = 0;
							break;
						}
					}
					if (flag == 0) {
						backmsg.setType("M_YET");
						byte[] buf = Serialize.ObjectToByte(backmsg);
						DatagramPacket backpacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
								packet.getPort());
						serverSocket.send(backpacket);
						System.out.println("服务器发送消息: " + backmsg.getType() + " 用户: " + backmsg.getId() + "姓名"
								+ backmsg.getName() + " " + "目标姓名" + backmsg.getTargetName() + backpacket.getAddress()
								+ " " + backpacket.getPort());
						flag = 1;
					} else {
						// 加密验证
						User tempuser = new User();
						tempuser = userDao.doLogin(bean);
						String skey = AESUtil.getInstance().encode(msg.getPassword(), tempuser.getSalt());
						if (!skey.equals(tempuser.getPassword())) {
							backmsg.setType("M_FAILURE");
							byte[] buf = Serialize.ObjectToByte(backmsg);
							DatagramPacket backpacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
									packet.getPort());
							serverSocket.send(backpacket);
							System.out.println("服务器发送消息: " + backmsg.getType() + " 用户: " + backmsg.getId() + "姓名"
									+ backmsg.getName() + " " + "目标姓名" + backmsg.getTargetName()
									+ backpacket.getAddress() + " " + backpacket.getPort());
						} else {
							bean.setUserId(tempuser.getUserId());
							bean.setAddress(packet.getAddress());
							bean.setPort(packet.getPort());
							backmsg.setId(tempuser.getUserId());
							backmsg.setType("M_SUCCESS");
							backmsg.setName(bean.getName());
							// 查询好友列表和在线用户
							HashMap<Integer, String> tmp = userDao.findFriend(bean);
							String[] tmprs = new String[tmp.size()];
							int i = 0;
							Iterator<Entry<Integer, String>> it0 = tmp.entrySet().iterator();
							while (it0.hasNext()) {
								Entry<Integer, String> entry = it0.next();
								tmprs[i] = entry.getValue();
								i++;
							}
							backmsg.setFriend(tmprs);

							String[] tmprs1 = new String[userList.size()];
							i = 0;
							Iterator<Entry<String, User>> it2 = userList.entrySet().iterator();
							while (it2.hasNext()) {
								Entry<String, User> entry2 = it2.next();
								tmprs1[i] = entry2.getValue().getName();
								i++;
							}
							backmsg.setUserList(tmprs1);
							// 更新在线表
							userList.put(msg.getName(), bean);
							parentUI.txtArea.append(new String("当前在线人数" + userList.size()) + "\n");
							// 返回登录成功消息
							byte[] buf = Serialize.ObjectToByte(backmsg);
							DatagramPacket backpacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
									packet.getPort());
							serverSocket.send(backpacket);
							System.out.println("服务器发送消息: " + backmsg.getType() + " 用户: " + backmsg.getId() + "姓名"
									+ backmsg.getName() + " " + "目标姓名" + backmsg.getTargetName()
									+ backpacket.getAddress() + " " + backpacket.getPort());

							parentUI.txtArea
									.append(new String(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
											+ "用户" + bean.getName() + "登录\n"));
							// 向所有其他在线用户发送M_LOGIN消息，向新登录者发送整个用户列表
							Iterator<User> it = userList.values().iterator();
							while (it.hasNext()) { // 遍历整个用户列表
								User user = it.next();
								// 向其他在线用户发送当前用户的M_LOGIN消息
								if (!msg.getName().equals(user.getName())) {
									Message recallmsg = new Message();
									recallmsg.setType("M_LOGIN");
									recallmsg.setId(msg.getId());
									recallmsg.setName(msg.getName());
									recallmsg.setTargetName(user.getName());
									byte[] buf1 = Serialize.ObjectToByte(recallmsg);
									DatagramPacket recall = new DatagramPacket(buf1, buf1.length, user.getAddress(),
											user.getPort());// 向其他用户发送的报文
									serverSocket.send(recall);
									System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名"
											+ msg.getName() + " " + "目标姓名" + msg.getTargetName() + recall.getAddress()
											+ " " + recall.getPort());
								} // end if
							} // end while
								// 更新服务器在线用户列表
							parentUI.updateUserList(msg);
						}
					}
				} // 私聊消息
				else if (msg.getType().equalsIgnoreCase("M_SCHAT")) {
					// 从在线列表中找到目标用户的端口号和地址
					User user = null;
					Iterator<User> it = userList.values().iterator();
					while (it.hasNext()) {
						user = it.next();
						if (msg.getTargetName().equals(user.getName())) {
							msg.setTargetId(user.getUserId());
							break;
						}
					}
					data = Serialize.ObjectToByte(msg);
					// 服务器转发消息
					if (!user.getAddress().equals(null)) {
						DatagramPacket backPacket = new DatagramPacket(data, data.length, user.getAddress(),
								user.getPort());
						serverSocket.send(backPacket);
						System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName()
								+ " " + "目标姓名" + msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
						// 写入数据库
						userDao.chat(msg.getChatmsg(), msg.getTargetId());
					} // 用户不在线回复M_FAILURE
					else {
						Message backmsg = new Message();
						backmsg.setId(msg.getId());
						backmsg.setType("M_FAILURE");
						byte[] buf = Serialize.ObjectToByte(backmsg);
						DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
								packet.getPort());
						serverSocket.send(backPacket);
						System.out.println("服务器发送消息: " + backmsg.getType() + " 用户: " + backmsg.getId() + "姓名"
								+ backmsg.getName() + " " + "目标姓名" + backmsg.getTargetName() + backPacket.getAddress()
								+ " " + backPacket.getPort());
					}
				}
				// 接收文件
				else if (msg.getType().equalsIgnoreCase("M_FILE")) {
					// 从在线列表中找到目标用户的端口号和地址
					User user = null;
					Iterator<User> it = userList.values().iterator();
					while (it.hasNext()) {
						user = it.next();
						if (msg.getTargetName().equals(user.getName())) {
							msg.setTargetId(user.getUserId());
							msg.setToAddr(user.getAddress());
							msg.setToPort(user.getPort());
							break;
						}
					}
					data = Serialize.ObjectToByte(msg);
					// 服务器转发消息
					if (!user.getAddress().equals(null)) {
						DatagramPacket backPacket = new DatagramPacket(data, data.length, user.getAddress(),
								user.getPort());
						serverSocket.send(backPacket);
						System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName()
								+ " " + "目标姓名" + msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
					} else {
						Message backmsg = new Message();
						backmsg.setId(msg.getId());
						backmsg.setType("M_FAILURE");
						byte[] buf = Serialize.ObjectToByte(backmsg);
						DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
								packet.getPort());
						serverSocket.send(backPacket);
						System.out.println("服务器发送消息: " + backmsg.getType() + " 用户: " + backmsg.getId() + "姓名"
								+ backmsg.getName() + " " + "目标姓名" + backmsg.getTargetName() + backPacket.getAddress()
								+ " " + backPacket.getPort());
					}

				}
				// 用户选择接收文件
				else if (msg.getType().equalsIgnoreCase("M_YSE")) {
					// 从在线列表中找到目标用户的端口号和地址
					User user = null;
					Iterator<User> it = userList.values().iterator();
					while (it.hasNext()) {
						user = it.next();
						if (msg.getName().equals(user.getName())) {
							break;
						}
					}
					data = Serialize.ObjectToByte(msg);
					DatagramPacket backPacket = new DatagramPacket(data, data.length, user.getAddress(),
							user.getPort());
					serverSocket.send(backPacket);
					System.out.println(
							"服务器发送消息: " + msg.getType() + " 用户: " + msg.getTargetId() + "姓名" + msg.getTargetName() + " "
									+ "目标姓名" + msg.getName() + user.getAddress() + " " + user.getPort());
					// 写入数据库
					userDao.chat(msg.getChatmsg(), msg.getTargetId());
				}
				// 用户拒绝接收文件
				else if (msg.getType().equalsIgnoreCase("M_NO")) {
					User user = null;
					Iterator<User> it = userList.values().iterator();
					while (it.hasNext()) {
						user = it.next();
						if (msg.getName().equals(user.getName())) {
							break;
						}
					}
					data = Serialize.ObjectToByte(msg);
					DatagramPacket backPacket = new DatagramPacket(data, data.length, user.getAddress(),
							user.getPort());
					serverSocket.send(backPacket);
					System.out.println(
							"服务器发送消息: " + msg.getType() + " 用户: " + msg.getTargetId() + "姓名" + msg.getTargetName() + " "
									+ "目标姓名" + msg.getName() + user.getAddress() + " " + user.getPort());
				}

				// 公聊消息
				else if (msg.getType().equalsIgnoreCase("M_PCHAT")) {
					Iterator<User> it = userList.values().iterator();
					while (it.hasNext()) {
						User user = it.next();
						data = Serialize.ObjectToByte(msg);
						if (msg.getId() != user.getUserId()) {
							if (!user.getAddress().equals(null)) {
								DatagramPacket backPacket = new DatagramPacket(data, data.length, user.getAddress(),
										user.getPort());
								serverSocket.send(backPacket);
								System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名"
										+ msg.getName() + " " + "目标姓名" + msg.getTargetName() + backPacket.getAddress()
										+ " " + backPacket.getPort());
							}
						}
					}
					// 写入数据库
					userDao.chat(msg.getChatmsg());
				}
				// 用户获取聊天记录
				else if (msg.getType().equalsIgnoreCase("M_SELECT")) {
					User bean = new User();
					bean.setUserId(msg.getId());
					msg.setTargetId(userDao.getId(msg.getTargetName()));
					msg.setChathistory(userDao.chatHistory(bean, msg.getTargetId()));
					// System.out.println(msg.getId() + " 请求和 " +
					// msg.getTargetName() + "的消息记录");
					/*
					 * for (int
					 * i=0;i<msg.getChathistory().length&&msg.getChathistory()[i
					 * ]!=null;i++) {
					 * System.out.println(msg.getChathistory()[i].getType()); }
					 */
					// 服务器转发消息
					byte[] buf = Serialize.ObjectToByte(msg);
					DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
							packet.getPort());
					serverSocket.send(backPacket);
					System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
							+ "目标姓名" + msg.getTargetName() + backPacket.getAddress() + " " + backPacket.getPort());
				}
				// 用户退出
				else if (msg.getType().equalsIgnoreCase("M_QUIT")) {
					// 更新显示
					parentUI.txtArea.append(new String(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
							+ "用户" + msg.getName() + "下线\n"));
					parentUI.updateUserList(msg);

					// 删除用户,并向其他用户发送下线消息
					Iterator<Entry<String, User>> it = userList.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, User> entry = it.next();
						if (!entry.getKey().equals(msg.getName())) {
							Message recallmsg = new Message();
							recallmsg.setType("M_QUIT");
							recallmsg.setId(msg.getId());
							recallmsg.setName(msg.getName());
							byte[] buf1 = Serialize.ObjectToByte(recallmsg);
							DatagramPacket recall = new DatagramPacket(buf1, buf1.length, entry.getValue().getAddress(),
									entry.getValue().getPort());// 向其他用户发送的报文
							serverSocket.send(recall);
							System.out.println("服务器发送消息: " + recallmsg.getType() + " 用户: " + recallmsg.getId() + "姓名"
									+ recallmsg.getName() + " " + "目标姓名" + recallmsg.getTargetName()
									+ recall.getAddress() + " " + recall.getPort());
						}
					}
					userList.remove(msg.getName());// 删除用户

				} // 删除好友
				else if (msg.getType().equalsIgnoreCase("M_DELEF")) {
					User bean = new User();
					bean.setUserId(msg.getId());
					/*
					 * int fid = userDao.getId(msg.getTargetName());
					 * userDao.deleteFriend(bean, fid); msg.setTargetId(fid);
					 */
					Iterator<Entry<String, User>> it = userList.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, User> entry = it.next();
						if (entry.getKey().equals(msg.getTargetName())) {
							msg.setTargetId(entry.getValue().getUserId());
							msg.setToAddr(entry.getValue().getAddress());
							msg.setToPort(entry.getValue().getPort());
							break;
						}
					}
					// 在数据库中双向删除
					userDao.deleteFriend(bean, msg.getTargetId());
					byte[] buf1 = Serialize.ObjectToByte(msg);
					DatagramPacket backPacket = new DatagramPacket(buf1, buf1.length, msg.getToAddr(), msg.getToPort());
					//如果该用户在线，更新其在线列表
					if(msg.getToAddr()!=null){
					serverSocket.send(backPacket);
					}
					System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
							+ "目标姓名" + msg.getTargetName() + backPacket.getAddress() + " " + backPacket.getPort());
				}
				// 添加好友
				else if (msg.getType().equalsIgnoreCase("M_ADDF")) {
					User bean = new User();
					bean.setUserId(msg.getId());
					Iterator<Entry<String, User>> it = userList.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, User> entry = it.next();
						if (entry.getKey().equals(msg.getTargetName())) {
							msg.setTargetId(entry.getValue().getUserId());
							msg.setToAddr(entry.getValue().getAddress());
							msg.setToPort(entry.getValue().getPort());
							break;
						}
					}
					// z在数据库中双向添加
					userDao.addFriend(bean, msg.getTargetId());
					// 发送消息
					byte[] buf1 = Serialize.ObjectToByte(msg);
					DatagramPacket backPacket = new DatagramPacket(buf1, buf1.length, msg.getToAddr(), msg.getToPort());
					serverSocket.send(backPacket);
					System.out.println("服务器发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
							+ "目标姓名" + msg.getTargetName() + backPacket.getAddress() + " " + backPacket.getPort());

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

package com.chill.chatapplet.client;

import java.awt.SystemColor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import com.chill.chatapplet.action.Serialize;
import com.chill.chatapplet.dao.UserDao;
import com.chill.chatapplet.entity.Chatmsg;
import com.chill.chatapplet.entity.Message;
import com.chill.chatapplet.entity.User;
import com.chill.chatapplet.server.ServerUI;

public class ClientReceiveMessage extends Thread {
	private DatagramSocket serverSocket; // 服务器套接字
	private DatagramPacket packet; // 报文
	private byte[] data = new byte[8096]; // 8K字节数组
	private ClientUI clientUI; // 消息窗口

	public ClientReceiveMessage(DatagramSocket socket, ClientUI clientUI) {
		serverSocket = socket;
		this.clientUI = clientUI;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				data = new byte[8096];
				packet = new DatagramPacket(data, data.length);
				if (serverSocket.isClosed()) {
					this.interrupt();
				}
				serverSocket.receive(packet);
				Message msg = (Message) Serialize.ByteToObject(packet.getData());
				System.out.println("客户端收到消息: " + msg.getType() + " 用户:" + msg.getId() + " 姓名" + msg.getName() + " 目标姓名"
						+ msg.getTargetName() + " " + packet.getAddress() + " " + packet.getPort());
				// 更新在线列表
				if (msg.getType().equalsIgnoreCase("M_LOGIN")) {
					// System.out.println(msg.getType()+" 用户"+
					// msg.getName()+"上线");
					clientUI.model1.addElement(new RenderObject("在线", msg.getName()));
					clientUI.panel.setText("在线人数:" + clientUI.model1.size());

				} else if (msg.getType().equalsIgnoreCase("M_QUIT")) {
					// System.out.println(msg.getType()+" 用户"+
					// msg.getName()+"下线");
					clientUI.model1.removeElement(clientUI.getRenderr(msg.getName()));
					clientUI.panel.setText("在线人数:" + clientUI.model1.size());
				}
				// 更新聊天面板
				else if (msg.getType().equalsIgnoreCase("M_SCHAT")) {
					msg.getChatmsg().setType(true);
					clientUI.addMsg(msg.getChatmsg(), clientUI.getRender(msg.getName()).getJpane());
					clientUI.setPanePrio(clientUI.getRender(msg.getName()));// 将有消息更新的面板优先级提高
					clientUI.list.setSelectedValue(clientUI.getRender(msg.getName()), true);// 将指针默认移动到新窗口上
				} else if (msg.getType().equalsIgnoreCase("M_PCHAT")) {
					msg.getChatmsg().setType(true);
					clientUI.addMsg(msg.getChatmsg(), clientUI.publicPane);
					clientUI.setPanePrio(clientUI.getRender(msg.getName()));// 将有消息更新的面板优先级提高
					clientUI.list.setSelectedIndex(0);// 将指针默认移动到新窗口上
				}
				// 更新好友列表
				else if (msg.getType().equalsIgnoreCase("M_ADDF")) {
					JTextPane jPane = new JTextPane();
					jPane.setBounds(0, 0, 500, 400);
					jPane.setBackground(SystemColor.inactiveCaptionBorder);
					jPane.setEditable(false);
					// 每个窗口都设置为滑动
					JScrollPane scrollPane = new JScrollPane(jPane);
					scrollPane.setBounds(0, 0, 500, 400);
					clientUI.layeredPane.add(scrollPane);
					clientUI.getHistory(msg.getName());// 再做一次朋友吧
					RenderObject ro = new RenderObject("好友", msg.getName(), jPane, scrollPane);
					clientUI.model.addElement(ro);
					clientUI.setPanePrio(ro);// 设置优先级
				} else if (msg.getType().equalsIgnoreCase("M_DELEF")) {

					clientUI.setPanePrio(clientUI.getRender("PUBLIC"));// 先将公聊面板置顶
					clientUI.contentPane.remove(clientUI.getRender(msg.getName()).getJscroll()); // 先移除滑动窗口
					clientUI.contentPane.remove(clientUI.getRender(msg.getName()).getJpane());// 再移除窗体
					clientUI.model.removeElement(clientUI.getRender(msg.getName()));
				}
				// 更新历史消息
				else if (msg.getType().equalsIgnoreCase("M_SELECT")) {
					// System.out.println(msg.getType()+" 获得与"+
					// msg.getTargetName()+"的历史消息");
					for (int i = 0; i < 10 && msg.getChathistory()[i] != null; i++) {
						clientUI.addMsg(msg.getChathistory()[i], clientUI.getRender(msg.getTargetName()).getJpane());
						// System.out.print(msg.getChathistory()[i].getChatmsg()+"
						// ");
					}
					// System.out.println();
				} else if (msg.getType().equalsIgnoreCase("M_FILE")) {
					int res = JOptionPane.showConfirmDialog(null, "是否接收文件 " + msg.getChatmsg().getFilename(),
							"来自用户" + msg.getName(), JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.YES_OPTION) {
						msg.setType("M_YSE");
						byte[] buf = Serialize.ObjectToByte(msg);
						DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
								packet.getPort());
						serverSocket.send(backPacket);
						System.out.println(msg.getToAddr() + " " + msg.getToPort() + " "
								+ msg.getChatmsg().getFileindex() + " " + msg.getChatmsg().getFilelength());
						// 新建一个ServerSocket服务器，等待文件接收
						ServerSocket server = new ServerSocket(msg.getToPort());
						Thread th = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									System.out.println("开始监听");
									Socket socket = server.accept();
									System.out.println("有链接");
									receiveFile(socket, msg.getChatmsg().getFilelength(),clientUI.progressBar);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (server != null)
										try {
											server.close();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
								}
							}

						});
						th.run();

					} else {
						msg.setType("M_NO");
						byte[] buf = Serialize.ObjectToByte(msg);
						DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(),
								packet.getPort());
						serverSocket.send(backPacket);
						System.out.println(
								"客户端发送消息: " + msg.getType() + " 用户:" + msg.getTargetId() + " 姓名" + msg.getTargetName()
										+ " 目标姓名" + msg.getId() + " " + packet.getAddress() + " " + packet.getPort());
					}
				} else if (msg.getType().equalsIgnoreCase("M_YSE")) {
					System.out.println(msg.getToAddr() + "  " + msg.getToPort());
					sendFile(msg, clientUI.progressBar);
				} else if (msg.getType().equalsIgnoreCase("M_NO")) {
					JOptionPane.showMessageDialog(null, "对方已拒绝", "文件发送", JOptionPane.WARNING_MESSAGE);
				} else if (msg.getType().equalsIgnoreCase("M_FAILURE")) {
					System.out.println("失败");
				}

			} catch (IOException | BadLocationException e) {
				e.printStackTrace();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendFile(Message msg, JProgressBar jbar) throws CloneNotSupportedException, IOException {
		jbar.setVisible(true);
		int length = 0;
		byte[] sendByte = null;
		Socket socket = null;
		DataOutputStream dout = null;
		FileInputStream fin = null;

		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(msg.getToAddr(), msg.getToPort()), 10 * 1000);
			dout = new DataOutputStream(socket.getOutputStream());
			File file = new File(msg.getChatmsg().getFileindex());
			Long filLength = file.length();
			fin = new FileInputStream(file);
			sendByte = new byte[1024 * 8];
			dout.writeUTF(file.getName());
			double curLength = 0;
			while ((length = fin.read(sendByte, 0, sendByte.length)) > 0) {
				dout.write(sendByte, 0, length);
				dout.flush();
				curLength ++;
				jbar.setValue((int) (curLength*100/(filLength/1024/8)));
			}
			sleep(1000);
			jbar.setVisible(false);
		} catch (Exception e) {

		} finally {
			if (dout != null)
				dout.close();
			if (fin != null)
				fin.close();
			if (socket != null)
				socket.close();
		}
	}

	public void receiveFile(Socket socket, Long  filelength ,JProgressBar pBar) throws IOException {
		 pBar.setVisible(true);
		byte[] inputByte = null;
		int length = 0;
		DataInputStream din = null;
		FileOutputStream fout = null;
		try {
			din = new DataInputStream(socket.getInputStream());
			fout = new FileOutputStream(new File("D:\\" + din.readUTF()));
			inputByte = new byte[1024 * 8]; // 8KB
			System.out.println("开始接收数据...");
			int i = 0;
			while (true) {
				if (din != null) {
					length = din.read(inputByte, 0, inputByte.length);
				}
				if (length == -1) {
					break;
				}
				i++;
				pBar.setValue((int) (i*100/(filelength/1024/8)));
		
				fout.write(inputByte, 0, length);
				fout.flush();
			}
			System.out.println("完成接收");
			sleep(1000);
			 pBar.setVisible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fout != null)
				fout.close();
			if (din != null)
				din.close();
			if (socket != null)
				socket.close();
		}
	}
}

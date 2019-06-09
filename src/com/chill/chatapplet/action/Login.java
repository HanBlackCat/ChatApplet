package com.chill.chatapplet.action;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.chill.chatapplet.dao.UserDao;
import com.chill.chatapplet.entity.*;
import com.chill.chatapplet.action.*;
import com.chill.chatapplet.client.ClientUI;

public class Login extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel nameLab = new JLabel("用户名:");
	JLabel passwdLab = new JLabel("密  码:");
	JTextField username = new JTextField();
	JPasswordField password = new JPasswordField();
	JButton regist = new JButton("注册");
	JButton reset = new JButton("重置");
	JButton btn = new JButton("登录");
	String queryResult = "";
	UserDao dbOperation = new UserDao();

	public Login() {
		this.setTitle("登录");
		this.setSize(430, 360);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		ImageIcon image = new ImageIcon("images/top.png");
		JLabel background = new JLabel(image);
		background.setBounds(0, 0, 430, 180);
		this.getContentPane().add(background);
		this.getContentPane().add(nameLab);
		this.getContentPane().add(passwdLab);
		this.getContentPane().add(username);
		this.getContentPane().add(password);
		this.getContentPane().add(regist);
		this.getContentPane().add(reset);
		this.getContentPane().add(btn);
		nameLab.setBounds(74, 186, 80, 20);
		nameLab.setForeground(new Color(0, 0, 0));
		nameLab.setFont(new Font("黑体", 0, 16));
		passwdLab.setBounds(74, 216, 60, 20);
		passwdLab.setForeground(new Color(0, 0, 0));
		passwdLab.setFont(new Font("黑体", 0, 16));
		username.setBounds(139, 181, 175, 30);
		password.setBounds(139, 210, 175, 30);

		/**
		 * 注册
		 */
		regist.setForeground(new Color(0, 142, 224));
		regist.setBounds(299, 181, 75, 30);
		regist.setContentAreaFilled(false);// 不绘制按钮区域
		regist.setBorderPainted(false);// 不绘制边框
		regist.setFont(new Font("黑体", Font.PLAIN, 14));
		regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Regist();
			}
		});
		/**
		 * 重置
		 */
		reset.setForeground(new Color(0, 142, 224));
		reset.setBounds(299, 211, 75, 30);
		reset.setContentAreaFilled(false);// 不绘制按钮区域
		reset.setBorderPainted(false);// 不绘制边框
		reset.setFont(new Font("黑体", Font.PLAIN, 14));
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				username.setText("");
				password.setText("");
			}
		});

		/**
		 * 登录
		 */
		btn.setForeground(Color.white);
		btn.setBackground(new Color(0, 142, 224));
		btn.setFont(new Font("黑体", 0, 16));
		btn.setBounds(184, 253, 75, 30);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String userName = username.getText().trim();
					String passWord = String.valueOf(password.getPassword()).trim();
					InetAddress remoteAddr = InetAddress.getByName("127.0.0.2");
					int remotePort = 5000;
					// 创建UDP套接字
					DatagramSocket clientSocket = new DatagramSocket();
					clientSocket.setSoTimeout(3000);// 设置超时时间
					Message msg = new Message();
					msg.setName(userName);// 登录名
					msg.setPassword(passWord);// 密码
					msg.setType("M_LOGIN"); // 登录消息类型
					msg.setToAddr(remoteAddr); // 目标地址
					msg.setToPort(remotePort); // 目标端口
					byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
					DatagramPacket packet = new DatagramPacket(data, data.length, remoteAddr, remotePort);
					// 发送登录报文
					clientSocket.send(packet);

					// 接收服务器回送的报文
					byte[] data1 =new  byte[8096];
					DatagramPacket backPacket = new DatagramPacket(data1, data1.length);
					clientSocket.receive(backPacket);
					clientSocket.setSoTimeout(0);// 取消超时时间
					Message backMsg = (Message) Serialize.ByteToObject(backPacket.getData());
				
					// 处理登录结果
					if (backMsg.getType().equalsIgnoreCase("M_SUCCESS")) { // 登录成功
						dispose(); // 关闭登录对话框
						ClientUI client = new ClientUI(clientSocket,backMsg); // 创建客户机界面
						client.setTitle("Hello, "+backMsg.getName()); // 设置标题
						client.setVisible(true); // 显示会话窗体
					} else if (backMsg.getType().equalsIgnoreCase("M_YET")) {
						JOptionPane.showMessageDialog(Login.this, "请勿重复登录");
					} else if (backMsg.getType().equalsIgnoreCase("M_FAILURE")) { // 登录失败
						JOptionPane.showMessageDialog(Login.this,
								"用户名或密码错误" + backMsg.getId() + " " + backMsg.getPassword());
					} else {
						JOptionPane.showMessageDialog(Login.this, "连接服务器失败");
					}
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

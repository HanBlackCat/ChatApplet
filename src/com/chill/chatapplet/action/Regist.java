package com.chill.chatapplet.action;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import javax.swing.border.EmptyBorder;

import com.chill.chatapplet.dao.UserDao;
import com.chill.chatapplet.entity.Message;
import com.chill.chatapplet.action.Serialize;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
public class Regist extends JFrame {

	private static final long serialVersionUID = 1L; // 序列化版本
	private JPanel imagePanel;
	private ImageIcon background;

	private JTextField name;
	private JPasswordField password;
	private JPasswordField password1;
	private JTextField chekcode;
	private ValidCode vcode;
	UserDao dbOperation = new UserDao();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Regist frame = new Regist();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Regist() {
		this.setLocationRelativeTo(null);//居中
		this.setTitle("注册");
		// 设置背景
		background = new ImageIcon("images/top.png");// 背景图片
		JLabel label = new JLabel(background);// 把背景图片显示在一个标签里面
		// 把标签的大小位置设置为图片刚好填充整个面板
		label.setBounds(0, 0, background.getIconWidth(), background.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		imagePanel.setLayout(null);
		// 把背景图片添加到分层窗格的最底层作为背景
		this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(430, 360);
		this.setResizable(false);
		this.setVisible(true);

		JLabel nameLab = new JLabel("用户名：");
		nameLab.setForeground(Color.WHITE);
		nameLab.setFont(new Font("黑体", Font.PLAIN, 15));
		nameLab.setToolTipText("");
		this.getContentPane().add(nameLab);

		JLabel passwordLab = new JLabel("密码：");
		passwordLab.setForeground(Color.WHITE);
		passwordLab.setFont(new Font("黑体", Font.PLAIN, 15));
		this.getContentPane().add(passwordLab);

		JLabel password1Lab = new JLabel("确认密码：");
		password1Lab.setForeground(Color.WHITE);
		password1Lab.setFont(new Font("黑体", Font.PLAIN, 15));
		this.getContentPane().add(password1Lab);
		password = new JPasswordField();
		password1 = new JPasswordField();
		password1.setHorizontalAlignment(SwingConstants.LEFT);
		password1.setColumns(10);
		this.getContentPane().add(password1);

		name = new JTextField();
		this.getContentPane().add(name);
		name.setColumns(10);

		password.setColumns(10);
		this.getContentPane().add(password);

		JLabel chekcodeLab = new JLabel("校验码：");
		chekcodeLab.setForeground(Color.WHITE);
		chekcodeLab.setFont(new Font("黑体", Font.PLAIN, 15));
		this.getContentPane().add(chekcodeLab);

		vcode = new ValidCode();
		vcode.setLocation(219, 227);
		this.getContentPane().add(vcode);

		chekcode = new JTextField();
		this.getContentPane().add(chekcode);

		JButton register = new JButton("注册");
		register.setForeground(Color.white);
		register.setBackground(new Color(0, 142, 224));
		register.setFont(new Font("黑体", 0, 16));
		this.getContentPane().add(register);
		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userName = name.getText().trim();
				String passWord = String.valueOf(password.getPassword()).trim();
				String passWord1 = String.valueOf(password1.getPassword()).trim();
				String  salt = AESUtil.getInstance().generateNewKey();//自动生成 
				if(passWord.equals(passWord1)&&!passWord.isEmpty()){
					if(ValidCode.compare(vcode.getCode(), chekcode.getText())){
					//连接服务器
						try {
							InetAddress remoteAddr=InetAddress.getByName("127.0.0.2");
							int remotePort=5000;
							//创建UDP套接字
							DatagramSocket registerSocket=new DatagramSocket();
							registerSocket.setSoTimeout(3000);//设置超时时间
							Message msg=new Message();
							msg.setType("M_REGISTER");
							msg.setName(userName);
							msg.setSalt(salt);
							msg.setPassword(AESUtil.getInstance().encode(passWord, salt));
							System.out.println(msg.getName()+"   "+msg.getSalt());
							byte[] buf = Serialize.ObjectToByte(msg);							
							DatagramPacket packet=new DatagramPacket(buf,buf.length,remoteAddr,remotePort);
							registerSocket.send(packet);
							DatagramPacket backpacket=new DatagramPacket(buf,buf.length);
							registerSocket.receive(backpacket);
							registerSocket.setSoTimeout(0);
							Message backmsg = (Message) Serialize.ByteToObject(backpacket.getData());
							if (backmsg.getType().equalsIgnoreCase("M_SUCCESS")) {
								JOptionPane.showMessageDialog(Regist.this,"注册成功r\n 用户id是"+backmsg.getId());
								Thread.sleep(500);
								dispose();
							}
							else {
								JOptionPane.showMessageDialog(Regist.this,"注册失败");
							}
							registerSocket.close();
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SocketException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else{
						JOptionPane.showMessageDialog(Regist.this,"验证码错误");
					}
				}
				else{
					JOptionPane.showMessageDialog(Regist.this,"密码不一致/密码不为空");
				}

			}
		});

		nameLab.setBounds(120, 35, 66, 40);
		passwordLab.setBounds(120, 85, 56, 40);
		password1Lab.setBounds(120, 135, 90, 40);
		chekcodeLab.setBounds(120, 185, 90, 40);
		register.setBounds(156, 270, 90, 40);
		name.setBounds(219, 40, 98, 24);
		password.setBounds(219, 90, 98, 24);
		password1.setBounds(219, 140, 98, 24);
		chekcode.setBounds(219, 190, 98, 24);

		this.setVisible(true);
	}

}

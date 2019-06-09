package com.chill.chatapplet.server;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Component;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import com.chill.chatapplet.action.Serialize;
import com.chill.chatapplet.entity.*;

public class ServerUI extends JFrame {
	private DatagramSocket serverSocket;
	private HashMap<String,User> userList=new HashMap<String,User>(); //用户列表
	
	private JTextField txtHostname;
	private JTextField txtPort;
	JTextArea txtArea;
	private JScrollPane scrollPane;
	private JPanel panelNotify;
	private JPanel panelStart;
	private JButton btnStart;
	private DefaultListModel<String> model =new DefaultListModel();
	private JList panelList = new JList(model);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerUI frame = new ServerUI();
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
	public ServerUI() {
		setTitle("QQServer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 430);
		
		panelStart = new JPanel();
		panelStart.setBorder(BorderFactory.createTitledBorder(null, "启动服务器",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 1, 14)));
		
		panelNotify = new JPanel();
		panelNotify.setBorder(BorderFactory.createTitledBorder(null, "聊天室大厅",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 1, 14)));
		
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setBorder(BorderFactory.createTitledBorder(null, "在线用户列表",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 1, 14)));
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panelNotify, GroupLayout.PREFERRED_SIZE, 379, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
						.addComponent(panelStart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
						.addComponent(panelNotify, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		panelList.setBackground(Color.WHITE);
		scrollPane1.setViewportView(panelList);
		panelList.setLayout(new GridLayout(0, 1, 0, 0));
		
		scrollPane = new JScrollPane();
		GroupLayout gl_panelNotify = new GroupLayout(panelNotify);
		gl_panelNotify.setHorizontalGroup(
			gl_panelNotify.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelNotify.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelNotify.setVerticalGroup(
			gl_panelNotify.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelNotify.createSequentialGroup()
					.addGap(12)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtArea = new JTextArea();
		txtArea.setColumns(20);
		txtArea.setRows(7);
		txtArea.setFont(new Font("宋体", Font.BOLD, 16));
		scrollPane.setViewportView(txtArea);
		panelNotify.setLayout(gl_panelNotify);
		
		JLabel lblHostname = new JLabel("\u4E3B\u673A\u540D\uFF1A");
		lblHostname.setFont(new Font("宋体", Font.BOLD, 14));
		
		txtHostname = new JTextField();
		txtHostname.setFont(new Font("宋体", Font.BOLD, 14));
		txtHostname.setText("127.0.0.2");
		txtHostname.setColumns(10);
		
		JLabel lblPort = new JLabel("\u7AEF\u53E3\uFF1A");
		lblPort.setFont(new Font("宋体", Font.BOLD, 14));
		
		txtPort = new JTextField();
		txtPort.setFont(new Font("宋体", Font.BOLD, 14));
		txtPort.setText("5000");
		txtPort.setColumns(10);
		
		btnStart = new JButton("\u542F \u52A8");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnStartActionPerformed(e);
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GroupLayout gl_panelStart = new GroupLayout(panelStart);
		gl_panelStart.setHorizontalGroup(
			gl_panelStart.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStart.createSequentialGroup()
					.addGap(24)
					.addComponent(lblHostname)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtHostname, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblPort)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(19, Short.MAX_VALUE))
		);
		gl_panelStart.setVerticalGroup(
			gl_panelStart.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStart.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_panelStart.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPort)
						.addComponent(txtHostname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblHostname)
						.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(25, Short.MAX_VALUE))
		);
		panelStart.setLayout(gl_panelStart);
		getContentPane().setLayout(groupLayout);
	}
	private void btnStartActionPerformed(java.awt.event.ActionEvent evt) throws SocketException {
		try {
			//获取服务器工作地址端口
			String hostName=txtHostname.getText();
			int hostPort=Integer.parseInt(txtPort.getText());
			//创建UDP数据报套接字,在指定端口侦听
			serverSocket=new DatagramSocket(hostPort);
			txtArea.append("服务器开始侦听...\n");
			//创建并启动UDP消息接收线程
			Thread recvThread=new ReceiveMessage(serverSocket,this);
			recvThread.start();
			//创建并启动文件接收线程
		} catch (NumberFormatException  e) {
			e.printStackTrace();
		}
		btnStart.setEnabled(false);
	}
	public void setUserList(HashMap<String, User> userList) {
		this.userList = userList;
	}
	public JList getPanelList() {
		return panelList;
	}
	public void updateUserList(Message msg) {
		if(msg.getType().equalsIgnoreCase("M_LOGIN")){
			
			model.addElement(msg.getName());
		}
		else if(msg.getType().equalsIgnoreCase("M_QUIT")){
			model.removeElement(msg.getName());
		}
	}
}

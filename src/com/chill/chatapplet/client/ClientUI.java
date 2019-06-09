package com.chill.chatapplet.client;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.chill.chatapplet.action.Serialize;
import com.chill.chatapplet.client.*;
import com.chill.chatapplet.entity.Chatmsg;
import com.chill.chatapplet.entity.Message;
import com.chill.chatapplet.entity.User;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore.Entry.Attribute;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.FlowLayout;
import java.awt.AWTException;
import java.awt.BorderLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.SystemTray;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.event.MouseAdapter;
import javax.swing.JProgressBar;

public class ClientUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JPanel contentPane;
	public DatagramSocket clientSocket; // 客户机套接字
	public InputStream in; // 文件输入流
	public Socket socket; // 传输文件socket
	public Message msg; // 消息对象
	/** 初始化的好友列表 */
	public String[] friend;
	public int id;// 用户id
	public String name;// 用户名字
	public InetAddress toAddress;// 文件传输地址
	public int toPort; // 文件传输端口
	/** 初始化的用户在线列表 */
	public String[] userList;
	/**消息接收线程*/
	Thread recThread;
	public JPanel left; // 左侧区域
	public JScrollPane scrollPane_3;
	public DefaultListModel<RenderObject> model;
	public CellRenderer cellRenderer;
	public JList<RenderObject> list;
	public JPopupMenu popupMenu; // 右键菜单
	public JMenuItem delete;// 删除好友
	public JScrollPane publicscroller; // 中心区域
	public JLayeredPane layeredPane;
	/**
	 * 好友列表里每个cell对应的JTextPane 可以使用model
	 */
	//public HashMap<String, JTextPane> listPane;
	/*** 好友列表里每个cell对应的JScrollPane */
	//public HashMap<String, JScrollPane> listScroll;
	public JTextPane publicPane;// 一个公聊
	public JPanel bottom; // 底部区域
	public JScrollPane scrollPane_2;
	public JTextArea txtAreaInput;
	public JMenu SendImag;
	public JMenu SendFile;
	public JButton btnSend;
	public JLayeredPane right;
	public JLabel panel; // 右侧区域
	public JPopupMenu rpopupMenu; // 右键菜单
	public JMenuItem add;// 添加好友
	public JScrollPane scrollPane_1;
	public DefaultListModel<RenderObject> model1;
	public JList<RenderObject> list_1;
	public LittleRenderer little;
	public JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUI frame = new ClientUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 构造无参Frame
	 */
	public ClientUI() {
		Image icon = Toolkit.getDefaultToolkit().getImage("images/我.png");
		setIconImage(icon);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		/**
		 * 左侧窗口
		 **/

		left = new JPanel();
		left.setBorder(UIManager.getBorder("TextField.border"));
		left.setBackground(Color.WHITE);
		contentPane.add(left);
		left.setBounds(0, 0, 170, 600);
		left.setLayout(new BorderLayout(0, 0));

		model = new DefaultListModel<RenderObject>();
		// model.addElement(new RenderObject("公聊", "PUBLIC"));
		cellRenderer = new CellRenderer();
		list = new JList<RenderObject>(model);
		list.setBackground(SystemColor.inactiveCaptionBorder);
		list.setForeground(Color.LIGHT_GRAY);
		list.setCellRenderer(cellRenderer);
		scrollPane_3 = new JScrollPane(list);
		scrollPane_3.setBorder(null);
		left.add(scrollPane_3);
		list.setSize(200, 600);
		list.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override // 在组件上单击（按下并释放）鼠标按钮时调用
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				setPanePrio(list.getSelectedValue());// 设置显示的窗口
				// 点击选择好友
				if (list.getSelectedIndex() != 0) {
					// 右键选择好友--删除
					if (e.getButton() == 3 && list.getSelectedIndex() >= 0) {

						popupMenu = new JPopupMenu();
						delete = new JMenuItem("删除好友");
						delete.setFont(new Font("黑体", 0, 16));
						delete.setForeground(new Color(0, 142, 224));
						delete.setBackground(Color.WHITE);
						delete.setSize(new Dimension(70, 40));
						delete.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								String name = ((RenderObject) model.getElementAt(list.getSelectedIndex())).getName();
								System.out.println("删除好友"+list.getSelectedValue().getName());
								deleteFriend(name);
							}
						});
						popupMenu.add(delete);
						popupMenu.show(list, e.getX(), e.getY());

					}
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		/**
		 * 中心窗口
		 **/
		layeredPane = new JLayeredPane();
		contentPane.add(layeredPane);

		layeredPane.setBounds(170, 0, 500, 400);

		publicPane = new JTextPane();
		publicPane.setBackground(Color.WHITE);
		publicPane.setBounds(0, 0, 500, 400);
		publicPane.setEditable(false);

		publicscroller = new JScrollPane(publicPane);
		layeredPane.setLayer(publicscroller, 4);
		publicscroller.setBounds(0, 0, 500, 400);

		layeredPane.add(publicscroller);
		// 初始化聊天列表的list RenderObject
		model.addElement(new RenderObject("公聊", "PUBLIC", publicPane, publicscroller));
		
		
		/**
		 * 底部窗口
		 */
		bottom = new JPanel();
		bottom.setLayout(null);
		bottom.setBackground(Color.WHITE);
		contentPane.add(bottom);
		bottom.setBounds(170, 400, 500, 153);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBorder(null);
		bottom.add(scrollPane_2);
		txtAreaInput = new JTextArea();
		txtAreaInput.setFont(new Font("微软雅黑", 0, 20));
		txtAreaInput.setLineWrap(true);
		scrollPane_2.setViewportView(txtAreaInput);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(null);
		menuBar.setLayout(null);
		menuBar.setForeground(Color.BLACK);
		menuBar.setBackground(Color.WHITE);
		scrollPane_2.setColumnHeaderView(menuBar);
		menuBar.setPreferredSize(new Dimension(500, 24));

		SendImag = new JMenu("");
		SendImag.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				SendImag.setSelected(false);
			}
		});
		SendImag.setFocusPainted(false);
		SendImag.setBounds(5, 0, 30, 24);
		SendImag.setIcon(new ImageIcon(("images/image.PNG")));
		menuBar.add(SendImag);

		SendFile = new JMenu("");
		SendFile.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				SendFile.setSelected(true);
				String filepath = null;
				Long filelength = null;
				String filename = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("选择上传文件");
				fileChooser.setApproveButtonText("选择");
				int choice = fileChooser.showOpenDialog(fileChooser);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					filelength = file.length();
					filename = file.getName();
					filepath = file.getAbsolutePath();// 文件路径
				}
				Chatmsg cmsg = new Chatmsg();
				cmsg.setFilename(filename);
				cmsg.setFileindex(filepath);
				cmsg.setFilelength(filelength);
				cmsg.setTime(new Timestamp(System.currentTimeMillis()));
				String tarname = list.getSelectedValue().getName();
				try {
					chatpri(cmsg,tarname); //传文件前进行一次对话，对方是否接收
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		SendFile.setFocusPainted(false);
		SendFile.setBounds(40,0,30,24);
		SendFile.setIcon(new ImageIcon(("images/File.png")));
		menuBar.add(SendFile);

		JPanel panelClick = new JPanel();
		panelClick.setBackground(Color.WHITE);
		bottom.add(panelClick);

		btnSend = new JButton("发送");
		bottom.add(btnSend);
		btnSend.setVisible(true);
		btnSend.setForeground(Color.white);
		btnSend.setBackground(new Color(0, 142, 224));
		btnSend.setFont(new Font("黑体", 0, 16));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Chatmsg cmsg = new Chatmsg();
				cmsg.setId(id);
				cmsg.setTime(new Timestamp(System.currentTimeMillis()));
				cmsg.setChatmsg(txtAreaInput.getText().trim());
				cmsg.setType(false);
				try {
					if (list.getSelectedIndex() == 0) {
						chatPub(cmsg); // 发送消息
						addMsg(cmsg, publicPane);// 写入数据框
						System.out.println("公共聊天");
						txtAreaInput.setText("");
					} else if (list.getSelectedIndex() > 0) {
						// 好友聊天
						String name = list.getSelectedValue().getName();
						chatpri(cmsg, name);
						addMsg(cmsg, list.getSelectedValue().getJpane());
						System.out.println("好友聊天");
						txtAreaInput.setText("");
					}
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		scrollPane_2.setBounds(0, 5, 500, 120);
		txtAreaInput.setBounds(5, 5, 500, 120);
		menuBar.setBounds(10, 5, 500, 23);

		progressBar = new JProgressBar();
		menuBar.add(progressBar);
		progressBar.setVisible(false);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		panelClick.setBounds(0, 120, 386, 33);
		progressBar.setBounds(350, 0,100, 24);
		btnSend.setBounds(400, 120, 80, 30);
		/**
		 * 右侧窗口
		 */
		right = new JLayeredPane();
		right.setBackground(Color.WHITE);
		contentPane.add(right);
		right.setBounds(670, 0, 212, 553);
		right.setLayout(null);

		panel = new JLabel();
		panel.setBorder(UIManager.getBorder("List.noFocusBorder"));
		panel.setBackground(SystemColor.inactiveCaptionBorder);
		right.add(panel);
		panel.setBounds(0, 0, 220, 40);
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		right.add(scrollPane_1);
		scrollPane_1.setBounds(0, 40, 220, 560);
		model1 = new DefaultListModel<RenderObject>();
		little = new LittleRenderer();
		list_1 = new JList<RenderObject>(model1);
		list_1.setBackground(SystemColor.inactiveCaptionBorder);
		list_1.setForeground(Color.LIGHT_GRAY);
		list_1.setCellRenderer(little);
		scrollPane_1.setViewportView(list_1);
		list_1.setBounds(0, 40, 220, 500);
		list_1.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				// 右键选择好友--添加
				if (e.getButton() == 3 && list_1.getSelectedIndex() >= 0) {

					rpopupMenu = new JPopupMenu();
					add = new JMenuItem("添加好友");
					add.setFont(new Font("黑体", 0, 10));
					add.setForeground(new Color(0, 142, 224));
					add.setBackground(Color.WHITE);
					add.setSize(new Dimension(70, 40));
					add.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// addFriend(((RenderObject)
							// model1.getElementAt(list_1.getSelectedIndex())).getName());
							addFriend(list_1.getSelectedValue().getName());
							System.out.println("正在添加好友" + list_1.getSelectedValue().getName());
						}
					});
					rpopupMenu.add(add);
					rpopupMenu.show(list_1, e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		// 关闭窗口，执行退出操作(发送退出消息)
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				exit();
			}
		});
	}

	/**
	 * 根据登录返回的信息初始化聊天界面
	 * 
	 * @throws SocketException
	 */
	public ClientUI(DatagramSocket loginsocket, Message recmsg) throws SocketException {
		this(); // 调用无参数构造函数，初始化界面
		clientSocket = loginsocket; // 初始化会话套接字
		//listPane = new HashMap<String, JTextPane>();
		//listScroll = new HashMap<String, JScrollPane>();
		System.out.println("id: " + recmsg.getId() + " " + " name: " + recmsg.getName());
		name = recmsg.getName();
		id = recmsg.getId();
		friend = recmsg.getFriend();
		userList = recmsg.getUserList();
		list.setSelectedIndex(0);
		// 启动UDP收发线程 ,此时将这个socket的接收线程receive，从login那儿通过再发一次消息转移过来
	  recThread = new ClientReceiveMessage(clientSocket, this);
		recThread.start();
		// 初始化好友列表和在线列表
		for (String string : friend) {
			JTextPane jPane = new JTextPane();
			jPane.setBounds(0, 0, 500, 400);
			jPane.setBackground(SystemColor.inactiveCaptionBorder);
			jPane.setEditable(false);
			// 每个窗口都设置为滑动
			JScrollPane scrollPane = new JScrollPane(jPane);
			scrollPane.setBounds(0, 0, 500, 400);
			layeredPane.add(scrollPane);
			getHistory(string);
			model.addElement(new RenderObject("好友", string, jPane, scrollPane));
			//System.out.println();
			//listPane.put(string, jPane);
			//listScroll.put(string, scrollPane);
		}
		for (String string1 : userList) {
			model1.addElement(new RenderObject("在线", string1));
		}
		panel.setText("在线人数:" + model1.size());
		// 设置托盘
		createSystemTrayIcon();
		this.validate();// 重绘
		this.repaint();
	}

	/** 设置窗口优先级 */
	public void setPanePrio(RenderObject ro) {
		// 将所有窗口的优先级设为2，被选中的设为4

		for (int i = 0; i < model.size(); i++) {
			layeredPane.setLayer(model.getElementAt(i).getJscroll(), 2);
		}

		/*
		 * for(String member:friend){
		 * layeredPane.setLayer(listScroll.get(member),2);
		 * //System.out.print(member+"优先级设为2"+" "); }
		 */
		layeredPane.setLayer(publicscroller, 2);
		if (ro.getType().equals("公聊")) {
			// System.out.println("显示公共聊天");
			layeredPane.setLayer(publicscroller, 4);
		} else {
			layeredPane.setLayer(ro.getJscroll(), 4);
			// System.out.println("显示和"+ro.getName()+"的聊天");
		}

	}
	/**通过好友名字找到对应的RenderObject*/
	public RenderObject getRender(String name) {
		for (int i = 0; i < model.size(); i++) {
			if(model.getElementAt(i).getName().equals(name)){
				return model.getElementAt(i);
			}
		}
		return null;
	}
	/**通过好友名字找到在线列表中的RenderObject*/
	public RenderObject getRenderr(String name) {
		for (int i = 0; i < model1.size(); i++) {
			if(model1.getElementAt(i).getName().equals(name)){
				return model1.getElementAt(i);
			}
		}
		return null;
	}
	
	/** 初始化聊天窗口,查看历史记录 */
	public void getHistory(String tarname) {
		try {
			if (id != 0) { // 防止无参面板发送消息
				msg = new Message();
				msg.setType("M_SELECT"); // 登录消息类型
				msg.setId(id);
				msg.setName(name);
				msg.setTargetName(tarname);
				byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.2"), 5000);
				// 发送登录报文
				clientSocket.send(packet);
				System.out.println("客户端发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
						+"目标姓名"+ msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 添加好友 */
	public void addFriend(String tarname) {
		
		//1在自己列表中添加  2把加好友消息通过服务器发送给目的用户
		JTextPane jPane = new JTextPane();
		jPane.setBounds(0, 0, 500, 400);
		jPane.setBackground(SystemColor.inactiveCaptionBorder);
		jPane.setEditable(false);
		//每个窗口都设置为滑动
		JScrollPane scrollPane =new JScrollPane(jPane);
		scrollPane.setBounds(0, 0, 500, 400);
		layeredPane.add(scrollPane);			
		getHistory(tarname);//查找以前的消息		
		RenderObject ro=new RenderObject("好友", tarname,jPane,scrollPane);
		model.addElement(ro);
	    setPanePrio(ro);//设置优先级
		try {
			if (id != 0) { // 防止无参面板发送消息
				msg = new Message();
				msg.setType("M_ADDF"); // 登录消息类型
				msg.setId(id);
				msg.setName(name);
				msg.setTargetName(tarname);
				byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.2"), 5000);
				// 发送登录报文
				clientSocket.send(packet);
				System.out.println("客户端发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
						+"目标姓名"+ msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 删除好友 */
	public void deleteFriend(String tarname) {
		try {
			setPanePrio(getRender("PUBLIC"));//先将公聊面板置顶
			contentPane.remove(getRender(tarname).getJscroll()); //先移除滑动窗口
			contentPane.remove(getRender(tarname).getJpane());//再移除窗体
			this.model.removeElement(getRender(tarname));
			if (id != 0) { // 防止无参面板发送消息
				// 发送删除报文
				msg = new Message();
				msg.setType("M_DELEF"); // 登录消息类型
				msg.setId(id);
				msg.setName(name);
				msg.setTargetName(tarname);
				byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.2"), 5000);
				clientSocket.send(packet);
				System.out.println("客户端发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
						+"目标姓名"+ msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 用户退出 */
	private void exit() {
		try {
			if (id != 0) { // 防止无参面板发送消息
				msg = new Message();
				msg.setType("M_QUIT"); // 登录消息类型
				msg.setId(id);
				msg.setName(name);
				byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.2"), 5000);
				// 发送登录报文
				clientSocket.send(packet);
				System.out.println("客户端发送消息: " + msg.getType() + " 用户: " + msg.getId() + "姓名" + msg.getName() + " "
						+"目标姓名"+ msg.getTargetName() + packet.getAddress() + " " + packet.getPort());
				recThread.stop();
				clientSocket.close();				
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.dispose();
	}

	/**
	 * 公共聊天
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void chatPub(Chatmsg cmsg) throws CloneNotSupportedException {
		try {
			if (id != 0) { // 防止无参面板发送消息
				msg = new Message();
				msg.setChatmsg(cmsg);
				msg.setType("M_PCHAT"); // 登录消息类型
				msg.setId(id);
				msg.setName(name);
				byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.2"), 5000);
				clientSocket.send(packet);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 私人聊天/准备发送文件消息
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void chatpri(Chatmsg cmsg, String tar) throws CloneNotSupportedException {
		try {
			if (id != 0) { // 防止无参面板发送消息
				msg = new Message();
				msg.setChatmsg(cmsg);
				if (cmsg.getFileindex() != null) {
					msg.setType("M_FILE"); 
				} else {
					msg.setType("M_SCHAT");
				}
				msg.setId(id);
				msg.setName(name);
				msg.setTargetName(tar);
				byte[] data = Serialize.ObjectToByte(msg); // 消息对象序列化
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.2"), 5000);
				clientSocket.send(packet);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 添加一条消息
	 * 
	 * @throws BadLocationException
	 */
	public void addMsg(Chatmsg cmsg, JTextPane pane) throws BadLocationException {
		Color msgColor;
		if (cmsg.getId() == 0)
			return;
		if (cmsg.getType() == true) {// false发送绿色 true接收蓝色
			msgColor = new Color(0, 142, 224);
		} else {
			msgColor = new Color(46, 139, 87);
		}
		SimpleAttributeSet attrset = new SimpleAttributeSet();
		StyleConstants.setForeground(attrset, msgColor);
		StyleConstants.setFontSize(attrset, 16);// 字体大小
		StyleConstants.setFontFamily(attrset, "微软雅黑");// 字体名称
		Document docs = pane.getDocument();
		docs.insertString(docs.getLength(), "   " + cmsg.getId() + " " + cmsg.getTime() + "\n", attrset);

		StyleConstants.setForeground(attrset, Color.black);
		StyleConstants.setFontSize(attrset, 20);
		if (cmsg.getFileindex() == null) {// 如果不是文件传输消息
			docs.insertString(docs.getLength(), "  " + cmsg.getChatmsg() + "\n", attrset);
		} else {
			docs.insertString(docs.getLength(), "  文件:" + cmsg.getFilename() + " 目录:" + cmsg.getFileindex() + "\n",
					attrset);
		}
	}

	/** 设置托盘 */
	private void createSystemTrayIcon() {
		if (SystemTray.isSupported()) {// 判断系统是否托盘
			try {
				TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("images/托盘.png"));// 创建一个托盘图标对象
				icon.setImageAutoSize(true);
				icon.setToolTip("TIM:" + name + "(" + id + ")");
				PopupMenu menu = new PopupMenu();// 创建弹出菜单
				MenuItem openUI = new MenuItem("打开主面板");// 创建一个菜单项
				MenuItem quitUI = new MenuItem("退出");// 创建一个菜单项
				openUI.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setState(JFrame.NORMAL);
						setVisible(true);
					}
				});
				quitUI.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						exit();
						System.exit(0);
					}
				});
				menu.add(openUI);// 将菜单项添加到菜单列表
				menu.add(quitUI);// 将菜单项添加到菜单列表
				icon.setPopupMenu(menu);// 将菜单添加到托盘图标
				SystemTray tray = SystemTray.getSystemTray();// 获取系统托盘
				tray.add(icon);// 将托盘图表添加到系统托盘
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
}

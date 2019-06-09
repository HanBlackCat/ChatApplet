package com.chill.chatapplet.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class TestJList extends JFrame implements ActionListener {
	JList jl;
	JScrollPane jsp;// 滑块
	DefaultListModel dlm;// Jlist里面的内容的对象
	JButton jb1;// 删除按钮
	JButton jb2;// 添加按钮
	JPanel jp;// 放2个按钮的panel
	JPanel jp2;// 放文本框的panel
	JTextField jtf;// 文本框，用于输入要插入的文字

	public TestJList() {
		this.setLocationRelativeTo(null);
		// TODO Auto-generated constructor stub
		jb1 = new JButton("删除");
		jb2 = new JButton("添加");
		jp = new JPanel();
		jp2 = new JPanel();
		jtf = new JTextField("请在这里输入想添加的内容");
		dlm = new DefaultListModel();
		jl = new JList(dlm);// 创建一个包含DefaultListModel的Jlist
		jsp = new JScrollPane(jl);// 创建一个包含Jlist的滑块
		// 以下是布局，大体就是把按钮添加到panel，把panel添加到窗体
		jp.setLayout(new FlowLayout());
		jp.add(jb1);
		jp.add(jb2);
		jp2.setLayout(new FlowLayout());
		jp2.add(jtf);
		setLayout(new GridLayout(3, 1));
		add(jsp);
		add(jp2);
		add(jp);
		jb1.addActionListener(this);// 把监听注册个按钮
		jb2.addActionListener(this);// 把监听注册个按钮
		setSize(200, 400);
		setVisible(true);
		// 添加一个点击右上“叉”关闭窗口的事件
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0); // 关闭
			}
		});
	}

	// 添加一个按钮的监听
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 如果按的是删除按钮
		if (e.getSource() == jb1) {
			// 移除当前选择的内容
			dlm.remove(jl.getSelectedIndex());
		}
		// 如果按的是田间按钮
		if (e.getSource() == jb2) {
			// 吧文本框中的内容添加到列表
			dlm.addElement(jtf.getText());
		}
	}

	// 测试主函数
	public static void main(String[] args) {
		TestJList tj = new TestJList();
	}
}

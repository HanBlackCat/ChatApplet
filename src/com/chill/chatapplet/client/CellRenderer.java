package com.chill.chatapplet.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
/**
 * 自定义好友单元格传入对象**/
class RenderObject{
	private String type;
	private String name;
	private JTextPane jpane; //聊天窗体
	private JScrollPane jscroll; //聊天窗体外的滑动框
	public String getType() {
		return type;
	}
	public RenderObject(String type, String name,JTextPane jpane,JScrollPane jscroll) {
		super();
		this.type = type;
		this.name = name;
		this.jpane=jpane;
		this.jscroll=jscroll;
	}
	public RenderObject(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public JTextPane getJpane() {
		return jpane;
	}
	public void setJpane(JTextPane jpane) {
		this.jpane = jpane;
	}
	public JScrollPane getJscroll() {
		return jscroll;
	}
	public void setJscroll(JScrollPane jscroll) {
		this.jscroll = jscroll;
	}
	
}
/**
 * 自定义好友列表单元格**/
public class CellRenderer extends JPanel implements ListCellRenderer{
	private ImageIcon icon;
	private String name;
	private String type;
	private Color background ,foreground;
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// TODO Auto-generated method stub
		type =  ((RenderObject)value).getType();
		icon = new ImageIcon("images/"+type+".png");
		name = ((RenderObject)value).getName();
		foreground = isSelected ?list.getBackground():list.getForeground();
		background = isSelected ?list.getForeground():list.getBackground();
		return this;
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(background);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(new Color(0, 0, 0));
		g.drawImage(icon.getImage(), 10, 5,null);
		g.setFont(new Font("微软雅黑",0,20));
		g.drawString(name, icon.getImage().getWidth(null)+20, 40);

	}
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(200,80);
	}
	
}

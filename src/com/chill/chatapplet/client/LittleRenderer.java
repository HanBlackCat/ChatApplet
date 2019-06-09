package com.chill.chatapplet.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

	public class LittleRenderer extends JPanel implements ListCellRenderer{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
			g.drawString(name, 50, 25);

		}
		@Override
		public Dimension getPreferredSize() {
			// TODO Auto-generated method stub
			return new Dimension(200,40);
		}
		
	}

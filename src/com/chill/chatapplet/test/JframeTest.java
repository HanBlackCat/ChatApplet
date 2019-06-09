package com.chill.chatapplet.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import java.awt.CardLayout;
import javax.swing.JProgressBar;

public class JframeTest extends JFrame {

	private JPanel contentPane;
	private JTextPane textField;
	private JTextPane textField_1;
	private JTextPane textField_2;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JframeTest frame = new JframeTest();
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
	public JframeTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLayeredPane layeredPane = new JLayeredPane();
		contentPane.add(layeredPane);
			
		textField_1 = new JTextPane();
		textField_1.setBackground(Color.ORANGE);
		textField_1.setBounds(0, 0, 400, 300);
		layeredPane.setLayout(null);
		
		textField = new JTextPane();
		textField.setBackground(Color.RED);
		textField.setBounds(0, 0, 300, 400);
		
		textField_2 = new JTextPane();
		textField_2.setBackground(Color.CYAN);
		textField_2.setBounds(0, 0, 400, 300);
		
		JScrollPane scrollPane=new  JScrollPane(textField);
		scrollPane.setBounds(0, 0, 472, 316);
		layeredPane.add(scrollPane);
		
		JScrollPane scrollPane1=new  JScrollPane(textField_1);
		scrollPane1.setBounds(0, 0, 472, 316);
		layeredPane.add(scrollPane1);
		
		JScrollPane scrollPane2=new  JScrollPane(textField_2);
		scrollPane2.setBounds(0, 0, 472, 316);
		layeredPane.add(scrollPane2);
		
		layeredPane.setLayer(scrollPane, 4);
		layeredPane.setLayer(scrollPane1, 2);
		layeredPane.setLayer(scrollPane2, 3);
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int flag=0; //不可行
				if(flag==0){
				layeredPane.setLayer(scrollPane, 100);
				validate();
				flag=1;
				}else{
				layeredPane.setLayer(scrollPane, 300);
				validate();
				flag=0;
				}
			}
		});
		contentPane.add(btnNewButton, BorderLayout.NORTH);
		
		JProgressBar progressBar = new JProgressBar();
		contentPane.add(progressBar, BorderLayout.SOUTH);
	}
}

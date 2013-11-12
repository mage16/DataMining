package edu.georgiasouthern.gui;

import java.applet.Applet;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class ConsoleWindow extends Panel{

protected JTextArea consoleWindow;
public ConsoleWindow(){
	init();
}
	private static final long serialVersionUID = 1L;
	public void init() {
		setSize(290, 80);
		setLayout(null);
		
		consoleWindow = new JTextArea();
		consoleWindow.setBounds(0, 0, 270, 80);
		consoleWindow.setVisible(true);
		JScrollPane scrollPane = new JScrollPane(consoleWindow);
		scrollPane.setBounds(0, 0, 290, 80);
		add(scrollPane);
	//	add(consoleWindow);
	//add(but);	
	PrintStream p = new PrintStream(new OutputStream() {
		StringBuilder s = new StringBuilder();
		@Override
		public void write(int b) throws IOException {

				consoleWindow.append(String.valueOf((char)b));
				if(consoleWindow.getText().length()>2048)consoleWindow.replaceRange(null, 0, 1024);
				if(b==13 || b==10)consoleWindow.setCaretPosition(consoleWindow.getText().length());
				
		}
	});
	System.setOut(p);
	
	}

}

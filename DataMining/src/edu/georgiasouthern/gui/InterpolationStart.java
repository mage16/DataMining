package edu.georgiasouthern.gui;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InterpolationStart {
	public static void main(String[] args){
		InterpolationStart is = new InterpolationStart();
		
	}   
public InterpolationStart(){
	JFrame j = new JFrame("Interpolation System");
	j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	j.setLayout(new FlowLayout());
	j.setSize(540,600);
	Gui g = new Gui();
	j.add(g);
	g.init();
	g.start();
	JPanel jp = new JPanel(new FlowLayout());
	jp.add(new JLabel("CSCI 7090 Interpolation Project Georgia Southern Univeristy Spring 2013"));
	JPanel jp2 = new JPanel(new FlowLayout());
	jp2.add(new JLabel("(C) 2013 Michael Grecol, Travis Losser, Wes Clemmons, Charles Yorke"));
	jp.setVisible(true);
	jp2.setVisible(true);
	j.add(jp);
	j.add(jp2);
	j.setVisible(true);
	
}
}

package edu.georgiasouthern.visualization;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
public class AniMAP extends JPanel{
	private static final long serialVersionUID = 1L;
	JSlider slider = new JSlider();
	JLabel status = new JLabel();
JLabel legend = new JLabel();
	
	JButton play = new JButton("Play");
	JButton stop = new JButton("Stop");
    public static ArrayList<Area> usa1 = new ArrayList<Area>();
    public static ArrayList<Anim> layer1 = new ArrayList<Anim>(); 
	private Timer atimer;

    public AniMAP(ArrayList<Area> usa, ArrayList<Anim> layerA){  
        for(int k = 0; k < usa.size(); k++){
            usa1.add(usa.get(k));
        } 
        for(int k = 0; k < layerA.size(); k++){
            layer1.add(layerA.get(k));
        }
        ChangeListener changeAction=new ChangeListener()
        {
            public void stateChanged (ChangeEvent event)
            {
            	repaint();
            }
        };
        



		atimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				slider.setValue(slider.getValue() + 1);
			}
		});

		play.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Execute when button is pressed
				atimer.start();
			}

		});
		stop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Execute when button is pressed
				atimer.stop();
			}

		});
		
		
		play.setLocation(20, 20);
		play.setSize(90, 30);
		stop.setLocation(120, 20);
		stop.setSize(90, 30);
		
		this.add(play);
		this.add(stop);
        
    	slider.setValue(0);
    	slider.addChangeListener(changeAction);
    	slider.setMajorTickSpacing(10); 
    	slider.setPaintLabels(true); 
    	slider.setMinimum(0);
    	slider.setMaximum(363);
    	slider.setPaintTicks(true); 
    	slider.setBounds(0, 0, 1150, 50);
    	status = new JLabel("", JLabel.LEFT);
    	//status.setVerticalAlignment(JLabel.TOP);
    	legend = new JLabel("<html><ul>pm25 >= 50</ul><ul>pm25 = 37.5</ul><ul>pm25 = 25</ul><ul>pm25 = 12.5</ul><ul>pm25 = 0</ul></html>", JLabel.LEFT);
		legend.setFont(new Font(legend.getFont().getName(), Font.PLAIN, 15));
		setLayout(new BorderLayout());
		
		this.add(legend, BorderLayout.WEST);
		this.add(slider, BorderLayout.PAGE_END);
		this.add(status, BorderLayout.NORTH);
    }
    

    
    public void paintComponent(Graphics g){  
    	


    	
    	

        super.paintComponent(g);
        for(int i = 0; i < usa1.size(); i++){
            Polygon p = usa1.get(i).boundary;          
                g.drawPolygon(p);               
        } 
        
		Graphics2D g2d = (Graphics2D) g;
		GradientPaint gradient = new GradientPaint(15, 130, Color.red, 20, 280, Color.green);
		g2d.setPaint(gradient);
		g2d.fillRect(15, 130, 20, 180);        
        
        
        status.setText("Slider Value: " + Integer.toString(slider.getValue()) + " Year: " + layer1.get(0).inter.get(slider.getValue()).year + " Month: " + layer1.get(0).inter.get(slider.getValue()).month + " Day: " + layer1.get(0).inter.get(slider.getValue()).day);      
        
        for(int i = 0; i < layer1.size(); i++){
        	
        		int p = (int)((layer1.get(i).inter.get(slider.getValue()).pm25) * 12);
        		if (p > 255) p = 255;
        		Color r = new Color(p, 255 - p, 0);
        		g.setColor(r);
                int x = layer1.get(i).x;
                int y = layer1.get(i).y;
                g.fillOval(x-2, y-2, 4, 4);               
        }
        

        
        
   }      

}

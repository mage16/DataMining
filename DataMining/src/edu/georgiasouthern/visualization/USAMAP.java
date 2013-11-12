package edu.georgiasouthern.visualization;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import javax.swing.JPanel;
public class USAMAP extends JPanel{
    public static ArrayList<Area> usa1 = new ArrayList<Area>();
    public static ArrayList<Centriod> layer1 = new ArrayList<Centriod>(); 
    public static ArrayList<Centriod> layer2 = new ArrayList<Centriod>();
    public USAMAP(ArrayList<Area> usa, ArrayList<Centriod> layerA, ArrayList<Centriod> layerB){  
        for(int k = 0; k < usa.size(); k++){
            usa1.add(usa.get(k));
        } 
        for(int k = 0; k < layerA.size(); k++){
            layer1.add(layerA.get(k));
        }
         for(int k = 0; k < layerB.size(); k++){
            layer2.add(layerB.get(k));
        } 
    }
    
    protected void paintComponent(Graphics g){  
        super.paintComponent(g);
        for(int i = 0; i < usa1.size(); i++){
            Polygon p = usa1.get(i).boundary;          
                g.drawPolygon(p);               
        } 
        g.setColor(Color.BLUE);
        for(int i = 0; i < layer1.size(); i++){
                int x = layer1.get(i).x;
                int y = layer1.get(i).y;
                g.fillOval(x-2, y-2, 4, 4);               
        }
        
        g.setColor(Color.RED);
        for(int i = 0; i < layer2.size(); i++){
                int x = layer2.get(i).x;
                int y = layer2.get(i).y;
                g.fillOval(x-2, y-2, 4, 4);               
        } 
   }        
}

package edu.georgiasouthern.visualization;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
//import java.io.IOException;
import java.util.ArrayList;
//import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
public class AnimTest {
	public  ArrayList<Area> usa = new ArrayList<Area>();  
	public  ArrayList<GeoTag> layersTag = new ArrayList<GeoTag>();
	public void testMap() throws FileNotFoundException{
		JSlider slider;
		JLabel sliderLabel;
		File file = new File("src//st99_d00.dat");
		File file1 = new File("src//st99_d00a.dat");
		File file2 = new File("src//blkgrp_xy.txt");
		File file3 = new File("src//county_xy.txt");
		File file4 = new File("src//test3.txt");

		if(!(file.exists())){
			System.out.println("file does not exit");
			System.exit(0);
		} 
		if(!(file1.exists())){
			System.out.println("file1 does not exit");
			System.exit(0);
		} 
		if(!(file2.exists())){
			System.out.println("file2 does not exit");
			System.exit(0);
		}       
		if(!(file3.exists())){
			System.out.println("file3 does not exit");
			System.exit(0);
		} 

		StateTag(new FileReader(file1));
		coordinates(new FileReader(file));      
		//ArrayList<Centriod> blkgp = processLocation(file2);
		//ArrayList<Centriod> county = processLocation(file3);
		ArrayList<Anim> interp = processInterpolated(new FileReader(file3), new FileReader(file4));
		AniMAP map = new AniMAP(usa, interp);
		map.setFocusable(true);       
		JFrame frame = new JFrame("Geo Interpolation");
		frame.add(map);
		frame.setSize(1200, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	public  static void main(String []args) throws FileNotFoundException{
	AnimTest a = new AnimTest();
	a.testMap();
//	JFrame frame = new JFrame("Geo Interpolation");
//	frame.add(a.displayMap(new File("src//county_xy.txt"), new File("src//test3.txt")));
//	frame.setSize(1200, 500);
//	frame.setLocationRelativeTo(null);
//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	frame.setVisible(true);
	} 
	public AniMAP displayMap(Reader coordinateFile, Reader interpolatedFile) throws FileNotFoundException{
	//	InputStream input = getClass().getResourceAsStream("/classpath/to/my/file");
		StateTag(new InputStreamReader(getClass().getResourceAsStream("/st99_d00a.dat")));//Need to change these to resources in the JAR
		coordinates(new InputStreamReader(getClass().getResourceAsStream("/st99_d00.dat"))); 
		ArrayList<Anim> interp = processInterpolated(coordinateFile, interpolatedFile);
		return new AniMAP(usa, interp);
	}
	public   void coordinates(Reader reader) throws FileNotFoundException{        
		boolean start = true;          
		ArrayList<Polygon> list1 = new ArrayList<Polygon>(); 
		// Scanner input = new Scanner(file);   

		Polygon polygon = new Polygon();
		int id = -1000000000;
		BufferedReader br = new BufferedReader(reader);
		String s;
		try{
			while((s = br.readLine()) != null){

				s = s.trim().toUpperCase();
				if(id==-99999){
					id =195;
				}
				if(s.compareTo("END")==0){
					if(id>-1){
						list1.add(polygon);                            
						Area newArea = new Area();
						newArea.ID = id;
						newArea.boundary = polygon;                     
						newArea.FID = layersTag.get(id).Fid;
						newArea.Name = layersTag.get(id).Name;
						newArea.FID1 = layersTag.get(id).Fid2;
						usa.add(newArea);     
					}  
					start =true;
					id =-999;
					polygon = new Polygon();                                      
					continue;                  
				}  
				ArrayList<String> list2 = new ArrayList<String>();
				String [] k= s.split("\\s+");                     
				for (int i = 0; i < k.length; i++){
					k[i] = k[i].trim();
					if(k[i].length()>0){
						list2.add(k[i]);
					}
				}

				if(list2.size()==0){
					continue;
				}                   
				int xcoor1 = 0;
				int ycoor1 = 0;                
				if(start){
					if(list2.size()==3){                             
						id = Integer.parseInt(list2.get(0));                       
						double  xcoor = Double.parseDouble(list2.get(1));                            
						xcoor1 =(int) Math.round(((xcoor + 180) * 13.333));
						xcoor1 -= 350;                            
						double  ycoor = Double.parseDouble(list2.get(2));               
						ycoor = Math.abs((ycoor -90) * 13.333);
						ycoor1 =(int)Math.round(ycoor);                
						ycoor1 -= 480;
						polygon.addPoint(xcoor1, ycoor1);                          
					}
				}
				else if(list2.size()==2){                        
					double  xcoor = Double.parseDouble(list2.get(0));                   
					xcoor1 =(int) Math.round(((xcoor + 180) * 13.333));
					xcoor1 -= 350;                          
					double  ycoor = Double.parseDouble(list2.get(1));               
					ycoor = Math.abs((ycoor -90) * 13.333);
					ycoor1 =(int)Math.round(ycoor);                
					ycoor1 -= 480;
					polygon.addPoint(xcoor1, ycoor1); 

				}                      
				start = false;                  

			}
			br.close();
		}
		catch(Exception ex){ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}

	}

	public  void StateTag(Reader reader) throws FileNotFoundException{              
		try{               
			//Scanner input = new Scanner(file);   
			BufferedReader br = new BufferedReader(reader);
			int index = 0;
			GeoTag tag = new GeoTag();  
			String item;
			while((item = br.readLine()) != null){                                                                       
				//String item = input.nextLine(); 
				index++;
				switch (index){
				case 1:
					tag.Id = Integer.parseInt(item.trim());
					break;
				case 2:
					tag.Fid = item;
					break;
				case 3:
					tag.Name = item;
					break;
				case 4:
					tag.Fid2 = item;
					break;
				case 5:
					tag.Unknown = item;
					layersTag.add(tag);                            
					index = 0;
					//if(input.hasNext())
						//    input.nextLine();
					br.readLine();
					tag = new GeoTag();
					break;
				}                        
			}
			br.close();

		}                
		catch (Exception ex){                               
			JOptionPane.showMessageDialog(null, ex.toString());    
		} 
		//System.out.println();
	}   

	public ArrayList<Centriod> processLocation(Reader reader) throws FileNotFoundException{          
		ArrayList<Centriod> layer = new ArrayList<Centriod>();            
		try{                

			//Scanner input = new Scanner(file);
			BufferedReader br = new BufferedReader(reader);
			String s = br.readLine();
			while((s = br.readLine()) != null){ 
				//String s = input.nextLine();
				String[] tokens = s.split("\\t");

				//Scanner s1 = new Scanner(s);                                
				Centriod location = new Centriod();
				location.ID = tokens[0];                                               
				int xcoor1 = 0;
				double  xcoor = Double.parseDouble(tokens[1]);                   
				xcoor1 =(int) Math.round(((xcoor + 180) * 13.333));
				xcoor1 -= 350; 
				int ycoor1 = 0;
				double  ycoor = Double.parseDouble(tokens[2]);
				ycoor = Math.abs((ycoor -90) * 13.333);
				ycoor1 =(int)Math.round(ycoor);                  
				ycoor1 -= 480;  
				location.x = xcoor1;
				location.y = ycoor1;
				layer.add(location);                                    
			}        
			br.close();

		}                
		catch (Exception ex){                            
			JOptionPane.showMessageDialog(null, ex.toString());    
		}  
		return layer;
	}   
	
	public  ArrayList<Anim> processInterpolated(Reader coordReader, Reader interpReader) throws FileNotFoundException{          
		ArrayList<Anim> layer = new ArrayList<Anim>();            
		try{                

			//Scanner input = new Scanner(file);
			BufferedReader br = new BufferedReader(coordReader);
			BufferedReader br2 = new BufferedReader(interpReader);
			String s = br.readLine();
			String s2 = br2.readLine();
			//s2 = br2.readLine();
			while((s = br.readLine()) != null){ 
				//String s = input.nextLine();
				String[] tokens = s.split("\\t");
				//Scanner s1 = new Scanner(s);
				
				Anim location = new Anim();
				location.ID = tokens[0];                                               
				int xcoor1 = 0;
				double  xcoor = Double.parseDouble(tokens[1]);                   
				xcoor1 =(int) Math.round(((xcoor + 180) * 13.333));
				xcoor1 -= 350; 
				int ycoor1 = 0;
				double  ycoor = Double.parseDouble(tokens[2]);
				ycoor = Math.abs((ycoor -90) * 13.333);
				ycoor1 =(int)Math.round(ycoor);                  
				ycoor1 -= 480;  
				location.x = xcoor1;
				location.y = ycoor1;
				
				while(((s2 = br2.readLine()) != null) && (s2.startsWith(location.ID))){
					Interp in = new Interp();
					String[] tokens2 = s2.split("\\t");
					in.year = Integer.parseInt(tokens2[1]);
					in.month = Integer.parseInt(tokens2[2]);
					in.day = Integer.parseInt(tokens2[3]);
					in.pm25 = Double.parseDouble(tokens2[4]);
					location.inter.add(in);
				}
				layer.add(location);                                    
			}        
			br.close();
			br2.close();

		}                
		catch (Exception ex){                            
			JOptionPane.showMessageDialog(null, ex.toString());    
		}  
		return layer;
	}   
	
}

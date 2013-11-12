package edu.georgiasouthern.interpolation;import java.util.ArrayList;
import java.util.List;

import edu.georgiasouthern.common.GisData;


public class Sector {
public Sector root;
public Sector parent;
public List<Sector> children;
public double maxX;
public double maxY;
public double maxDate;
public double minX;
public double minY;
public double minDate;
private double xAdder;
private double yAdder;
private double dateAdder;
public List<GisData> data;
public Sector(double minX, 
		double maxX,
		double minY,
		double maxY,
		double minDate,
		double maxDate
		
		){
	this.root= this;
	//Max must be a little above the actual max for the comparrisons to work
	root.xAdder = ((maxX-minX)*.0001);
	maxX = maxX+xAdder;
	root.yAdder = ((maxY-minY)*.0001);
	maxY = maxY+ yAdder;
	root.dateAdder = ((maxDate-minDate)*.0001);
	maxDate = maxDate+dateAdder;
	this.init(minX,maxX,minY,maxY,minDate,maxDate);
}
private void init(	double minX,
		double maxX,
		double minY,
		double maxY,
		double minDate,
		double maxDate
	
		){
	this.maxX = maxX;
	this.minX = minX;
	this.maxY = maxY;
	this.minY = minY;
	this.maxDate = maxDate;
	this.minDate = minDate;
}
public Sector (	double minX,
		double maxX,
		double minY,
		double maxY,
		double minDate,
		double maxDate,
		Sector root,
		Sector parent,
		int levels
		){
	this.init(minX,maxX,minY,maxY,minDate,maxDate);
	this.root=root;
	this.parent=parent;
if (levels>0)	makeTree(levels);
	
}
public void makeTree(int levels){

	double xMidPoint = minX+((maxX-minX)/2);
	double yMidPoint = minY+((maxY-minY)/2);
	double dateMidPoint =minDate+( (maxDate-minDate)/2);
	children = new ArrayList<Sector>();
	children.add(new Sector(
			minX,xMidPoint,
			minY,yMidPoint,
			minDate,dateMidPoint,
			root,this,levels-1
			));
	children.add(new Sector(
			xMidPoint,maxX,
			minY,yMidPoint,
			minDate,dateMidPoint,
			root,this,levels-1
			));
	children.add(new Sector(
			minX,xMidPoint,
			yMidPoint,maxY,
			minDate,dateMidPoint,
			root,this,levels-1
			));
	children.add(new Sector(
			xMidPoint,maxX,
			yMidPoint,maxY,
			minDate,dateMidPoint,
			root,this,levels-1
			));
	children.add(new Sector(
			minX,xMidPoint,
			minY,yMidPoint,
			dateMidPoint,maxDate,
			root,this,levels-1
			));
	children.add(new Sector(
			xMidPoint,maxX,
			minY,yMidPoint,
			dateMidPoint,maxDate,
			root,this,levels-1
			));
	children.add(new Sector(
			minX,xMidPoint,
			yMidPoint,maxY,
			dateMidPoint,maxDate,
			root,this,levels-1
			));
	children.add(new Sector(
			xMidPoint,maxX,
			yMidPoint,maxY,
			dateMidPoint,maxDate,
			root,this,levels-1
			));
	
	

}
public Sector findSector(double x, double y, double date){
	for (int i=0; i<children.size(); i++){
		if (children.get(i).isInSector(x, y, date)){
			if(children.get(i).children!=null){
				return children.get(i).findSector(x, y, date);
			}else return children.get(i);
		}
	}
	return null;
}

public boolean isInSector(double x, double y, double date){
	if (x>root.maxX) x=root.maxX-root.xAdder;
	if(y>root.maxY)y=root.maxY-root.yAdder;
	if(date>root.maxDate) date = root.maxDate-root.dateAdder;
	if (y<root.minY)y=root.minY;
	if (x<root.minX)x=root.minX;
	if (date<root.minDate)date=root.minDate;
	
	
	return (x>=minX && x<maxX && y>=minY && y<maxY && date>=minDate && date <maxDate );
}

public void insertObject(GisData o,double x, double y, double date){
	Sector s = findSector(x,y,date);
	if(s.data==null)s.data=new ArrayList<GisData>();
	s.data.add(o);
}
public String toString(boolean b){
	return "x: " + minX + " - "   + maxX +"\ty: " + minY + " - " + maxY +"\tDate: " + minDate + " - " + maxDate;
}
public String toString(){
	String s = "";
	s+= this.hashCode();
	s+="  x: " + minX + " - " + maxX +", y: " + minY + " - " + maxY +", Date: " + minDate + " - " + maxDate;
	if (data !=null)s+=data.toString();
	//sb.append("Children: \r\n");
	if(children!=null){
	for (Sector se : children){
		s+=se.toString("\t" + se.toString());
	}
	s+="\r\n";
	}
return s;	
}
public String toString(String sb){
	return sb + "\t" + this.hashCode() + "  x: " + minX + "-" + maxX +", y: " + minY + "-" + maxY +", Date: " + minDate + "-" + maxDate  + "\r\n";	 
}
public List<Sector> findCloserSector(double distance, double X,double Y, double DateVal){
	List<Sector> sl = null;
	for (Sector sp : this.parent.parent.children){
		for (Sector s : sp.children){
		if(isCloserSector(distance, X, Y, DateVal, s)){
			if(sl==null)sl= new ArrayList<Sector>();
			sl.add(s);
			}
		}
	}
	return sl;
}
public boolean isCloserSector(double distance, double X,double Y, double DateVal, Sector s){
	//The sector is the same
	if (this==s)return false;
	double borderX=0;
	double borderY=0;
	double borderDate=0;
	//This is not adjacent
	if(!(this.minX==s.minX || this.minX==s.maxX||
		this.maxX==s.maxX || this.maxX==s.minX ||	
		this.minY==s.minY || this.minY==s.maxY||
		this.maxY==s.maxY || this.maxY==s.minY ||	
		this.minDate==s.minDate || this.minDate==s.maxDate||
		this.maxDate==s.maxDate || this.maxDate==s.minDate 	
			))return false;
	if(s.minX==this.minX && s.maxX==this.maxX){
		borderX=X;
		borderY = (s.maxY == this.minY)?s.maxY:s.minY;
		borderDate = (s.maxDate== this.maxDate)?s.maxDate:s.minDate;
	}else if(s.minY==this.minY && s.maxY==this.maxY){
	borderY=Y;
	borderX= (s.minX==this.maxX)?s.minX:s.maxX;
	borderDate = (s.minDate==this.maxDate)?s.minDate:s.maxDate;
	}else if(s.minDate==this.minDate && s.maxDate==this.maxDate){
	borderDate = DateVal;
	borderX = (s.minX==this.maxX)?s.minX:s.maxX;
	borderY = (s.minY==this.minY)?s.minY:s.minY;
	}else //Corner Sector
		if (this.maxX==s.minX && 
			this.maxY==s.minY &&  
			this.maxDate==s.minDate){
			borderX=this.maxX;
			borderY = this.maxY;
			borderDate = this.maxDate;
		
	}else //Corner Sector
		if(this.minX==s.maxX && 
			this.minY==s.maxY &&  
			this.minDate==s.maxDate){
			borderX=this.minX;
			borderY = this.minY;
			borderDate = this.minDate;
			}
return (Math.sqrt(((borderX-X)*(borderX-X))+((borderY-Y)*(borderY-Y))+ ((borderDate-DateVal)*(borderDate-DateVal)))<=distance);
	
}
public List<GisData> getAllChildren (List<GisData> l){
	if (this.children==null){
		if (this.data!=null)l.addAll(this.data);
		return l;
	}
	else {
		for (Sector s : this.children){
			s.getAllChildren(l);
		}
		return l;
	}
	
}
}

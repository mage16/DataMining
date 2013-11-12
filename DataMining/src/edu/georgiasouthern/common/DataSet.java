package edu.georgiasouthern.common;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;

import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.interpolation.Sector;

public class DataSet
{
	private String dataSetFileName;
	private String fileDelimiter;
	private GisDate.DateDomain dateDomain;
	private ArrayList<GisData> data;
	private ArrayList<String> header;
	private ArrayList<String[]> otherData;
	private int dataCount;
	private String fileType;
	//to populate SectorTree
	private int TREE_SIZE=5;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private double minDate;
	private double maxDate;
	private Sector sectorTree;
	private double dateFactor;
	private void populateTree(){
		//System.out.println("Building tree and inserting data");
		dateFactor = ((maxX-minX + maxY-minY) /2)/(maxDate-minDate); 
		sectorTree = new Sector(minX,maxX,minY, maxY, minDate*dateFactor, maxDate*dateFactor);
		sectorTree.parent=null;
		sectorTree.root=sectorTree;
		sectorTree.makeTree(TREE_SIZE);
		for (GisData g: data){
			g.getDate().setDateFactor(dateFactor);
			sectorTree.insertObject(g,g.getX(), g.getY(), g.getDateId());
		}
	}
	public enum FileDelimiter
	{
		comma, tab
	}
	
	public DataSet(String fileName, FileDelimiter delimiter)
	{
		fileType = "OTHER";
		otherData = new ArrayList<String[]>(); // the database in memory (intially empty)
		this.dataSetFileName = fileName;
		this.dateDomain = null;
		switch(delimiter)
		{
			case comma:
				this.fileDelimiter = ",";
				break;
			case tab:
				this.fileDelimiter = "\t";
				break;
			default:
				this.fileDelimiter = "\t";
				break;
		}
		dataCount = 0;
		try {
		// scan the database to load it into memory and count the support of each single item at the same time
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		// for each line (transactions) until the end of the file
		while (((line = reader.readLine()) != null)) {
			// if the line is  a comment, is  empty or is a
			// kind of metadata
			if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%'
					|| line.charAt(0) == '@') {
				continue;
			}
			String[] lineSplitted = line.split(fileDelimiter);
					
			// create an array of String to store the items in this transaction
			String transaction[] = new String[lineSplitted.length];
					
			// for each item in this line (transaction)
			for (int i=0; i< lineSplitted.length; i++) { 
				// transform this item from a string to an integer
				String item = lineSplitted[i];
				// store the item in the memory representation of the database
				transaction[i] = item;
			}
			// add the transaction to the database
			otherData.add(transaction);
			// increase the number of transaction
			dataCount++;
		}
		// close the input file
		reader.close();
		}
		catch(IOException e) {
			
		}
	}
	
	public DataSet(String fileName, GisDate.DateDomain domain, FileDelimiter delimiter)
	{
		fileType = "GIS";
		data = new ArrayList<GisData>();
		header = new ArrayList<String>();
		this.dataSetFileName = fileName;
		this.dateDomain =domain;  
		switch(delimiter)
		{
			case comma:
				this.fileDelimiter = ",";
				break;
			case tab:
				this.fileDelimiter = "\t";
				break;
			default:
				this.fileDelimiter = "\t";
				break;
		}
		dataCount = 0;
		importDataSet();
	}
	
	public void sortData()
	{
		Collections.sort(data);
	}
	
	private void importDataSet()
	{
		try
		{
			FileReader fr = new FileReader(dataSetFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine(); // read the header line
			parseHeaderLine(line);
			while((line = br.readLine()) != null)
			{
				parseDataLine(line);
			}
			
			dataCount = data.size();
			fr.close();
			populateTree();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File Not Found: " + dataSetFileName);
		}
		catch(IOException e)
		{
			System.out.println("File error");
		}
	}
	
	private void parseHeaderLine(String headerLine)
	{
		String s[] = headerLine.split(fileDelimiter);
		
		for(int i = 0; i < s.length; i++)
		{
			header.add(i, s[i]);
		}
	}
	
	private void parseDataLine(String dataLine)
	{
		String s[] = dataLine.split(fileDelimiter);
		long id;
		GisDate date;
		double x, y, measurement;
		int year, month, day, quarter;
		
		switch(dateDomain)
		{
			case Y:
				if(s.length == 5)
				{
					id = Long.parseLong(s[0]);
					year = Integer.parseInt(s[1]);
					x = Double.parseDouble(s[2]);
					y = Double.parseDouble(s[3]);
					measurement = Double.parseDouble(s[4]);
					date = new GisDateY(year,1.0);
					addData(id, x, y, measurement, date);
				
				}
				break;
			case YM:
				if(s.length == 6)
				{
					id = Long.parseLong(s[0]);
					year = Integer.parseInt(s[1]);
					month = Integer.parseInt(s[2]);
					x = Double.parseDouble(s[3]);
					y = Double.parseDouble(s[4]);
					measurement = Double.parseDouble(s[5]);
					date = new GisDateYM(year, month,1.0);
					addData(id, x, y, measurement, date);
					
				}
				break;
			case YMD:
				if(s.length == 7)
				{
					id = Long.parseLong(s[0]);
					year = Integer.parseInt(s[1]);
					month = Integer.parseInt(s[2]);
					day = Integer.parseInt(s[3]);
					x = Double.parseDouble(s[4]);
					y = Double.parseDouble(s[5]);
					measurement = Double.parseDouble(s[6]);
					date = new GisDateYMD(year, month, day,1.0);
					addData(id, x, y, measurement, date);
			
				}
				break;
			case YQ:
				if(s.length == 6)
				{
					id = Long.parseLong(s[0]);
					year = Integer.parseInt(s[1]);
					quarter = Integer.parseInt(s[2]);
					x = Double.parseDouble(s[3]);
					y = Double.parseDouble(s[4]);
					measurement = Double.parseDouble(s[5]);
					date = new GisDateYQ(year, quarter,1.0);
					addData(id, x, y, measurement, date);
				
				}
				break;
		}
	}
	private void addData(long id, double x,double y, double measurement, GisDate date){
		if (x<minX)minX=x;
		if(x>maxX) maxX=x;
		if (y<minY)minY=y;
		if(y>maxY) maxY=y;
		if (date.getDateId()<minDate)minDate=date.getDateId();
		if(date.getDateId()>maxDate)maxDate = date.getDateId();
		data.add(new GisData(id, x, y, measurement, date));
	}
	public ArrayList<GisData> getData()
	{
		return data;
	}
	
	public ArrayList<String[]> getOtherData()
	{
		return otherData;
	}
	
	public ArrayList<String> getHeader()
	{
		return header;
	}
	
	public int getDataCount()
	{
		return dataCount;
	}
	
	public String getFileType()
	{
		return fileType;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSet pm25 = new DataSet("src//pm25_2009_measured.txt", GisDate.DateDomain.YMD, FileDelimiter.tab);
		ArrayList<String> header = pm25.getHeader();
		ArrayList<GisData> data = pm25.getData();
		int dataCount = pm25.getDataCount();
		for(int i = 0; i < header.size(); i++)
		{
			System.out.print(header.get(i) + "\t");
		}
		System.out.println();
		for(int i = 0; i < dataCount; i++)
		{
			GisData row = data.get(i);
			long id = row.getId();
			double x = row.getX();
			double y = row.getY();
			double measurement = row.getMeasurement();
			GisDate.DateDomain domain = row.getDate().getDateDomain();
			int year, month, day, quarter;
			
			switch(domain)
			{
			case Y:
				GisDateY aDateY = (GisDateY)row.getDate();
				year = aDateY.getYear();
				System.out.print(id + "\t" + year + "\t" + x + "\t" + y + "\t" + measurement);
				break;
			case YM:
				GisDateYM aDateYM = (GisDateYM)row.getDate();
				year = aDateYM.getYear();
				month = aDateYM.getMonth();
				System.out.print(id + "\t" + year + "\t" + month + "\t" + x + "\t" + y + "\t" + measurement);
				break;
			case YMD:
				GisDateYMD aDateYMD = (GisDateYMD)row.getDate();
				year = aDateYMD.getYear();
				month = aDateYMD.getMonth();
				day = aDateYMD.getDay();
				System.out.print(id + "\t" + year + "\t" + month + "\t" + day + "\t" + x + "\t" + y + "\t" + measurement);
				break;
			case YQ:
				GisDateYQ aDateYQ = (GisDateYQ)row.getDate();
				year = aDateYQ.getYear();
				quarter = aDateYQ.getQuarter();
				System.out.print(id + "\t" + year + "\t" + quarter + "\t" + x + "\t" + y + "\t" + measurement);
				break;
			}
			
			System.out.println();
		}
	}

	public Sector getSectorTree() {
		return sectorTree;
	}

	public void setSectorTree(Sector sectorTree) {
		this.sectorTree = sectorTree;
	}

	public double getDateFactor() {
		return dateFactor;
	}

	public void setDateFactor(double dateFactor) {
		this.dateFactor = dateFactor;
	}

	public GisDate.DateDomain getDateDomain() {
		return dateDomain;
	}

	public void setDateDomain(GisDate.DateDomain dateDomain) {
		this.dateDomain = dateDomain;
	}

	public String getDataSetFileName() {
		return dataSetFileName;
	}

	public void setDataSetFileName(String dataSetFileName) {
		this.dataSetFileName = dataSetFileName;
	}



}

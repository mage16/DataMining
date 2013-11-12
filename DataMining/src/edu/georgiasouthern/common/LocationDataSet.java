package edu.georgiasouthern.common;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.util.ArrayList;


public class LocationDataSet
{
	private String dataSetFileName;
	private String fileDelimiter;
	private ArrayList<GisData> data;
	private ArrayList<String> header;
	private int dataCount;
	
	public enum FileDelimiter
	{
		comma, tab
	}
	
	public LocationDataSet(String fileName, FileDelimiter delimiter)
	{
		data = new ArrayList<GisData>();
		header = new ArrayList<String>();
		this.dataSetFileName = fileName;
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
		importLocationDataSet();
	}
	
	private void importLocationDataSet()
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
		long id=0;
		GisData gisData;
		double x, y, measurement;
		
		if(s.length == 3)
		{
			id = Long.parseLong(s[0]);
			x = Double.parseDouble(s[1]);
			y = Double.parseDouble(s[2]);
			measurement = 0.0;
			gisData = new GisData(id, x, y, measurement);
			data.add(gisData);
		}
	}
	
	public ArrayList<GisData> getData()
	{
		return data;
	}
	
	public ArrayList<String> getHeader()
	{
		return header;
	}
	
	public int getDataCount()
	{
		return dataCount;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LocationDataSet county = new LocationDataSet("src//blkgrp_xy.txt", FileDelimiter.tab);
		ArrayList<String> header = county.getHeader();
		ArrayList<GisData> data = county.getData();
		int dataCount = county.getDataCount();
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
				
			System.out.print(id + "\t" + x + "\t" + y);
			
			System.out.println();
		}
		System.out.println("Total Data Count: " + dataCount);
	}

	public String getFileDelimiter() {
		return fileDelimiter;
	}

	public void setFileDelimiter(String fileDelimiter) {
		this.fileDelimiter = fileDelimiter;
	}

	public String getDataSetFileName() {
		return dataSetFileName;
	}

	public void setDataSetFileName(String dataSetFileName) {
		this.dataSetFileName = dataSetFileName;
	}

}

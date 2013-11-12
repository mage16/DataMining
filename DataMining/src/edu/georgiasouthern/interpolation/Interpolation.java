package edu.georgiasouthern.interpolation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.DecimalFormat;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.LocationDataSet;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateYMD;

public class Interpolation
{
	private int numNeighbors;
	private float exponent;
	private DataSet measuredDataSet;
	private LocationDataSet locationDataSet;
	private GisDate.DateDomain dateDomain;
	private String measuredDataFileName;
	private String locationDataFileName;
	private String outputDataFileName;
	private ArrayList<GisData> interpolatedDataSet;
	private ArrayList<String> header;
	private LocationDataSet.FileDelimiter fileDelimiter;
	
	public Interpolation(String measuredDataFileName, 
						 String locationDataFileName, 
						 String outputDataFileName,
						 DataSet.FileDelimiter measuredFileDelimiter,
						 LocationDataSet.FileDelimiter locationFileDelimiter,
						 GisDate.DateDomain domain,
						 int numNeighbors,
						 float exponent)
	{
		interpolatedDataSet = new ArrayList<GisData>();
		header = new ArrayList<String>();
		this.measuredDataFileName = measuredDataFileName;
		this.locationDataFileName = locationDataFileName;
		this.outputDataFileName = outputDataFileName;
		this.numNeighbors = numNeighbors;
		this.exponent = exponent;
		this.dateDomain = domain;
		this.fileDelimiter = locationFileDelimiter;
		this.measuredDataSet = new DataSet(measuredDataFileName, domain, measuredFileDelimiter);
		this.locationDataSet = new LocationDataSet(locationDataFileName, locationFileDelimiter);
		
		this.measuredDataSet.sortData();
	}
	
	public void interpolateUnMeasuredLocations()
	{
		GisData interpolatedData;
		int dataCount = locationDataSet.getDataCount();
		int count = 0;
		for(int i = 0; i < dataCount; i++)
 
		{
			switch(dateDomain)
			{
			case YMD:
				GisDateYMD ymd = (GisDateYMD)measuredDataSet.getData().get(0).getDate();
				int year = ymd.getYear();
				int month = 0;
				int day = 1;
				GregorianCalendar calendar = new GregorianCalendar(year, month, day);
				for(int j = 0; j < 365; j++)
				{
					year = calendar.get(Calendar.YEAR);
					month = calendar.get(Calendar.MONTH) + 1;
					day = calendar.get(Calendar.DAY_OF_MONTH);
					GisDate gisDateYMD = new GisDateYMD(year, month, day,measuredDataSet.getDateFactor());
					locationDataSet.getData().get(i).setDate(gisDateYMD);
					
					InterpolateLocation interpolateLocation = 
								new InterpolateLocation(measuredDataSet,
											locationDataSet.getData().get(i),
											numNeighbors,
											exponent);
					interpolatedData = interpolateLocation.interpolateData2();
					interpolatedDataSet.add(interpolatedData);
					
					calendar.add(Calendar.DATE, 1);
					count++;
				}
				System.out.println("Running..." + count);
				break;
			case YQ:
				break;
			case YM:
				break; 
			case Y:
				break;
			}
		}
		System.out.println("interpolate Data Count: " + interpolatedDataSet.size());
	}
	
	public void writeInterpolationsToFile()
	{
		BufferedWriter writer = null;
		String delimiter = "\t";
		switch(fileDelimiter)
		{
		case tab:
			delimiter = "\t";
			break;
		case comma:
			delimiter = ",";
			break;
		}
		
		try
		{
			writer = new BufferedWriter(new FileWriter(outputDataFileName));
			switch(dateDomain)
			{
			case YMD:
				writer.write("id" + delimiter + "year" + delimiter + "month" + delimiter + "day" + delimiter + "pm25");
				writer.newLine();
				for(int i = 0; i < interpolatedDataSet.size(); i++)
				{
					GisData data = interpolatedDataSet.get(i);
					long id = data.getId();
					GisDateYMD dateYMD = (GisDateYMD)data.getDate();
					int year = dateYMD.getYear();
					int month = dateYMD.getMonth();
					int day = dateYMD.getDay();
					double value = data.getInterpolationValue();
					DecimalFormat df = new DecimalFormat("#.0");
					writer.write(id + delimiter + year + delimiter + month + delimiter + day + delimiter + df.format(value));
					writer.newLine();
				}
			break;
			case YQ:
				break;
			case YM:
				break;
			case Y:
				break;
			}
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getMeasuredDataFileName()
	{
		return measuredDataFileName;
	}
	
	public String getLocationDataFileName()
	{
		return locationDataFileName;
	}
	
	public int getNumNeighbors()
	{
		return numNeighbors;
	}
	
	public float getExponent()
	{
		return exponent;
	}
	
	public DataSet getMeasuredDataSet()
	{
		return measuredDataSet;
	}
	
	public LocationDataSet getLocationDataSet()
	{
		return locationDataSet;
	}
	
	public ArrayList<GisData> getInterpolatedDataSet()
	{
		return interpolatedDataSet;
	}
	
	public ArrayList<String> getInterpolatedDataSetHeader()
	{
		return header;
	} 
	
	public GisDate.DateDomain getDateDomain()
	{
		return dateDomain;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Interpolation interpolation = new Interpolation("src//pm25_2009_measured.txt", 
				 "src//county_xy.txt", 
				 "src//county_id_t_w.txt",
				 DataSet.FileDelimiter.tab,
				 LocationDataSet.FileDelimiter.tab,
				 GisDate.DateDomain.YMD,
				 3,
				 1.0f);
		interpolation.interpolateUnMeasuredLocations();
		interpolation.writeInterpolationsToFile();
	}

}

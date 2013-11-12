package edu.georgiasouthern.interpolation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateYMD;
import edu.georgiasouthern.common.GisDateYQ;
import edu.georgiasouthern.common.GisDateYM;
import edu.georgiasouthern.common.GisDateY;
import edu.georgiasouthern.common.LocationDataSet;

public class ConcurrentInterpolationSec implements Runnable{


	private int numNeighbors;
	private float exponent;
	DataSet measuredDataSet;
	LocationDataSet locationDataSet;
	GisDate.DateDomain dateDomain;
	private String measuredDataFileName;
	private String locationDataFileName;
	private String outputDataFileName;
	ArrayList<GisData> interpolatedDataSet;
	double[][] interpolatedResults;
	private ArrayList<String> header;
	private LocationDataSet.FileDelimiter fileDelimiter;
	private int numberOfInterpolationsPerformed;

	int measuredDataSetCount;
	
	public ConcurrentInterpolationSec(String measuredDataFileName, 
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
		numberOfInterpolationsPerformed = 0;
		measuredDataSetCount = this.measuredDataSet.getDataCount();
		this.measuredDataSet.sortData();
		this.setupInterpolationResults();
	}
	
	public ConcurrentInterpolationSec(DataSet measuredDataSet, LocationDataSet locDataSet, int numNeighbors,
			 float exponent, String outputFilename )
{
interpolatedDataSet = new ArrayList<GisData>();
header = new ArrayList<String>();
this.outputDataFileName = outputFilename;
this.numNeighbors = numNeighbors;
this.exponent = exponent;
this.dateDomain = measuredDataSet.getDateDomain();
this.fileDelimiter = (locDataSet.getFileDelimiter().equals(","))?LocationDataSet.FileDelimiter.comma:LocationDataSet.FileDelimiter.tab;
this.measuredDataSet = measuredDataSet;
this.locationDataSet = locDataSet;
numberOfInterpolationsPerformed = 0;
measuredDataSetCount = this.measuredDataSet.getDataCount();
this.measuredDataSet.sortData();
this.setupInterpolationResults();
}

	private void setupInterpolationResults()
	{
		int dataCount = locationDataSet.getDataCount();
		switch(dateDomain)
		{
		case YMD:
			interpolatedResults = new double[dataCount][365];
			break;
		case YM:
			interpolatedResults = new double[dataCount][12];
			break;
		case YQ:
			interpolatedResults = new double[dataCount][4];
			break;
		case Y:
			interpolatedResults = new double[dataCount][1];
			break;
		}
	}
	
	
	public synchronized void increment()
	{
		numberOfInterpolationsPerformed++;
		if(numberOfInterpolationsPerformed%100==0||numberOfInterpolationsPerformed==0 )
		System.out.println("Performed interpolation: " + numberOfInterpolationsPerformed);
	}
	
	public synchronized int getNumberOfInterpolationsPerformed()
	{
		return numberOfInterpolationsPerformed;
	}
	
	public void run()
	{
		ExecutorService exec = Executors.newCachedThreadPool();
		try
		{
			InterpolatorQueue interpolatorQueue = new InterpolatorQueue();
			for(int i = 0; i < locationDataSet.getDataCount(); i++)
			{
				interpolatorQueue.put(i);
			}
			int availableProcessors = Runtime.getRuntime().availableProcessors() - 2;
			if(availableProcessors < 1)
			{
				availableProcessors = 1;
			}
			for(int i = 0; i < availableProcessors; i++)
			{
				exec.execute(new InterpolateLocationSec(this, interpolatorQueue));
			}
			int totalInterpolations = locationDataSet.getDataCount() * interpolatedResults[0].length;
			while(numberOfInterpolationsPerformed < totalInterpolations)
				Thread.yield();
			exec.shutdownNow();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeInterpolationsToFile(int fromMonth, int toMonth)
	
	{
		System.out.println("Writing File: " + this.outputDataFileName);
		BufferedWriter writer = null;
		BufferedWriter queryWriter = null;
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
			queryWriter = new BufferedWriter(new FileWriter(outputDataFileName+"-Queried.txt"));
			switch(dateDomain)
			{
			case YMD:
				writer.write("id" + delimiter + "year" + delimiter + "month" + delimiter + "day" + delimiter + "pm25");
				writer.newLine();
				queryWriter.write("id" + delimiter + "year" + delimiter + "month" + delimiter + "day" + delimiter + "pm25");
				queryWriter.newLine();
				for(int i = 0; i < locationDataSet.getDataCount(); i++)
				{
					GisDateYMD ymd = (GisDateYMD)measuredDataSet.getData().get(0).getDate();
					int year = ymd.getYear();
					int month = 0;
					int day = 1;
					GregorianCalendar calendar = new GregorianCalendar(year, month, day);
					long id = locationDataSet.getData().get(i).getId();
					for(int j = 0; j < 365; j++)
					{
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH) + 1;
						day = calendar.get(Calendar.DAY_OF_MONTH);
						double value = this.interpolatedResults[i][j];
						//System.out.println("Value: " + value);
						DecimalFormat df = new DecimalFormat("#.0");
						writer.write(id + delimiter + year + delimiter + month + delimiter + day + delimiter + df.format(value));
						writer.newLine();
						if(month>=fromMonth && month<=toMonth){
						queryWriter.write(id + delimiter + year + delimiter + month + delimiter + day + delimiter + df.format(value));
						queryWriter.newLine();
						}
						calendar.add(Calendar.DATE, 1);
					}
				}
			break;
			case YQ:
				writer.write("id" + delimiter + "year" + delimiter + "quarter" + delimiter + "pm25");
				writer.newLine();
				for(int i = 0; i < locationDataSet.getDataCount(); i++)
				{
					GisDateYQ yq = (GisDateYQ)measuredDataSet.getData().get(0).getDate();
					int year = yq.getYear();
					int quarter = 0;
					long id = locationDataSet.getData().get(i).getId();
					for(int j = 0; j < 4; j++)
					{
						quarter += 1;
						double value = this.interpolatedResults[i][j];
						//System.out.println("Value: " + value);
						DecimalFormat df = new DecimalFormat("#.0");
						writer.write(id + delimiter + year + delimiter + quarter + delimiter + df.format(value));
						writer.newLine();
					}
				}
				break;
			case YM:
				writer.write("id" + delimiter + "year" + delimiter + "month" + delimiter + "pm25");
				writer.newLine();
				for(int i = 0; i < locationDataSet.getDataCount(); i++)
				{
					GisDateYM ym = (GisDateYM)measuredDataSet.getData().get(0).getDate();
					int year = ym.getYear();
					int month = 0;
					long id = locationDataSet.getData().get(i).getId();
					for(int j = 0; j < 12; j++)
					{
						month += 1;
						double value = this.interpolatedResults[i][j];
						//System.out.println("Value: " + value);
						DecimalFormat df = new DecimalFormat("#.0");
						writer.write(id + delimiter + year + delimiter + month + delimiter + df.format(value));
						writer.newLine();
					}
				}
				break;
			case Y:
				writer.write("id" + delimiter + "year" + delimiter + "pm25");
				writer.newLine();
				for(int i = 0; i < locationDataSet.getDataCount(); i++)
				{
					GisDateY dateY = (GisDateY)measuredDataSet.getData().get(0).getDate();
					int year = dateY.getYear();
					long id = locationDataSet.getData().get(i).getId();
					for(int j = 0; j < 1; j++)
					{
						double value = this.interpolatedResults[i][j];
						//System.out.println("Value: " + value);
						DecimalFormat df = new DecimalFormat("#.0");
						writer.write(id + delimiter + year + delimiter + df.format(value));
						writer.newLine();
					}
				}
				break;
			}
			writer.close();
			queryWriter.close();
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
		double start = System.currentTimeMillis();
		ConcurrentInterpolationSec interpolation = new ConcurrentInterpolationSec("src//pm25_2009_measured.txt", 
				 "src//county_xy.txt", 
				 "src//test1_day.txt",
				 DataSet.FileDelimiter.tab,
				 LocationDataSet.FileDelimiter.tab,
				 GisDate.DateDomain.YMD,
				 5,
				 2.0f);
		
		try
		{
			Thread t = new Thread(interpolation);
			t.start();
			t.join();
			double end = System.currentTimeMillis();
			double duration = (end - start) / 1000;
			System.out.println("Interpolation took " + duration + " seconds.");
			interpolation.writeInterpolationsToFile(7,8);
		}
		catch(InterruptedException e)
		{
		}
	}
}

package edu.georgiasouthern.interpolation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.text.DecimalFormat;

import edu.wlu.cs.levy.CG.KDTree;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.LocationDataSet;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateYMD;

public class ConcurrentInterpolation implements Runnable
{
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
	private double[][] keys;
	KDTree<Integer> kd;
	int measuredDataSetCount;
	
	public ConcurrentInterpolation(String measuredDataFileName, 
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
		keys = new double[measuredDataSetCount][3];
		this.measuredDataSet.sortData();
		//this.setupUnMeasuredLocations();
		this.setupInterpolationResults();
		this.setupKDTree();
	}
	
	private void setupKDTree()
	{
		// make a 3-dimensional KD-tree
		kd = new KDTree<Integer>(3);
		try
		{
			for(int i = 0; i < measuredDataSetCount; i++)
			{
				GisData data = measuredDataSet.getData().get(i);
				keys[i][0] = data.getX();
				keys[i][1] = data.getY();
				keys[i][2] = data.getDate().getDateId();
				kd.insert(keys[i], i);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
			break;
		}
	}
	
	/*
	private void setupUnMeasuredLocations()
	{
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
					GisDate gisDateYMD = new GisDateYMD(year, month, day);
					locationDataSet.getData().get(i).setDate(gisDateYMD);
					GisData data = new GisData(locationDataSet.getData().get(i).getId(),
							 				   locationDataSet.getData().get(i).getX(),
							 				   locationDataSet.getData().get(i).getY(),
							 				   0.0,
							 				   locationDataSet.getData().get(i).getDate());
					interpolatedDataSet.add(data);
					System.out.println("Count = " + count);
					count++;
					calendar.add(Calendar.DATE, 1);
				}
				break;
			case YQ:
				break;
			case YM:
				break;
			case Y:
				break;
			}
		}
	}
	*/
	
	public synchronized void increment()
	{
		numberOfInterpolationsPerformed++;
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
				exec.execute(new InterpolateLocations(this, interpolatorQueue));
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
						calendar.add(Calendar.DATE, 1);
					}
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
		double start = System.currentTimeMillis();
		ConcurrentInterpolation interpolation = new ConcurrentInterpolation("src//pm25_2009_measured.txt", 
				 "src//county_xy.txt", 
				 "src//test3.txt",
				 DataSet.FileDelimiter.tab,
				 LocationDataSet.FileDelimiter.tab,
				 GisDate.DateDomain.YMD,
				 3,
				 1.0f);
		
		try
		{
			Thread t = new Thread(interpolation);
			t.start();
			t.join();
			double end = System.currentTimeMillis();
			double duration = (end - start) / 1000;
			System.out.println("Interpolation took " + duration + " seconds.");
			interpolation.writeInterpolationsToFile();
		}
		catch(InterruptedException e)
		{
		}
	}

}



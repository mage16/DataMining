package edu.georgiasouthern.validation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisDate;

public class LOOCV implements Runnable
{
	private int numNeighbors;
	public static String statsFilename;
	private float exponent;
	DataSet measuredDataSet;
	double[] interpolatedResults;
	private int numberOfInterpolationsPerformed;
	int measuredDataSetCount;
	LOOCVInterpolationQueue interpolationQueue;
	LOOCVSearchNeighborQueue searchNeighborQueue;
	ErrorStatistics errors;
	public LOOCV(DataSet measuredDataSet, int numNeighbors, float exponent)
	{
		this.numNeighbors = numNeighbors;
		this.exponent = exponent;
		this.measuredDataSet = measuredDataSet;
		numberOfInterpolationsPerformed = 0;
		measuredDataSetCount = this.measuredDataSet.getDataCount();
		this.setupInterpolationResults();
	}
	
	private void setupInterpolationResults()
	{
		int dataCount = measuredDataSet.getDataCount();
		interpolatedResults = new double[dataCount];
	}
	
	public synchronized void increment()
	{
		numberOfInterpolationsPerformed++;
		if(numberOfInterpolationsPerformed==0 || (numberOfInterpolationsPerformed%100)==0)
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
			interpolationQueue = new LOOCVInterpolationQueue();
			searchNeighborQueue = new LOOCVSearchNeighborQueue();
			for(int i = 0; i < measuredDataSet.getDataCount(); i++)
			{
				searchNeighborQueue.put(i);
			}
			int availableProcessors = Runtime.getRuntime().availableProcessors() - 4;
			if(availableProcessors < 1)
			{
				availableProcessors = 1;
			}
			for(int i = 0; i < availableProcessors; i++)
			{
				exec.execute(new LOOCVSearchNeighbors(this));
			}
			exec.execute(new LOOCVInterpolation(this));
			exec.execute(new LOOCVInterpolation(this));
			int totalInterpolations = measuredDataSet.getDataCount();
			while(numberOfInterpolationsPerformed < totalInterpolations)
				Thread.yield();
			exec.shutdownNow();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getNumNeighbors()
	{
		return numNeighbors;
	}
	
	public float getExponent()
	{
		return exponent;
	}
	
	public String getHeaderName()
	{
		return "n" + getNumNeighbors() + "e" + getExponent();
	}
	
	public DataSet getMeasuredDataSet()
	{
		return measuredDataSet;
	}
	
	public double[] getInterpolatedResults()
	{
		return interpolatedResults;
	}
	
	public static LOOCV executeLOOCVInterpolation(DataSet measuredDataSet,
												  int numNeighbors,
												  float exponent)
	{
		double start = System.currentTimeMillis();
		LOOCV interpolation = new LOOCV(measuredDataSet, numNeighbors, exponent);
		
		try
		{
			Thread t = new Thread(interpolation);
			t.start();
			t.join();
			double end = System.currentTimeMillis();
			double duration = (end - start) / 1000;
			System.out.println("Interpolation took " + duration + " seconds.");
			double I[] = new double[interpolation.getNumberOfInterpolationsPerformed()];
			double O[] = new double[interpolation.getNumberOfInterpolationsPerformed()];
			for(int i = 0; i < interpolation.getNumberOfInterpolationsPerformed(); i++)
			{
				double original = interpolation.getMeasuredDataSet().getData().get(i).getMeasurement();
				double interpolatedValue = interpolation.getInterpolatedResults()[i];
				I[i]= interpolatedValue;
				O[i]=original;
				if(i==0 || (i%100)==0)
				System.out.println(original + "\t" + interpolatedValue);

			}
			interpolation.setErrors(new ErrorStatistics(I,O));
			System.out.println("Error Statistics \r\n" + interpolation.getErrors());
			if(statsFilename ==null || statsFilename.equals("")) statsFilename="error-statistics.txt";
			interpolation.getErrors().writeErrorStatistics(statsFilename, "\t");
		}
		catch(InterruptedException e)
		{
		}
		
		return interpolation;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSet measuredDataSet = new DataSet("src//pm25_2009_measured.txt",
											GisDate.DateDomain.YMD,
											DataSet.FileDelimiter.tab);
		LOOCV.executeLOOCVInterpolation(measuredDataSet, 7, 5.0f);
	}

	public ErrorStatistics getErrors() {
		return errors;
	}

	public void setErrors(ErrorStatistics errors) {
		this.errors = errors;
	}

	public String getStatsFilename() {
		return statsFilename;
	}

	public void setStatsFilename(String statsFilename) {
		this.statsFilename = statsFilename;
	}
}

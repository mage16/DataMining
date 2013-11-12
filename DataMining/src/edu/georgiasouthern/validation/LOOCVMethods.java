package edu.georgiasouthern.validation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateYMD;

public class LOOCVMethods
{
	static int[] numNeighbors = {3, 4, 5, 6, 7};
	static float[] exponents = {1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f};
	private DataSet measuredDataSet;
	private String measuredDataFileName;
	private GisDate.DateDomain dateDomain;
	private DataSet.FileDelimiter fileDelimiter;
	
	public LOOCVMethods(String measuredDataFileName,
						GisDate.DateDomain domain,
						DataSet.FileDelimiter fileDelimiter)
	{
		this.measuredDataFileName = measuredDataFileName;
		this.dateDomain = domain;
		this.fileDelimiter = fileDelimiter;
		this.measuredDataSet = new DataSet(measuredDataFileName,
										   domain,
										   fileDelimiter);
	}
	
	public String getMeasuredDataFileName()
	{
		return measuredDataFileName;
	}
	
	public GisDate.DateDomain getDateDomain()
	{
		return dateDomain;
	}
	
	public DataSet.FileDelimiter getFileDelimiter()
	{
		return fileDelimiter;
	}
	
	public DataSet getMeasuredDataSet()
	{
		return measuredDataSet;
	}
	
	public static void executeIDWMethods(String measuredDataFileName, 
										 GisDate.DateDomain domain,
										 DataSet.FileDelimiter fileDelimiter,
										 String outputFileName)
	{
		int neighborsListSize = LOOCVMethods.numNeighbors.length;
		int exponentsListSize = LOOCVMethods.exponents.length;
		int columnCount = neighborsListSize * exponentsListSize;
		LOOCV[] validationResults = new LOOCV[columnCount];
		ErrorStatistics[] allErrors = new ErrorStatistics[columnCount];
		LOOCVMethods methods = new LOOCVMethods(measuredDataFileName,
												domain,
												fileDelimiter);
		DataSet dataToValidate = methods.getMeasuredDataSet();
		int count = 0;
		for(int neighborCount : LOOCVMethods.numNeighbors)
		{
			for(float exponent : LOOCVMethods.exponents)
			{
				validationResults[count] = LOOCV.executeLOOCVInterpolation(dataToValidate, neighborCount, exponent);
				allErrors[count] = validationResults[count].getErrors();
				count++;
			}
		}
		ErrorStatistics averageErrors = ErrorStatistics.Average(allErrors);
		System.out.println("Average Errors");
		System.out.println(averageErrors);
	
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
		averageErrors.writeErrorStatistics("error_statistics_idw.txt", delimiter,allErrors);
		try
		{
			writer = new BufferedWriter(new FileWriter(outputFileName));
			writer.write("original" + delimiter);
			for(int i = 0; i < columnCount; i++)
			{
				if(i < columnCount - 1)
					writer.write(validationResults[i].getHeaderName() + delimiter);
				else
					writer.write(validationResults[i].getHeaderName());
			}
			writer.newLine();
			DecimalFormat df = new DecimalFormat("#.0");
			for(int i = 0; i < dataToValidate.getDataCount(); i++)
			{
				writer.write(df.format(dataToValidate.getData().get(i).getMeasurement()) + delimiter);
				for(int j = 0; j < columnCount; j++)
				{
					writer.write(df.format(validationResults[j].interpolatedResults[i]) + delimiter);
				}
				writer.newLine();
			}
			
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LOOCVMethods.executeIDWMethods("src//pm25_2009_measured.txt",
				GisDate.DateDomain.YMD,
				DataSet.FileDelimiter.tab,
				"src//loocv_idw.txt");
	}
}

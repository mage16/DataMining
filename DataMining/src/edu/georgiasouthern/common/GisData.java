package edu.georgiasouthern.common;

import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;

public class GisData implements Comparable<GisData>
{
	private long id;
	private double x;
	private double y;
	private double measurement;
	private double interpolationValue;
	private GisDate date;
	private int gisDataCount;

	public GisData(long id, double x, double y, double measurement, GisDate date)
	{
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setMeasurement(measurement);
		this.setDate(date);
		GisDate.DateDomain domain = date.getDateDomain();
		this.gisDataCount = 4;
		switch(domain)
		{
		case Y:
			this.gisDataCount += 1;
			break;
		case YM:
			this.gisDataCount += 2;
			break;
		case YMD:
			this.gisDataCount += 3;
			break;
		case YQ:
			this.gisDataCount += 2;
			break;
		}
	}
	
	public GisData(long id, double x, double y, double measurement)
	{
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setMeasurement(measurement);
		this.gisDataCount = 4;
	}
	
	public int compareTo(GisData d) {
		Double y1 = new Double(this.getY());
		Double y2 = new Double(d.getY());
		Double x1 = new Double(this.getX());
		Double x2 = new Double(d.getX());
		
		int compareY = y1.compareTo(y2);
		int compareX = x1.compareTo(x2);
		if(compareX == 0)
		{
			return compareY;
		}
		else
		{
			return compareX;
		}
	}


	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}

	public double getX()
	{
		return x;
	}
	public void setX(double x)
	{
		this.x = x;
	}
	
	public double getY()
	{
		return y;
	}
	public void setY(double y)
	{
		this.y = y;
	}
	
	public GisDate getDate()
	{
		return date;
	}
	public void setDate(GisDate date)
	{
		this.date = date;
		GisDate.DateDomain domain = date.getDateDomain();
		this.gisDataCount = 4;
		switch(domain)
		{
		case Y:
			this.gisDataCount += 1;
			break;
		case YM:
			this.gisDataCount += 2;
			break;
		case YMD:
			this.gisDataCount += 3;
			break;
		case YQ:
			this.gisDataCount += 2;
			break;
		}
	}
	
	public double getDateId()
	{
		return date.getDateId();
	}
	public void setDateId(double dateId)
	{
		date.setDateId(dateId);
	}
	
	public double getMeasurement()
	{
		return measurement;
	}
	public void setMeasurement(double measurement)
	{
		this.measurement = measurement;
	}
	
	public int getGisDataCount()
	{
		return gisDataCount;
	}
	
	public double getDistance(GisData data1)
	{
		double distance;
		double data1X = data1.getX();
		double data1Y = data1.getY();
		double data1Date = data1.getDate().getDateId();
		
		double xDiff = data1X - getX();
		double yDiff = data1Y - getY();
		double dateDiff = data1Date - getDateId();
		distance = Math.sqrt((xDiff * xDiff) + (yDiff * yDiff) + (dateDiff * dateDiff));
		
		return distance;
	}
	
	public void setInterpolationValue(double value)
	{
		this.interpolationValue = value;
	}
	
	public double getInterpolationValue()
	{
		return interpolationValue;
	}
}

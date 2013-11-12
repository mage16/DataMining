package edu.georgiasouthern.common;

import edu.georgiasouthern.common.GisDate.DateDomain;

public class GisDateYQ extends GisDate
{
	private int year;
	private int quarter;
	
	public GisDateYQ(int year, int quarter, double dateFactor)
	{
		setYear(year);
		setQuarter(quarter);
		setDateDomain(DateDomain.YQ);
		this.dateFactor = dateFactor;
		calcDateId();
	}
	
	public int compareTo(GisDateYQ date)
	{
		if(this.getYear() != date.getYear())
		{
			return this.getYear() - date.getYear();
		}
		else if(this.getQuarter() != date.getQuarter())
		{
			return this.getQuarter() - date.getQuarter();
		}
		
		return 0;
	}
	
	public static int compare(GisDateYQ d1, GisDateYQ d2)
	{
		if(d1.getYear() != d2.getYear())
		{
			return d1.getYear() - d2.getYear();
		}
		else if(d1.getQuarter() != d2.getQuarter())
		{
			return d1.getQuarter() - d2.getQuarter();
		}
		
		return 0;
	}
	
	@Override
	public void calcDateId()
	{
		this.dateId = (this.year * 4 + this.quarter)* dateFactor;
		dateCalculated= true;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public void setYear(int year)
	{
		this.year = year;
	}
	
	public int getQuarter()
	{
		return quarter;
	}
	
	public void setQuarter(int quarter)
	{
		this.quarter = quarter;
	}
	public String toString(){
		return Integer.toString(year) +"-Q"+ Integer.toString(quarter);
	}
}


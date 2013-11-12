package edu.georgiasouthern.common;

import edu.georgiasouthern.common.GisDate.DateDomain;

public class GisDateYM extends GisDate
{
	private int year;
	private int month;
	
	public GisDateYM(int year, int month, double dateFactor)
	{
		setYear(year);
		setMonth(month);
		setDateDomain(DateDomain.YM);
		this.dateFactor = dateFactor;
		calcDateId();
	}
	public GisDateYM(){
		dateFactor = 1;
	}
	public int compareTo(GisDateYM date)
	{
		if(this.getYear() != date.getYear())
		{
			return this.getYear() - date.getYear();
		}
		else if(this.getMonth() != date.getMonth())
		{
			return this.getMonth() - date.getMonth();
		}
		
		return 0;
	}
	
	public static int compare(GisDateYM d1, GisDateYM d2)
	{
		if(d1.getYear() != d2.getYear())
		{
			return d1.getYear() - d2.getYear();
		}
		else if(d1.getMonth() != d2.getMonth())
		{
			return d1.getMonth() - d2.getMonth();
		}
		
		return 0;
	}
	
	@Override
	public void calcDateId()
	{
		this.dateId = ((this.year * 12) + this.month)* dateFactor;
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
	
	public int getMonth()
	{
		return month;
	}
	
	public void setMonth(int month)
	{
		this.month = month;
	}
	public String toString(){
		return Integer.toString(year) +"-"+ Integer.toString(month);
	}
}


package edu.georgiasouthern.common;

import java.util.GregorianCalendar;
import java.util.Calendar;

import edu.georgiasouthern.common.GisDate.DateDomain;

public class GisDateYMD extends GisDate
{
	private int year;
	private int month;
	private int day;
	private GregorianCalendar calendar = new GregorianCalendar();
	
	public GisDateYMD(int year, int month, int day, double dateFactor)
	{
		setYear(year);
		setMonth(month);
		setDay(day);
		setDateDomain(DateDomain.YMD);
		this.dateFactor = dateFactor;
		calcDateId();
		
	}
	
	public int compareTo(GisDateYMD date)
	{
		if(this.getYear() != date.getYear())
		{
			return this.getYear() - date.getYear();
		}
		else if(this.getMonth() != date.getMonth())
		{
			return this.getMonth() - date.getMonth();
		}
		else if(this.getDay() != date.getDay())
		{
			return this.getDay() - date.getDay();
		}
		
		return 0;
	}
	
	public static int compare(GisDateYMD d1, GisDateYMD d2)
	{
		if(d1.getYear() != d2.getYear())
		{
			return d1.getYear() - d2.getYear();
		}
		else if(d1.getMonth() != d2.getMonth())
		{
			return d1.getMonth() - d2.getMonth();
		}
		else if(d1.getDay() != d2.getDay())
		{
			return d1.getDay() - d2.getDay();
		}
		
		return 0;
	}
	
	@Override
	public void calcDateId()
	{
		calendar.set(Calendar.YEAR,  getYear());
		calendar.set(Calendar.MONTH,  getMonth() - 1);
		calendar.set(Calendar.DAY_OF_MONTH, getDay());
		dateId= (calendar.get(Calendar.DAY_OF_YEAR) * year) * dateFactor;
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
	
	public int getDay()
	{
		return day;
	}
	
	public void setDay(int day)
	{
		this.day = day;
	}
	public String toString(){
		return Integer.toString(year) +"-"+ Integer.toString(month) +"-"+ Integer.toString(day);
	}
}


package edu.georgiasouthern.common;

public class GisDateY extends GisDate
{
	private int year;
	public GisDateY(){
		dateFactor = 1;
	}
	public GisDateY(int year, double dateFactor)
	{
		setYear(year);
		setDateDomain(DateDomain.Y);
		this.dateFactor =dateFactor;
		calcDateId();
	}
	
	public int compareTo(GisDateY date)
	{
		if(this.getYear() != date.getYear())
		{
			return this.getYear() - date.getYear();
		}
		
		return 0;
	}
	
	public static int compare(GisDateY d1, GisDateY d2)
	{
		if(d1.getYear() != d2.getYear())
		{
			return d1.getYear() - d2.getYear();
		}
		
		return 0;
	}

	@Override
	public void calcDateId()
	{
		dateCalculated = true;
		this.dateId=((double)year)* dateFactor;
		dateCalculated= true;
	}
	
	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
		calcDateId();
	}
	public String toString(){
		return Integer.toString(year);
	}
}






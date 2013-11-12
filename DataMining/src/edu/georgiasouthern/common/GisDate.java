package edu.georgiasouthern.common;

public abstract class GisDate
{
	public enum DateDomain
	{
		Y, YM, YQ, YMD, OTHER
	}
	protected double dateFactor;
	protected boolean dateCalculated;
	protected double dateId;
	protected DateDomain dateDomain;
	
	public int compareTo(GisDate date)
	{
		return 0;
	}
	
	public static int compare(GisDate d1, GisDate d2)
	{
		return 0;
	}
	
	public void calcDateId()
	{
		dateId = 0.0;
	}
	
	public double getDateId()
	{
		if (!dateCalculated)calcDateId();
		return dateId;
	}
	
	public void setDateId(double dateId)
	{
		this.dateId = dateId;
	}
	
	public DateDomain getDateDomain()
	{
		return dateDomain;
	}
	
	public void setDateDomain(DateDomain dateDomain)
	{
		this.dateDomain = dateDomain;  
	}

	public double getDateFactor() {
		return dateFactor;
	}

	public void setDateFactor(double dateFactor) {
		this.dateCalculated=false;
		this.dateFactor = dateFactor;
	}

}


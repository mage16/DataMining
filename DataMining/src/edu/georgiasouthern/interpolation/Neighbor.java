package edu.georgiasouthern.interpolation;

import edu.georgiasouthern.common.*;

public class Neighbor implements Comparable<Neighbor>
{
	private double distanceFromLocation;
	private GisData data;
	
	public Neighbor(GisData data, double distance)
	{
		this.setData(data);
		this.setDistanceFromLocation(distance);
	}
	
	public int compareTo(Neighbor d){
        Double _t = new Double(this.getDistanceFromLocation());        
        Double _d = new Double(d.getDistanceFromLocation());
        return _t.compareTo(_d);
    }

	
	public double getDistanceFromLocation()
	{
		return this.distanceFromLocation;
	}
	
	public void setDistanceFromLocation(double distance)
	{
		this.distanceFromLocation = distance;
	}
	
	public GisData getData()
	{
		return this.data;
	}
	
	public void setData(GisData data)
	{
		this.data = data;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

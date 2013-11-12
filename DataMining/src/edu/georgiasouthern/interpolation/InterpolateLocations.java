package edu.georgiasouthern.interpolation;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateYMD;

public class InterpolateLocations implements Runnable
{
	private InterpolatorQueue interpolatorQueue;
	private NearestNeighborsList neighborList;
	private ConcurrentInterpolation interpolator;
	private List<Integer> neighbors;
	
	public InterpolateLocations(ConcurrentInterpolation interpolator,
						  		InterpolatorQueue iq)
	{
		this.interpolatorQueue = iq;
		this.interpolator = interpolator;
	}
	
	public void run()
	{
		int index;
		try
		{
			while(!Thread.interrupted() && interpolatorQueue.size() > 0)
			{
				double[] target = new double[3];
				index = interpolatorQueue.take();
				
				switch(interpolator.dateDomain)
				{
				case YMD:
					GisDateYMD ymd = (GisDateYMD)interpolator.measuredDataSet.getData().get(0).getDate();
					int year = ymd.getYear();
					int month = 0;
					int day = 1;
					GregorianCalendar calendar = new GregorianCalendar(year, month, day);
					double x = interpolator.locationDataSet.getData().get(index).getX();
					double y = interpolator.locationDataSet.getData().get(index).getY();
					long id = interpolator.locationDataSet.getData().get(index).getId();
					for(int j = 0; j < 365; j++)
					{
						neighborList = new NearestNeighborsList(interpolator.getNumNeighbors());
						neighborList.setIndex(index);
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH) + 1;
						day = calendar.get(Calendar.DAY_OF_MONTH);
						GisDate gisDateYMD = new GisDateYMD(year, month, day, interpolator.measuredDataSet.getDateFactor());
						GisData dataToInterpolate = new GisData(id, x, y, 0.0, gisDateYMD);
						target[0] = dataToInterpolate.getX();
						target[1] = dataToInterpolate.getY();
						target[2] = dataToInterpolate.getDate().getDateId();
						neighbors = interpolator.kd.nearest(target, interpolator.getNumNeighbors());
						for(int k : neighbors)
						{
							GisData measuredData = interpolator.measuredDataSet.getData().get(k);
							double distance = dataToInterpolate.getDistance(measuredData);
							Neighbor neighbor = new Neighbor(measuredData, distance);
							neighborList.addLeastNeighbor(neighbor);
							//System.out.println(neighbor.getData().getId() + "\t" + neighbor.getData().getX() + "\t" + neighbor.getData().getY() + "\t" + neighbor.getData().getDate().getDateId());
							//System.out.println(dataToInterpolate.getId() + "\t" + dataToInterpolate.getX() + "\t" + dataToInterpolate.getY() + "\t" + dataToInterpolate.getDate().getDateId());
						}
				
						double measurement = 0.0;
						for(int k = 0; k < interpolator.getNumNeighbors(); k++)
						{
							Neighbor neighbor = neighborList.get(k);
							measurement += neighbor.getData().getMeasurement()
									* weight(neighbor, interpolator.getExponent());
						}
						interpolator.interpolatedResults[index][j] = measurement;
						interpolator.increment();
						calendar.add(Calendar.DATE, 1);
					}
					break;
				case YM:
					break;
				case YQ:
					break;
				case Y:
					break;
				}
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private double weight(Neighbor neighbor, float exponent)
	{
		double w = weightNumerator(neighbor, exponent) / weightDenominator(exponent);
		return w;
	}
	
	private double weightNumerator(Neighbor neighbor, float exponent)
	{
		double numerator = Math.pow((1 / neighbor.getDistanceFromLocation()), exponent);
		return numerator;
	}
	
	private double weightDenominator(float exponent)
	{
		double denominator = 0.0;
		for(int i = 0; i < interpolator.getNumNeighbors(); i++)
		{
			denominator += Math.pow(1 / neighborList.get(i).getDistanceFromLocation(), exponent);
		}
		return denominator;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



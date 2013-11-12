package edu.georgiasouthern.interpolation;

import java.util.Queue;

import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.DataSet;

public class InterpolateLocation
{
	private DataSet measuredDataSet;
	private GisData dataToInterpolate;
	int numNeighbors;
	float exponent;
	private NearestNeighborsList neighbors;
	
	public InterpolateLocation(DataSet measuredDataSet,
							   GisData dataToInterpolate,
							   int numNeighbors,
							   float exponent)
	{
		this.numNeighbors = numNeighbors;
		this.exponent = exponent;
		this.measuredDataSet = measuredDataSet;
		this.dataToInterpolate = new GisData(dataToInterpolate.getId(),
											 dataToInterpolate.getX(),
											 dataToInterpolate.getY(),
											 0.0,
											 dataToInterpolate.getDate());
		neighbors = new NearestNeighborsList(this.numNeighbors);
	}
	public GisData interpolateData2(){
		int numMeasurements = measuredDataSet.getDataCount();
	 	neighbors.clear();
		for(int j = 0; j < numMeasurements; j++)
		{
			GisData measuredData = measuredDataSet.getData().get(j);
			double distance = dataToInterpolate.getDistance(measuredData);
			Neighbor neighbor = new Neighbor(measuredData, distance);
			neighbors.addLeastNeighbor(neighbor);
			}
			 
		double measurement = 0.0;
		for(int j = 0; j < numNeighbors; j++)
		{
			Neighbor neighbor = neighbors.get(j);
			measurement += neighbor.getData().getMeasurement()
							* weight(neighbor, exponent);
		}
		dataToInterpolate.setInterpolationValue(measurement);
		//System.out.println("Interpolated measurement: " + measurement);
		//System.out.println("Neighbor measurement: " + neighbors.get(0).getData().getMeasurement());
		
		return dataToInterpolate;

		
	}
	public GisData interpolateData()
	{
		double distanceThreshold = 3.0;
		
		int total = 0;
		while(neighbors.getNeighborList().size() < numNeighbors)
		{
			if(total > 0)
			{
				neighbors.clear();
				distanceThreshold += 1.0;
			}
			findNeighbors(distanceThreshold);
			total++;
		}
		neighbors.sortNeighbors();

		//System.out.println("Total Neighbors: " + neighbors.getNeighborList().size());
		double measurement = 0.0;
		for(int j = 0; j < numNeighbors; j++)
		{
			Neighbor neighbor = neighbors.get(j);
			measurement += neighbor.getData().getMeasurement()
							* weight(neighbor, exponent);
		}
		dataToInterpolate.setInterpolationValue(measurement);
		//System.out.println("Interpolated measurement: " + measurement);
		//System.out.println("Neighbor measurement: " + neighbors.get(0).getData().getMeasurement());
		
		return dataToInterpolate;
	}
	
	private void findNeighbors(double distanceThreshold)
	{
		int numMeasurements = measuredDataSet.getDataCount();
		for(int j = 0; j < numMeasurements; j++)
		{
			GisData measuredData = measuredDataSet.getData().get(j);
			double distance = dataToInterpolate.getDistance(measuredData);
			if(distance <= distanceThreshold)
			{
				Neighbor neighbor = new Neighbor(measuredData, distance);
				neighbors.addNeighbor(neighbor);
			}
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
		for(int i = 0; i < numNeighbors; i++)
		{
			denominator += Math.pow(1 / neighbors.get(i).getDistanceFromLocation(), exponent);
		}
		return denominator;
	}
}

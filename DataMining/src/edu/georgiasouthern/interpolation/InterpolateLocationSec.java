package edu.georgiasouthern.interpolation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateYMD;
import edu.georgiasouthern.common.GisDateYM;
import edu.georgiasouthern.common.GisDateYQ;
import edu.georgiasouthern.common.GisDateY;

public class InterpolateLocationSec implements Runnable{

	private InterpolatorQueue interpolatorQueue;
	private NearestNeighborsList neighborList;
	private ConcurrentInterpolationSec interpolator;
	
	public InterpolateLocationSec(ConcurrentInterpolationSec interpolator,
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
				int year;
				int month;
				int day;
				int quarter;
				double x;
				double y;
				long id;
				GregorianCalendar calendar;
				
				switch(interpolator.dateDomain)
				{
				case YMD:
					GisDateYMD ymd = (GisDateYMD)interpolator.measuredDataSet.getData().get(0).getDate();
					year = ymd.getYear();
					month = 0;
					day = 1;
					calendar = new GregorianCalendar(year, month, day);
					x = interpolator.locationDataSet.getData().get(index).getX();
					y = interpolator.locationDataSet.getData().get(index).getY();
					id = interpolator.locationDataSet.getData().get(index).getId();
					for(int j = 0; j < 365; j++)
					{
						neighborList = new NearestNeighborsList(interpolator.getNumNeighbors());
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH) + 1;
						day = calendar.get(Calendar.DAY_OF_MONTH);
						GisDate gisDateYMD = new GisDateYMD(year, month, day,interpolator.measuredDataSet.getDateFactor());
						GisData dataToInterpolate = new GisData(id, x, y, 0.0, gisDateYMD);
						Sector s = interpolator.measuredDataSet.getSectorTree().findSector(x, y,gisDateYMD.getDateId());
						if(s==null){
							System.out.println(s);
						}
		if(s!=null){
						neighborList.addListGisData( s.data,dataToInterpolate);
		}
						List<Sector> closerSectors = null;
						
						
						if (neighborList==null || neighborList.size()<1||neighborList.size()<interpolator.getNumNeighbors()){
							if(closerSectors == null) closerSectors= new ArrayList<Sector>();
							
							while(neighborList.size()<interpolator.getNumNeighbors()){
								if(s.parent!=null)s=s.parent;else break;
								neighborList.addListGisData(s.getAllChildren(new ArrayList<GisData>()), dataToInterpolate);
							}
						}else{
							closerSectors = s.findCloserSector(neighborList.get(neighborList.size()-1).getDistanceFromLocation(), x, y, gisDateYMD.getDateId());
							if(closerSectors !=null){
							for (Sector cs:closerSectors){
								if(cs.data!=null){
								neighborList.addListGisData(cs.data, dataToInterpolate);
								}
							}
							}
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
					GisDateYM ym = (GisDateYM)interpolator.measuredDataSet.getData().get(0).getDate();
					year = ym.getYear();
					month = 0;
					x = interpolator.locationDataSet.getData().get(index).getX();
					y = interpolator.locationDataSet.getData().get(index).getY();
					id = interpolator.locationDataSet.getData().get(index).getId();
					for(int j = 0; j < 12; j++)
					{
						neighborList = new NearestNeighborsList(interpolator.getNumNeighbors());
						month += 1;
						GisDate gisDateYM = new GisDateYM(year, month, interpolator.measuredDataSet.getDateFactor());
						GisData dataToInterpolate = new GisData(id, x, y, 0.0, gisDateYM);
						Sector s = interpolator.measuredDataSet.getSectorTree().findSector(x, y,gisDateYM.getDateId());
						if(s==null){
							System.out.println(s);
						}
		if(s!=null){
						neighborList.addListGisData( s.data,dataToInterpolate);
		}
						List<Sector> closerSectors = null;
						
						
						if (neighborList==null || neighborList.size()<1||neighborList.size()<interpolator.getNumNeighbors()){
							if(closerSectors == null) closerSectors= new ArrayList<Sector>();
							
							while(neighborList.size()<interpolator.getNumNeighbors()){
								if(s.parent!=null)s=s.parent;else break;
								neighborList.addListGisData(s.getAllChildren(new ArrayList<GisData>()), dataToInterpolate);
							}
						}else{
							closerSectors = s.findCloserSector(neighborList.get(neighborList.size()-1).getDistanceFromLocation(), x, y, gisDateYM.getDateId());
							if(closerSectors !=null){
							for (Sector cs:closerSectors){
								if(cs.data!=null){
								neighborList.addListGisData(cs.data, dataToInterpolate);
								}
							}
							}
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
					}
					break;
				case YQ:
					GisDateYQ yq = (GisDateYQ)interpolator.measuredDataSet.getData().get(0).getDate();
					year = yq.getYear();
					quarter = 0;
					x = interpolator.locationDataSet.getData().get(index).getX();
					y = interpolator.locationDataSet.getData().get(index).getY();
					id = interpolator.locationDataSet.getData().get(index).getId();
					for(int j = 0; j < 4; j++)
					{
						neighborList = new NearestNeighborsList(interpolator.getNumNeighbors());
						quarter += 1;
						GisDate gisDateYQ = new GisDateYQ(year, quarter, interpolator.measuredDataSet.getDateFactor());
						GisData dataToInterpolate = new GisData(id, x, y, 0.0, gisDateYQ);
						Sector s = interpolator.measuredDataSet.getSectorTree().findSector(x, y,gisDateYQ.getDateId());
						if(s==null){
							System.out.println(s);
						}
		if(s!=null){
						neighborList.addListGisData( s.data,dataToInterpolate);
		}
						List<Sector> closerSectors = null;
						
						
						if (neighborList==null || neighborList.size()<1||neighborList.size()<interpolator.getNumNeighbors()){
							if(closerSectors == null) closerSectors= new ArrayList<Sector>();
							
							while(neighborList.size()<interpolator.getNumNeighbors()){
								if(s.parent!=null)s=s.parent;else break;
								neighborList.addListGisData(s.getAllChildren(new ArrayList<GisData>()), dataToInterpolate);
							}
						}else{
							closerSectors = s.findCloserSector(neighborList.get(neighborList.size()-1).getDistanceFromLocation(), x, y, gisDateYQ.getDateId());
							if(closerSectors !=null){
							for (Sector cs:closerSectors){
								if(cs.data!=null){
								neighborList.addListGisData(cs.data, dataToInterpolate);
								}
							}
							}
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
					}
					break;
				case Y:
					GisDateY dateY = (GisDateY)interpolator.measuredDataSet.getData().get(0).getDate();
					year = dateY.getYear();
					x = interpolator.locationDataSet.getData().get(index).getX();
					y = interpolator.locationDataSet.getData().get(index).getY();
					id = interpolator.locationDataSet.getData().get(index).getId();
					for(int j = 0; j < 1; j++)
					{
						neighborList = new NearestNeighborsList(interpolator.getNumNeighbors());
						GisDate gisDateY = new GisDateY(year, interpolator.measuredDataSet.getDateFactor());
						GisData dataToInterpolate = new GisData(id, x, y, 0.0, gisDateY);
						Sector s = interpolator.measuredDataSet.getSectorTree().findSector(x, y,gisDateY.getDateId());
						if(s==null){
							System.out.println(s);
						}
		if(s!=null){
						neighborList.addListGisData( s.data,dataToInterpolate);
		}
						List<Sector> closerSectors = null;
						
						
						if (neighborList==null || neighborList.size()<1||neighborList.size()<interpolator.getNumNeighbors()){
							if(closerSectors == null) closerSectors= new ArrayList<Sector>();
							
							while(neighborList.size()<interpolator.getNumNeighbors()){
								if(s.parent!=null)s=s.parent;else break;
								neighborList.addListGisData(s.getAllChildren(new ArrayList<GisData>()), dataToInterpolate);
							}
						}else{
							closerSectors = s.findCloserSector(neighborList.get(neighborList.size()-1).getDistanceFromLocation(), x, y, gisDateY.getDateId());
							if(closerSectors !=null){
							for (Sector cs:closerSectors){
								if(cs.data!=null){
								neighborList.addListGisData(cs.data, dataToInterpolate);
								}
							}
							}
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
					}
					break;
				}
			}
		}
		catch(InterruptedException e)
		{
			//e.printStackTrace();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
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

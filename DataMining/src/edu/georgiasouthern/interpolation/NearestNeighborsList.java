package edu.georgiasouthern.interpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.interpolation.Neighbor;

public class NearestNeighborsList
{
	private Neighbor maxNeighbor;
	private int index;
	private ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
	private int numNeighbors;
	
	public NearestNeighborsList(int numNeighbors)
	{
		this.numNeighbors = numNeighbors;
	}
	public void addLeastNeighbor(Neighbor n){
		boolean added = false;
		if (neighbors.size()<1) {
			neighbors.add(n);
			
		}else{
		for (int i=0; i<neighbors.size(); i++){
			Neighbor nb = neighbors.get(i);
			if (n.getDistanceFromLocation()<nb.getDistanceFromLocation()){
				neighbors.add(i,n);
				added=true;
				break;
			}
		}
		if(neighbors.size() < numNeighbors && !added) neighbors.add(n);
		while (neighbors.size()> numNeighbors)neighbors.remove(neighbors.size()-1);
		}
	}
	public void addListGisData(List<GisData> l, GisData dataToInterpolate){
		if (l!=null){
		for (GisData d: l){
			addLeastNeighbor(new Neighbor(d,dataToInterpolate.getDistance(d)));
			
		}
		}
	}
	public void addListGisDataLOOCV(List<GisData> l, GisData dataToInterpolate)
	{
		if (l != null)
		{
			for (GisData d: l)
			{
				double distance = dataToInterpolate.getDistance(d);
				if(distance <= 0.01)
					continue;
				else
					addLeastNeighbor(new Neighbor(d, distance));
			}
		}
	}
	public void addNeighbor(Neighbor n)
	{
		neighbors.add(n);
	}
	
	public void sortNeighbors()
	{
		Collections.sort(neighbors);
		maxNeighbor = neighbors.get(neighbors.size() - 1);
	}
	
	public ArrayList<Neighbor> getNeighborList()
	{
		return neighbors;
	}
	
	public void clear()
	{
		neighbors.clear();
	}
	
	public Neighbor get(int index)
	{
		return neighbors.get(index);
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public boolean isFull()
	{
		if(neighbors.size() == numNeighbors)
			return true;
		return false;
	}
	
	public double maxDistance()
	{
		return maxNeighbor.getDistanceFromLocation();
	}
	
	public void replaceMax(Neighbor n)
	{
		neighbors.add(n);
		neighbors.remove(maxNeighbor);
		Collections.sort(neighbors);
		maxNeighbor = neighbors.get(neighbors.size() - 1);
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	public int size()
	{
		return neighbors.size();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package edu.georgiasouthern.validation;

import edu.georgiasouthern.interpolation.NearestNeighborsList;
import edu.georgiasouthern.interpolation.Neighbor;
import edu.georgiasouthern.interpolation.Sector;
import edu.georgiasouthern.common.GisData;

import java.util.List;
import java.util.ArrayList;

public class LOOCVSearchNeighbors implements Runnable {

	/**
	 * 
	 */
	private LOOCV leaveOneOutCV;

	public LOOCVSearchNeighbors(LOOCV leaveOneOutCV) {
		this.leaveOneOutCV = leaveOneOutCV;
	}

	public void run() {
		int index;
		try {
			while (!Thread.interrupted()) {
				index = leaveOneOutCV.searchNeighborQueue.take();
				//System.out.println("Finding Neighbors for index: " + index);
				NearestNeighborsList neighbors = new NearestNeighborsList(leaveOneOutCV.getNumNeighbors());
				neighbors.setIndex(index);
				GisData dataToInterpolate = leaveOneOutCV.measuredDataSet.getData().get(index);
				double x = dataToInterpolate.getX();
				double y = dataToInterpolate.getY();
				double date = dataToInterpolate.getDate().getDateId();
				Sector s = leaveOneOutCV.measuredDataSet.getSectorTree().findSector(x, y, date);
				if(s!=null)
				{
					neighbors.addListGisDataLOOCV( s.data,dataToInterpolate);
				}
				List<Sector> closerSectors = null;

				if (neighbors==null || neighbors.size()<1||neighbors.size()<leaveOneOutCV.getNumNeighbors())
				{
					if(closerSectors == null)
						closerSectors= new ArrayList<Sector>();

					while(neighbors.size()<leaveOneOutCV.getNumNeighbors())
					{
						if(s.parent!=null)
							s=s.parent;
						else
							break;
						neighbors.addListGisDataLOOCV(s.getAllChildren(new ArrayList<GisData>()), dataToInterpolate);
					}
				}
				else
				{
					closerSectors = s.findCloserSector(neighbors.get(neighbors.size()-1).getDistanceFromLocation(), x, y, date);
					if(closerSectors !=null)
					{
						for (Sector cs:closerSectors)
						{
							if(cs.data!=null)
							{
								neighbors.addListGisDataLOOCV(cs.data, dataToInterpolate);
							}
						}
					}
				}


				/*
				for (int j = 0; j < leaveOneOutCV.measuredDataSetCount; j++) {
					double distance = leaveOneOutCV.measuredDataSet.getData().get(index).getDistance(leaveOneOutCV.measuredDataSet.getData().get(j));
					if(index == j) {
						continue;
					}
					else if (!neighbors.isFull()) {
						Neighbor neighbor = new Neighbor(leaveOneOutCV.measuredDataSet.getData().get(j), distance);
						neighbors.addLeastNeighbor(neighbor);
					} else if (distance < neighbors.maxDistance()) {
						Neighbor neighbor = new Neighbor(leaveOneOutCV.measuredDataSet.getData().get(j), distance);
						neighbors.replaceMax(neighbor);
					}
				}
				*/
				leaveOneOutCV.interpolationQueue.put(neighbors);
			}
		} catch (InterruptedException e) {
		}
	}
}

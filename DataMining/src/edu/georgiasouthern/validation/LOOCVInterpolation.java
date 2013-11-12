package edu.georgiasouthern.validation;

import edu.georgiasouthern.interpolation.NearestNeighborsList;
import edu.georgiasouthern.interpolation.Neighbor;


public class LOOCVInterpolation implements Runnable {
	/**
	 * 
	 */
	private final LOOCV leaveOneOutCV;
	
	LOOCVInterpolation(LOOCV leaveOneOutCV) {
		this.leaveOneOutCV = leaveOneOutCV;
	}
 
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				NearestNeighborsList neighbors = leaveOneOutCV.interpolationQueue.take();
				int index = neighbors.getIndex();
				double measurement = 0.0;
				for (int i = 0; i < this.leaveOneOutCV.getNumNeighbors(); i++) {
					Neighbor thisNeighbor = neighbors.get(i);
					measurement += thisNeighbor.getData().getMeasurement()
							* weight(neighbors, thisNeighbor, leaveOneOutCV.getExponent());
				}
				
				leaveOneOutCV.interpolatedResults[index] = measurement;
				leaveOneOutCV.increment();
				neighbors = null;
			}
		}

		catch (InterruptedException e) {
		}
	}

	private double weight(NearestNeighborsList neighbors, Neighbor neighbor, float exponent) {
		return weightNumerator(neighbor, exponent)
				/ weightDenominator(neighbors, exponent);
	}

	private double weightDenominator(NearestNeighborsList neighbors, float exponent) {
		double sum = 0.0;
		for (int i = 0; i < leaveOneOutCV.getNumNeighbors(); i++) {
			sum += Math.pow(1 / neighbors.get(i).getDistanceFromLocation(), exponent);
		}
		return sum;
	}

	private double weightNumerator(Neighbor neighbor, float exponent) {
		double num = Math.pow((1 / neighbor.getDistanceFromLocation()), exponent);
		return num;
	}
}

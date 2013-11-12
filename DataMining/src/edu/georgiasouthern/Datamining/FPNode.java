package edu.georgiasouthern.Datamining;


import java.util.ArrayList;
import java.util.List;

import edu.georgiasouthern.Datamining.Itemset;

/**
 * This is an implementation of a FPTree node as used by the FPGrowth algorithm.
 *
 * @see FPTree
 * @see Itemset
 * @see AlgoFPGrowth
 */
public class FPNode {
	String itemID = "-1";  // item id
	int counter = 1;  // frequency counter  (a.k.a. support)
	
	// the parent node of that node or null if it is the root
	FPNode parent = null; 
	// the child nodes of that node
	List<FPNode> childs = new ArrayList<FPNode>();
	
	FPNode nodeLink = null; // link to next node with the same item id (for the header table).
	
	/**
	 * constructor
	 */
	FPNode(){
		
	}

	/**
	 * Return the immmediate child of this node having a given ID.
	 * If there is no such child, return null;
	 */
	FPNode getChildWithID(String id) {
		// for each child node
		for(FPNode child : childs){
			// if the id is the one that we are looking for
			if(child.itemID.equalsIgnoreCase(id)){
				// return that node
				return child;
			}
		}
		// if not found, return null
		return null;
	}

}

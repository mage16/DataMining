package edu.georgiasouthern.Datamining;



import java.util.ArrayList;
import java.util.List;

/**
 * This is an implementation of a MISTree node used by the CFPGrowth algorithm.
 * <br/><br/>
 *
 * This implementation was made by Azadeh Soltani based on the FPGrowth
 * implementation by Philippe Fournier-Viger
 * 
 * @see AlgoCFPGrowth_saveToFile
 * @see MISTree
 * @author Azadeh Soltani
 */
public class MISNode {
	String itemID = "-1";  // item represented by this node
	int counter = 1;  // frequency counter
	
	// link to parent node
	MISNode parent = null; 
	
	// links to child nodes
	List<MISNode> childs = new ArrayList<MISNode>();
	
	 // link to next node with the same item id (for the header table).
	MISNode nodeLink = null;
	
	/**
	 * constructor
	 */
	MISNode(){
		
	}

	/**
	 * Return the immmediate child of this node having a given ID.
	 * If there is no such child, return null;
	 */
	MISNode getChildWithID(String item) {
		// for each child
		for(MISNode child : childs){
			// if the id is found, return the node
			if(child.itemID == item){
				return child;
			}
		}
		return null; // if not found, return null
	}
	
	/**
	 * Return the index of the immmediate child of this node having a given ID.
	 * If there is no such child, return -1;
	 */
	MISNode getChildIndexWithID(String id) {
		int i=0;
		// for each child
		for(MISNode child : childs){
			// if the id is found, return the index
			if(child.itemID.equalsIgnoreCase(id)){
				// return that node
				return child;
			}
			i++;
		}
		return null; // if not found, return -1
	}
}

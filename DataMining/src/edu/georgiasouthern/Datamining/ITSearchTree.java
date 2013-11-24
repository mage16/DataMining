package edu.georgiasouthern.Datamining;
    
import edu.georgiasouthern.Datamining.ITNode;
import edu.georgiasouthern.Datamining.Eclat_Charm.Itemset;

/**
 * This class represents an ITSearchTree used by the Charm and Eclat algorithm.
 * 
 * 
 * @see AlgoCharm
 * @see AlgoEclat
 * @see Itemset
 * @see ITNode
 */
class ITSearchTree {
	// the root node
	private ITNode root;

	/**
	 * Default constructor.
	 */
	public ITSearchTree() {

	}

	/**
	 * Set the root node of the tree as a given node.
	 * @param root the given node
	 */
	public void setRoot(ITNode root) {
		this.root = root;
	}

	/**
	 * Get the root node of the tree.
	 * @return an ITNode.
	 */
	public ITNode getRoot() {
		return root;
	}

}

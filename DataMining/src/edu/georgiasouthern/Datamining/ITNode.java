package edu.georgiasouthern.Datamining;
   
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.georgiasouthern.Datamining.Eclat_Charm.Itemset;

/**
 * This class represents an ITNode from the ITSearch Tree 
 * used by Charm and Eclat algorithms.<br/><br/>
 * 
 * @see ITSearchTree
 * @see AlgoCharm
 * @see AlgoEclat
 * @see Itemset
 */
class ITNode {

	// the itemset stored in that node
	private Itemset itemset;
	// the tidset associated to that node
	private Set<Integer> tidset;

	// the parent node of that node
	private ITNode parent = null;
	// the child nodes of that node
	private List<ITNode> childNodes = new ArrayList<ITNode>();

	/**
	 * Constructor of the node.
	 * @param itemset the itemset for that node.
	 */
	public ITNode(Itemset itemset) {
		this.itemset = itemset;
	}

	/**
	 * Get the itemset of that node.
	 * @return an Itemset.
	 */
	public Itemset getItemset() {
		return itemset;
	}

	/**
	 * Set the itemset of that node.
	 * @param itemset an itemset.
	 */
	public void setItemset(Itemset itemset) {
		this.itemset = itemset;
	}

	/**
	 * Get the tidset of that node
	 * @return the tidset as a Set of Integers.
	 */
	public Set<Integer> getTidset() {
		return tidset;
	}

	/**
	 * Set the tidset of that node.
	 * @param tidset 
	 */
	public void setTidset(Set<Integer> tidset) {
		this.tidset = tidset;
	}

	/**
	 * Get the child nodes of this node
	 * @return a list of ITNodes.
	 */
	public List<ITNode> getChildNodes() {
		return childNodes;
	}

	/**
	 * Set the child node of this node.
	 * @param childNodes a list of nodes.
	 */
	public void setChildNodes(List<ITNode> childNodes) {
		this.childNodes = childNodes;
	}

	/**
	 * Get the parent of this node
	 * @return a node or null if no parent.
	 */
	public ITNode getParent() {
		return parent;
	}

	/**
	 * Set the parent of this node to a given node.
	 * @param parent the given node.
	 */
	public void setParent(ITNode parent) {
		this.parent = parent;
	}

	/**
	 * Method used by Charm to replace all itemsets in the subtree defined
	 * by this node as the itemsets union a replacement itemset.
	 * @param replacement the replacement itemset
	 */
	void replaceInChildren(Itemset replacement) {
		// for each child node
		for (ITNode node : getChildNodes()) {
			// get the itemset of the child node
			Itemset itemset = node.getItemset();
			// could be optimized... not very efficient..
			// in particular, instead of using a list in itemset, we could use a
			// set.
			
			// for each item in the replacement
			for (String item : replacement.getItems()) {
				// if it is not in the itemset already
				if (!itemset.contains(item)) {
					// add it
					itemset.addItem(item);
				}
			}
			// recursive call for the children of the current node
			node.replaceInChildren(replacement);
		}
	}

}

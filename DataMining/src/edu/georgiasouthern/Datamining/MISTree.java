package edu.georgiasouthern.Datamining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an implementation of a MISTree (which is modified from a fptree) used
 * by the CFPGrowth algorithm.
 * <br/><br/>
 * 
 * This implementation was made by Azadeh Soltani based on the FPGrowth
 * implementation by Philippe Fournier-Viger.
 * 
 * @see AlgoCFPGrowth_saveToFile
 * @see MISNode
 * @author Azadeh Soltani
 */
public class MISTree {
	// List of items in the header table
		List<String> headerList = null;
		
		// List of pairs (item, frequency) of the header table
		Map<String, MISNode> mapItemNodes = new HashMap<String, MISNode>();
		
		// flag that indicate if the tree has more than one path
		boolean hasMoreThanOnePath = false;
	// root of the tree
	MISNode root = new MISNode(); // null node

	/**
	 * Constructor
	 */
	MISTree() {

	}

	/**
	 * Method for adding a transaction to the fp-tree (for the initial
	 * construction of the FP-Tree).
	 * 
	 * @param transaction
	 */
	public void addTransaction(List<String> transaction) {
		MISNode currentNode = root;
		// For each item in the transaction
		for (String item : transaction) {
			// look if there is a node already in the FP-Tree
			MISNode child = currentNode.getChildWithID(item);
			if (child == null) {
				// there is no node, we create a new one
				MISNode newNode = new MISNode();
				newNode.itemID = item;
				newNode.parent = currentNode;
				// we link the new node to its parrent
				currentNode.childs.add(newNode);
				
				// check if more than one path
//				if(!hasMoreThanOnePath && currentNode.childs.size() > 1) {
//					hasMoreThanOnePath = true;
//				}

				// we take this node as the current node for the next for loop
				// iteration
				currentNode = newNode;

				// We update the header table.
				// We check if there is already a node with this id in the
				// header table
				MISNode headernode = mapItemNodes.get(item);
				if (headernode == null) { // there is not
					mapItemNodes.put(item, newNode);
				} else { // there is
							// we find the last node with this id.
					while (headernode.nodeLink != null) {
						headernode = headernode.nodeLink;
					}
					headernode.nodeLink = newNode;
				}
			} else {
				// there is a node already, we update it
				child.counter++;
				currentNode = child;
			}
		}
	}

	/**
	 * Method for adding a prefixpath to a fp-tree.
	 * 
	 * @param prefixPath 
	 *            The prefix path
	 * @param mapSupportBeta
	 *            The frequencies of items in the prefixpaths
	 * @param relativeMinsupp the minMIS parameter
	 */
	void addPrefixPath(List<MISNode> prefixPath,
			Map<String, Integer> mapSupportBeta, double relativeMinsupp) {
		// the first element of the prefix path contains the path support
		int pathCount = prefixPath.get(0).counter;

		MISNode currentNode = root;
		// For each item in the transaction (in backward order)
		// (and we ignore the first element of the prefix path)
		for (int i = prefixPath.size() - 1; i >= 1; i--) {
			MISNode pathItem = prefixPath.get(i);
			// if the item is not frequent we skip it
			if (mapSupportBeta.get(pathItem.itemID) < relativeMinsupp) {
				continue;
			}

			// look if there is a node already in the FP-Tree
			MISNode child = currentNode.getChildWithID(pathItem.itemID);
			if (child == null) {
				// there is no node, we create a new one
				MISNode newNode = new MISNode();
				newNode.itemID = pathItem.itemID;
				newNode.parent = currentNode;
				newNode.counter = pathCount; // SPECIAL
				currentNode.childs.add(newNode);
				
				// check if more than one path
//				if(!hasMoreThanOnePath && currentNode.childs.size() > 1) {
//					hasMoreThanOnePath = true;
//				}
				
				currentNode = newNode;
				// We update the header table.
				// We check if there is already a node with this id in the
				// header table
				MISNode headernode = mapItemNodes.get(pathItem.itemID);
				if (headernode == null) { // there is not
					mapItemNodes.put(pathItem.itemID, newNode);
				} else { // there is
							// we find the last node with this id.
					while (headernode.nodeLink != null) {
						headernode = headernode.nodeLink;
					}
					headernode.nodeLink = newNode;
				}
			} else {
				// there is a node already, we update it
				child.counter += pathCount;
				currentNode = child;
			}
		}
	}

	/**
	 * Method for creating the list of items in the header table, in descending
	 * order of frequency.
	 * 
	 * @param itemComparator a Comparator of items
	 */
	// az--------------------------
	void createHeaderList(final Map<String, Integer> mapSupport) {
		// create an array to store the header list with
		// all the items stored in the map received as parameter
		headerList =  new ArrayList<String>(mapItemNodes.keySet());
		
		// sort the header table by decreasing order of support
		Collections.sort(headerList, new Comparator<String>(){
			public int compare(String id1, String id2){
				// compare the support
				int compare = mapSupport.get(id2) - mapSupport.get(id1);
				// if the same frequency, we check the lexical ordering!
				if(compare ==0){ 
					//return (id1 - id2);
					return id1.compareTo(id2);
				}
				// otherwise we use the support
				return compare;
			}
		});
	}
	/**
	 * Delete an item from the header list
	 * @param item the item
	 * @param itemComparator a Comparator for comparing items
	 */
	

	/**
	 * Perform MIS pruning with an item
	 * @param item
	 */
	void MISPruning(int item) {
		// for each header node
		MISNode headernode = mapItemNodes.get(item);
		while (headernode != null) {
			// if it is a leaf then remove the link directly
			if (headernode.childs.isEmpty()) {
				headernode.parent.childs.remove(headernode);
			}// if removed the node and parent node will be linked to 
				// its child node
			else {
				// remove it before adding the childs
				headernode.parent.childs.remove(headernode); 
				headernode.parent.childs.addAll(headernode.childs);
				for (MISNode node : headernode.childs) {
					node.parent = headernode.parent;
				}
			}// else
			headernode = headernode.nodeLink;
		}// while
	}

	/**
	 * MIS merge procedure (a recursive method)
	 * @param treeRoot a subtree root
	 */
	void MISMerge(MISNode treeRoot) {
		// stop recursion
		if (treeRoot == null)
			return;

		for(int i=0; i< treeRoot.childs.size(); i++){
			MISNode node1 = treeRoot.childs.get(i);
			for(int j=i+1; j< treeRoot.childs.size(); j++){
				MISNode node2 = treeRoot.childs.get(j);
				
				if (node2.itemID == node1.itemID) {
					// (1) merge node1 and node2 
					node1.counter += node2.counter;
					node1.childs.addAll(node2.childs);
					// remove node 2 from child list
					treeRoot.childs.remove(j);   
					j--;                         
					
					// (2) remove node2 from the header list
					// If node2 is the first item in the header list:
					MISNode headernode = mapItemNodes.get(node1.itemID);   
					if(headernode == node2){
						mapItemNodes.put(node2.itemID, node2.nodeLink);
					}
					else{// Otherwise, search for node 2 and then remove it
						while (headernode.nodeLink != node2){ 
							headernode = headernode.nodeLink;
						}
						// fix nodelink
						headernode.nodeLink = headernode.nodeLink.nodeLink; 
					}
				}// if
			}
		}
		// for all children, merge their children
		for (MISNode node1 : treeRoot.childs){
			MISMerge(node1);
		}
	}

	/**
	 * Print a MIS tree to System.out (recursive method)
	 * @param TRoot the root of the subtree to be printed.
	 */
	public void print(MISNode TRoot) {
		// char a[]={'z','a','b','c','d','e','f','g','h'};
		// prefix print
		if (TRoot.itemID != "-1")
			System.out.print(TRoot.itemID);
		System.out.print(' ');
		for (MISNode node : TRoot.childs) {
			print(node); // recursive call
		}

	}
}

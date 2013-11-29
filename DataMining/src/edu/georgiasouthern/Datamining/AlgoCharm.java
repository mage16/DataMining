package edu.georgiasouthern.Datamining;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.georgiasouthern.Datamining.TriangularMatrix;
import edu.georgiasouthern.Datamining.TransactionDatabase;
import edu.georgiasouthern.Datamining.Eclat_Charm.Itemset;
import edu.georgiasouthern.Datamining.Eclat_Charm.Itemsets;

/**
 * This is an implementation of the CHARM algorithm that was proposed by MOHAMED
 * ZAKI. The paper describing charm:
 * <br/><br/>
 * 
 * Zaki, M. J., & Hsiao, C. J. (2002, April). CHARM: An Efficient Algorithm for Closed Itemset Mining. In SDM (Vol. 2, pp. 457-473).
 * 
 * This implementation may not be fully optimized. In particular, Zaki proposed
 * various extensions that I have not implemented (for example diffsets).<br/><br/>
 * 
 * This version saves the result to a file or keep it into memory if no output
 * path is provided by the user to the runAlgorithm() method.<br/><br/>
 * 
 * @see TriangularMatrix
 * @see TransactionDatabase
 * @see Itemset
 * @see Itemsets
 * @see HashTable
 * @see ITSearchTree
 */
public class AlgoCharm {

	// parameters
	private double minsupRelative; // relative minimum support
	private TransactionDatabase database; // the transaction database

	// for statistics
	private long startTimestamp; // start time of the last execution
	private long endTimestamp; // end time of the last execution

	// The patterns that are found
	// (if the user want to keep them into memory)
	protected Itemsets frequentItemsets;
	BufferedWriter writer = null; // object to write the output file
	private int itemsetCount; // the number of patterns found
	
	ArrayList<String> frequentItems = new ArrayList<String>();
	
	private MemoryLogger memoryLogger = null;


	// for optimization with a hashTable
	private HashTable hash;

	/**
	 * Default constructor
	 */
	public AlgoCharm() {

	}

	/**
	 * Run the Charm algorithm.
	 * 
	 * @param output the filepath for saving the result
	 * @param database a transaction database taken as input
	 * @param minsuppAbsolute the ABSOLUTE minimum support (double)
	 * @param hashTableSize  the size of the hashtable to be used by charm
	 * @return the frequent closed itemsets found by charm
	 * @throws IOException if an error occurs while writting to file.
	 */
	public Itemsets runAlgorithm(String output, TransactionDatabase database,
			int hashTableSize, double minsuppAbsolute) throws IOException {

		//initialize tool to record memory usage
		memoryLogger = new MemoryLogger();
		memoryLogger.checkMemory();
				
		this.database = database;
		
		// create hash table to store candidate itemsets
		this.hash = new HashTable(hashTableSize);
		
		// convert from an absolute minimum support to relative minimum support
		// by multiplying with the database size
		this.minsupRelative = minsuppAbsolute;

		// start the algorithm!
		return run(output);
	}

	/**
	 * Run the algorithm.
	 * @param output an output file path for writing the result or if null the result is saved into memory and returned
	 * @return the result
	 * @throws IOException exception if error while writing the file.
	 */
	private Itemsets run(String output) throws IOException {

		// if the user want to keep the result into memory
		if (output == null) {
			writer = null;
			frequentItemsets = new Itemsets("FREQUENT ITEMSETS");
		} else { // if the user want to save the result to a file
			frequentItemsets = null;
			writer = new BufferedWriter(new FileWriter(output));
		}

		// reset the number of itemset found to 0
		itemsetCount = 0;
		
		// record the start timestamp
		startTimestamp = System.currentTimeMillis();

		// A set that will contains all transactions IDs
		Set<Integer> allTIDS = new HashSet<Integer>();

		// (1) First database pass : calculate tidsets of each item.
		String maxItemId = "";
		// This map will contain the tidset of each item
		// Key: item   Value :  tidset
		final Map<String, Set<Integer>> mapItemCount = new HashMap<String, Set<Integer>>();
		// for each transaction
		for (int i = 0; i < database.size(); i++) {
			// add the transaction id to the set of all transaction ids
			allTIDS.add(i); 
			// for each item in that transaction
			for (String item : database.getTransactions().get(i)) {
				// add the transaction ID to the tidset of that item
				Set<Integer> set = mapItemCount.get(item);
				if (set == null) {
					set = new HashSet<Integer>();
					mapItemCount.put(item, set);
					// if the current item is larger than all items until
					// now, remember that!
					if (item.compareTo(maxItemId) > 0) {
						maxItemId = new String(item);
					}
				}
				set.add(i); // add tid to the tidset of the item
			}
		}

		// (2) create ITSearchTree with the empty set as root node
		ITSearchTree tree = new ITSearchTree();
		// add the empty set
		ITNode root = new ITNode(new Itemset());
		// the empty set as all tids as its tidset
		root.setTidset(allTIDS);
		tree.setRoot(root);

		// (3) create a child node of the root node for each frequent item.
		
		// For each item
		for (Entry<String, Set<Integer>> entry : mapItemCount.entrySet()) {
			//if the item is frequent
			if (entry.getValue().size() >= minsupRelative) {
				// create a  new node for that item
				Itemset itemset = new Itemset();
				itemset.addItem(entry.getKey());
				ITNode newNode = new ITNode(itemset);
				// set its tidset as the tidset that we have calculated previously
				newNode.setTidset(entry.getValue());
				// set its parent as the root
				newNode.setParent(root);
				// add the new node as child of the root node
				root.getChildNodes().add(newNode);
			}
		}

		// save root node
		// save(root);

		// for optimization, sort the child of the root according to the support
		sortChildren(root);

		// while there is at least one child node of the root
		while (root.getChildNodes().size() > 0) {
			// get the first child node
			ITNode child = root.getChildNodes().get(0);
			// extend it
			extend(child);
			// save it
			save(child);
			// delete it
			delete(child);
		}

		// close the output file if the result was saved to a file
		if (writer != null) {
			writer.close();
		}

		// record the end time for statistics
		endTimestamp = System.currentTimeMillis();

		// check the memory usage
		memoryLogger.checkMemory();
		
		// Return all frequent closed itemsets found.
		return frequentItemsets; 
	}

	/**
	 * This is the "extend" method as described in the paper.
	 * @param currNode the current node.
	 * @throws IOException exception if error while writing to file.
	 */
	private void extend(ITNode currNode) throws IOException {
		// loop over the brothers of that node
		int i = 0;
		while (i < currNode.getParent().getChildNodes().size()) {
			// get the brother i
			ITNode brother = currNode.getParent().getChildNodes().get(i);
			// if the brother is not the current node
			if (brother != currNode) {

				// Property 1
				// If the tidset of the current node is the same as the one
				// of its brother
				if (currNode.getTidset().equals(brother.getTidset())) {
					// we can replace the current node itemset in the current node
					// and the subtree by the union of the brother itemset
					// and the current node itemset
					replaceInSubtree(currNode, brother.getItemset());
					// then we delete the brother
					delete(brother);
				}
				// Property 2
				// If the brother tidset contains the tidset of the current node
				else if (brother.getTidset().containsAll(currNode.getTidset())) {
					// Same as previous if condition except that we
					// do not delete the brother.
					replaceInSubtree(currNode, brother.getItemset());
					i++;
				}
				// Property 3
				// If the tidset of the current node contains the tidset of the
				// brother
				else if (currNode.getTidset().containsAll(brother.getTidset())) {
					// Generate a candidate by performing
					// the union of the itemsets of the current node and its brother
					ITNode candidate = getCandidate(currNode, brother);
					// delete the brother
					delete(brother);
					// if a candidate was obtained
					if (candidate != null) {
						// add the candidate as child node of the current node
						currNode.getChildNodes().add(candidate);
						candidate.setParent(currNode);
					}
				}
				// Property 4
				// if the tidset of the current node is not equal to the tidset
				// of its brother
				else if (!currNode.getTidset().equals(brother.getTidset())) {
					// Generate a candidate by performing
					// the union of the itemsets of the current node and its brother
					ITNode candidate = getCandidate(currNode, brother);
					// if a candidate was obtained
					if (candidate != null) {
						// add the candidate as child node of the current node
						currNode.getChildNodes().add(candidate);
						candidate.setParent(currNode);
					}
					i++; // go to next node
				} else {
					i++; // go to next node
				}
			} else {
				i++;  // go to next node
			} 
		}

		// for optimization, sort the child of the root according to the support
		sortChildren(currNode);

		// while the current node has child node
		while (currNode.getChildNodes().size() > 0) {
			// get the first child
			ITNode child = currNode.getChildNodes().get(0);
			extend(child);  // extend it (charm is a depth-first search algorithm)
			save(child); // save the node
			delete(child); // then delte it
		}
	}

	/**
	 * Replace the itemset of a current node by another itemset in 
	 * a subtree (including the current node).
	 * @param currNode the current node.
	 * @param itemset  the itemset
	 */
	private void replaceInSubtree(ITNode currNode, Itemset itemset) {
		// make the union
		Itemset union = new Itemset();
		union.getItems().addAll(currNode.getItemset().getItems());
		union.getItems().addAll(itemset.getItems());
		// replace for this node
		currNode.setItemset(union);
		// recursively perform replacement for childs and their childs, etc.
		currNode.replaceInChildren(union);
	}

	/**
	 * Generate a candidate by performing the union of the current node and a brother of that node.
	 * @param currNode the current node
	 * @param brother  the itemset of the brother node
	 * @return  a candidate or null if the resulting candidate do not have enough support.
	 */
	private ITNode getCandidate(ITNode currNode, ITNode brother) {

		// create list of common tids of the itemset of the current node
		// and the brother node
		Set<Integer> commonTids = new HashSet<Integer>();
		// for each tid in the tidset of the current node
		for (Integer tid : currNode.getTidset()) {
			// if it is in the tidset of the brother node
			if (brother.getTidset().contains(tid)) {
				// add it to the set of common tids
				commonTids.add(tid);
			}
		}

		// (2) check if the two itemsets have enough common tids
		// if not, we don't need to generate a rule for them.
		
		// if the common tids cardinality is enough for the minimum support
		if (commonTids.size() >= minsupRelative) {
			// perform the union of the itemsets
			Itemset union = currNode.getItemset().union(brother.getItemset());
			// create a new node with the union
			ITNode node = new ITNode(union);
			// set the tidset as the intersection of the tids of both itemset
			node.setTidset(commonTids);
			// return the node
			return node;
		}
		// otherwise return null because the candidate did not have enough support
		return null;
	}

	/**
	 * Delete a child from its parent node.
	 * @param child the child node
	 */
	private void delete(ITNode child) {
		child.getParent().getChildNodes().remove(child);
	}

	/**
	 * Save a node (as described in the paper).
	 * @param node the node
	 * @throws IOException
	 */
	private void save(ITNode node) throws IOException {
		// get the itemset of that node and set its tidset
		Itemset itemset = node.getItemset();
		itemset.setTidset(node.getTidset());

		// if it has no superset already in the hash table
		// it is a frequent closed itemset
		if (!hash.containsSupersetOf(itemset)) {
			// increase the itemset count
			itemsetCount++;
			// if the result should be saved to memory
			if (writer == null) {
				// save it to memory
				frequentItemsets.addItemset(itemset, itemset.size());
			} else {
				// otherwise if the result should be saved to a file,
				// then write it to the output file
				writer.write(node.getItemset().toString() + " #SUP: "
						+ node.getTidset().size());
				writer.newLine();
				frequentItems.add(node.getItemset().toString() + " #SUP: " + node.getTidset().size());
			}
			// add the itemset to the hashtable
			hash.put(itemset);
		}
	}

	/**
	 *  Sort the children of a node according to the order of support.
	 * @param node the node.
	 */
	private void sortChildren(ITNode node) {
		// sort children of the node according to the support.
		Collections.sort(node.getChildNodes(), new Comparator<ITNode>() {
			// Returns a negative integer, zero, or a positive integer as
			// the first argument is less than, equal to, or greater than the
			// second.
			public int compare(ITNode o1, ITNode o2) {
				return o1.getTidset().size() - o2.getTidset().size();
			}
		});
	}
	
	public void SaveStats() throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("CHARMStats", "UTF-8");
		writer.println("=============  CHARM - STATS =============");
		long temps = endTimestamp - startTimestamp;
		writer.println(" Transactions count from database : "
				+ database.size());
		writer.println(" Max memory usage: " + memoryLogger.getMaxMemory() + " mb \n");
		writer.println(" Frequent closed itemsets count : " + itemsetCount);
		writer.println(" Total time ~ " + temps + " ms");
		writer.println("===================================================");
		writer.close();
	
	}

	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("=============  CHARM - STATS =============");
		long temps = endTimestamp - startTimestamp;
		System.out.println(" Transactions count from database : "
				+ database.size());
		System.out.println(" Frequent closed itemsets count : " + itemsetCount);
		System.out.println(" Total time ~ " + temps + " ms");
		System.out
				.println("===================================================");
		SaveStats();
	}
	
	/**
	 * Get the statistics as a string.
	 */
	public ArrayList<String> getStats() {
		ArrayList<String> stats = new ArrayList<String>();
		
		stats.add("=============  CHARM - STATS =============");
		long temps = endTimestamp - startTimestamp;
		stats.add(" Transactions count from database : "
				+ database.size());
		stats.add(" Max memory usage: " + memoryLogger.getMaxMemory() + " mb \n");
		stats.add(" Frequent closed itemsets count : " + itemsetCount);
		stats.add(" Total time ~ " + temps + " ms");
		stats.add("===================================================");
		
		return stats;
	}

	/**
	 * Get the set of frequent closed itemsets found by Charm.
	 * @return the set of frequent closed itemsets.
	 */
	public Itemsets getClosedItemsets() {
		return frequentItemsets;
	}
	
	public ArrayList<String> getFrequentItemsets() {
		return frequentItems;
	}
}

package edu.georgiasouthern.Datamining.Eclat_Charm;
 
   
import java.util.HashSet;
import java.util.Set;

import edu.georgiasouthern.Datamining.AbstractItemset;
 
/**
 * This class represents an itemset implemented as a set of integers where
 * the transactions/sequences ids (tids) containing this itemset are represented
 * with a set of integers.
* 
* @see AbstractItemset
* @see Itemsets
 */
public class Itemset extends AbstractItemset{
	/**  the items */
	 public Set<String> itemset = new HashSet<String>(); 
	/** the list of transaction/sequence ids containing this itemset */
	 public Set<Integer> tidset = new HashSet<Integer>(); 
	 /**
	  * Get this itemset as a string.
	  */
	public String toString() {
		StringBuffer r = new StringBuffer();
		for (String attribute : itemset) {

			r.append(attribute);
			
			r.append(' ');
		}
		return r.toString();
	}
	
	/**
	 * Get the relative support of this itemset
	 * @param nbObject  the number of transactions/sequences in the database where the itemset was found
	 * @return the relative support as a double (percentage)
	 */
	public double getRelativeSupport(int nbObject) {
		return ((double) tidset.size()) / ((double) nbObject);
	}

	/**
	 * Get the support of this itemset
	 * @return the support of this itemset
	 */
	public int getAbsoluteSupport() {
		return tidset.size();
	}

	/**
	 * Get the number of items in this itemset
	 * @return the size of this itemset
	 */
	public int size() {
		return itemset.size();
	}

	/** 
	 * Check if this itemset contains a given item
	 * @param item the given item
	 * @return true if contained
	 */
	public boolean contains(String item) {
		return itemset.contains(item);
	}
	
	/**
	 * This method returns the set of items in this itemset.
	 * @return A set of Integers.
	 */
	public Set<String> getItems(){
		return itemset;
	}

	/**
	 * This class returns a new itemset that is the union of this itemset
	 * and another given itemset.
	 * @param itemset a given itemset.
	 * @return the union.
	 */
	public Itemset union(Itemset itemset) {
		Itemset union = new Itemset();
		union.getItems().addAll(getItems());
		union.getItems().addAll(itemset.getItems());
		return union;
	}

	/**
	 * Add an item to that itemset.
	 * @param item an item (Integer)
	 */
	public void addItem(String item) {
		getItems().add(item);
	}

	/**
	 * Set the tidset of this itemset
	 * @param tidset a set of Integers
	 */
	public void setTidset(Set<Integer> tidset) {
		this.tidset = tidset;
	}

	/**
	 * Get the set of transaction IDs.
	 * @return a Set of Integer
	 */
	public Set<Integer>  getTidset() {
		return this.tidset;
	}

}

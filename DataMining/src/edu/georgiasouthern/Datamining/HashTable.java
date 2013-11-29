package edu.georgiasouthern.Datamining;

import java.util.ArrayList;
import java.util.List;

import edu.georgiasouthern.Datamining.TriangularMatrix;
import edu.georgiasouthern.Datamining.TransactionDatabase;
import edu.georgiasouthern.Datamining.Eclat_Charm.Itemset;
import edu.georgiasouthern.Datamining.Eclat_Charm.Itemsets;

/**
 * This class represents an HashTable for storing itemsets used by the Charm
 * algorithm.
 * 
 * @see TriangularMatrix
 * @see TransactionDatabase
 * @see Itemset
 * @see Itemsets
 * @see AlgoCharm
 */
class HashTable {

	// the internal array for the hash table
	private List<Itemset>[] table;

	/**
	 * Constructor
	 * @param size size of the internal array for the hash table.
	 */
	public HashTable(int size) {
		table = new ArrayList[size];
	}

	/**
	 * Check if the hash table contains a superset of a given itemset
	 * @param itemset  the given itemset
	 * @return true if it contains at least one superset, otherwise false
	 */
	public boolean containsSupersetOf(Itemset itemset) {
		// calculate the hashcode of the itemset
		int hashcode = hashCode(itemset);
		// if the position in the array is empty return false
		if (table[hashcode] == null) {
			return false;
		}
		// for each itemset at that hashcode position
		for (Object object : table[hashcode]) {
			Itemset itemset2 = (Itemset) object;
			// if the support is the same and the given itemset is contained
			// in that later itemset
			if (itemset2.getAbsoluteSupport() == itemset.getAbsoluteSupport()
					&& itemset2.getItems().containsAll(itemset.getItems())) {
				return true;
			}
		}
		// otherwise no superset is in the hashtable so return false
		return false;
	}

	/**
	 * Add an itemset to the hash table.
	 * @param itemset the itemset to be added
	 */
	public void put(Itemset itemset) {
		// calculate the hashcode
		int hashcode = hashCode(itemset);
		// if the position in the array is empty create a new array list
		// for that position
		if (table[hashcode] == null) {
			table[hashcode] = new ArrayList<Itemset>();
		}
		// store the itemset in the arraylist of that position
		table[hashcode].add(itemset);
	}

	/**
	 * Calculate the hashcode of an itemset as the sum of the tids of its tids set,
	 * modulo the internal array length.
	 * @param itemset an itemset.
	 * @return the hashcode (an integer)
	 */
	public int hashCode(Itemset itemset) {
		int hashcode = 0;
		// for each tid in the tidset
		for (Integer tid : itemset.getTidset()) {
			// make the sum
			hashcode += tid;
		}
		// to fix the bug of overpassing the size of an integer when running on large datasets such as accidents
		if(hashcode < 0){
			hashcode = 0 - hashcode;
		}
		// make the modulo according to the size of the internal array
		return (hashcode % table.length);
	}

}

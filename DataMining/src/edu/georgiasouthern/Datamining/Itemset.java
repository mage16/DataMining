package edu.georgiasouthern.Datamining;



/**
 * This class represents an itemset (a set of items) implemented as an array of integers with
 * a variable to store the support count of the itemset.
* 
 */
public class Itemset extends AbstractOrderedItemset{
	/** the array of items **/
	public String[] itemset; 

	/**  the support of this itemset */
	public int support = 0; 
	
	/**
	 * Get the items as array
	 * @return the items
	 */
	public String[] getItems() {
		return itemset;
	}
	
	/**
	 * Constructor
	 */
	public Itemset(){
		itemset = new String[]{};
	}
	
	/**
	 * Constructor 
	 * @param item an item that should be added to the new itemset
	 */
	public Itemset(String item){
		itemset = new String[]{item};
	}

	/**
	 * Constructor 
	 * @param items an array of items that should be added to the new itemset
	 */
	public Itemset(String [] items){
		this.itemset = items;
	}
	
	/**
	 * Get the support of this itemset
	 */
	public int getAbsoluteSupport(){
		return support;
	}
	
	/**
	 * Get the size of this itemset 
	 */
	public int size() {
		return itemset.length;
	}

	/**
	 * Get the item at a given position in this itemset
	 */
	public String get(int position) {
		return itemset[position];
	}

	/**
	 * Set the support of this itemset
	 * @param support the support
	 */
	public void setAbsoluteSupport(Integer support) {
		this.support = support;
	}

	/**
	 * Increase the support of this itemset by 1
	 */
	public void increaseTransactionCount() {
		this.support++;
	}


	/**
	 * Make a copy of this itemset but exclude a given item
	 * @param itemsetToRemove the given item
	 * @return the copy
	 */
	public Itemset cloneItemSetMinusOneItem(String itemsetToRemove) {
		// create the new itemset
		String[] newItemset = new String[itemset.length -1];
		int i=0;
		// for each item in this itemset
		for(int j =0; j < itemset.length; j++){
			// copy the item except if it is the item that should be excluded
			if(itemset[j].compareTo(itemsetToRemove) != 0){
				newItemset[i++] = itemset[j];
			}
		}
		return new Itemset(newItemset); // return the copy
	}

	/**
	 * Make a copy of this itemset but exclude a set of items
	 * @param itemsetToNotKeep the set of items to be excluded
	 * @return the copy
	 */
	public Itemset cloneItemSetMinusAnItemset(Itemset itemsetToNotKeep) {
		// create a new itemset
		String[] newItemset = new String[itemset.length - itemsetToNotKeep.size()];
		int i=0;
		// for each item of this itemset
		for(int j =0; j < itemset.length; j++){
			// copy the item except if it is not an item that should be excluded
			if(itemsetToNotKeep.contains(itemset[j]) == false){
				newItemset[i++] = itemset[j];
			}
		}
		return new Itemset(newItemset); // return the copy
	}

}

package edu.georgiasouthern.Datamining;
//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.georgiasouthern.Datamining.Itemset;
import edu.georgiasouthern.Datamining.Itemsets;
import edu.georgiasouthern.Datamining.MemoryLogger;
import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateY;
import edu.georgiasouthern.common.GisDateYM;
import edu.georgiasouthern.common.GisDateYMD;
import edu.georgiasouthern.common.GisDateYQ;
import edu.georgiasouthern.common.DataSet.FileDelimiter;

public class AlgoApriori {

	// the current level k in the breadth-first search
	protected int k; 

	// variables for statistics
	protected int totalCandidateCount = 0; // number of candidate generated during last execution
	protected long startTimestamp; // start time of last execution
	protected long endTimestamp; // end time of last execution
	private int itemsetCount;  // itemset found during last execution
	private int databaseSize;
	
	// the minimum support set by the user
	private double minsupRelative;
	
	// A memory representation of the database.
	// Each position in the list represents a transaction
	private List<String[]> database = null;
	
	// The  patterns that are found 
	// (if the user want to keep them into memory)
	protected Itemsets patterns = null;

	// object to write the output file (if the user wants to write to a file)
	BufferedWriter writer = null;
	ArrayList<String> frequentItemsets = new ArrayList<String>();
	
	/**
	 * Default constructor
	 */
	public AlgoApriori() {
		
	}

	/**
	 * Method to run the algorithm
	 * @param minsup  a minimum support value as a percentage
	 * @param input  the path of an input file
	 * @param output the path of an input if the result should be saved to a file. If null,
	 *               the result will be kept into memory and this
	 *               method will return the result.
	 * @throws IOException exception if error while writing or reading the input/output file
	 */
	public Itemsets runAlgorithm(DataSet pm25, double minsup, String input, String output) throws IOException {
		
		// if the user want to keep the result into memory
		if(output == null){
			writer = null;
			patterns =  new Itemsets("FREQUENT ITEMSETS");
	    }else{ // if the user want to save the result to a file
	    	patterns = null;
			writer = new BufferedWriter(new FileWriter("../" + output)); 
		}
		
		// record the start time
		startTimestamp = System.currentTimeMillis();
		
		// set the number of itemset found to zero
		itemsetCount = 0;
		// set the number of candidate found to zero
		totalCandidateCount = 0;
		// reset the utility for checking the memory usage
		MemoryLogger.getInstance().reset();
		
		//ArrayList<String> header = pm25.getHeader();
		ArrayList<GisData> data = pm25.getData();
		ArrayList<String[]> otherData = pm25.getOtherData();

		// READ THE INPUT FILE
		// variable to count the number of transactions
		//databaseSize = 0;
		databaseSize = pm25.getDataCount();
		String fileType = pm25.getFileType();
		
		// Map to count the support of each item
		// Key: item  Value : support
		Map<String, Integer> mapItemCount = new HashMap<String, Integer>(); // to count the support of each item
		
		database = new ArrayList<String[]>(); // the database in memory (intially empty)
		
		// for each line (transactions) until the end of the data
		// count the support of each single item
		if(fileType.equalsIgnoreCase("GIS")) {
			for(int i = 0; i < databaseSize; i++)
			{
				GisData row = data.get(i);
				String id = Long.valueOf(row.getId()).toString();
				String x = Double.valueOf(row.getX()).toString();
				String y = Double.valueOf(row.getY()).toString();
				String measurement = Double.valueOf(row.getMeasurement()).toString();
				GisDate.DateDomain domain = row.getDate().getDateDomain();
				String year, month, day, quarter;
				int lineCount = row.getGisDataCount();
			
				// create an array of double to store the items in this transaction
				String transaction[] = new String[lineCount];
			
				Integer count = null;
				String item = null;
			
				// store the GIS data in the memory representation of the database
				transaction[0] = id;
				item = new String(transaction[0]);
				count = mapItemCount.get(item);
				if(count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			
				transaction[1] = x;
				item = new String(transaction[1]);
				count = mapItemCount.get(item);
				if(count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			
				transaction[2] = y;
				item = new String(transaction[2]);
				count = mapItemCount.get(item);
				if(count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			
				transaction[3] = measurement;
				item = new String(transaction[3]);
				count = mapItemCount.get(item);
				if(count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			
				switch(domain)
				{
				case Y:
					GisDateY aDateY = (GisDateY)row.getDate();
					year = Integer.valueOf(aDateY.getYear()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				case YM:
					GisDateYM aDateYM = (GisDateYM)row.getDate();
					year = Integer.valueOf(aDateYM.getYear()).toString();
					month = Integer.valueOf(aDateYM.getMonth()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
				
					transaction[5] = month;
					item = new String(transaction[5]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + month + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				case YMD:
					GisDateYMD aDateYMD = (GisDateYMD)row.getDate();
					year = Integer.valueOf(aDateYMD.getYear()).toString();
					month = Integer.valueOf(aDateYMD.getMonth()).toString();
					day = Integer.valueOf(aDateYMD.getDay()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
				
					transaction[5] = month;
					item = new String(transaction[5]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
				
					transaction[6] = day;
					item = new String(transaction[6]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + month + "\t" + day + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				case YQ:
					GisDateYQ aDateYQ = (GisDateYQ)row.getDate();
					year = Integer.valueOf(aDateYQ.getYear()).toString();
					quarter = Integer.valueOf(aDateYQ.getQuarter()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
				
					transaction[5] = quarter;
					item = new String(transaction[5]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + quarter + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				}
			
				// add the transaction to the database
				database.add(transaction);
			
				//System.out.println();
			}
		}
		else {
			for(int i = 0; i < databaseSize; i++)
			{
				// get the transaction data
				String transaction[] = otherData.get(i);
				int lineCount = transaction.length;
			
				Integer count = null;
				String item = null;
			
				// store the data in the memory representation of the database
				for(int j = 0; j < lineCount; j++)
				{
					item = new String(transaction[j]);
					count = mapItemCount.get(item);
					if(count == null) {
						mapItemCount.put(item, 1);
					} else {
						mapItemCount.put(item, ++count);
					}
				}
				// add the transaction to the database
				database.add(transaction);
				
			}
		}
			
		System.out.println("Starting the Apriori Algorithm!!!");
		// convert the minimum support as a percentage to a 
		// relative minimum support as an integer
		//this.minsupRelative = (int) Math.ceil(minsup * databaseSize);
		this.minsupRelative = minsup;
		System.out.println("Minimum Support Threshold = " + this.minsupRelative);
		
		// we start looking for itemset of size 1
		k = 1;
		
		// We add all frequent items to the set of candidate of size 1
		List<String> frequent1 = new ArrayList<String>();
		for(Entry<String, Integer> entry : mapItemCount.entrySet()){
			if(entry.getValue() >= minsupRelative){
				frequent1.add(entry.getKey());
				saveItemsetToFile(entry.getKey(), entry.getValue());
			}
		}
		mapItemCount = null;
		
		// We sort the list of candidates by lexical order
		// (Apriori need to use a total order otherwise it does not work)
		Collections.sort(frequent1, new Comparator<String>() {
			public int compare(String o1, String o2) {
				//return o1 - o2;
				return o1.compareTo(o2);
			}
		});
		
		// If no frequent item, the algorithm stops!
		if(frequent1.size() == 0){
			// close the output file if we used it
			if(writer != null){
				writer.close();
			}
			System.out.println("No frequent patterns!!!!");
			return patterns; 
		}
		
		// add the frequent items of size 1 to the total number of candidates
		totalCandidateCount += frequent1.size();
		
		
		// Now we will perform a loop to find all frequent itemsets of size > 1
		// starting from size k = 2.
		// The loop will stop when no candidates can be generated.
		List<Itemset> level = null;
		k = 2;
		do{
			// we check the memory usage
			MemoryLogger.getInstance().checkMemory();
			
			// Generate candidates of size K
			List<Itemset> candidatesK;
			
			// if we are at level k=2, we use an optimization to generate candidates
			if(k ==2){
				candidatesK = generateCandidate2(frequent1);
			}else{
			// otherwise we use the regular way to generate candidates
				candidatesK = generateCandidateSizeK(level);
			}
			
			// we add the number of candidates generated to the total
			totalCandidateCount += candidatesK.size();

			// We scan the database one time to calculate the support
			// of each candidates and keep those with higher suport.
			// For each transaction:
			for(String[] transaction: database){
				// for each candidate:
	 loopCand:	for(Itemset candidate : candidatesK){
		 			// a variable that will be use to check if 
		 			// all items of candidate are in this transaction
					int pos = 0;
					// for each item in this transaction
					for(String item: transaction){
						// if the item correspond to the current item of candidate
						if(item.equalsIgnoreCase(candidate.itemset[pos])){
							// we will try to find the next item of candidate next
							pos++;
							// if we found all items of candidate in this transaction
							if(pos == candidate.itemset.length){
								// we increase the support of this candidate
								candidate.support++;
								continue loopCand;
							}
					    // Because of lexical order, we don't need to 
						// continue scanning the transaction if the current item
						// is larger than the one that we search  in candidate.
						//}else if(item > candidate.itemset[pos]){
						}else if(item.compareTo(candidate.itemset[pos]) > 0){
							continue loopCand;
						}
						
					}
				}
			}

			// We build the level k+1 with all the candidates that have
			// a support higher than the minsup threshold.
			level = new ArrayList<Itemset>();
			for (Itemset candidate : candidatesK) {
				// if the support is > minsup
				if (candidate.getAbsoluteSupport() >= minsupRelative) {
					// add the candidate
					level.add(candidate);
					// the itemset is frequent so save it into results
					saveItemset(candidate);
				}
			}
			// we will generate larger itemsets next.
			k++;
		}while(level.isEmpty() == false);

		// record end time
		endTimestamp = System.currentTimeMillis();
		// check the memory usage
		MemoryLogger.getInstance().checkMemory();

		// close the output file if the result was saved to a file.
		if(writer != null){
			writer.close();
		}
		printStats();
		return patterns;
	}

	/**
	 * Return the number of transactions in the last database read by the algorithm.
	 * @return the number of transactions.
	 */
	public int getDatabaseSize() {
		return databaseSize;
	}

	/**
	 * This method generates candidates itemsets of size 2 based on
	 * itemsets of size 1.
	 * @param frequent1  the list of frequent itemsets of size 1.
	 * @return a List of Itemset that are the candidates of size 2.
	 */
	private List<Itemset> generateCandidate2(List<String> frequent1) {
		List<Itemset> candidates = new ArrayList<Itemset>();
		
		// For each itemset I1 and I2 of level k-1
		for (int i = 0; i < frequent1.size(); i++) {
			String item1 = frequent1.get(i);
			for (int j = i + 1; j < frequent1.size(); j++) {
				String item2 = frequent1.get(j);

				// Create a new candidate by combining itemset1 and itemset2
				candidates.add(new Itemset(new String []{item1, item2}));
			}
		}
		return candidates;
	}

	/**
	 * Method to generate itemsets of size k from frequent itemsets of size K-1.
	 * @param levelK_1  frequent itemsets of size k-1
	 * @return itemsets of size k
	 */
	protected List<Itemset> generateCandidateSizeK(List<Itemset> levelK_1) {
		// create a variable to store candidates
		List<Itemset> candidates = new ArrayList<Itemset>();

		// For each itemset I1 and I2 of level k-1
		loop1: for (int i = 0; i < levelK_1.size(); i++) {
			String[] itemset1 = levelK_1.get(i).itemset;
			loop2: for (int j = i + 1; j < levelK_1.size(); j++) {
				String[] itemset2 = levelK_1.get(j).itemset;

				// we compare items of itemset1 and itemset2.
				// If they have all the same k-1 items and the last item of
				// itemset1 is smaller than
				// the last item of itemset2, we will combine them to generate a
				// candidate
				for (int k = 0; k < itemset1.length; k++) {
					// if they are the last items
					if (k == itemset1.length - 1) {
						// the one from itemset1 should be smaller (lexical
						// order)
						// and different from the one of itemset2
						if (itemset1[k].compareTo(itemset2[k]) >= 0) {
							continue loop1;
						}
					}
					// if they are not the last items, and
					else if (itemset1[k].compareTo(itemset2[k]) < 0) {
						continue loop2; // we continue searching
					} else if (itemset1[k].compareTo(itemset2[k]) > 0) {
						continue loop1; // we stop searching: because of lexical
										// order
					}
				}

				// Create a new candidate by combining itemset1 and itemset2
				String newItemset[] = new String[itemset1.length+1];
				System.arraycopy(itemset1, 0, newItemset, 0, itemset1.length);
				newItemset[itemset1.length] = itemset2[itemset2.length -1];

				// The candidate is tested to see if its subsets of size k-1 are
				// included in
				// level k-1 (they are frequent).
				if (allSubsetsOfSizeK_1AreFrequent(newItemset, levelK_1)) {
					candidates.add(new Itemset(newItemset));
				}
			}
		}
		return candidates; // return the set of candidates
	}

	/**
	 * Method to check if all the subsets of size k-1 of a candidate of size k are freuqnet
	 * @param candidate a candidate itemset of size k
	 * @param levelK_1  the frequent itemsets of size k-1
	 * @return true if all the subsets are frequet
	 */
	protected boolean allSubsetsOfSizeK_1AreFrequent(String[] candidate, List<Itemset> levelK_1) {
		// generate all subsets by always each item from the candidate, one by one
		for(int posRemoved=0; posRemoved< candidate.length; posRemoved++){

			// perform a binary search to check if  the subset appears in  level k-1.
	        int first = 0;
	        int last = levelK_1.size() - 1;
	       
	        // variable to remember if we found the subset
	        boolean found = false;
	        // the binary search
	        while( first <= last )
	        {
	        	int middle = ( first + last ) / 2;

	            if(sameAs(levelK_1.get(middle), candidate, posRemoved)  < 0 ){
	            	first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
	            }
	            else if(sameAs(levelK_1.get(middle), candidate, posRemoved)  > 0 ){
	            	last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
	            }
	            else{
	            	found = true; //  we have found it so we stop
	                break;
	            }
	        }

			if(found == false){  // if we did not find it, that means that candidate is not a frequent itemset because
				// at least one of its subsets does not appear in level k-1.
				return false;
			}
		}
		return true;
	}

	
/**
 * Method to compare two itemsets and see if they are the same
 * @param itemset an itemset
 * @param candidate a candidate itemset
 * @param posRemoved  the position of an item that should be removed from "candidate" to perform the comparison.
 * @return 0 if they are the same, 1 if itemset is larger according to lexical order,
 *         -1 if smaller.
 */
private int sameAs(Itemset itemset, String [] candidate, int posRemoved) {
		// a variable to know which item from candidate we are currently searching
		int j=0;
		// loop on items from "itemset"
		for(int i=0; i<itemset.itemset.length; i++){
			// if it is the item that we should ignore, we skip it
			if(j == posRemoved){
				j++;
			}
			// if we found the item j, we will search the next one
			if(itemset.itemset[i].equalsIgnoreCase(candidate[j])){
				j++;
			// if  the current item from i is larger than j, 
		    // it means that "itemset" is larger according to lexical order
			// so we return 1
			}else if (itemset.itemset[i].compareTo(candidate[j]) > 0){
				return 1;
			}else{
				// otherwise "itemset" is smaller so we return -1.
				return -1;
			}
		}
		return 0;
	}
	

	 void saveItemset(Itemset itemset) throws IOException {
		itemsetCount++;
		
		// if the result should be saved to a file
		if(writer != null){
			writer.write(itemset.toString() + " #SUP: "
					+ itemset.getAbsoluteSupport());
			writer.newLine();
			frequentItemsets.add(itemset.toString() + " #SUP: "
					+ itemset.getAbsoluteSupport());
		}// otherwise the result is kept into memory
		else{
			patterns.addItemset(itemset, itemset.size());
		}
	}
	
	 void saveItemsetToFile(String item, Integer support) throws IOException {
		itemsetCount++;
		
		// if the result should be saved to a file
		if(writer != null){
			writer.write(item + " #SUP: " + support);
			writer.newLine();
			frequentItemsets.add(item + " #SUP: " + support);
		}// otherwise the result is kept into memory
		else{
			Itemset itemset = new Itemset(item);
			itemset.setAbsoluteSupport(support);
			patterns.addItemset(itemset, 1);
		}
	}
	public void SaveStats() throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("APRIORIStats", "UTF-8");
		writer.println("=============  APRIORI - STATS =============");
		writer.println(" Candidates count : " + totalCandidateCount);
		writer.println(" The algorithm stopped at size " + (k - 1)
				+ ", because there is no candidate");
		writer.println(" Frequent itemsets count : " + itemsetCount);
		writer.println(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
		writer.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		writer.println("===================================================");
		writer.close();
	
	}
	/**
	 * Print statistics about the algorithm execution to System.out.
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public void printStats() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("=============  APRIORI - STATS =============");
		System.out.println(" Candidates count : " + totalCandidateCount);
		System.out.println(" The algorithm stopped at size " + (k - 1)
				+ ", because there is no candidate");
		System.out.println(" Frequent itemsets count : " + itemsetCount);
		System.out.println(" Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println("===================================================");
		SaveStats();
	}
	
	public ArrayList<String> getFrequentItemsets() {
		return frequentItemsets;
	}
}

package edu.georgiasouthern.Datamining;
 
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 


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

public class AlgoFPGrowth {

	// for statistics
	private long startTimestamp; // start time of the latest execution
	private long endTime; // end time of the latest execution
	private int transactionCount = 0; // transaction count in the database
	private int itemsetCount; // number of freq. itemsets found
	
	// A memory representation of the database.
	// Each position in the list represents a transaction
	private List<String[]> database = null;
	
	// parameter
	public double relativeMinsupp;// the relative minimum support
	
	BufferedWriter writer = null; // object to write the output file
	
	// The  patterns that are found 
	// (if the user want to keep them into memory)
	protected Itemsets patterns = null;
	
	
	private MemoryLogger memoryLogger = null;
        
    ArrayList<String> frequentItemsets = new ArrayList<String>();

	/**
	 * Constructor
	 */
	public AlgoFPGrowth() {
		
	}

	/**
	 * Method to run the FPGRowth algorithm.
	 * @param input the path to an input file containing a transaction database.
	 * @param output the output file path for saving the result (if null, the result 
	 *        will be returned by the method instead of being saved).
	 * @param minsupp the minimum support threshold.
	 * @return the result if no output file path is provided.
	 * @throws IOException exception if error reading or writing files
	 */
        public Itemsets runAlgorithm(DataSet pm25, double minsup, String input, String output) throws IOException {
		// record start time
		startTimestamp = System.currentTimeMillis();
		// number of itemsets found
		itemsetCount =0;
		
		//initialize tool to record memory usage
		memoryLogger = new MemoryLogger();
		memoryLogger.checkMemory();
		
		// if the user want to keep the result into memory
		if(output == null){
			writer = null;
			patterns =  new Itemsets("FREQUENT ITEMSETS");
	    }else{ // if the user want to save the result to a file
			patterns = null;
			writer = new BufferedWriter(new FileWriter("../" + output)); 
		}
		
		ArrayList<GisData> data = pm25.getData();
		ArrayList<String[]> otherData = pm25.getOtherData();

		// READ THE INPUT FILE
		// variable to count the number of transactions
		transactionCount = pm25.getDataCount();
		String fileType = pm25.getFileType();
		
		// (1) PREPROCESSING: Initial database scan to determine the frequency of each item
		// The frequency is stored in a map:
		//    key: item   value: support
		final Map<String, Integer> mapSupport = new HashMap<String, Integer>();
		database = new ArrayList<String[]>(); // the database in memory (intially empty)
		
		// for each line (transactions) until the end of the data
		// count the support of each single item
		if(fileType.equalsIgnoreCase("GIS")) {
			for(int i = 0; i < transactionCount; i++)
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
				count = mapSupport.get(item);
				if(count == null) {
					mapSupport.put(item, 1);
				} else {
					mapSupport.put(item, ++count);
				}
					
				transaction[1] = x;
				item = new String(transaction[1]);
				count = mapSupport.get(item);
				if(count == null) {
					mapSupport.put(item, 1);
				} else {
					mapSupport.put(item, ++count);
				}
					
				transaction[2] = y;
				item = new String(transaction[2]);
				count = mapSupport.get(item);
				if(count == null) {
					mapSupport.put(item, 1);
				} else {
					mapSupport.put(item, ++count);
				}
					
				transaction[3] = measurement;
				item = new String(transaction[3]);
				count = mapSupport.get(item);
				if(count == null) {
					mapSupport.put(item, 1);
				} else {
					mapSupport.put(item, ++count);
				}
					
				switch(domain)
				{
				case Y:
					GisDateY aDateY = (GisDateY)row.getDate();
					year = Integer.valueOf(aDateY.getYear()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				case YM:
					GisDateYM aDateYM = (GisDateYM)row.getDate();
					year = Integer.valueOf(aDateYM.getYear()).toString();
					month = Integer.valueOf(aDateYM.getMonth()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
						
					transaction[5] = month;
					item = new String(transaction[5]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
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
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
						
					transaction[5] = month;
					item = new String(transaction[5]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
						
					transaction[6] = day;
					item = new String(transaction[6]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + month + "\t" + day + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				case YQ:
					GisDateYQ aDateYQ = (GisDateYQ)row.getDate();
					year = Integer.valueOf(aDateYQ.getYear()).toString();
					quarter = Integer.valueOf(aDateYQ.getQuarter()).toString();
					transaction[4] = year;
					item = new String(transaction[4]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
						
					transaction[5] = quarter;
					item = new String(transaction[5]);
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
					//System.out.print(id + "\t" + year + "\t" + quarter + "\t" + x + "\t" + y + "\t" + measurement);
					break;
				case OTHER:
					break;
				}
				
				// add the transaction to the database
				database.add(transaction);
			}
		}
		else {
			for(int i = 0; i < transactionCount; i++)
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
					count = mapSupport.get(item);
					if(count == null) {
						mapSupport.put(item, 1);
					} else {
						mapSupport.put(item, ++count);
					}
				}
				// add the transaction to the database
				database.add(transaction);		
			}
		}
		
		//scanDatabaseToDetermineFrequencyOfSingleItems(input, mapSupport);
		System.out.println("Starting the FPGrowth Algorithm!!!");
		
		// convert the minimum support as percentage to a
		// relative minimum support
		//this.relativeMinsupp = (int) Math.ceil(minsup * transactionCount);
		this.relativeMinsupp = minsup;
		
		System.out.println("Minimum Support Threshold = " + this.relativeMinsupp);
		
		// (2) Scan the database again to build the initial FP-Tree
		// Before inserting a transaction in the FPTree, we sort the items
		// by descending order of support.  We ignore items that
		// do not have the minimum support.
		FPTree tree = new FPTree();
		
		// for each line (transaction) until the end of the database
		for(int i = 0; i < database.size(); i++){
			List<String> transaction = new ArrayList<String>();
			String[] transactions = database.get(i);
			// for each item in the transaction
			for(int j = 0; j < transactions.length; j++){ 
				String item = transactions[j];
				// only add items that have the minimum support
				if( //alreadySeen.contains(item)  == false  &&
						mapSupport.get(item) >= relativeMinsupp){
					transaction.add(item);
					//alreadySeen.add(item);
				}
			}
			// sort item in the transaction by descending order of support
			Collections.sort(transaction, new Comparator<String>(){
				public int compare(String item1, String item2){
					// compare the frequency
					int compare = mapSupport.get(item2) - mapSupport.get(item1);
					// if the same frequency, we check the lexical ordering!
					if(compare == 0){ 
						//return (item1 - item2);
						return item1.compareTo(item2);
					}
					// otherwise, just use the frequency
					return compare;
				}
			});
			// add the sorted transaction to the fptree.
			tree.addTransaction(transaction);
		}
		
		// We create the header table for the tree
		tree.createHeaderList(mapSupport);
		
		// (5) We start to mine the FP-Tree by calling the recursive method.
		// Initially, the prefix alpha is empty.
		String[] prefixAlpha = new String[0];
		fpgrowth(tree, prefixAlpha, transactionCount, mapSupport);
		
		// close the output file if the result was saved to a file
		if(writer != null){
			writer.close();
		}
		// record the execution end time
		endTime= System.currentTimeMillis();
		
		// check the memory usage
		memoryLogger.checkMemory();
		
		// return the result (if saved to memory)
		return patterns;
	}

	/**
	 * This method scans the input database to calculate the support of single items
	 * @param input the path of the input file
	 * @param mapSupport a map for storing the support of each item (key: item, value: support)
	 * @throws IOException  exception if error while writing the file
	 */
    /*
	private void scanDatabaseToDetermineFrequencyOfSingleItems(String input,
			final Map<String, Integer> mapSupport)
			throws FileNotFoundException, IOException {
		//Create object for reading the input file
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
		// for each line (transaction) until the end of file
		while( ((line = reader.readLine())!= null)){ 
			// if the line is  a comment, is  empty or is a
			// kind of metadata
			if (line.isEmpty() == true ||
					line.charAt(0) == '#' || line.charAt(0) == '%'
							|| line.charAt(0) == '@') {
				continue;
			}
			
			// split the line into items
			String[] lineSplited = line.split(" ");
			// for each item
			for(String itemString : lineSplited){  
				// increase the support count of the item
				String item = itemString;
				// increase the support count of the item
				Integer count = mapSupport.get(item);
				if(count == null){
					mapSupport.put(item, 1);
				}else{
					mapSupport.put(item, ++count);
				}
			}
			// increase the transaction count
			transactionCount++;
		}
		// close the input file
		reader.close();
	}
*/

	/**
	 * This method mines pattern from a Prefix-Tree recursively
	 * @param tree  The Prefix Tree
	 * @param prefix  The current prefix "alpha"
	 * @param mapSupport The frequency of each item in the prefix tree.
	 * @throws IOException  exception if error writing the output file
	 */
	private void fpgrowth(FPTree tree, String[] prefixAlpha, int prefixSupport, Map<String, Integer> mapSupport) throws IOException {
		// We need to check if there is a single path in the prefix tree or not.
		if(tree.hasMoreThanOnePath == false){
			// That means that there is a single path, so we 
			// add all combinations of this path, concatenated with the prefix "alpha", to the set of patterns found.
			addAllCombinationsForPathAndPrefix(tree.root.childs.get(0), prefixAlpha); // CORRECT?
			
		}else{ // There is more than one path
			fpgrowthMoreThanOnePath(tree, prefixAlpha, prefixSupport, mapSupport);
		}
	}
	
	/**
	 * Mine an FP-Tree having more than one path.
	 * @param tree  the FP-tree
	 * @param prefix  the current prefix, named "alpha"
	 * @param mapSupport the frequency of items in the FP-Tree
	 * @throws IOException  exception if error writing the output file
	 */
	private void fpgrowthMoreThanOnePath(FPTree tree, String [] prefixAlpha, int prefixSupport, Map<String, Integer> mapSupport) throws IOException {
		// For each frequent item in the header table list of the tree in reverse order.
		for(int i= tree.headerList.size()-1; i>=0; i--){
			// get the item
			String item = tree.headerList.get(i);
			
			// get the support of the item
			int support = mapSupport.get(item);
			// if the item is not frequent, we skip it
			if(support <  relativeMinsupp){
				continue;
			}
			// Create Beta by concatening Alpha with the current item
			// and add it to the list of frequent patterns
			String [] beta = new String[prefixAlpha.length+1];
			System.arraycopy(prefixAlpha, 0, beta, 0, prefixAlpha.length);
			beta[prefixAlpha.length] = item;
			
			// calculate the support of beta
			int betaSupport = (prefixSupport < support) ? prefixSupport: support;
			// save beta to the output file
			saveItemset(beta, betaSupport);
			
			// === Construct beta's conditional pattern base ===
			// It is a subdatabase which consists of the set of prefix paths
			// in the FP-tree co-occuring with the suffix pattern.
			List<List<FPNode>> prefixPaths = new ArrayList<List<FPNode>>();
			FPNode path = tree.mapItemNodes.get(item);
			while(path != null){
				// if the path is not just the root node
				if(!path.parent.itemID.equalsIgnoreCase("-1")){
					// create the prefixpath
					List<FPNode> prefixPath = new ArrayList<FPNode>();
					// add this node.
					prefixPath.add(path);   // NOTE: we add it just to keep its support,
					// actually it should not be part of the prefixPath
					
					//Recursively add all the parents of this node.
					FPNode parent = path.parent;
					while(!parent.itemID.equalsIgnoreCase("-1")){
						prefixPath.add(parent);
						parent = parent.parent;
					}
					// add the path to the list of prefixpaths
					prefixPaths.add(prefixPath);
				}
				// We will look for the next prefixpath
				path = path.nodeLink;
			}
			
			// (A) Calculate the frequency of each item in the prefixpath
			// The frequency is stored in a map such that:
			// key:  item   value: support
			Map<String, Integer> mapSupportBeta = new HashMap<String, Integer>();
			// for each prefixpath
			for(List<FPNode> prefixPath : prefixPaths){
				// the support of the prefixpath is the support of its first node.
				int pathCount = prefixPath.get(0).counter;  
				 // for each node in the prefixpath,
				// except the first one, we count the frequency
				for(int j=1; j<prefixPath.size(); j++){ 
					FPNode node = prefixPath.get(j);
					// if the first time we see that node id
					if(mapSupportBeta.get(node.itemID) == null){
						// just add the path count
						mapSupportBeta.put(node.itemID, pathCount);
					}else{
						// otherwise, make the sum with the value already stored
						mapSupportBeta.put(node.itemID, mapSupportBeta.get(node.itemID) + pathCount);
					}
				}
			}
			
			// (B) Construct beta's conditional FP-Tree
			// Create the tree.
			FPTree treeBeta = new FPTree();
			// Add each prefixpath in the FP-tree.
			for(List<FPNode> prefixPath : prefixPaths){
				treeBeta.addPrefixPath(prefixPath, mapSupportBeta, relativeMinsupp); 
			}  
			// Create the header list.
			treeBeta.createHeaderList(mapSupportBeta); 
			
			// Mine recursively the Beta tree if the root as child(s)
			if(treeBeta.root.childs.size() > 0){
				// recursive call
				fpgrowth(treeBeta, beta, betaSupport, mapSupportBeta);
			}
		}
		
	}

	/**
	 * This method is for adding recursively all combinations of nodes in a path, concatenated with a given prefix,
	 * to the set of patterns found.
	 * @param nodeLink the first node of the path
	 * @param prefix  the prefix
	 * @param minsupportForNode the support of this path.
	 * @throws IOException exception if error while writing the output file
	 */
	private void addAllCombinationsForPathAndPrefix(FPNode node, String[] prefix) throws IOException {
		// Concatenate the node item to the current prefix
		String [] itemset = new String[prefix.length+1];
		System.arraycopy(prefix, 0, itemset, 0, prefix.length);
		itemset[prefix.length] = node.itemID;

		// save the resulting itemset to the file with its support
		saveItemset(itemset, node.counter);
		
		// recursive call if there is a node link
//		if(node.nodeLink != null){
//			addAllCombinationsForPathAndPrefix(node.nodeLink, prefix);
		if(node.childs.size() != 0) {
			addAllCombinationsForPathAndPrefix(node.childs.get(0), itemset);
			addAllCombinationsForPathAndPrefix(node.childs.get(0), prefix);
		}
	}

	/**
	 * Write a frequent itemset that is found to the output file or
	 * keep into memory if the user prefer that the result be saved into memory.
	 */
	private void saveItemset(String [] itemset, int support) throws IOException {
		// increase the number of itemsets found for statistics purpose
		itemsetCount++;
		
		// We sort the itemset before showing it to the user so that it is
		// in lexical order.
		Arrays.sort(itemset);
		
		// if the result should be saved to a file
		if(writer != null){
			// Create a string buffer
			StringBuffer buffer = new StringBuffer();
			// write the items of the itemset
			for(int i=0; i< itemset.length; i++){
				buffer.append(itemset[i]);
				if(i != itemset.length-1){
					buffer.append(' ');
				}
			}
			// Then, write the support
			buffer.append(" #SUP: ");
			buffer.append(support);
			// write to file and create a new line
			writer.write(buffer.toString());
			writer.newLine();
			frequentItemsets.add(buffer.toString());
		}// otherwise the result is kept into memory
		else{
			// create an object Itemset and add it to the set of patterns 
			// found.
			Itemset itemsetObj = new Itemset(itemset);
			itemsetObj.setAbsoluteSupport(support);
			patterns.addItemset(itemsetObj, itemsetObj.size());
		}
	}
	
	public void SaveStats() throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("FPGrowthStats", "UTF-8");
		writer.println("=============  FP-GROWTH - STATS =============");
		long temps = endTime - startTimestamp;
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
		System.out
				.println("=============  FP-GROWTH - STATS =============");
		long temps = endTime - startTimestamp;
		System.out.println(" Transactions count from database : " + transactionCount);
		System.out.print(" Max memory usage: " + memoryLogger.getMaxMemory() + " mb \n");
		System.out.println(" Frequent itemsets count : " + itemsetCount); 
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
		
		stats.add("=============  FP-GROWTH - STATS =============");
		long temps = endTime - startTimestamp;
		stats.add(" Transactions count from database : " + transactionCount);
		stats.add(" Max memory usage: " + memoryLogger.getMaxMemory() + " mb \n");
		stats.add(" Frequent itemsets count : " + itemsetCount); 
		stats.add(" Total time ~ " + temps + " ms");
		stats.add("===================================================");
		
		return stats;
	}

	/**
	 * Get the number of transactions in the last transaction database read.
	 * @return the number of transactions.
	 */
	public int getDatabaseSize() {
		return transactionCount;
	}
        
        public ArrayList<String> getFrequentItemsets() {
		return frequentItemsets;
	}
}

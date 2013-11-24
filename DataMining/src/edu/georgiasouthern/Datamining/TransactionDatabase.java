package edu.georgiasouthern.Datamining;
     
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.GisDateY;
import edu.georgiasouthern.common.GisDateYM;
import edu.georgiasouthern.common.GisDateYMD;
import edu.georgiasouthern.common.GisDateYQ;

/**
 * This class represents a transaction database (a.k.a. binary context), implemented
 * as a list of Strings. It can read a transaction database directly from a file. 
 */
public class TransactionDatabase {

	// The list of items in this database
	private final Set<String> items = new HashSet<String>();
	// the list of transactions
	private final List<List<String>> transactions = new ArrayList<List<String>>();
	private int databaseSize = 0;

	/**
	 * Method to add a new transaction to this database.
	 * @param transaction  the transaction to be added
	 */
	public void addTransaction(List<String> transaction) {
		transactions.add(transaction);
		items.addAll(transaction);
	}
 
	/**
	 * Method to load the transaction data.
	 * @param path the path of the file
	 * @throws IOException exception if error reading the file
	 */
	public void loadTransactionData(DataSet pm25) throws IOException {
		ArrayList<GisData> data = pm25.getData();
		ArrayList<String[]> otherData = pm25.getOtherData();
		databaseSize = pm25.getDataCount();
		String fileType = pm25.getFileType();
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
					
					
						// store the GIS data in the memory representation of the database
						transaction[0] = id;
						transaction[1] = x;
						transaction[2] = y;
						transaction[3] = measurement;
					
						switch(domain)
						{
						case Y:
							GisDateY aDateY = (GisDateY)row.getDate();
							year = Integer.valueOf(aDateY.getYear()).toString();
							transaction[4] = year;
							//System.out.print(id + "\t" + year + "\t" + x + "\t" + y + "\t" + measurement);
							break;
						case YM:
							GisDateYM aDateYM = (GisDateYM)row.getDate();
							year = Integer.valueOf(aDateYM.getYear()).toString();
							month = Integer.valueOf(aDateYM.getMonth()).toString();
							transaction[4] = year;
							transaction[5] = month;
							//System.out.print(id + "\t" + year + "\t" + month + "\t" + x + "\t" + y + "\t" + measurement);
							break;
						case YMD:
							GisDateYMD aDateYMD = (GisDateYMD)row.getDate();
							year = Integer.valueOf(aDateYMD.getYear()).toString();
							month = Integer.valueOf(aDateYMD.getMonth()).toString();
							day = Integer.valueOf(aDateYMD.getDay()).toString();
							transaction[4] = year;
							transaction[5] = month;
							transaction[6] = day;
							//System.out.print(id + "\t" + year + "\t" + month + "\t" + day + "\t" + x + "\t" + y + "\t" + measurement);
							break;
						case YQ:
							GisDateYQ aDateYQ = (GisDateYQ)row.getDate();
							year = Integer.valueOf(aDateYQ.getYear()).toString();
							quarter = Integer.valueOf(aDateYQ.getQuarter()).toString();
							transaction[4] = year;
							transaction[5] = quarter;
							//System.out.print(id + "\t" + year + "\t" + quarter + "\t" + x + "\t" + y + "\t" + measurement);
							break;
						case OTHER:
							break;
						}
					
						// add the transaction to the database
						addTransaction(transaction);
					
						//System.out.println();
					}
				}
				else {
					for(int i = 0; i < databaseSize; i++)
					{
						// get the transaction data
						String transaction[] = otherData.get(i);

						// add the transaction to the database
						addTransaction(transaction);
						
					}
				}
	}

	/**
	 * This method process a line from a file that is read.
	 * @param tokens the items contained in this line
	 */
	private void addTransaction(String itemsString[]) {
		// create an empty transaction
		List<String> itemset = new ArrayList<String>();
		// for each item in this line
		for (String attribute : itemsString) {
			String item = attribute;
			// add the item to the current transaction
			itemset.add(item); 
			// add the item to the set of all items in this database
			items.add(item);
		}
		// add the transactions to the list of all transactions in this database.
		transactions.add(itemset);
	}

	/**
	 * Method to print the content of the transaction database to the console.
	 */
	public void printDatabase() {
		System.out
				.println("===================  TRANSACTION DATABASE ===================");
		int count = 0; 
		// for each transaction
		for (List<String> itemset : transactions) { // pour chaque objet
			System.out.print("0" + count + ":  ");
			print(itemset); // print the transaction 
			System.out.println("");
			count++;
		}
	}
	
	/**
	 * Method to print a transaction to System.out.
	 * @param itemset a transaction
	 */
	private void print(List<String> itemset){
		StringBuffer r = new StringBuffer();
		// for each item in this transaction
		for (String item : itemset) {
			// append the item to the stringbuffer
			r.append(item);
			r.append(' ');
		}
		System.out.println(r); // print to System.out
	}

	/**
	 * Get the number of transactions in this transaction database.
	 * @return the number of transactions.
	 */
	public int size() {
		return databaseSize;
	}

	/**
	 * Get the list of transactions in this database
	 * @return A list of transactions (a transaction is a list of Strings).
	 */
	public List<List<String>> getTransactions() {
		return transactions;
	}

	/**
	 * Get the set of items contained in this database.
	 * @return The set of items.
	 */
	public Set<String> getItems() {
		return items;
	}
}

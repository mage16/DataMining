package edu.georgiasouthern.gui;
   
import java.applet.Applet;

import java.awt.TextField;

import edu.georgiasouthern.common.DataSet;
import edu.georgiasouthern.common.GisData;
import edu.georgiasouthern.common.GisDate;
import edu.georgiasouthern.common.DataSet.FileDelimiter;
import edu.georgiasouthern.common.GisDate.DateDomain;
import edu.georgiasouthern.common.LocationDataSet;
import edu.georgiasouthern.fileio.FileIO;
import edu.georgiasouthern.interpolation.ConcurrentInterpolationSec;
import edu.georgiasouthern.validation.LOOCV;
import edu.georgiasouthern.visualization.AnimTest;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import java.awt.Choice;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import edu.georgiasouthern.Datamining.*;
public class Gui extends Applet {
	public Gui() {
	}

	String FileFormat;
	GisDate.DateDomain DateFormat;
	JLabel lblDataFileName;
	JLabel lblInterFileName;
	DataSet dataSet;
	LocationDataSet locationDataSet;
	JTextPane txtDataSet;
	JTextPane txtLocationDataSet;
	JTable dsTable;
	JTable ldsTable;
	JLabel lblErrors;
	private JTextField frequentItemsetFileName;
	String[] algorithms = {"Apriori", "FP-Growth", "Eclat", "A-Close", "Closet+", "CHARM"};
	JComboBox algorithmList;
	ArrayList<String> frequentItemsets = null;
	
	public void init() {

		setSize(500, 500);
		setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 500, 95);
		add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Data File:");
		lblNewLabel.setBounds(10, 4, 154, 14);
		panel.add(lblNewLabel);

		JButton btnFileDialog = new JButton("...");
		btnFileDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File f = getFileName();
				if (f != null) {
					OpenFileOptions();
					System.out.println("File Format" + FileFormat);
					System.out.println("Date Format" + DateFormat);
					
					switch(DateFormat)
					{
						case OTHER:
							Gui.this.dataSet = new DataSet(f.getAbsolutePath(),
									DataSet.FileDelimiter.valueOf(FileFormat));
							break;
						default:
							Gui.this.dataSet = new DataSet(f.getAbsolutePath(),
									DateFormat, DataSet.FileDelimiter
											.valueOf(FileFormat));
							break;
					}

					if(Gui.this.dataSet.getDataCount()<1){
						Gui.this.dataSet = null;
						lblDataFileName.setText("No File Selected");
					}else{
						lblDataFileName.setText(f.getName());
						dsTable.setModel(TableDs());
						lblErrors.setText("");
					}

				}
			}
		});
		btnFileDialog.setBounds(145, 0, 38, 23);
		panel.add(btnFileDialog);

		lblDataFileName = new JLabel("No File Selected");
		lblDataFileName.setBounds(200, 4, 300, 14);
		panel.add(lblDataFileName);

		JLabel lblFrequentItemsetFile = new JLabel("Frequent-Itemset File:");
		lblFrequentItemsetFile.setBounds(10, 29, 154, 14);
		panel.add(lblFrequentItemsetFile);
		
		frequentItemsetFileName = new JTextField();
		frequentItemsetFileName.setText("frequent_itemset.txt");
		frequentItemsetFileName.setBounds(145, 29, 154, 20);
		panel.add(frequentItemsetFileName);
		frequentItemsetFileName.setColumns(10);

		
		lblErrors = new JLabel("");
		lblErrors.setBounds(1,45,499,23);
		panel.add(lblErrors);
		
		JLabel lblSelectAlgorithm = new JLabel("Select Algorithm:");
		lblSelectAlgorithm.setBounds(10, 54, 154, 14);
		panel.add(lblSelectAlgorithm);

		algorithmList = new JComboBox(algorithms);
		algorithmList.setSelectedIndex(0);
		algorithmList.setBounds(145, 54, 154, 20);
		panel.add(algorithmList);

		JButton btnFrequentItemsets = new JButton("Frequent-Itemsets!");
		btnFrequentItemsets.setBounds(340, 40, 154, 23);
		btnFrequentItemsets.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
			
				final JDialog dialog2 = new JDialog();
				dialog2.setBounds(0, 0, 300,300);
				dialog2.getContentPane().setLayout(new FlowLayout());
				if(dataSet==null){
					dialog2.getContentPane().add(new JLabel("You must select a dataset file first"));
					JButton jb = new JButton("OK");
					jb.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					dialog2.setVisible(false);
						
					}
				});
					dialog2.getContentPane().add(jb);
					dialog2.setVisible(true);
				}
				else if(algorithmList.getSelectedIndex() == 4 || algorithmList.getSelectedIndex() == 5) {
					dialog2.getContentPane().add(new JLabel("Algorithm not implemented yet."));
					JButton jb = new JButton("OK");
					jb.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					dialog2.setVisible(false);
						
					}
				});
					dialog2.getContentPane().add(jb);
					dialog2.setVisible(true);
				}else{
					dialog2.getContentPane().add(new JLabel("Please Enter the minimum support threshold."));					
					// 
					JPanel pnlExpNei = new JPanel(new FlowLayout());
					pnlExpNei.setBounds(0,0,300,50);
					
					pnlExpNei.add(new JLabel("Minimum Support: "));
					final JTextField minsup = new JTextField("2.0     ");
					pnlExpNei.add(minsup);
					dialog2.getContentPane().add(pnlExpNei);
					//
					
					
					dialog2.setResizable(false);
					dialog2.setModal(true);
					final JButton jb = new JButton("OK");
					jb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						final Double minsupport = Double.parseDouble(minsup.getText().trim());
						Thread interThread = new Thread(new Runnable() {
						    public void run() {
						    	dialog2.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						    	jb.setEnabled(false);
						       runAlgorithm(dialog2,minsupport);
						    }}); 
						
						interThread.start();
						
					}
				});
					
					dialog2.getContentPane().add(jb);
					dialog2.getContentPane().add(new ConsoleWindow());
					dialog2.setVisible(true);
					

				}
				
			}
			
		});
	 
		panel.add(btnFrequentItemsets);
		
		
		
				JPanel panel_1 = new JPanel();
				panel_1.setBounds(0, 96, 495, 400);
				add(panel_1);
				panel_1.setLayout(null);
				
						dsTable = new JTable(TableDs());
						dsTable.setBounds(-3, -2, 0, 393);
						panel_1.add(dsTable);
						
								ldsTable = new JTable(TableLds());
								ldsTable.setBounds(-3, -2, 0, 393);
								panel_1.add(ldsTable);
								
										JScrollPane scrollPane = new JScrollPane(dsTable);
										scrollPane.setBounds(0, 22, 240, 370);
										panel_1.add(scrollPane);
										scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
										
												JScrollPane scrollPane_1 = new JScrollPane(ldsTable);
												scrollPane_1.setBounds(245, 22, 243, 373);
												panel_1.add(scrollPane_1);
												
														JLabel lblDataPane = new JLabel("Data Pane");
														lblDataPane.setBounds(0, 0, 129, 14);
														panel_1.add(lblDataPane);
														
																JLabel lblFrequentItemsetsPane = new JLabel("Frequent Itemsets Pane");
																lblFrequentItemsetsPane.setBounds(251, 0, 156, 14);
																panel_1.add(lblFrequentItemsetsPane);
		FileIO f = new FileIO();

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
	}

	private TableModel TableDs() {
		TableModel dsDataModel = new AbstractTableModel() {
			public int getColumnCount() {
				if(dataSet != null && dataSet.getFileType().equalsIgnoreCase("GIS"))
				{
					return 4;
				}
				else
				{
					return 1;
				}
			}

			public int getRowCount() {
				if (dataSet != null && dataSet.getData() != null && dataSet.getFileType().equalsIgnoreCase("GIS"))
					return dataSet.getData().size();
				else if(dataSet != null && dataSet.getOtherData() != null && dataSet.getFileType().equalsIgnoreCase("OTHER"))
					return dataSet.getOtherData().size();
				else
					return 0;
			}

			public Object getValueAt(int row, int col) {
				if (dataSet != null && dataSet.getData() != null && dataSet.getFileType().equalsIgnoreCase("GIS")) {
					switch (col) {
					case 0:
						return dataSet.getData().get(row).getX();
					case 1:
						return dataSet.getData().get(row).getY();
					case 2:
						return dataSet.getData().get(row).getDate();
					default:
						return dataSet.getData().get(row).getMeasurement();
					}
				}
				else if(dataSet != null && dataSet.getOtherData() != null && dataSet.getFileType().equalsIgnoreCase("OTHER"))
				{
					String[] transactions = dataSet.getOtherData().get(row);
					String data = "";
					for(int i = 0; i < transactions.length; i++)
					{
						if(i < transactions.length - 1)
						{
							data += transactions[i] + "     ";
						}
						else
							data += transactions[i];
					}
					return data;
				}
				else
					return null;
			}
		};
		return dsDataModel;
	}

	private TableModel TableLds() {

		TableModel ldsDataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return 1;
			}

			public int getRowCount() {
				//if (locationDataSet != null
				//		&& locationDataSet.getData() != null)
				//	return locationDataSet.getData().size();
				//else
				//	return 0;
				if(frequentItemsets != null) {
					return frequentItemsets.size();
				}
				else
					return 0;
			}

			public Object getValueAt(int row, int col) {
				/*
				if (locationDataSet != null
						&& locationDataSet.getData() != null) {
					switch (col) {
					case 0:
						return locationDataSet.getData().get(row).getX();
					case 1:
						return locationDataSet.getData().get(row).getY();
					case 2:
						return locationDataSet.getData().get(row).getDate();
					default:
						return locationDataSet.getData().get(row)
								.getMeasurement();
					}
					*/
				if(frequentItemsets != null) {
					return frequentItemsets.get(row);
				} else
					return null;
			}
		};
		return ldsDataModel;
	}

	private File getFileName() {
		JFileChooser fc = new JFileChooser();
		int val = fc.showOpenDialog(Gui.this);
		if (val == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else
			return null;
	}
 
	private void OpenFileOptions() {
		final JDialog dialog = new JDialog(
				SwingUtilities.windowForComponent(this));
		dialog.setModal(true);
		dialog.getContentPane().setLayout(null);
		dialog.setSize(300, 200);
		JLabel lblNewLabel_2 = new JLabel("Please Select the file format.");
		lblNewLabel_2.setBounds(0, 0, 200, 14);
		dialog.getContentPane().add(lblNewLabel_2);
		JLabel lblNewLabel_1 = new JLabel("Date Format");
		lblNewLabel_1.setBounds(10, 23, 75, 14);
		dialog.getContentPane().add(lblNewLabel_1);

		JComboBox cmbDate = new JComboBox(GisDate.DateDomain.values());
		cmbDate.setBounds(16, 36, 65, 20);
		// cmbDate.setModel(new DefaultComboBoxModel(new String[] {"Y", "YM",
		// "YMD", "YQ"}));
		DateFormat = (DateDomain) cmbDate.getSelectedItem();
		dialog.getContentPane().add(cmbDate);
		JRadioButton rdbComma = new JRadioButton("Comma");
		rdbComma.setBounds(87, 37, 75, 23);
		rdbComma.setSelected(true);
		FileFormat = "comma";
		rdbComma.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Gui.this.FileFormat = "comma";
			}

		});
		dialog.getContentPane().add(rdbComma);

		JRadioButton rdbTab = new JRadioButton("Tab");
		rdbTab.setBounds(170, 35, 61, 23);
		rdbTab.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Gui.this.FileFormat = "tab";
			}

		});
		dialog.getContentPane().add(rdbTab);
		ButtonGroup group = new ButtonGroup();
		group.add(rdbTab);
		group.add(rdbComma);

		JLabel lblFileFormat = new JLabel("File Delimiter");
		lblFileFormat.setBounds(95, 23, 83, 14);
		dialog.getContentPane().add(lblFileFormat);
		JButton btnDone = new JButton("Done.");
		btnDone.setBounds(0, 70, 80, 25);
		dialog.getContentPane().add(btnDone);
		btnDone.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.setVisible(false);
			}

		});

		dialog.setVisible(true);
		DateFormat = (DateDomain) cmbDate.getSelectedItem();

	}
	
public void runAlgorithm(JDialog dialog2, double minimumSupportThreshold){
	double minsup = minimumSupportThreshold;
	String input = dataSet.getDataSetFileName();
	String output = frequentItemsetFileName.getText();
	int selectedAlgorithm = algorithmList.getSelectedIndex();
	try {
		if(algorithms[selectedAlgorithm].equals("Apriori")){
			AlgoApriori Apriori = new AlgoApriori();
			Apriori.runAlgorithm(dataSet, minsup, input, output);
			frequentItemsets = Apriori.getFrequentItemsets();
			ldsTable.setModel(TableLds());
		}
		else if(algorithms[selectedAlgorithm].equals("FP-Growth")){
			// run the FP-Growth algorithm
			AlgoFPGrowth FPGrowth = new AlgoFPGrowth();
			FPGrowth.runAlgorithm(dataSet,  minsup,  input,  output);
			frequentItemsets = FPGrowth.getFrequentItemsets();
			ldsTable.setModel(TableLds());
		}
		else if(algorithms[selectedAlgorithm].equals("A-Close")){
			// run the A-Close algorithm
			AlgoAprioriClose AClose = new AlgoAprioriClose();
			AClose.runAlgorithm(dataSet,  minsup,  input,  output);
			frequentItemsets = AClose.getFrequentItemsets();
			ldsTable.setModel(TableLds());
		}
		else if(algorithms[selectedAlgorithm].equals("Eclat")){
			// run the Eclat algorithm
			AlgoEclat EClat = new AlgoEclat();
			TransactionDatabase database = new TransactionDatabase();
			database.loadTransactionData(dataSet);
			EClat.runAlgorithm(output, database, minsup);
			frequentItemsets = EClat.getFrequentItemsets();
			ldsTable.setModel(TableLds());
		}
	}
	catch(IOException e) {
		e.printStackTrace();
	}
	dialog2.setVisible(false);
}

}

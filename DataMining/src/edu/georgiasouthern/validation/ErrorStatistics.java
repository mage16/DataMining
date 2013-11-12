package edu.georgiasouthern.validation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class ErrorStatistics {
public double MAE, MSE, RMSE, MARE, MSRE, RMSRE;
public int N;
public double[] I;
public double[] O;
public ErrorStatistics(){

}
public ErrorStatistics(double[] I, double[] O){

double MAENum=0.0;
double MSENum=0.0;
double MARENum=0.0;
double MSRENum=0.0;
for (int i = 0;i<O.length; i++){
MAENum += Math.abs(I[i]-O[i]);
MSENum +=Math.pow(I[i]-O[i], 2.0);
MARENum += Math.abs(I[i]-O[i])/O[i];
MSRENum +=Math.pow(I[i]-O[i], 2.0)/O[i];
}
	MAE = MAENum/(O.length);
	MSE = MSENum/(O.length);
	RMSE = Math.sqrt(MSE);
	MARE = MARENum/(O.length);
	MSRE = MSRENum/(O.length);
	RMSRE = Math.sqrt(MSRE);
}
public String toStringRounded(){
	DecimalFormat df = new DecimalFormat("#.####");
	
	return "MAE:" + df.format(MAE) + " MSE:" + df.format(MSE) + " RMSE:"  + df.format(RMSE) +" MARE:" + df.format(MARE) + " MSRE:" + df.format(MSRE) + " RMSRE:"+ df.format(RMSRE);
}
public String toString(){
		return "MAE: " +MAE + " MSE: " + MSE + " RMSE: "  + RMSE +" MARE: " + MARE + " MSRE: " + MSRE + " RMSRE: "+ RMSRE;
}
public String toLine(String delimiter){
	return MAE + delimiter + MSE + delimiter + RMSE + delimiter+ MARE + delimiter + MSRE + delimiter+ RMSRE + "\r\n";
}
public String header(String delimiter){
	return "MAE" + delimiter + "MSE" + delimiter + "RMSE" + delimiter + "MARE" + delimiter + "MSRE" + delimiter + "RMSRE" + "\r\n";
}
public static ErrorStatistics Average(ErrorStatistics[] errors ){
	ErrorStatistics errorOut = new ErrorStatistics();
	for (int i=0;i<errors.length;i++){
		errorOut.MAE+=errors[i].MAE;
		errorOut.MARE+=errors[i].MARE;
		errorOut.MSE+=errors[i].MSE;
		errorOut.MSRE+=errors[i].MSRE;
		errorOut.RMSE+=errors[i].RMSE;
		errorOut.RMSRE+=errors[i].RMSRE;
	}
	errorOut.MAE=errorOut.MAE/errors.length;
	errorOut.MARE=errorOut.MARE/errors.length;
	errorOut.MSE=errorOut.MSE/errors.length;
	errorOut.MSRE=errorOut.MSRE/errors.length;
	errorOut.RMSE=errorOut.RMSE/errors.length;
	errorOut.RMSRE=errorOut.RMSRE/errors.length;
	return errorOut;
}
public void writeErrorStatistics(String fileName, String delimiter, ErrorStatistics[] errors){
if(delimiter==null||delimiter.equals(""))delimiter = "\t";
	BufferedWriter writer = null;
	try {
		writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(this.header(delimiter));
		for (int i = 0; i<errors.length;i++){
			writer.write(errors[i].toLine(delimiter));
		}
		writer.flush();
		writer.close();
	} catch (IOException e) {
		e.printStackTrace();
	}

}
public void writeErrorStatistics (String fileName, String delimiter){
	if(delimiter==null||delimiter.equals(""))delimiter = "\t";
	BufferedWriter writer = null;
	try {
		writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(this.header(delimiter)+ this.toLine(delimiter));
		writer.flush();
		writer.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}


}

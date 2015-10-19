package edu.pitt.cs.main;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import edu.pitt.cs.analysis.GeneralAnalysis;
import edu.pitt.cs.io.ParseResultReader;
import edu.pitt.cs.io.TablePrinter;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.Table;

public class CommandMainAll {
	public static void main(String[] args) {
		Hashtable<String, String> parameters = new Hashtable<String, String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-")) {
				if (i > args.length - 1) {
					System.out.println("Parameter error");
					return;
				} else {
					String val = args[i + 1];
					i++;
					parameters.put(arg, val);
				}
			}
		}

		String input = "-i";
		String output = "-o";
		
		String inputPath = parameters.get(input);
		if (inputPath == null) {
			System.out.println("Input not specified");
			return;
		}
		try {
			List<ParseResultFile> results = ParseResultReader
					.readFiles(inputPath);
			Table table1 = GeneralAnalysis.statTable1(results);
			Table table2 = GeneralAnalysis.statTable1Merge(results);
			Table table3 = GeneralAnalysis.statTable2(results);
			String outputFolder = parameters.get(output);
			String outputPath1 = outputFolder + "/" + "pdtb1.csv";
			String outputPath2 = outputFolder + "/" + "pdtb1Merge.csv";
			String outputPath3 = outputFolder + "/" + "pdtb2.csv";
			File f = new File(outputPath1);
			if(!f.exists()) {
				f.getAbsoluteFile().getParentFile().mkdirs();
			} 
			TablePrinter.printCSV(table1, outputPath1);
			TablePrinter.printCSV(table2, outputPath2);
			TablePrinter.printCSV(table3, outputPath3);
		} catch(Exception exp) {
			exp.printStackTrace();
		}
	}
}

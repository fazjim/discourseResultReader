package edu.pitt.cs.main;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import edu.pitt.cs.analysis.GeneralAnalysis;
import edu.pitt.cs.io.ParseResultReader;
import edu.pitt.cs.io.TablePrinter;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.Table;

public class CommandMain {
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
		String tableOption = "-t";
		String pdtbOption = "-p";
		String output = "-o";

		String inputPath = parameters.get(input);
		if (inputPath == null) {
			System.out.println("Input not specified");
			return;
		}
		try {
			List<ParseResultFile> results = ParseResultReader
					.readFiles(inputPath);
			String tableOpValue = parameters.get(tableOption);
			String pdtbOpValue = parameters.get(pdtbOption);
			if (tableOption == null || pdtbOpValue == null) {
				System.out
						.println("Please specifiy the table index and pdtb analysis level, table index should be 1 or 2 or 3; pdtb should be 1 or 2 or m");
				return;
			}
			Table table = null;
			if (tableOption.equals("1") || tableOption.equals("2")
					|| tableOption.equals("3")) {
				if (pdtbOpValue.equals("1")) {
					if (tableOption.equals("1")) {
						table = GeneralAnalysis.analyzeTable1(results);
					} else if (tableOption.equals("2")) {
						table = GeneralAnalysis.analyzeTable2(results);
					} else if (tableOption.equals("3")) {
						table = GeneralAnalysis.analyzeTable3PDTB1(results);
					} else {
						System.out.println("Specify the right table index");
						return;
					}
				} else if (pdtbOpValue.equals("2")) {
					if (tableOption.equals("1")) {
						System.out
								.println("PDTB2 has to be merged to generate Table 1, use '-p m' instead");
						return;
					} else if (tableOption.equals("2")) {
						table = GeneralAnalysis.analyzeTable2PDTB2(results);
					} else if (tableOption.equals("3")) {
						table = GeneralAnalysis.analyzeTable3(results);
					} else {
						System.out.println("Specify the right table index");
						return;
					}
				} else if (pdtbOpValue.equals("M") || pdtbOpValue.equals("m")) {
					if (tableOption.equals("1")) {
						table = GeneralAnalysis.analyzeTable1PDTB2(results);
					} else if (tableOption.equals("2")) {
						table = GeneralAnalysis
								.analyzeTable2PDTB2Merge(results);
					} else if (tableOption.equals("3")) {
						table = GeneralAnalysis.analyzeTable3Merge(results);
					} else {
						System.out.println("Specify the right table index");
						return;
					}
				} else {
					System.out
							.println("Please specify the pdtb index, values should be 1, 2 or m");
				}
			} else {
				System.out.println("Please specify the right table index");
			}
			if(parameters.containsKey(output)) {
				String outputPath = parameters.get(output);
				File f = new File(outputPath);
				if(!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				} 
				if(f.getName().endsWith(".xlsx")) {
					TablePrinter.printExcel(table, outputPath);
				} else {
					TablePrinter.printFile(table, outputPath);
				}
			} else {
				TablePrinter.printScreen(table);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

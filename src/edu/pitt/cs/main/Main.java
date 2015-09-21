package edu.pitt.cs.main;

import java.util.List;
import java.util.Scanner;

import edu.pitt.cs.analysis.GeneralAnalysis;
import edu.pitt.cs.io.ParseResultReader;
import edu.pitt.cs.io.TablePrinter;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.Table;

/**
 * Main file for general analysis 
 * @author zhangfan
 *
 */
public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			try {
				System.out.println("Enter the folder:");
				String path =  sc.nextLine();
				List<ParseResultFile> results = ParseResultReader
						.readFiles(path);

				System.out.println("Files read! Select from the following options:");
				System.out.println("1. Generate Table 1");
				System.out.println("2. Generate Table 2");
				System.out.println("3. Generate Table 3");

				int option = sc.nextInt();
				Table table = null;
				if (option == 1) {
					table = GeneralAnalysis.analyzeTable1(results);
				} else if (option == 2) {
					table = GeneralAnalysis.analyzeTable2(results);
				} else if (option == 3) {
					table = GeneralAnalysis.analyzeTable3(results);
				}
				System.out.println("Analyze complete!");
				System.out.println("Enter your option:");
				System.out.println("1. Print to screen");
				System.out.println("2. Print to txt files");
				System.out.println("3. Print to excel files");

				int option2 = sc.nextInt();
				if (option2 == 1) {
					TablePrinter.printScreen(table);
				} else {
					System.out
							.println("Enter the path of output with the file extension");
					String filePath=null;
					while(filePath == null||filePath.trim().length()==0)
						filePath = sc.nextLine();
					System.out.println("ENTERED:"+filePath);
					if (option2 == 2)
						TablePrinter.printFile(table, filePath);
					else
						TablePrinter.printExcel(table, filePath);
				}
				System.out.println("Analysis END");
			} catch (Exception exp) {
				exp.printStackTrace();
				System.err.println("Please enter the right option");
			}
		}
		
	}
}

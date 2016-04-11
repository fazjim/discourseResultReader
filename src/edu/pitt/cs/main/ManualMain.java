package edu.pitt.cs.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.analysis.GeneralAnalysis;
import edu.pitt.cs.analysis.ManualGeneralAnalysis;
import edu.pitt.cs.io.ManualParseResultReader;
import edu.pitt.cs.io.TablePrinter;
import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.Table;

public class ManualMain {
	public static void main(String[] args) throws IOException {
		String manualFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
		List<ManualParseResultFile> manualResults = ManualParseResultReader
				.readFiles(manualFolderPath);
		List<ManualParseResultFile> d1 = new ArrayList<ManualParseResultFile>();
		Iterator<ManualParseResultFile> it = manualResults.iterator();
		while(it.hasNext()) {
			ManualParseResultFile m = it.next();
			if(m.getFileName().contains("draft1")) {
				d1.add(m);
				it.remove();
			}
		}
		Table table1 = ManualGeneralAnalysis.statTable1(d1);
		String outputFolder = "C:\\Not Backed Up";
		String outputPath1 = outputFolder + "/" + "pdtb1-draft1.csv";
		String outputPath2 = outputFolder + "/" + "pdtb1-draft2.csv";
		Table table2 = ManualGeneralAnalysis.statTable1(manualResults);
		File f = new File(outputPath1);
		if(!f.exists()) {
			f.getAbsoluteFile().getParentFile().mkdirs();
		} 
		
		
		TablePrinter.printCSV(table1, outputPath1);
		TablePrinter.printCSV(table2, outputPath2);
	}
}

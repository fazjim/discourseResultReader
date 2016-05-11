package edu.pitt.cs.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.analysis.GeneralAnalysis;
import edu.pitt.cs.analysis.ManualGeneralAnalysis;
import edu.pitt.cs.io.ManualParseResultReader;
import edu.pitt.cs.io.ModificationRemover;
import edu.pitt.cs.io.TablePrinter;
import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.PipeUnit;
import edu.pitt.cs.model.Table;

public class ManualMain {
	public static void processWithinSentence(ManualParseResultFile file) {
		Iterator<PipeUnit> it = file.getPipes().iterator();
		while(it.hasNext()) {
			PipeUnit unit = it.next();
			String arg1Sent = ModificationRemover.compressStr(unit.getRange1Txt());
			String arg2Sent = ModificationRemover.compressStr(unit.getRange2Txt());
			if(file.containsBothInOne(arg1Sent, arg2Sent)) {
				it.remove();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		String manualFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
		List<ManualParseResultFile> manualResults = ManualParseResultReader
				.readFiles(manualFolderPath);
		List<ManualParseResultFile> d1 = new ArrayList<ManualParseResultFile>();
		Iterator<ManualParseResultFile> it = manualResults.iterator();
		String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		
		
		while(it.hasNext()) {
			ManualParseResultFile m = it.next();
			ModificationRemover.feedTxtInfo(m, path);
			//processWithinSentence(m);
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

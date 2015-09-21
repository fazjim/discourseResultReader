package edu.pitt.cs.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.io.ParseResultReader;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeAttribute;
import edu.pitt.cs.model.PipeUnit;
import edu.pitt.cs.model.Table;

/**
 * General analysis
 * 
 * @author zhangfan
 *
 */
public class GeneralAnalysis {
	
	/**
	 * Analyze Table 1
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static Table analyzeTable1(List<ParseResultFile> files) throws IOException {
		Table table = new Table("Table 1");
		table.addColumn("PDTB Relations");
		table.addColumn("No. of Columns");

		//List<ParseResultFile> files = ParseResultReader.readFiles(folder);
		Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
		for (ParseResultFile file : files) {
			if (file.isPDTB1()) {
				List<PipeUnit> pipes = file.getPipes();
				for (PipeUnit pipe : pipes) {
					String relation = pipe.getAttr(PipeAttribute.RELATION_TYPE)
							.trim();
					int val = 0;
					if (counts.containsKey(relation)) {
						val = counts.get(relation);
					}
					val++;
					counts.put(relation, val);
				}
			}
		}

		Iterator<String> it = counts.keySet().iterator();
		while (it.hasNext()) {
			String type = it.next();
			int count = counts.get(type);
			List<String> attrs = new ArrayList<String>();
			attrs.add(type);
			attrs.add(Integer.toString(count));
			table.addRow(attrs);
		}
		return table;
	}
	
	public static Table analyzeTable2(List<ParseResultFile> files) {
		Table table = new Table("Table 2");
		table.addColumn("Level-1 PDTB Senses");
		table.addColumn("No. of explicits");
		table.addColumn("No. of implicits");
		
		Hashtable<String, Integer> explicitCounts = new Hashtable<String, Integer>();
		Hashtable<String, Integer> implicitCounts = new Hashtable<String, Integer>();
		for (ParseResultFile file : files) {
			if (file.isPDTB1()) {
				List<PipeUnit> pipes = file.getPipes();
				for (PipeUnit pipe : pipes) {
					String relation = pipe.getAttr(PipeAttribute.RELATION_TYPE)
							.trim();
					int val = 0;
					if (counts.containsKey(relation)) {
						val = counts.get(relation);
					}
					val++;
					counts.put(relation, val);
				}
			}
		}
	}
}

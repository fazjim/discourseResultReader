package edu.pitt.cs.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.io.ParseResultReader;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeAttribute;
import edu.pitt.cs.model.PipeUnit;
import edu.pitt.cs.model.Table;

/**
 * General analysis to generate the three tables
 * 
 * @author zhangfan
 *
 */
public class GeneralAnalysis {

	/**
	 * Analyze Table 1
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static Table analyzeTable1(List<ParseResultFile> files)
			throws IOException {
		Table table = new Table("Table 1");
		table.addColumn("PDTB Relations");
		table.addColumn("No. of Columns");

		// List<ParseResultFile> files = ParseResultReader.readFiles(folder);
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
			int count = 0;
			if (counts.containsKey(type))
				count = counts.get(type);
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

		HashSet<String> senses = new HashSet<String>();

		Hashtable<String, Integer> explicitCounts = new Hashtable<String, Integer>();
		Hashtable<String, Integer> implicitCounts = new Hashtable<String, Integer>();
		for (ParseResultFile file : files) {
			if (file.isPDTB1()) {
				List<PipeUnit> pipes = file.getPipes();
				for (PipeUnit pipe : pipes) {
					String relation = pipe.getAttr(PipeAttribute.RELATION_TYPE)
							.trim();
					String pdtbsense = pipe
							.getAttr(PipeAttribute.FIRST_SEMCLASS_CONN);
					if (pdtbsense != null) {
						senses.add(pdtbsense);
						if (relation.equals("Explicit")) {
							int val = 0;
							if (explicitCounts.containsKey(pdtbsense)) {
								val = explicitCounts.get(pdtbsense);
							}
							val++;
							explicitCounts.put(pdtbsense, val);
						} else if (relation.equals("Implicit")) {
							int val = 0;
							if (implicitCounts.containsKey(pdtbsense)) {
								val = implicitCounts.get(pdtbsense);
							}
							val++;
							implicitCounts.put(pdtbsense, val);
						}
					}
				}
			}
		}

		Iterator<String> it = senses.iterator();
		while (it.hasNext()) {
			String sense = it.next();
			int explicitCount = 0;
			if (explicitCounts.containsKey(sense))
				explicitCount = explicitCounts.get(sense);
			int implicitCount = 0;
			if (implicitCounts.containsKey(sense))
				implicitCount = implicitCounts.get(sense);
			List<String> attrs = new ArrayList<String>();
			attrs.add(sense);
			attrs.add(Integer.toString(explicitCount));
			attrs.add(Integer.toString(implicitCount));
			table.addRow(attrs);
		}

		return table;
	}

	public static Table analyzeTable3(List<ParseResultFile> files) {
		Table table = new Table("Table 2");
		table.addColumn("count");
		table.addColumn("connective");
		table.addColumn("sense");

		Hashtable<String, Hashtable<String, Integer>> counts = new Hashtable<String, Hashtable<String, Integer>>();
		for (ParseResultFile file : files) {
			if (!file.isPDTB1()) {
				List<PipeUnit> pipes = file.getPipes();
				for (PipeUnit pipe : pipes) {
					String relation = pipe.getAttr(PipeAttribute.RELATION_TYPE)
							.trim();
					if (relation.equals("Explicit")) {
						String connective = pipe.getAttr(
								PipeAttribute.CONN_HEAD);
						if (connective != null) {
							connective = connective.trim();
							Hashtable<String, Integer> tempCounts;
							if (counts.containsKey(connective)) {
								tempCounts = counts.get(connective);
							} else {
								tempCounts = new Hashtable<String, Integer>();
								counts.put(connective, tempCounts);
							}

							int val = 0;
							if (tempCounts.containsKey(relation))
								val = tempCounts.get(relation);
							tempCounts.put(relation, val + 1);
						}
					}
				}
			}
		}

		Iterator<String> connectives = counts.keySet().iterator();
		while (connectives.hasNext()) {
			String connective = connectives.next();
			Hashtable<String, Integer> vals = counts.get(connective);
			Iterator<String> it = vals.keySet().iterator();
			while (it.hasNext()) {
				String relation = it.next();
				int count = vals.get(relation);
				List<String> attrs = new ArrayList<String>();
				attrs.add(Integer.toString(count));
				attrs.add(connective);
				attrs.add(relation);
				table.addRow(attrs);
			}
		}
		return table;
	}

}

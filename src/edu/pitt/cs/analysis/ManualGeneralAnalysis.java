package edu.pitt.cs.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.io.ParseResultReader;
import edu.pitt.cs.model.ManualParseResultFile;
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
public class ManualGeneralAnalysis {
	public static void enterTable(ManualParseResultFile file, Table table) {
		List<String> attrs = new ArrayList<String>();
		List<String> columns = table.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			String columnName = columns.get(i);
			if (columnName.equals("FileName")) {
				String fileName = new File(file.getFileName()).getName();
				if (fileName.contains(" - "))
					fileName = fileName.substring(0, fileName.indexOf(" - "))
							.trim();
				fileName = fileName.replaceAll("\\.txt", "").trim();
				attrs.add(fileName);
			} else {
				if (columnName.endsWith("_exp")) {
					List<Integer> categories = new ArrayList<Integer>();
					List<String> values = new ArrayList<String>();

					categories.add(PipeAttribute.RELATION_TYPE);
					//categories.add(PipeAttribute.FIRST_SEMCLASS_CONN);
					categories.add(8);
					values.add("Explicit");
					values.add(columnName.substring(0, columnName.indexOf("_")));

					attrs.add(Integer.toString(file
							.getCount(categories, values)));
				} else if (columnName.endsWith("_imp")) {
					List<Integer> categories = new ArrayList<Integer>();
					List<String> values = new ArrayList<String>();

					categories.add(PipeAttribute.RELATION_TYPE);
					//categories.add(PipeAttribute.FIRST_SEMCLASS_CONN);
					categories.add(8);
					values.add("Implicit");
					values.add(columnName.substring(0, columnName.indexOf("_")));

					attrs.add(Integer.toString(file
							.getCount(categories, values)));
				} else if (columnName.equals("Explicit")
						|| columnName.equals("Implicit")
						|| columnName.equals("EntRel")) {
					int categoryType = PipeAttribute.RELATION_TYPE;
					attrs.add(Integer.toString(file.getCount(categoryType,
							columnName)));
				} else {
					//int categoryType = PipeAttribute.FIRST_SEMCLASS_CONN;
					int categoryType = 8;
					attrs.add(Integer.toString(file.getCount(categoryType,
							columnName)));
				}
			}
		}

		table.addRow(attrs);
	}

	public static void enterTable1Merge(ParseResultFile file, Table table) {
		List<String> attrs = new ArrayList<String>();
		List<String> columns = table.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			String columnName = columns.get(i);
			if (columnName.equals("FileName")) {
				attrs.add(new File(file.getFileName()).getName());
			} else {
				if (columnName.endsWith("_exp")) {
					List<Integer> categories = new ArrayList<Integer>();
					List<String> values = new ArrayList<String>();

					categories.add(PipeAttribute.RELATION_TYPE);
					//categories.add(PipeAttribute.FIRST_SEMCLASS_CONN);
					categories.add(8);
					values.add("Explicit");
					values.add(columnName.substring(0, columnName.indexOf("_")));

					attrs.add(Integer.toString(file.getCountMerge(categories,
							values)));
				} else if (columnName.endsWith("_imp")) {
					List<Integer> categories = new ArrayList<Integer>();
					List<String> values = new ArrayList<String>();

					categories.add(PipeAttribute.RELATION_TYPE);
					//categories.add(PipeAttribute.FIRST_SEMCLASS_CONN);
					categories.add(8);
					values.add("Implicit");
					values.add(columnName.substring(0, columnName.indexOf("_")));

					attrs.add(Integer.toString(file.getCountMerge(categories,
							values)));
				} else if (columnName.equals("Explicit")
						|| columnName.equals("Implicit")
						|| columnName.equals("EntRel")) {
					int categoryType = PipeAttribute.RELATION_TYPE;
					attrs.add(Integer.toString(file.getCount(categoryType,
							columnName)));
				} else {
					//int categoryType = PipeAttribute.FIRST_SEMCLASS_CONN;
					int categoryType = 8;
					attrs.add(Integer.toString(file.getCountMerge(categoryType,
							columnName)));
				}
			}
		}

		table.addRow(attrs);
	}

	public static Table statTable1(List<ManualParseResultFile> files) {
		Table table = new Table("PDTB 1");
		// Relation Types: Explicit/Implicit/AltLex/EntRel/NoRel
		// pdtb senses: Comparison/Contingency/Temporal/Expansion
		table.addColumn("FileName");
		table.addColumn("Explicit");
		table.addColumn("Implicit");
		table.addColumn("EntRel");

		table.addColumn("Comparison");
		table.addColumn("Comparison_exp");
		table.addColumn("Comparison_imp");
		table.addColumn("Contingency");
		table.addColumn("Contingency_exp");
		table.addColumn("Contingency_imp");
		table.addColumn("Temporal");
		table.addColumn("Temporal_exp");
		table.addColumn("Temporal_imp");
		table.addColumn("Expansion");
		table.addColumn("Expansion_exp");
		table.addColumn("Expansion_imp");

		for (ManualParseResultFile file : files) {
			if (file.isPDTB1()) {
				enterTable(file, table);
			}
		}
		return table;
	}

	public static Table statTable1Merge(List<ParseResultFile> files) {
		Table table = new Table("PDTB 1 Merge");
		// Relation Types: Explicit/Implicit/AltLex/EntRel/NoRel
		// pdtb senses: Comparison/Contingency/Temporal/Expansion
		table.addColumn("FileName");
		table.addColumn("Explicit");
		table.addColumn("Implicit");
		table.addColumn("EntRel");

		table.addColumn("Comparison");
		table.addColumn("Comparison_exp");
		table.addColumn("Comparison_imp");
		table.addColumn("Contingency");
		table.addColumn("Contingency_exp");
		table.addColumn("Contingency_imp");
		table.addColumn("Temporal");
		table.addColumn("Temporal_exp");
		table.addColumn("Temporal_imp");
		table.addColumn("Expansion");
		table.addColumn("Expansion_exp");
		table.addColumn("Expansion_imp");

		for (ParseResultFile file : files) {
			if (!file.isPDTB1()) {
				enterTable1Merge(file, table);
			}
		}
		return table;
	}

	public static void addAttrColumn(Table table, String colName) {
		table.addColumn(colName);
		table.addColumn(colName + "_exp");
		table.addColumn(colName + "_imp");
	}

	public static Table statTable2(List<ManualParseResultFile> files) {
		Table table = new Table("PDTB 2");
		// Relation Types: Explicit/Implicit/AltLex/EntRel/NoRel
		// pdtb senses: Comparison/Contingency/Temporal/Expansion
		table.addColumn("FileName");
		table.addColumn("Explicit");
		table.addColumn("Implicit");
		table.addColumn("EntRel");

		addAttrColumn(table, "Comparison.Contrast");
		addAttrColumn(table, "Comparison.Concession");
		addAttrColumn(table, "Contingency.Cause");
		addAttrColumn(table, "Contingency.Condition");
		addAttrColumn(table, "Temporal.Asynchronous");
		addAttrColumn(table, "Temporal.Synchronous");
		addAttrColumn(table, "Expansion.Conjunction");
		addAttrColumn(table, "Expansion.Instantiation");
		addAttrColumn(table, "Expansion.Restatement");
		addAttrColumn(table, "Expansion.Alternative");
		addAttrColumn(table, "Expansion.Exception");
		addAttrColumn(table, "Expansion.List");

		for (ManualParseResultFile file : files) {
			if (!file.isPDTB1()) {
				enterTable(file, table);
			}
		}
		return table;
	}

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
		ArrayList<String> keys = new ArrayList<String>();
		while (it.hasNext()) {
			String type = it.next();
			keys.add(type);
		}
		Collections.sort(keys);
		for (String key : keys) {
			String type = key;
			if (type.trim().length() > 0) {
				int count = 0;
				if (counts.containsKey(type))
					count = counts.get(type);
				List<String> attrs = new ArrayList<String>();
				attrs.add(type);
				attrs.add(Integer.toString(count));
				table.addRow(attrs);
			}
		}
		return table;
	}

	/**
	 * Analyze Table 1 PDTB2
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static Table analyzeTable1PDTB2(List<ParseResultFile> files)
			throws IOException {
		Table table = new Table("Table 1");
		table.addColumn("PDTB Relations");
		table.addColumn("No. of Columns");

		// List<ParseResultFile> files = ParseResultReader.readFiles(folder);
		Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
		for (ParseResultFile file : files) {
			if (!file.isPDTB1()) {
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
		ArrayList<String> keys = new ArrayList<String>();
		while (it.hasNext()) {
			String type = it.next();
			keys.add(type);
		}
		Collections.sort(keys);
		for (String key : keys) {
			String type = key;
			if (type.trim().length() > 0) {
				int count = 0;
				if (counts.containsKey(type))
					count = counts.get(type);
				List<String> attrs = new ArrayList<String>();
				attrs.add(type);
				attrs.add(Integer.toString(count));
				table.addRow(attrs);
			}
		}
		return table;
	}

	/**
	 * Analyze table 2
	 * 
	 * @param files
	 * @return
	 */
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
		ArrayList<String> keys = new ArrayList<String>();
		while (it.hasNext()) {
			String sense = it.next();
			keys.add(sense);
		}
		Collections.sort(keys);
		for (String key : keys) {
			String sense = key;
			if (sense.trim().length() > 0) {
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
		}

		return table;
	}

	/**
	 * Analyze Table 2 PDTB2
	 * 
	 * @param files
	 * @return
	 */
	public static Table analyzeTable2PDTB2(List<ParseResultFile> files) {
		Table table = new Table("Table 2");
		table.addColumn("Level-2 PDTB Senses");
		table.addColumn("No. of explicits");
		table.addColumn("No. of implicits");

		HashSet<String> senses = new HashSet<String>();

		Hashtable<String, Integer> explicitCounts = new Hashtable<String, Integer>();
		Hashtable<String, Integer> implicitCounts = new Hashtable<String, Integer>();
		for (ParseResultFile file : files) {
			if (!file.isPDTB1()) {
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
		ArrayList<String> keys = new ArrayList<String>();
		while (it.hasNext()) {
			String sense = it.next();
			keys.add(sense);
		}
		Collections.sort(keys);
		for (String key : keys) {
			String sense = key;
			if (sense.trim().length() > 0) {
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
		}

		return table;
	}

	/**
	 * Analyze Table 2 PDTB2 Merge Level 1
	 * 
	 * @param files
	 * @return
	 */
	public static Table analyzeTable2PDTB2Merge(List<ParseResultFile> files) {
		Table table = new Table("Table 2");
		table.addColumn("Level-1 PDTB Senses");
		table.addColumn("No. of explicits");
		table.addColumn("No. of implicits");

		HashSet<String> senses = new HashSet<String>();

		Hashtable<String, Integer> explicitCounts = new Hashtable<String, Integer>();
		Hashtable<String, Integer> implicitCounts = new Hashtable<String, Integer>();
		for (ParseResultFile file : files) {
			if (!file.isPDTB1()) {
				List<PipeUnit> pipes = file.getPipes();
				for (PipeUnit pipe : pipes) {
					String relation = pipe.getAttr(PipeAttribute.RELATION_TYPE)
							.trim();
					String pdtbsense = pipe
							.getAttr(PipeAttribute.FIRST_SEMCLASS_CONN);
					if (pdtbsense.contains("."))
						pdtbsense = pdtbsense.substring(0,
								pdtbsense.indexOf("."));
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
		ArrayList<String> keys = new ArrayList<String>();
		while (it.hasNext()) {
			String sense = it.next();
			keys.add(sense);
		}
		Collections.sort(keys);
		for (String key : keys) {
			String sense = key;
			if (sense.trim().length() > 0) {
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
		}

		return table;
	}

	public static Table analyzeTable3(List<ParseResultFile> files) {
		Table table = new Table("Table 3");
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
						String connective = pipe
								.getAttr(PipeAttribute.CONN_HEAD);
						String pdtbsense = pipe
								.getAttr(PipeAttribute.FIRST_SEMCLASS_CONN);
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
							if (tempCounts.containsKey(pdtbsense))
								val = tempCounts.get(pdtbsense);
							tempCounts.put(pdtbsense, val + 1);
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
				if (relation.trim().length() > 0) {
					int count = vals.get(relation);
					List<String> attrs = new ArrayList<String>();
					attrs.add(Integer.toString(count));
					attrs.add(connective);
					attrs.add(relation);
					table.addRow(attrs);
				}
			}
		}
		return table;
	}

	public static Table analyzeTable3PDTB1(List<ParseResultFile> files) {
		Table table = new Table("Table 3");
		table.addColumn("count");
		table.addColumn("connective");
		table.addColumn("sense");

		Hashtable<String, Hashtable<String, Integer>> counts = new Hashtable<String, Hashtable<String, Integer>>();
		for (ParseResultFile file : files) {
			if (file.isPDTB1()) {
				List<PipeUnit> pipes = file.getPipes();
				for (PipeUnit pipe : pipes) {
					String relation = pipe.getAttr(PipeAttribute.RELATION_TYPE)
							.trim();
					if (relation.equals("Explicit")) {
						String connective = pipe
								.getAttr(PipeAttribute.CONN_HEAD);
						String pdtbsense = pipe
								.getAttr(PipeAttribute.FIRST_SEMCLASS_CONN);
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
							if (tempCounts.containsKey(pdtbsense))
								val = tempCounts.get(pdtbsense);
							tempCounts.put(pdtbsense, val + 1);
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
				if (relation.trim().length() > 0) {
					int count = vals.get(relation);
					List<String> attrs = new ArrayList<String>();
					attrs.add(Integer.toString(count));
					attrs.add(connective);
					attrs.add(relation);
					table.addRow(attrs);
				}
			}
		}
		return table;
	}

	public static Table analyzeTable3Merge(List<ParseResultFile> files) {
		Table table = new Table("Table 3");
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
						String connective = pipe
								.getAttr(PipeAttribute.CONN_HEAD);
						String pdtbsense = pipe
								.getAttr(PipeAttribute.FIRST_SEMCLASS_CONN);
						if (pdtbsense.contains("."))
							pdtbsense = pdtbsense.substring(0,
									pdtbsense.indexOf("."));
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
							if (tempCounts.containsKey(pdtbsense))
								val = tempCounts.get(pdtbsense);
							tempCounts.put(pdtbsense, val + 1);
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
				if (relation.trim().length() > 0) {
					int count = vals.get(relation);
					List<String> attrs = new ArrayList<String>();
					attrs.add(Integer.toString(count));
					attrs.add(connective);
					attrs.add(relation);
					table.addRow(attrs);
				}
			}
		}
		return table;
	}
}

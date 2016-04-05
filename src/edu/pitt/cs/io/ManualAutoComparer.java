package edu.pitt.cs.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeUnit;

class PipeUnitPair {
	PipeUnit manualUnit;
	PipeUnit autoUnit;

	public String toString() {
		String mType = manualUnit.getElementType();
		String aType = autoUnit.getElementType();
		String mSenseType = manualUnit.getManualRelationType();
		String aSenseType = autoUnit.getRelationType();

		String mSenseType2 = "";
		HashSet<String> mSenses = manualUnit.getManualRelationTypes();
		for (String mSense : mSenses) {
			if (mSense.trim().length() > 0)
				mSenseType2 += mSense + ",";
		}
		if (mSenseType2.length() > 0)
			mSenseType2 = mSenseType2.substring(0, mSenseType2.length() - 1);

		String str = "PARSER|" + aType + "|" + aSenseType + "|"
				+ autoUnit.getRange1TxtAuto() + "|"
				+ autoUnit.getRange2TxtAuto();
		str += "\n";
		str += "KFR|" + mType + "|" + mSenseType2 + "|"
				+ manualUnit.getRange1Txt() + "|" + manualUnit.getRange2Txt();
		str += "\n";
		return str;
	}
}

class TotalSum {
	int agreedAll = 0;
	int totalAgreed = 0;
	int arg2AgreedArg1NotAgreed = 0;
	int arg1AgreedArg2NotAgreed = 0;
	int agreedExplicit = 0;
	int agreedImplicit = 0;
	int agreedEnt = 0;
	int agreedNoRel = 0;
	int kfrTotal = 0;
	int parserTotal = 0;

	int agreedSense = 0;
	int agreedCompare = 0;
	int agreedContingency = 0;
	int agreedExpansion = 0;
	int agreedTemporal = 0;
	int totalSenseAgreed = 0;

	Hashtable<String, Integer> overallCounting = new Hashtable<String, Integer>();

	public void addCount(String key) {
		int count = 0;
		if (overallCounting.containsKey(key)) {
			count = overallCounting.get(key);
		}
		count++;
		overallCounting.put(key, count);
	}

	public String printCounting() {
		String str = "";
		str += generateOverallCountStr();
		str += "\n";
	
		str += "Type Agreed:\n";
		String[] types = {"Explicit", "Implicit", "EntRel", "AltLex"};
		for(String type: types) {
			str += generateAgreeCountStr(type);
			str += "\n";
		}
		
		str += generateNotAgreedTypeStr() + "\n";
		str += generateOnlyStr();
		return str;
	}

	public String generateOverallCountStr() {
		String[] matches = { "ExactMatch", "PartialMatch",
				"Arg1Match-Arg2NotMatch", "Arg2Match-Arg1NotMatch" };
		String[] types = {"Explicit", "Implicit", "EntRel", "AltLex"};
		int agreedTotal = 0;
		int notAgreedTotal = 0;
		String str = "";
		for(String type: types) {
			for(String match: matches) {
				String matchkey = match + "-" + type;
				int val = 0;
				Iterator<String> it = overallCounting.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					if(key.contains(matchkey)) {
						int count = 0;
						if(overallCounting.containsKey(key)) count = overallCounting.get(key);
						val += count;
					} 
				}
				str += val + " " + type + " with " + match + "\n";
				agreedTotal += val;
			}
		}
		
		Iterator<String> it = overallCounting.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(key.contains("NotAgreed")) {
				int count = 0;
				if(overallCounting.containsKey(key)) count = overallCounting.get(key);
				notAgreedTotal += count;
			} 
		}
		str = agreedTotal + " Agreed Type\n\n" + notAgreedTotal + " No Agreed Type\n\n" + str;
		return str;
	}
	
	public String generateOnlyStr() {
		String[] starts = {"KFR ONLY", "PARSER ONLY"};
		String str = "";
		for(String start: starts) {
			int countAll = 0;
			String startStr = "";
			Iterator<String> it = overallCounting.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.contains(start)) {
					int count = 0;
					if(overallCounting.containsKey(key)) count = overallCounting.get(key);
					startStr += count + " " + key + "\n";
					countAll += count;
				}
			}
			startStr = countAll + " " + start +"\n" + startStr;
			str += startStr + "\n";
		}
		return str;
	}
	
	public String generateAgreeCountStr(String type) {
		String str = "On agreed " + type + " cases\n";
		// String[] types = {"Explicit", "Implicit", "EntRel", "AltLex"};
		String[] matches = { "ExactMatch", "PartialMatch",
				"Arg1Match-Arg2NotMatch", "Arg2Match-Arg1NotMatch" };
		String[] agreeSenses = { "KFR Comparison PARSER Comparison",
				"KFR Contingency PARSER Contingency",
				"KFR Expansion PARSER Expansion",
				"KFR Temporal PARSER Temporal" };
		for (String match : matches) {
			str += "\n";
			str += "For ranges " + match + "\n";
			str += "Senses agree:\n";
			for (String agreeSense : agreeSenses) {
				String tag = match + "-" + type + " " + agreeSense;
				int count = 0;
				if(overallCounting.containsKey(tag)) count = overallCounting.get(tag);
				str += count + " " + tag + "\n";
			}
			str += "Senses disagree:\n";
			Iterator<String> it = overallCounting.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.contains(match + "-" + type)) {
					if (!key.contains(agreeSenses[0])
							&& !key.contains(agreeSenses[1])
							&& !key.contains(agreeSenses[2])
							&& !key.contains(agreeSenses[3])) {
						int count = 0;
						if(overallCounting.containsKey(key)) count = overallCounting.get(key);
						str += count + " " + key + "\n";
					}
				}
			}
		}

		str += "\n";
		return str;
	}

	public String generateNotAgreedTypeStr() {
		String str = "";
		int notAgreed = 0;
		String[] matches = { "ExactMatch", "PartialMatch",
				"Arg1Match-Arg2NotMatch", "Arg2Match-Arg1NotMatch" }; 
		
		for(String match: matches) {
			str += "\n";
			str += match + " cases\n";
			String matchKey = match + "-" + "NotAgreed";
			Iterator<String> it = overallCounting.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.contains(matchKey)) {
					int count = 0;
					if(overallCounting.containsKey(key)) count = overallCounting.get(key);
					str += count +  " " + key + "\n";
					notAgreed += count;
				}
			}
		}
		str = notAgreed + " TYPE NOT AGREED\n" + str;
		return str;
	}
	

	Hashtable<String, Integer> disagreedEntTable = new Hashtable<String, Integer>();
	Hashtable<String, Integer> disagreedTypeTable = new Hashtable<String, Integer>();

	Hashtable<String, Integer> kateSpecialDisagreedTypeTable = new Hashtable<String, Integer>();

	void addKateSpecialDisagreedSense(Hashtable<String, Integer> table) {
		Iterator<String> it = table.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (kateSpecialDisagreedTypeTable.containsKey(key)) {
				kateSpecialDisagreedTypeTable.put(key, table.get(key)
						+ kateSpecialDisagreedTypeTable.get(key));
			} else {
				kateSpecialDisagreedTypeTable.put(key, table.get(key));
			}
		}
	}

	void addSum(TotalSum sum) {
		this.agreedAll += sum.agreedAll;
		this.totalAgreed += sum.totalAgreed;
		this.arg2AgreedArg1NotAgreed += sum.arg2AgreedArg1NotAgreed;
		this.arg1AgreedArg2NotAgreed += sum.arg1AgreedArg2NotAgreed;
		this.agreedExplicit += sum.agreedExplicit;
		this.agreedImplicit += sum.agreedImplicit;
		this.agreedEnt += sum.agreedEnt;
		this.agreedNoRel += sum.agreedNoRel;
		this.kfrTotal += sum.kfrTotal;
		this.parserTotal += sum.parserTotal;

		this.agreedSense += sum.agreedSense;
		this.agreedCompare += sum.agreedCompare;
		this.agreedContingency += sum.agreedContingency;
		this.agreedExpansion += sum.agreedExpansion;
		this.agreedTemporal += sum.agreedTemporal;
		this.totalSenseAgreed += sum.totalSenseAgreed;

		Iterator<String> disEntIt = sum.disagreedEntTable.keySet().iterator();
		while (disEntIt.hasNext()) {
			String key = disEntIt.next();
			if (!disagreedEntTable.containsKey(key)) {
				disagreedEntTable.put(key, sum.disagreedEntTable.get(key));
			} else {
				disagreedEntTable.put(key, disagreedEntTable.get(key)
						+ sum.disagreedEntTable.get(key));
			}
		}

		Iterator<String> disTypeIt = sum.disagreedTypeTable.keySet().iterator();
		while (disTypeIt.hasNext()) {
			String key = disTypeIt.next();
			if (!disagreedTypeTable.containsKey(key)) {
				disagreedTypeTable.put(key, sum.disagreedTypeTable.get(key));
			} else {
				disagreedTypeTable.put(key, disagreedTypeTable.get(key)
						+ sum.disagreedTypeTable.get(key));
			}
		}
	}

	void addDiagreedEnt(String str) {
		int total = 0;
		if (disagreedEntTable.containsKey(str)) {
			total = disagreedEntTable.get(str);
		}
		total++;
		disagreedEntTable.put(str, total);
	}

	void addDisagreedType(String str) {
		int total = 0;
		if (disagreedTypeTable.containsKey(str)) {
			total = disagreedTypeTable.get(str);
		}
		total++;
		disagreedTypeTable.put(str, total);
	}

	public String toString() {
		String str = "Across all files:\n";
		str += "Arg 1 range matched while arg2 range not matched: "
				+ arg1AgreedArg2NotAgreed + "\n";
		str += "Arg 2 range matched while arg1 range not matched: "
				+ arg2AgreedArg1NotAgreed + "\n";
		str += "Relation Type: " + agreedAll + "/" + totalAgreed + "\n";
		str += "KFR:" + kfrTotal + " annotated relations\n";
		str += "PARSER:" + parserTotal + " annotated relations\n";
		str += "AGREED:\n";
		str += agreedExplicit + " Explicit \n";
		str += agreedImplicit + " Implicit \n";
		str += agreedEnt + " EntRel \n";
		str += agreedNoRel + " AltLex \n";
		str += "\n";
		str += "DISAGREED:\n";
		Iterator<String> it = disagreedEntTable.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			int value = disagreedEntTable.get(key);
			str += value + " " + key + "\n";
		}
		str += "\n";
		str += "Relation Sense: " + agreedSense + "/" + totalSenseAgreed + "\n";
		str += "AGREED: \n";
		str += agreedCompare + " Comparison \n";
		str += agreedContingency + " Contingency \n";
		str += agreedExpansion + " Expansion \n";
		str += agreedTemporal + " Temporal \n";

		str += "DISAGREED:\n";
		Iterator<String> it2 = disagreedTypeTable.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			int value = disagreedTypeTable.get(key);
			str += value + " " + key + "\n";
		}
		return str;
	}
}

public class ManualAutoComparer {
	public static void main(String[] args) throws IOException {
		String manualFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
		String autoFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		String outputFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\compareOutput";
		TotalSum sum1 = new TotalSum();
		TotalSum sum2 = new TotalSum();
		TotalSum[] sums = { sum1, sum2 };
		compareV3(manualFolderPath, autoFolderPath, outputFolderPath, sums);

		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outputFolderPath + "/" + "compareSumD1.txt"));
		writer.write(sums[0].printCounting());
		writer.close();
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(
				outputFolderPath + "/" + "compareSumD2.txt"));
		writer2.write(sums[1].printCounting());
		writer2.close();
	}

	public static void compare(String manualFolderPath, String autoFolderPath,
			String outputFolderPath, TotalSum[] sums) throws IOException {
		List<ManualParseResultFile> manualResults = ManualParseResultReader
				.readFiles(manualFolderPath);
		List<ParseResultFile> autoResults = ParseResultReader
				.readFiles(autoFolderPath);
		Hashtable<String, ManualParseResultFile> tableD1 = new Hashtable<String, ManualParseResultFile>();
		Hashtable<String, ManualParseResultFile> tableD2 = new Hashtable<String, ManualParseResultFile>();
		String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		for (ManualParseResultFile mFile : manualResults) {
			ModificationRemover.feedTxtInfo(mFile, path);
			String fileName = mFile.getFileName();
			boolean isD1 = false;
			if (fileName.contains("draft1"))
				isD1 = true;
			fileName = (new File(fileName)).getName();
			if (fileName.contains(" - "))
				fileName = fileName.substring(0, fileName.indexOf(" - "))
						.trim();
			fileName = fileName.replaceAll("\\.txt", "").trim();
			/*
			 * fileName = fileName.substring(0, fileName.lastIndexOf(".txt"))
			 * .trim();
			 */
			if (isD1)
				tableD1.put(fileName, mFile);
			else
				tableD2.put(fileName, mFile);
		}
		for (ParseResultFile aFile : autoResults) {
			if (aFile.isPDTB1()) {
				String fileName = aFile.getFileName();
				boolean isD1 = false;
				if (fileName.contains("draft1")) {
					isD1 = true;
				}
				fileName = (new File(fileName)).getName();
				if (fileName.contains(" - "))
					fileName = fileName.substring(0, fileName.indexOf(" - "))
							.trim();
				fileName = fileName.replaceAll("\\.txt", "").trim();
				fileName = fileName.replaceAll("\\.pipe", "").trim();

				ManualParseResultFile mFile = null;
				String postFix = "draft1";
				if (isD1)
					mFile = tableD1.get(fileName);
				else {
					mFile = tableD2.get(fileName);
					postFix = "draft2";
				}
				if (mFile == null)
					System.out.println(fileName);
				else {
					TotalSum sum = null;
					if (isD1) {
						sum = sums[0];
					} else {
						sum = sums[1];
					}
					String compareStr = compareV2(mFile, aFile, sum);
					String outputPath = outputFolderPath + "/" + postFix + "/"
							+ fileName + ".log";
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							outputPath));
					writer.write(compareStr);
					writer.close();
				}
			}
		}
	}
	
	
	public static void compareV3(String manualFolderPath, String autoFolderPath,
			String outputFolderPath, TotalSum[] sums) throws IOException {
		List<ManualParseResultFile> manualResults = ManualParseResultReader
				.readFiles(manualFolderPath);
		List<ParseResultFile> autoResults = ParseResultReader
				.readFiles(autoFolderPath);
		Hashtable<String, ManualParseResultFile> tableD1 = new Hashtable<String, ManualParseResultFile>();
		Hashtable<String, ManualParseResultFile> tableD2 = new Hashtable<String, ManualParseResultFile>();
		String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		for (ManualParseResultFile mFile : manualResults) {
			ModificationRemover.feedTxtInfo(mFile, path);
			String fileName = mFile.getFileName();
			boolean isD1 = false;
			if (fileName.contains("draft1"))
				isD1 = true;
			fileName = (new File(fileName)).getName();
			if (fileName.contains(" - "))
				fileName = fileName.substring(0, fileName.indexOf(" - "))
						.trim();
			fileName = fileName.replaceAll("\\.txt", "").trim();
			/*
			 * fileName = fileName.substring(0, fileName.lastIndexOf(".txt"))
			 * .trim();
			 */
			if (isD1)
				tableD1.put(fileName, mFile);
			else
				tableD2.put(fileName, mFile);
		}
		for (ParseResultFile aFile : autoResults) {
			if (aFile.isPDTB1()) {
				String fileName = aFile.getFileName();
				boolean isD1 = false;
				if (fileName.contains("draft1")) {
					isD1 = true;
				}
				fileName = (new File(fileName)).getName();
				if (fileName.contains(" - "))
					fileName = fileName.substring(0, fileName.indexOf(" - "))
							.trim();
				fileName = fileName.replaceAll("\\.txt", "").trim();
				fileName = fileName.replaceAll("\\.pipe", "").trim();

				ManualParseResultFile mFile = null;
				String postFix = "draft1";
				if (isD1)
					mFile = tableD1.get(fileName);
				else {
					mFile = tableD2.get(fileName);
					postFix = "draft2";
				}
				if (mFile == null)
					System.out.println(fileName);
				else {
					TotalSum sum = null;
					if (isD1) {
						sum = sums[0];
					} else {
						sum = sums[1];
					}
					String compareStr = compareV3(mFile, aFile, sum);
					/*String outputPath = outputFolderPath + "/" + postFix + "/"
							+ fileName + ".log";
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							outputPath));
					writer.write(compareStr);
					writer.close();*/
				}
			}
		}
	}


	public static String compareV3(ManualParseResultFile manualFile,
			ParseResultFile autoFile, TotalSum sum) {
		List<PipeUnit> manualUnits = manualFile.getPipes();
		int manualCount = manualUnits.size();
		List<PipeUnit> autoUnits = autoFile.getPipes();
		int autoCount = autoUnits.size();

		TotalSum temp = new TotalSum();

		List<PipeUnitPair> agreedExplicitPairsExact = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedImplicitPairsExact = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedEntRelPairsExact = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedAltLexPairsExact = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> notAgreedExact = new ArrayList<PipeUnitPair>();

		List<PipeUnitPair> agreedExplicitPairsPartial = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedImplicitPairsPartial = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedEntRelPairsPartial = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedAltLexPairsPartial = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> notAgreedPartial = new ArrayList<PipeUnitPair>();

		List<PipeUnitPair> agreedExplicitPairsArg1 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedImplicitPairsArg1 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedEntRelPairsArg1 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedAltLexPairsArg1 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> notAgreedArg1 = new ArrayList<PipeUnitPair>();

		List<PipeUnitPair> agreedExplicitPairsArg2 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedImplicitPairsArg2 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedEntRelPairsArg2 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedAltLexPairsArg2 = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> notAgreedArg2 = new ArrayList<PipeUnitPair>();

		addAgreedExplicitsExact(manualUnits, autoUnits,
				agreedExplicitPairsExact);
		addAgreedOthersExact(manualUnits, autoUnits, agreedImplicitPairsExact,
				"Implicit");
		addAgreedOthersExact(manualUnits, autoUnits, agreedEntRelPairsExact,
				"EntRel");
		addAgreedOthersExact(manualUnits, autoUnits, agreedAltLexPairsExact,
				"AltLex");
		addOnlyRangesExact(manualUnits, autoUnits, notAgreedExact);

		addAgreedExplicits(manualUnits, autoUnits, agreedExplicitPairsPartial);
		addAgreedOthers(manualUnits, autoUnits, agreedImplicitPairsPartial,
				"Implicit");
		addAgreedOthers(manualUnits, autoUnits, agreedEntRelPairsPartial,
				"EntRel");
		addAgreedOthers(manualUnits, autoUnits, agreedAltLexPairsPartial,
				"AltLex");
		addOnlyRangeMatches(manualUnits, autoUnits, notAgreedPartial);

		addAgreedArg2OnlyExplicit(manualUnits, autoUnits,
				agreedExplicitPairsArg2);
		addAgreedOthersArg2Only(manualUnits, autoUnits,
				agreedImplicitPairsArg2, "Implicit");
		addAgreedOthersArg2Only(manualUnits, autoUnits, agreedEntRelPairsArg2,
				"EntRel");
		addAgreedOthersArg2Only(manualUnits, autoUnits, agreedAltLexPairsArg2,
				"AltLex");
		addOnlyRangeMatchesArg2(manualUnits, autoUnits, notAgreedArg2);

		addAgreedArg1OnlyExplicit(manualUnits, autoUnits,
				agreedExplicitPairsArg1);
		addAgreedOthersArg1Only(manualUnits, autoUnits,
				agreedImplicitPairsArg1, "Implicit");
		addAgreedOthersArg1Only(manualUnits, autoUnits, agreedEntRelPairsArg1,
				"EntRel");
		addAgreedOthersArg1Only(manualUnits, autoUnits, agreedAltLexPairsArg1,
				"AltLex");
		addOnlyRangeMatchesArg1(manualUnits, autoUnits, notAgreedArg1);

		addCount(agreedExplicitPairsExact, "ExactMatch-Explicit", sum);
		addCount(agreedExplicitPairsPartial, "PartialMatch-Explicit", sum);
		addCount(agreedExplicitPairsArg1, "Arg1Match-Arg2NotMatch-Explicit",
				sum);
		addCount(agreedExplicitPairsArg2, "Arg2Match-Arg1NotMatch-Explicit",
				sum);
		addCountNotAgreed(notAgreedExact, "ExactMatch-NotAgreed", sum);

		addCount(agreedImplicitPairsExact, "ExactMatch-Implicit", sum);
		addCount(agreedImplicitPairsPartial, "PartialMatch-Implicit", sum);
		addCount(agreedImplicitPairsArg1, "Arg1Match-Arg2NotMatch-Implicit",
				sum);
		addCount(agreedImplicitPairsArg2, "Arg2Match-Arg1NotMatch-Implicit",
				sum);
		addCountNotAgreed(notAgreedPartial, "PartialMatch-NotAgreed", sum);

		addCount(agreedEntRelPairsExact, "ExactMatch-EntRel", sum);
		addCount(agreedEntRelPairsPartial, "PartialMatch-EntRel", sum);
		addCount(agreedEntRelPairsArg1, "Arg1Match-Arg2NotMatch-EntRel", sum);
		addCount(agreedEntRelPairsArg2, "Arg2Match-Arg1NotMatch-EntRel", sum);
		addCountNotAgreed(notAgreedArg1, "Arg1Match-Arg2NotMatch-NotAgreed",
				sum);

		addCount(agreedAltLexPairsExact, "ExactMatch-AltLex", sum);
		addCount(agreedAltLexPairsPartial, "PartialMatch-AltLex", sum);
		addCount(agreedAltLexPairsArg1, "Arg1Match-Arg2NotMatch-AltLex", sum);
		addCount(agreedAltLexPairsArg2, "Arg2Match-Arg1NotMatch-AltLex", sum);
		addCountNotAgreed(notAgreedArg2, "Arg2Match-Arg1NotMatch-NotAgreed",
				sum);

		addCountParserOnly(autoUnits, sum);
		addCountKFROnly(manualUnits, sum);
		
		
		addCount(agreedExplicitPairsExact, "ExactMatch-Explicit", temp);
		addCount(agreedExplicitPairsPartial, "PartialMatch-Explicit", temp);
		addCount(agreedExplicitPairsArg1, "Arg1Match-Arg2NotMatch-Explicit",
				temp);
		addCount(agreedExplicitPairsArg2, "Arg2Match-Arg1NotMatch-Explicit",
				temp);
		addCountNotAgreed(notAgreedExact, "ExactMatch-NotAgreed", temp);

		addCount(agreedImplicitPairsExact, "ExactMatch-Implicit", temp);
		addCount(agreedImplicitPairsPartial, "PartialMatch-Implicit", temp);
		addCount(agreedImplicitPairsArg1, "Arg1Match-Arg2NotMatch-Implicit",
				temp);
		addCount(agreedImplicitPairsArg2, "Arg2Match-Arg1NotMatch-Implicit",
				temp);
		addCountNotAgreed(notAgreedPartial, "PartialMatch-NotAgreed", temp);

		addCount(agreedEntRelPairsExact, "ExactMatch-EntRel", temp);
		addCount(agreedEntRelPairsPartial, "PartialMatch-EntRel", temp);
		addCount(agreedEntRelPairsArg1, "Arg1Match-Arg2NotMatch-EntRel", temp);
		addCount(agreedEntRelPairsArg2, "Arg2Match-Arg1NotMatch-EntRel", temp);
		addCountNotAgreed(notAgreedArg1, "Arg1Match-Arg2NotMatch-NotAgreed",
				temp);

		addCount(agreedAltLexPairsExact, "ExactMatch-AltLex", temp);
		addCount(agreedAltLexPairsPartial, "PartialMatch-AltLex", temp);
		addCount(agreedAltLexPairsArg1, "Arg1Match-Arg2NotMatch-AltLex", temp);
		addCount(agreedAltLexPairsArg2, "Arg2Match-Arg1NotMatch-AltLex", temp);
		addCountNotAgreed(notAgreedArg2, "Arg2Match-Arg1NotMatch-NotAgreed",
				temp);

		addCountParserOnly(autoUnits, temp);
		addCountKFROnly(manualUnits, temp);

		return temp.printCounting();
	}

	public static void addCount(List<PipeUnitPair> pairs, String rootKey,
			TotalSum sum) {
		for (PipeUnitPair pair : pairs) {
			PipeUnit manual = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			String key = "";
			if (manual.getManualRelationTypes()
					.contains(auto.getRelationType())) {
				key = rootKey + " KFR " + auto.getRelationType() + " PARSER "
						+ auto.getRelationType();
			} else {
				String manualKey = manual.getManualRelationTypeStr();
				String autoKey = auto.getRelationType();
				key = rootKey + " KFR " + manualKey + " PARSER " + autoKey;
			}
			sum.addCount(key);
		}
	}

	public static void addCountNotAgreed(List<PipeUnitPair> pairs,
			String rootKey, TotalSum sum) {
		for (PipeUnitPair pair : pairs) {
			PipeUnit manual = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			String key = "";
			if (manual.getManualRelationTypes()
					.contains(auto.getRelationType())) {
				key = rootKey + " KFR " + manual.getElementType() + "|"
						+ auto.getRelationType() + " PARSER "
						+ auto.getElementType() + "|" + auto.getRelationType();
			} else {
				String manualKey = manual.getManualRelationTypeStr();
				String autoKey = auto.getRelationType();
				key = rootKey + " KFR " + manual.getElementType() + "|"
						+ manualKey + " PARSER " + auto.getElementType() + "|"
						+ autoKey;
			}
			sum.addCount(key);
		}
	}
	
	public static void addCountParserOnly(List<PipeUnit> autoUnits, TotalSum sum) {
		for(PipeUnit unit: autoUnits) {
			String key = "PARSER ONLY|"+unit.getElementType()+"|"+unit.getRelationType();
			sum.addCount(key);
		}
	}
	
	public static void addCountKFROnly(List<PipeUnit> manualUnits, TotalSum sum) {
		for(PipeUnit unit: manualUnits) {
			String key = "KFR ONLY|"+unit.getElementType()+"|"+unit.getManualRelationTypeStr();
			sum.addCount(key);
		}
	}

	public static String compareV2(ManualParseResultFile manualFile,
			ParseResultFile autoFile, TotalSum sum) {
		List<PipeUnit> manualUnits = manualFile.getPipes();
		int manualCount = manualUnits.size();
		List<PipeUnit> autoUnits = autoFile.getPipes();
		int autoCount = autoUnits.size();

		TotalSum temp = new TotalSum();

		List<String> agreedDetails = new ArrayList<String>();
		List<String> typeAgreedDetails = new ArrayList<String>();
		List<String> disagreedDetails = new ArrayList<String>();
		List<String> typeDisagreedDetails = new ArrayList<String>();

		List<String> arg2Matcharg1NoMatchDetails = new ArrayList<String>();
		List<String> arg1Matcharg2NoMatchDetails = new ArrayList<String>();
		List<String> arg2TypeAgreedDetails = new ArrayList<String>();
		List<String> arg2TypeDisagreedDetails = new ArrayList<String>();

		List<PipeUnitPair> agreedExplicitPairs = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedImplicitPairs = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedEntRelPairs = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedAltLexPairs = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> onlyRangeMatches = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedArg2notAgreedArg1Pairs = new ArrayList<PipeUnitPair>();
		List<PipeUnitPair> agreedArg1notAgreedArg2Pairs = new ArrayList<PipeUnitPair>();

		addAgreedExplicits(manualUnits, autoUnits, agreedExplicitPairs);
		addAgreedOthers(manualUnits, autoUnits, agreedImplicitPairs, "Implicit");
		addAgreedOthers(manualUnits, autoUnits, agreedEntRelPairs, "EntRel");
		addAgreedOthers(manualUnits, autoUnits, agreedAltLexPairs, "AltLex");
		addOnlyRangeMatches(manualUnits, autoUnits, onlyRangeMatches);
		addArg2AgreedArg1Disagreed(manualUnits, autoUnits,
				agreedArg2notAgreedArg1Pairs);
		addArg1AgreedArg2Disagreed(manualUnits, autoUnits,
				agreedArg1notAgreedArg2Pairs);

		temp.totalAgreed = agreedExplicitPairs.size()
				+ agreedImplicitPairs.size() + agreedEntRelPairs.size()
				+ agreedAltLexPairs.size() + onlyRangeMatches.size();
		temp.agreedExplicit = agreedExplicitPairs.size();
		temp.agreedImplicit = agreedImplicitPairs.size();
		temp.agreedEnt = agreedEntRelPairs.size();
		temp.agreedNoRel = agreedAltLexPairs.size();
		temp.agreedAll = temp.agreedExplicit + temp.agreedImplicit
				+ temp.agreedEnt + temp.agreedNoRel;
		temp.arg2AgreedArg1NotAgreed = agreedArg2notAgreedArg1Pairs.size();
		temp.arg1AgreedArg2NotAgreed = agreedArg1notAgreedArg2Pairs.size();
		temp.totalSenseAgreed = temp.agreedExplicit + temp.agreedImplicit;
		temp.kfrTotal = manualCount;
		temp.parserTotal = autoCount;

		countAgreedSense(agreedExplicitPairs, temp, agreedDetails,
				typeAgreedDetails, typeDisagreedDetails);
		countAgreedSense(agreedImplicitPairs, temp, agreedDetails,
				typeAgreedDetails, typeDisagreedDetails);
		countAgreedSense(agreedEntRelPairs, temp, agreedDetails,
				typeAgreedDetails, typeDisagreedDetails);
		countAgreedSense(agreedAltLexPairs, temp, agreedDetails,
				typeAgreedDetails, typeDisagreedDetails);

		TotalSum katesSpecialCases = new TotalSum();
		countAgreedSense(agreedArg2notAgreedArg1Pairs, katesSpecialCases,
				arg2Matcharg1NoMatchDetails, arg2TypeAgreedDetails,
				arg2TypeDisagreedDetails);

		for (PipeUnitPair arg1arg2 : agreedArg1notAgreedArg2Pairs) {
			arg1Matcharg2NoMatchDetails.add(arg1arg2.toString());
		}

		for (PipeUnitPair rangeMatch : onlyRangeMatches) {
			disagreedDetails.add(rangeMatch.toString());
		}

		addOnlyCasesAuto(autoUnits, disagreedDetails, temp);
		addOnlyCasesManual(manualUnits, disagreedDetails, temp);

		String kfrCountStr = "KFR: " + manualCount + " annotated relations\n";
		String autoCountStr = "PDTB: " + autoCount + " annotated relations\n";
		String agreedStr = "AGREED: \n" + temp.agreedExplicit + " Explicit\n"
				+ temp.agreedImplicit + " Implicit\n" + temp.agreedEnt
				+ " EntRel\n" + temp.agreedNoRel + " AltLex\n";

		String agreedElement = "AGREED:\n";
		for (String str : agreedDetails) {
			agreedElement += str + "\n";
		}
		String agreedType = "AGREED:\n";
		for (String str : typeAgreedDetails) {
			agreedType += str + "\n";
		}

		String disAgreedStr = "DISAGREED:\n";
		for (String str : disagreedDetails) {
			disAgreedStr += str + "\n";
		}

		String disAgreedTypeStr = "DISAGREED:\n";
		for (String str : typeDisagreedDetails) {
			disAgreedTypeStr += str + "\n";
		}

		String agreedArg2Arg1TypeStr = "All cases of ARG2 matches while ARG1 does not match:\n";

		for (PipeUnitPair pair : agreedArg2notAgreedArg1Pairs) {
			agreedArg2Arg1TypeStr += pair.toString() + "\n";
		}

		agreedArg2Arg1TypeStr += "Cases when Sense AGREED when ARG2 matches but ARG1 does not match: \n";
		for (String str : arg2TypeAgreedDetails) {
			agreedArg2Arg1TypeStr += str + "\n";
		}

		String start = "Relation Type: " + temp.agreedAll + " common/"
				+ temp.totalAgreed + " total\n";

		String secondStart = "4 Class Relation Sense: " + temp.agreedSense
				+ "/" + temp.agreedAll + " AGREED\n";
		secondStart += temp.agreedCompare + " Comparison\n";
		secondStart += temp.agreedContingency + " Contingency\n";
		secondStart += temp.agreedExpansion + " Expansion\n";
		secondStart += temp.agreedTemporal + " Temporal\n\n";

		String thirdStart = "ARG 1 matches but ARG 2 does not match: "
				+ temp.arg1AgreedArg2NotAgreed + " Cases\n\n";
		thirdStart += "ARG 2 matches but ARG 1 does not match: "
				+ temp.arg2AgreedArg1NotAgreed + " Cases\n\n";
		thirdStart += "Within ARG 2 matches but ARG 1 does not match\n, there are ";
		int agreedElementsTotal = katesSpecialCases.agreedCompare
				+ katesSpecialCases.agreedContingency
				+ katesSpecialCases.agreedExpansion
				+ katesSpecialCases.agreedTemporal;
		thirdStart += agreedElementsTotal + " agreed relation types\n\n";
		thirdStart += "Agreed senses:" + katesSpecialCases.agreedCompare
				+ " Comparison, " + katesSpecialCases.agreedContingency
				+ " Contingency, " + katesSpecialCases.agreedExpansion
				+ " Expansion, " + katesSpecialCases.agreedTemporal
				+ " Temporal\n";

		sum.addSum(temp);
		sum.addKateSpecialDisagreedSense(katesSpecialCases.disagreedTypeTable);
		String result = "";
		result += start;
		result += kfrCountStr;
		result += autoCountStr;
		result += agreedStr;
		result += "\n";
		result += secondStart;

		result += "Relation Type\n";
		result += agreedElement;
		result += disAgreedStr;

		result += "\n";
		result += "Relation sense\n";
		result += agreedType;
		result += disAgreedTypeStr;

		result += thirdStart;
		result += agreedArg2Arg1TypeStr;

		return result;
	}

	public static void countAgreedSense(List<PipeUnitPair> pairs,
			TotalSum temp, List<String> agreedDetails,
			List<String> typeAgreedDetails, List<String> typeDisagreedDetails) {
		for (PipeUnitPair pair : pairs) {
			PipeUnit manualUnit = pair.manualUnit;
			PipeUnit autoUnit = pair.autoUnit;
			if (manualUnit.getElementType().equals(autoUnit.getElementType())) {
				String mSenseType = manualUnit.getManualRelationType();
				HashSet<String> mSenseTypes = manualUnit
						.getManualRelationTypes();
				String aSenseType = autoUnit.getRelationType();

				agreedDetails.add(pair.toString());
				if (mSenseTypes.contains(aSenseType)) {
					if (aSenseType.equals("Comparison")) {
						temp.agreedCompare++;
						temp.agreedSense++;
					} else if (aSenseType.equals("Contingency")) {
						temp.agreedContingency++;
						temp.agreedSense++;
					} else if (aSenseType.equals("Expansion")) {
						temp.agreedExpansion++;
						temp.agreedSense++;
					} else if (aSenseType.equals("Temporal")) {
						temp.agreedTemporal++;
						temp.agreedTemporal++;
					}

					typeAgreedDetails.add(pair.toString());
				} else {
					String disagreedKey = "KFR|" + mSenseType + "--PARSER|"
							+ aSenseType;
					temp.addDisagreedType(disagreedKey);
					typeDisagreedDetails.add(pair.toString());
				}
			}
		}
	}

	public static void addOnlyCasesAuto(List<PipeUnit> autoUnits,
			List<String> disagreedDetails, TotalSum sum) {
		for (PipeUnit autoUnit : autoUnits) {
			String str = "PARSER ONLY|" + autoUnit.getElementType() + "|"
					+ autoUnit.getRelationType() + "|"
					+ autoUnit.getRange1TxtAuto() + "|"
					+ autoUnit.getRange2TxtAuto();
			disagreedDetails.add(str);
			String disagreedType = "PARSER ONLY";
			sum.addDisagreedType(disagreedType);
		}
	}

	public static void addOnlyCasesManual(List<PipeUnit> manualUnits,
			List<String> disagreedDetails, TotalSum sum) {
		for (PipeUnit manualUnit : manualUnits) {
			String str = "KFR ONLY|" + manualUnit.getElementType() + "|"
					+ manualUnit.getManualRelationType() + "|"
					+ manualUnit.getRange1Txt() + "|"
					+ manualUnit.getRange2Txt();
			disagreedDetails.add(str);
			String disagreedType = "KFR ONLY";
			sum.addDisagreedType(disagreedType);
		}
	}

	public static void addArg2AgreedArg1Disagreed(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (!isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					PipeUnitPair pair = new PipeUnitPair();
					pair.manualUnit = mUnit;
					pair.autoUnit = aUnit;
					pairs.add(pair);
					it.remove();
					autoIt.remove();
					break;
				}
			}
		}
	}

	public static void addArg1AgreedArg2Disagreed(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && !isRange2Match(mUnit, aUnit)) {
					PipeUnitPair pair = new PipeUnitPair();
					pair.manualUnit = mUnit;
					pair.autoUnit = aUnit;
					pairs.add(pair);
					it.remove();
					autoIt.remove();
					break;
				}
			}
		}
	}

	public static void addOnlyRangesExact(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1ExactMatch(mUnit, aUnit)
						&& isRange2ExactMatch(mUnit, aUnit)) {
					PipeUnitPair pair = new PipeUnitPair();
					pair.manualUnit = mUnit;
					pair.autoUnit = aUnit;
					pairs.add(pair);
					it.remove();
					autoIt.remove();
					break;
				}
			}
		}
	}

	public static void addDisagreedArg1Only(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && !isRange2Match(mUnit, aUnit)) {
					PipeUnitPair pair = new PipeUnitPair();
					pair.manualUnit = mUnit;
					pair.autoUnit = aUnit;
					pairs.add(pair);
					it.remove();
					autoIt.remove();
					break;
				}
			}
		}
	}

	public static void addDisagreedArg2Only(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (!isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					PipeUnitPair pair = new PipeUnitPair();
					pair.manualUnit = mUnit;
					pair.autoUnit = aUnit;
					pairs.add(pair);
					it.remove();
					autoIt.remove();
					break;
				}
			}
		}
	}

	public static void addDisagreedPartial(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					PipeUnitPair pair = new PipeUnitPair();
					pair.manualUnit = mUnit;
					pair.autoUnit = aUnit;
					pairs.add(pair);
					it.remove();
					autoIt.remove();
					break;
				}
			}
		}
	}

	public static void addAgreedExplicits(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					if (mUnit.getElementType().equals("Explicit")
							&& aUnit.getElementType().equals("Explicit")) {
						if (isConnectiveMatch(mUnit, aUnit)) {
							PipeUnitPair pair = new PipeUnitPair();
							pair.manualUnit = mUnit;
							pair.autoUnit = aUnit;
							pairs.add(pair);
							it.remove();
							autoIt.remove();
							break;
						}
					}
				}
			}
		}
	}

	public static void addAgreedExplicitsExact(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1ExactMatch(mUnit, aUnit)
						&& isRange2ExactMatch(mUnit, aUnit)) {
					if (mUnit.getElementType().equals("Explicit")
							&& aUnit.getElementType().equals("Explicit")) {
						if (isConnectiveMatch(mUnit, aUnit)) {
							PipeUnitPair pair = new PipeUnitPair();
							pair.manualUnit = mUnit;
							pair.autoUnit = aUnit;
							pairs.add(pair);
							it.remove();
							autoIt.remove();
							break;
						}
					}
				}
			}
		}
	}

	public static void addAgreedArg1OnlyExplicit(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && !isRange2Match(mUnit, aUnit)) {
					if (mUnit.getElementType().equals("Explicit")
							&& aUnit.getElementType().equals("Explicit")) {
						if (isConnectiveMatch(mUnit, aUnit)) {
							PipeUnitPair pair = new PipeUnitPair();
							pair.manualUnit = mUnit;
							pair.autoUnit = aUnit;
							pairs.add(pair);
							it.remove();
							autoIt.remove();
							break;
						}
					}
				}
			}
		}
	}

	public static void addAgreedArg2OnlyExplicit(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (!isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					if (mUnit.getElementType().equals("Explicit")
							&& aUnit.getElementType().equals("Explicit")) {
						if (isConnectiveMatch(mUnit, aUnit)) {
							PipeUnitPair pair = new PipeUnitPair();
							pair.manualUnit = mUnit;
							pair.autoUnit = aUnit;
							pairs.add(pair);
							it.remove();
							autoIt.remove();
							break;
						}
					}
				}
			}
		}
	}

	public static void addAgreedOthersExact(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs, String type) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1ExactMatch(mUnit, aUnit)
						&& isRange2ExactMatch(mUnit, aUnit)) {
					if (mUnit.getElementType().equals(type)
							&& aUnit.getElementType().equals(type)) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static void addAgreedOthers(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs, String type) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					if (mUnit.getElementType().equals(type)
							&& aUnit.getElementType().equals(type)) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static void addAgreedOthersArg1Only(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs, String type) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && !isRange2Match(mUnit, aUnit)) {
					if (mUnit.getElementType().equals(type)
							&& aUnit.getElementType().equals(type)) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static void addAgreedOthersArg2Only(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs, String type) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (!isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					if (mUnit.getElementType().equals(type)
							&& aUnit.getElementType().equals(type)) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static void addOnlyRangeMatches(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && isRange2Match(mUnit, aUnit)) {
					if (!(mUnit.getElementType().equals("Explicit") && aUnit
							.getElementType().equals("Explicit"))) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static void addOnlyRangeMatchesArg2(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && !isRange2Match(mUnit, aUnit)) {
					if (!(mUnit.getElementType().equals("Explicit") && aUnit
							.getElementType().equals("Explicit"))) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static void addOnlyRangeMatchesArg1(List<PipeUnit> manualUnits,
			List<PipeUnit> autoUnits, List<PipeUnitPair> pairs) {
		Iterator<PipeUnit> it = manualUnits.iterator();
		while (it.hasNext()) {
			Iterator<PipeUnit> autoIt = autoUnits.iterator();
			PipeUnit mUnit = it.next();
			while (autoIt.hasNext()) {
				PipeUnit aUnit = autoIt.next();
				if (isRange1Match(mUnit, aUnit) && !isRange2Match(mUnit, aUnit)) {
					if (!(mUnit.getElementType().equals("Explicit") && aUnit
							.getElementType().equals("Explicit"))) {
						PipeUnitPair pair = new PipeUnitPair();
						pair.manualUnit = mUnit;
						pair.autoUnit = aUnit;
						pairs.add(pair);
						it.remove();
						autoIt.remove();
						break;
					}
				}
			}
		}
	}

	public static boolean isRange1Match(PipeUnit manualUnit, PipeUnit autoUnit) {
		String range1TxtManual = manualUnit.getRange1Txt();
		String range1TxtAuto = autoUnit.getRange1TxtAuto();
		range1TxtManual = range1TxtManual.replaceAll("[^a-zA-Z]", "");
		range1TxtAuto = range1TxtAuto.replaceAll("[^a-zA-Z]", "");

		range1TxtManual = range1TxtManual.replaceAll(" ", "");
		range1TxtAuto = range1TxtAuto.replaceAll(" ", "");
		if (range1TxtManual.trim().length() == 0
				&& range1TxtAuto.trim().length() == 0) {
			return true;
		} else {
			if (range1TxtManual.contains(range1TxtAuto)
					|| range1TxtAuto.contains(range1TxtManual)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean isRange1ExactMatch(PipeUnit manualUnit,
			PipeUnit autoUnit) {
		String range1TxtManual = manualUnit.getRange1Txt();
		String range1TxtAuto = autoUnit.getRange1TxtAuto();

		range1TxtManual = range1TxtManual.replaceAll("[^a-zA-Z]", "");
		range1TxtAuto = range1TxtAuto.replaceAll("[^a-zA-Z]", "");
		range1TxtManual = range1TxtManual.replaceAll(" ", "");
		range1TxtAuto = range1TxtAuto.replaceAll(" ", "");

		return range1TxtManual.equals(range1TxtAuto);
	}

	public static boolean isRange2ExactMatch(PipeUnit manualUnit,
			PipeUnit autoUnit) {
		String range2TxtManual = manualUnit.getRange2Txt();
		String range2TxtAuto = autoUnit.getRange2TxtAuto();

		range2TxtManual = range2TxtManual.replaceAll("[^a-zA-Z]", "");
		range2TxtAuto = range2TxtAuto.replaceAll("[^a-zA-Z]", "");
		range2TxtManual = range2TxtManual.replaceAll(" ", "");
		range2TxtAuto = range2TxtAuto.replaceAll(" ", "");

		return range2TxtManual.equals(range2TxtAuto);
	}

	public static boolean isRange2Match(PipeUnit manualUnit, PipeUnit autoUnit) {
		String range2TxtManual = manualUnit.getRange2Txt();
		String range2TxtAuto = autoUnit.getRange2TxtAuto();
		range2TxtManual = range2TxtManual.replaceAll("[^a-zA-Z]", "");
		range2TxtAuto = range2TxtAuto.replaceAll("[^a-zA-Z]", "");
		range2TxtManual = range2TxtManual.replaceAll(" ", "");
		range2TxtAuto = range2TxtAuto.replaceAll(" ", "");

		if (range2TxtManual.trim().length() == 0
				&& range2TxtAuto.trim().length() == 0) {
			return true;
		} else {
			if (range2TxtManual.contains(range2TxtAuto)
					|| range2TxtAuto.contains(range2TxtManual)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean isConnectiveMatch(PipeUnit manualUnit,
			PipeUnit autoUnit) {
		String connectiveManual = manualUnit.getConnectiveManual();
		String connectiveAuto = autoUnit.getConnectiveAuto();
		if (connectiveAuto.trim().length() > 0) {
			if (connectiveManual.trim().length() > 0) {
				if (!connectiveManual.trim().contains(connectiveAuto.trim())
						&& !connectiveAuto.trim().contains(
								connectiveManual.trim())) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public static String compare(ManualParseResultFile manualFile,
			ParseResultFile autoFile, TotalSum sum) {
		String result = "";
		List<PipeUnit> manualUnits = manualFile.getPipes();
		int manualCount = manualUnits.size();
		List<PipeUnit> autoUnits = autoFile.getPipes();
		int autoCount = autoUnits.size();

		int biggerCount = manualCount;
		if (autoCount > biggerCount)
			biggerCount = autoCount;

		int agreedCount = 0;
		int agreedTypeCount = 0;

		int agreedExplicit = 0;
		int agreedImplicit = 0;
		int agreedEnt = 0;
		int agreedNoEnt = 0;

		int agreedComparison = 0;
		int agreedContingency = 0;
		int agreedExpansion = 0;
		int agreedTemporal = 0;

		List<String> agreedDetails = new ArrayList<String>();
		List<String> typeAgreedDetails = new ArrayList<String>();
		List<String> disagreedDetails = new ArrayList<String>();
		List<String> typeDisagreedDetails = new ArrayList<String>();

		for (PipeUnit manualUnit : manualUnits) {
			boolean found = false;
			for (PipeUnit autoUnit : autoUnits) {
				boolean isRange1Match = false;
				boolean isRange2Match = false;
				boolean isConnectiveMatch = true;

				String range1TxtManual = manualUnit.getRange1Txt();
				String range2TxtManual = manualUnit.getRange2Txt();
				String connectiveManual = manualUnit.getConnectiveManual();
				String range1TxtAuto = autoUnit.getRange1TxtAuto();
				String range2TxtAuto = autoUnit.getRange2TxtAuto();
				String connectiveAuto = autoUnit.getConnectiveAuto();

				/*
				 * range1TxtManual = range1TxtManual.replaceAll(" ", "");
				 * range2TxtManual = range2TxtManual.replaceAll(" ", "");
				 * range1TxtManual = range1TxtManual.replaceAll("“", "\"");
				 * range2TxtManual = range2TxtManual.replaceAll("”", "\"");
				 * range1TxtAuto = range1TxtAuto.replaceAll(" ", "");
				 * range2TxtAuto = range2TxtAuto.replaceAll(" ", "");
				 */
				range1TxtManual = range1TxtManual.replaceAll("[^a-zA-Z]", "");
				range2TxtManual = range2TxtManual.replaceAll("[^a-zA-Z]", "");
				range1TxtAuto = range1TxtAuto.replaceAll("[^a-zA-Z]", "");
				range2TxtAuto = range2TxtAuto.replaceAll("[^a-zA-Z]", "");

				if (range1TxtManual.trim().length() == 0
						&& range1TxtAuto.trim().length() == 0) {
					isRange1Match = true;
				} else {
					if (range1TxtManual.contains(range1TxtAuto)
							|| range1TxtAuto.contains(range1TxtManual)) {
						isRange1Match = true;
					} else {
						isRange1Match = false;
					}
				}

				if (connectiveManual.trim().length() > 0) {
					if (connectiveAuto.trim().length() > 0) {
						if (!connectiveManual.trim().contains(
								connectiveAuto.trim())
								&& !connectiveAuto.trim().contains(
										connectiveManual.trim())) {
							isConnectiveMatch = false;
						}
					} else {
						isConnectiveMatch = false;
					}
				}

				if (range2TxtManual.trim().length() == 0
						&& range2TxtAuto.trim().length() == 0) {
					isRange2Match = true;
				} else {
					if (range2TxtManual.contains(range2TxtAuto)
							|| range2TxtAuto.contains(range2TxtManual)) {
						isRange2Match = true;
					} else {
						isRange2Match = false;
					}
				}

				if (isRange1Match && isRange2Match && isConnectiveMatch) {
					found = true;
					agreedCount++;
					String mType = manualUnit.getElementType();
					String aType = autoUnit.getElementType();
					String mSenseType = manualUnit.getManualRelationType();
					String aSenseType = autoUnit.getRelationType();

					String str = "PARSER|" + aType + "|" + aSenseType + "|"
							+ autoUnit.getRange1TxtAuto() + "|"
							+ autoUnit.getRange2TxtAuto();
					str += "\n";
					str += "KFR|" + mType + "|" + mSenseType + "|"
							+ manualUnit.getRange1Txt() + "|"
							+ manualUnit.getRange2Txt();
					str += "\n";

					if (mType.equals(aType)) {
						if (mType.equals("Explicit")) {
							agreedExplicit++;
						} else if (mType.equals("Implicit")) {
							agreedImplicit++;
						} else if (mType.equals("EntRel")) {
							agreedEnt++;
						} else {
							agreedNoEnt++;
						}
						agreedDetails.add(str);

						if (mSenseType.equals(aSenseType)) {
							if (mSenseType.equals("Comparison")) {
								agreedComparison++;
								agreedTypeCount++;
							} else if (mSenseType.equals("Contingency")) {
								agreedContingency++;
								agreedTypeCount++;
							} else if (mSenseType.equals("Expansion")) {
								agreedExpansion++;
								agreedTypeCount++;
							} else if (mSenseType.equals("Temporal")) {
								agreedTemporal++;
								agreedTypeCount++;
							}

							typeAgreedDetails.add(str);
						} else {
							String disagreedKey = "KFR|" + mSenseType
									+ "--PARSER|" + aSenseType;
							sum.addDisagreedType(disagreedKey);
							typeDisagreedDetails.add(str);
						}
					} else {
						String disagreedKey = "KFR|" + mType + "--PARSER|"
								+ aType;
						sum.addDiagreedEnt(disagreedKey);
						disagreedDetails.add(str);
					}
				} else {

				}
			}
			if (found == false) {
				String str = "KFR ONLY|" + manualUnit.getElementType() + "|"
						+ manualUnit.getManualRelationType() + "|"
						+ manualUnit.getRange1Txt() + "|"
						+ manualUnit.getRange2Txt();
				disagreedDetails.add(str);
			}
		}

		for (PipeUnit autoUnit : autoUnits) {
			boolean found = false;
			for (PipeUnit manualUnit : manualUnits) {
				boolean isRange1Match = false;
				boolean isRange2Match = false;
				boolean isConnectiveMatch = true;

				String range1TxtManual = manualUnit.getRange1Txt();
				String range2TxtManual = manualUnit.getRange2Txt();
				String range1TxtAuto = autoUnit.getRange1TxtAuto();
				String range2TxtAuto = autoUnit.getRange2TxtAuto();

				range1TxtManual = range1TxtManual.replaceAll("[^a-zA-Z]", "");
				range2TxtManual = range2TxtManual.replaceAll("[^a-zA-Z]", "");
				range1TxtAuto = range1TxtAuto.replaceAll("[^a-zA-Z]", "");
				range2TxtAuto = range2TxtAuto.replaceAll("[^a-zA-Z]", "");

				if (range1TxtManual.trim().length() == 0
						&& range1TxtAuto.trim().length() == 0) {
					isRange1Match = true;
				} else {
					if (range1TxtManual.contains(range1TxtAuto)
							|| range1TxtAuto.contains(range1TxtManual)) {
						isRange1Match = true;
					} else {
						isRange1Match = false;
					}
				}

				String connectiveManual = manualUnit.getConnectiveManual();
				String connectiveAuto = autoUnit.getConnectiveAuto();

				if (connectiveManual.trim().length() > 0) {
					if (connectiveAuto.trim().length() > 0) {
						if (!connectiveManual.trim().contains(
								connectiveAuto.trim())
								&& !connectiveAuto.trim().contains(
										connectiveManual.trim())) {
							isConnectiveMatch = false;
						}
					} else {
						isConnectiveMatch = false;
					}
				}

				if (range2TxtManual.trim().length() == 0
						&& range2TxtAuto.trim().length() == 0) {
					isRange2Match = true;
				} else {
					if (range2TxtManual.contains(range2TxtAuto)
							|| range2TxtAuto.contains(range2TxtManual)) {
						isRange2Match = true;
					} else {
						isRange2Match = false;
					}
				}

				if (isRange1Match && isRange2Match && isConnectiveMatch)
					found = true;
			}
			if (found == false) {
				String str = "PARSER ONLY|" + autoUnit.getElementType() + "|"
						+ autoUnit.getRelationType() + "|"
						+ autoUnit.getRange1TxtAuto() + "|"
						+ autoUnit.getRange2TxtAuto();
				disagreedDetails.add(str);
			}
		}
		int senseCount = agreedExplicit + agreedImplicit;
		int agreedCorrect = agreedExplicit + agreedImplicit + agreedEnt
				+ agreedNoEnt;

		String start = "Relation Type: " + agreedCorrect + " common/"
				+ agreedCount + " total\n";
		sum.agreedAll += agreedCorrect;
		sum.totalAgreed += agreedCount;

		String kfrCountStr = "KFR: " + manualCount + " annotated relations\n";
		String autoCountStr = "PDTB: " + autoCount + " annotated relations\n";
		String agreedStr = "AGREED: \n" + agreedExplicit + " Explicit\n"
				+ agreedImplicit + " Implicit\n" + agreedEnt + " EntRel\n"
				+ agreedNoEnt + " NoRel\n";

		sum.kfrTotal += manualCount;
		sum.parserTotal += autoCount;

		sum.agreedExplicit += agreedExplicit;
		sum.agreedImplicit += agreedImplicit;
		sum.agreedEnt += agreedEnt;
		sum.agreedNoRel += agreedNoEnt;

		String agreedElement = "AGREED:\n";
		for (String str : agreedDetails) {
			agreedElement += str + "\n";
		}
		String agreedType = "AGREED:\n";
		for (String str : typeAgreedDetails) {
			agreedType += str + "\n";
		}

		String disAgreedStr = "DISAGREED:\n";
		for (String str : disagreedDetails) {
			disAgreedStr += str + "\n";
		}

		String disAgreedTypeStr = "DISAGREED:\n";
		for (String str : typeDisagreedDetails) {
			disAgreedTypeStr += str + "\n";
		}
		String secondStart = "4 Class Relation Sense: " + agreedTypeCount + "/"
				+ senseCount + " AGREED\n";
		secondStart += agreedComparison + " Comparison\n";
		secondStart += agreedContingency + " Contingency\n";
		secondStart += agreedExpansion + " Expansion\n";
		secondStart += agreedTemporal + " Temporal\n\n";

		sum.agreedSense += agreedTypeCount;
		sum.agreedCompare += agreedComparison;
		sum.agreedContingency += agreedContingency;
		sum.agreedExpansion += agreedExpansion;
		sum.agreedTemporal += agreedTemporal;
		sum.totalSenseAgreed += senseCount;

		result += start;
		result += kfrCountStr;
		result += autoCountStr;
		result += agreedStr;
		result += "\n";
		result += secondStart;

		result += "Relation Type\n";
		result += agreedElement;
		result += disAgreedStr;

		result += "\n";
		result += "Relation sense\n";
		result += agreedType;
		result += disAgreedTypeStr;
		return result;
	}
}

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
import edu.pitt.cs.model.PipeAttribute;
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
		if (autoUnit.getElementType().equals("Explicit"))
			str += "|" + autoUnit.getConnectiveAuto();
		str += "\n";
		str += "KFR|" + mType + "|" + mSenseType2 + "|"
				+ manualUnit.getRange1Txt() + "|" + manualUnit.getRange2Txt();
		if (manualUnit.getElementType().equals("Explicit"))
			str += "|" + manualUnit.getConnectiveManual();
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

	int nonadjacentCnt = 0;

	Hashtable<String, Integer> overallCounting = new Hashtable<String, Integer>();

	int explicit_comparision = 0;
	int explicit_contingency = 1;
	int explicit_expansion = 2;
	int explicit_temporal = 3;
	int implicit_comparison = 4;
	int implicit_contingency = 5;
	int implicit_expansion = 6;
	int implicit_temporal = 7;
	int entRel = 8;
	int altLex = 9;

	// To calculate precision/recall/f1
	// task1. recognizing explicit discourse connective
	int explicitConnective_KFR = 0;
	int explicitConnective_PARSER = 0;
	int agreedExplicitConnective = 0;
	
	
	int real_explicitConnective_KFR = 0;
	int real_explicitConnective_PARSER = 0;
	int real_agreedExplicitConnective = 0;

	public double getPrecTask1() {
		return real_agreedExplicitConnective * 1.0 / real_explicitConnective_PARSER;
	}

	public double getRecallTask1() {
		return real_agreedExplicitConnective * 1.0 / real_explicitConnective_KFR;
	}

	public double getF1Task1() {
		double p = getPrecTask1();
		double r = getRecallTask1();
		return 2 * p * r / (p + r);
	}

	// task2. Span identification
	int exactSpanMatch = 0;
	int exactSpanMatch_Explicit = 0;
	int exactSpanMatch_NonExplicit = 0;

	int partialSpanMatch = 0;
	int partialSpanMatch_Explicit = 0;
	int partialSpanMatch_NonExplicit = 0;

	int spans_KFR = 0;
	int spans_KFR_Explicit = 0;
	int spans_KFR_NonExplicit = 0;
	int spans_PARSER = 0;
	int spans_PARSER_Explicit = 0;
	int spans_PARSER_NonExplicit = 0;

	public double getPrecTask2_ExactSpan() {
		return exactSpanMatch * 1.0 / spans_PARSER;
	}

	public double getRecallTask2_ExactSpan() {
		return exactSpanMatch * 1.0 / spans_KFR;
	}

	public double getFTask2_ExactSpan() {
		double p = getPrecTask2_ExactSpan();
		double r = getRecallTask2_ExactSpan();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask2_ExactSpan_Explicit() {
		return exactSpanMatch_Explicit * 1.0 / spans_PARSER_Explicit;
	}

	public double getRecallTask2_ExactSpan_Explicit() {
		return exactSpanMatch_Explicit * 1.0 / spans_KFR_Explicit;
	}

	public double getFTask2_ExactSpan_Explicit() {
		double p = getPrecTask2_ExactSpan_Explicit();
		double r = getRecallTask2_ExactSpan_Explicit();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask2_ExactSpan_NonExplicit() {
		return exactSpanMatch_NonExplicit * 1.0 / spans_PARSER_NonExplicit;
	}

	public double getRecallTask2_ExactSpan_NonExplicit() {
		return exactSpanMatch_NonExplicit * 1.0 / spans_KFR_NonExplicit;
	}

	public double getFTask2_ExactSpan_NonExplicit() {
		double p = getPrecTask2_ExactSpan_NonExplicit();
		double r = getRecallTask2_ExactSpan_NonExplicit();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask2_Partial() {
		return partialSpanMatch * 1.0 / spans_PARSER;
	}

	public double getRecallTask2_Partial() {
		return partialSpanMatch * 1.0 / spans_KFR;
	}

	public double getFTask2_Partial() {
		double p = getPrecTask2_Partial();
		double r = getRecallTask2_Partial();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask2_Partial_Explicit() {
		return partialSpanMatch_Explicit * 1.0 / spans_PARSER_Explicit;
	}

	public double getRecallTask2_Partial_Explicit() {
		return partialSpanMatch_Explicit * 1.0 / spans_KFR_Explicit;
	}

	public double getFTask2_Partial_Explicit() {
		double p = getPrecTask2_Partial_Explicit();
		double r = getRecallTask2_Partial_Explicit();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask2_Partial_NonExplicit() {
		return partialSpanMatch_NonExplicit * 1.0 / spans_PARSER_NonExplicit;
	}

	public double getRecallTask2_Partial_NonExplicit() {
		return partialSpanMatch_NonExplicit * 1.0 / spans_KFR_NonExplicit;
	}

	public double getFTask2_Partial_NonExplicit() {
		double p = getPrecTask2_Partial_NonExplicit();
		double r = getRecallTask2_Partial_NonExplicit();
		return 2 * p * r / (p + r);
	}

	// task3. End to end
	int allCorrect = 0;
	int partialAllCorrect = 0;

	int allCorrect_Explicit = 0;
	int allCorrect_NonExplicit = 0;

	int allCorrect_Explicit_Partial = 0;
	int allCorrect_NonExplicit_Partial = 0;

	public double getPrecTask3() {
		return allCorrect * 1.0 / spans_PARSER;
	}

	public double getRecallTask3() {
		return allCorrect * 1.0 / spans_KFR;
	}

	public double getFTask3() {
		double p = getPrecTask3();
		double r = getRecallTask3();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask3_Explicit() {
		return allCorrect_Explicit * 1.0 / spans_PARSER_Explicit;
	}

	public double getRecallTask3_Explicit() {
		return allCorrect_Explicit * 1.0 / spans_KFR_Explicit;
	}

	public double getFTask3_Explicit() {
		double p = getPrecTask3_Explicit();
		double r = getRecallTask3_Explicit();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask3_NonExplicit() {
		return allCorrect_NonExplicit * 1.0 / spans_PARSER_NonExplicit;
	}

	public double getRecallTask3_NonExplicit() {
		return allCorrect_NonExplicit * 1.0 / spans_KFR_NonExplicit;
	}

	public double getFTask3_NonExplicit() {
		double p = getPrecTask3_NonExplicit();
		double r = getRecallTask3_NonExplicit();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask3_Partial() {
		return partialAllCorrect * 1.0 / spans_PARSER;
	}

	public double getRecallTask3_Partial() {
		return partialAllCorrect * 1.0 / spans_KFR;
	}

	public double getFTask3_Partial() {
		double p = getPrecTask3_Partial();
		double r = getRecallTask3_Partial();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask3_Explicit_Partial() {
		return allCorrect_Explicit_Partial * 1.0 / spans_PARSER_Explicit;
	}

	public double getRecallTask3_Explicit_Partial() {
		return allCorrect_Explicit_Partial * 1.0 / spans_KFR_Explicit;
	}

	public double getFTask3_Explicit_Partial() {
		double p = getPrecTask3_Explicit_Partial();
		double r = getRecallTask3_Explicit_Partial();
		return 2 * p * r / (p + r);
	}

	public double getPrecTask3_NonExplicit_Partial() {
		return allCorrect_NonExplicit_Partial * 1.0 / spans_PARSER_NonExplicit;
	}

	public double getRecallTask3_NonExplicit_Partial() {
		return allCorrect_NonExplicit_Partial * 1.0 / spans_KFR_NonExplicit;
	}

	public double getFTask3_NonExplicit_Partial() {
		double p = getPrecTask3_NonExplicit_Partial();
		double r = getRecallTask3_NonExplicit_Partial();
		return 2 * p * r / (p + r);
	}

	public void printAllPRF() {
		System.out.println("Nonadjacent KFR implicit: " + this.nonadjacentCnt);

		System.out.println("***Task 1: Explicit connective recognition***");
		System.out.println("Precision: " + getPrecTask1());
		System.out.println("Recall: " + getRecallTask1());
		System.out.println("F1: " + getF1Task1());
		System.out.println();

		System.out.println("***Task 2: Span recognition***");
		System.out.println("Exact matches");
		System.out.println("Overall precision: " + getPrecTask2_ExactSpan());
		System.out.println("Overall recall: " + getRecallTask2_ExactSpan());
		System.out.println("Overall F1: " + getFTask2_ExactSpan());

		System.out.println("Explicit precision: "
				+ getPrecTask2_ExactSpan_Explicit());
		System.out.println("Explicit recall: "
				+ getRecallTask2_ExactSpan_Explicit());
		System.out.println("Explicit F1: " + getFTask2_ExactSpan_Explicit());

		System.out.println("NonExplicit precision: "
				+ getPrecTask2_ExactSpan_NonExplicit());
		System.out.println("NonExplicit recall: "
				+ getRecallTask2_ExactSpan_NonExplicit());
		System.out.println("NonExplicit F1: "
				+ getFTask2_ExactSpan_NonExplicit());
		System.out.println();

		System.out.println("Partial matches");
		System.out.println("Overall precision: " + getPrecTask2_Partial());
		System.out.println("Overall recall: " + getRecallTask2_Partial());
		System.out.println("Overall F1: " + getFTask2_Partial());

		System.out.println("Explicit precision: "
				+ getPrecTask2_Partial_Explicit());
		System.out.println("Explicit recall: "
				+ getRecallTask2_Partial_Explicit());
		System.out.println("Explicit F1: " + getFTask2_Partial_Explicit());

		System.out.println("NonExplicit precision: "
				+ getPrecTask2_Partial_NonExplicit());
		System.out.println("NonExplicit recall: "
				+ getRecallTask2_Partial_NonExplicit());
		System.out
				.println("NonExplicit F1: " + getFTask2_Partial_NonExplicit());
		System.out.println();

		System.out.println("***Task 3: Getting everything correct***");
		System.out.println("Exact matches");
		System.out.println("Overall precision: " + getPrecTask3());
		System.out.println("Overall recall: " + getRecallTask3());
		System.out.println("Overall F1: " + getFTask3());

		System.out.println("Explicit precision: " + getPrecTask3_Explicit());
		System.out.println("Explicit recall: " + getRecallTask3_Explicit());
		System.out.println("Explicit F1: " + getFTask3_Explicit());

		System.out.println("NonExplicit precision: "
				+ getPrecTask3_NonExplicit());
		System.out.println("NonExplicit recall: "
				+ getRecallTask3_NonExplicit());
		System.out.println("NonExplicit F1: " + getFTask3_NonExplicit());
		System.out.println();

		System.out.println("Partial matches");
		System.out.println("Overall precision: " + getPrecTask3_Partial());
		System.out.println("Overall recall: " + getRecallTask3_Partial());
		System.out.println("Overall F1: " + getFTask3_Partial());

		System.out.println("Explicit precision: "
				+ getPrecTask3_Explicit_Partial());
		System.out.println("Explicit recall: "
				+ getRecallTask3_Explicit_Partial());
		System.out.println("Explicit F1: " + getFTask3_Explicit_Partial());

		System.out.println("NonExplicit precision: "
				+ getPrecTask3_NonExplicit_Partial());
		System.out.println("NonExplicit recall: "
				+ getRecallTask3_NonExplicit_Partial());
		System.out
				.println("NonExplicit F1: " + getFTask3_NonExplicit_Partial());
		System.out.println();
	}

	int[][] confusionMatrix = new int[10][10];

	public int[][] getCM() {
		return confusionMatrix;
	}

	public void addCountCM(String kfrType, String kfrSense, String autoType,
			String autoSense) {
		/*
		 * if (kfrType.equals("Explicit") && kfrSense.equals("Expansion") &&
		 * autoType.equals("Implicit") && autoSense.equals("Expansion")) {
		 * System.out.println("LLLL"); }
		 */
		int i = getIndex(kfrType, kfrSense);
		int j = getIndex(autoType, autoSense);
		confusionMatrix[i][j]++;
		/*
		 * if(i == 2 && j == 6) { System.out.println(kfrType);
		 * System.out.println(kfrSense); System.out.println(autoType);
		 * System.out.println(autoSense); }
		 */

	}

	public void countCorrect(String kfrType, String kfrSense, String autoType,
			String autoSense, boolean isExact) {
		if (kfrType.equals(autoType)) {
			if (kfrType.equals("EntRel") || kfrType.equals("AltLex")) {
				this.partialAllCorrect++;
				this.allCorrect_NonExplicit_Partial++;
				if (isExact) {
					this.allCorrect++;
					this.allCorrect_NonExplicit++;
				}
			} else {
				if (kfrSense.equals(autoSense)) {
					if (kfrType.equals("Explicit")) {
						this.partialAllCorrect++;
						this.allCorrect_Explicit_Partial++;
						if (isExact) {
							this.allCorrect++;
							this.allCorrect_Explicit++;
						}
					} else {
						this.partialAllCorrect++;
						this.allCorrect_NonExplicit_Partial++;
						if (isExact) {
							this.allCorrect++;
							this.allCorrect_NonExplicit++;
						}
					}
				}
			}
		}
	}

	public void addConfusionMatrix(TotalSum sum) {
		int[][] added = sum.getCM();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				confusionMatrix[i][j] += added[i][j];
			}
		}
	}

	public void printConfusionMatrix() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.print(confusionMatrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public int getIndex(String type, String sense) {
		if (type.equals("EntRel")) {
			return entRel;
		} else if (type.equals("AltLex")) {
			return altLex;
		} else {
			int i = 0;
			int j = 0;
			if (type.equals("Explicit")) {
				i = 0;
			} else if (type.equals("Implicit")) {
				i = 1;
			}

			if (sense.equals("Comparison")) {
				j = 0;
			} else if (sense.equals("Contingency")) {
				j = 1;
			} else if (sense.equals("Expansion")) {
				j = 2;
			} else if (sense.equals("Temporal")) {
				j = 3;
			}
			return i * 4 + j;
		}
	}

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
		String[] types = { "Explicit", "Implicit", "EntRel", "AltLex" };
		for (String type : types) {
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
		String[] types = { "Explicit", "Implicit", "EntRel", "AltLex" };
		int agreedTotal = 0;
		int notAgreedTotal = 0;
		String str = "";
		for (String type : types) {
			for (String match : matches) {
				String matchkey = match + "-" + type;
				int val = 0;
				Iterator<String> it = overallCounting.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (key.contains(matchkey)) {
						int count = 0;
						if (overallCounting.containsKey(key))
							count = overallCounting.get(key);
						val += count;
					}
				}
				str += val + " " + type + " with " + match + "\n";
				agreedTotal += val;
			}
		}

		Iterator<String> it = overallCounting.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.contains("NotAgreed")) {
				int count = 0;
				if (overallCounting.containsKey(key))
					count = overallCounting.get(key);
				notAgreedTotal += count;
			}
		}
		str = agreedTotal + " Agreed Type\n\n" + notAgreedTotal
				+ " No Agreed Type\n\n" + str;
		return str;
	}

	public String generateOnlyStr() {
		String[] starts = { "KFR ONLY", "PARSER ONLY" };
		String str = "";
		for (String start : starts) {
			int countAll = 0;
			String startStr = "";
			Iterator<String> it = overallCounting.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.contains(start)) {
					int count = 0;
					if (overallCounting.containsKey(key))
						count = overallCounting.get(key);
					startStr += count + " " + key + "\n";
					countAll += count;
				}
			}
			startStr = countAll + " " + start + "\n" + startStr;
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
				if (overallCounting.containsKey(tag))
					count = overallCounting.get(tag);
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
						if (overallCounting.containsKey(key))
							count = overallCounting.get(key);
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

		for (String match : matches) {
			str += "\n";
			str += match + " cases\n";
			String matchKey = match + "-" + "NotAgreed";
			Iterator<String> it = overallCounting.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.contains(matchKey)) {
					int count = 0;
					if (overallCounting.containsKey(key))
						count = overallCounting.get(key);
					str += count + " " + key + "\n";
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

		this.nonadjacentCnt += sum.nonadjacentCnt;
		this.agreedSense += sum.agreedSense;
		this.agreedCompare += sum.agreedCompare;
		this.agreedContingency += sum.agreedContingency;
		this.agreedExpansion += sum.agreedExpansion;
		this.agreedTemporal += sum.agreedTemporal;
		this.totalSenseAgreed += sum.totalSenseAgreed;

		this.agreedExplicitConnective += sum.agreedExplicitConnective;
		this.explicitConnective_KFR += sum.explicitConnective_KFR;
		this.explicitConnective_PARSER += sum.explicitConnective_PARSER;
		
		this.real_agreedExplicitConnective += sum.real_agreedExplicitConnective;
		this.real_explicitConnective_KFR += sum.real_explicitConnective_KFR;
		this.real_explicitConnective_PARSER += sum.real_explicitConnective_PARSER;

		this.exactSpanMatch += sum.exactSpanMatch;
		this.exactSpanMatch_Explicit += sum.exactSpanMatch_Explicit;
		this.exactSpanMatch_NonExplicit += sum.exactSpanMatch_NonExplicit;

		this.partialSpanMatch += sum.partialSpanMatch;
		this.partialSpanMatch_Explicit += sum.partialSpanMatch_Explicit;
		this.partialSpanMatch_NonExplicit += sum.partialSpanMatch_NonExplicit;

		this.spans_KFR += sum.spans_KFR;
		this.spans_KFR_Explicit += sum.spans_KFR_Explicit;
		this.spans_KFR_NonExplicit += sum.spans_KFR_NonExplicit;

		this.spans_PARSER += sum.spans_PARSER;
		this.spans_PARSER_Explicit += sum.spans_PARSER_Explicit;
		this.spans_PARSER_NonExplicit += sum.spans_PARSER_NonExplicit;

		this.allCorrect += sum.allCorrect;
		this.partialAllCorrect += sum.partialAllCorrect;
		this.allCorrect_Explicit += sum.allCorrect_Explicit;
		this.allCorrect_Explicit_Partial += sum.allCorrect_Explicit_Partial;
		this.allCorrect_NonExplicit += sum.allCorrect_NonExplicit;
		this.allCorrect_NonExplicit_Partial += sum.allCorrect_NonExplicit_Partial;

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
		// countSenses(manualFolderPath, autoFolderPath);
		TotalSum sum1 = new TotalSum();
		TotalSum sum2 = new TotalSum();
		TotalSum[] sums = { sum1, sum2 };
		compareV3(manualFolderPath, autoFolderPath, outputFolderPath, sums);
		sum1.addConfusionMatrix(sum2);
		sum1.addSum(sum2);

		sum1.allCorrect_Explicit += 34;
		sum1.allCorrect_Explicit_Partial += (34 + 14);
		// sum1.agreedExplicitConnective += 37 + 14;
		sum1.allCorrect += 34;
		sum1.partialAllCorrect += (34 + 14);

		sum1.exactSpanMatch += 37;
		sum1.partialSpanMatch += (37 + 14);
		sum1.exactSpanMatch_Explicit += 37;
		sum1.partialSpanMatch_Explicit += (37 + 14);

		sum1.spans_PARSER -= 584;
		sum1.spans_PARSER_Explicit -= 102;
		sum1.spans_PARSER_NonExplicit -= 482;
		sum1.explicitConnective_PARSER -= 102;

		sum1.printConfusionMatrix();

		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outputFolderPath + "/" + "compareSumD1.txt"));
		writer.write(sums[0].printCounting());
		writer.close();
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(
				outputFolderPath + "/" + "compareSumD2.txt"));
		writer2.write(sums[1].printCounting());
		writer2.close();

		sum1.printAllPRF();
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
				// ModificationRemover.removeBoundaryCases(aFile, path);
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

	public static void countTypes(String manualFolderPath, String autoFolderPath)
			throws IOException {
		List<ManualParseResultFile> manualResults = ManualParseResultReader
				.readFiles(manualFolderPath);
		List<ParseResultFile> autoResults = ParseResultReader
				.readFiles(autoFolderPath);

		int explicitD1 = 0;
		int implicitD1 = 0;
		int entRelD1 = 0;
		int altLexD1 = 0;
		int explicitD2 = 0;
		int implicitD2 = 0;
		int entRelD2 = 0;
		int altLexD2 = 0;

		for (ManualParseResultFile mFile : manualResults) {
			String fileName = mFile.getFileName();
			if (fileName.contains("draft1")) {
				List<PipeUnit> units = mFile.getPipes();
				for (PipeUnit unit : units) {
					if (unit.getElementType().equals("Explicit")) {
						explicitD1++;
					} else if (unit.getElementType().equals("Implicit")) {
						implicitD1++;
					} else if (unit.getElementType().equals("EntRel")) {
						entRelD1++;
					} else if (unit.getElementType().equals("AltLex")) {
						altLexD1++;
					}
				}
			} else if (fileName.contains("draft2")) {
				List<PipeUnit> units = mFile.getPipes();
				for (PipeUnit unit : units) {
					if (unit.getElementType().equals("Explicit")) {
						explicitD2++;
					} else if (unit.getElementType().equals("Implicit")) {
						implicitD2++;
					} else if (unit.getElementType().equals("EntRel")) {
						entRelD2++;
					} else if (unit.getElementType().equals("AltLex")) {
						altLexD2++;
					}
				}
			}
		}

		int autoExplicitD1 = 0;
		int autoImplicitD1 = 0;
		int autoEntRelD1 = 0;
		int autoAltLexD1 = 0;
		int autoExplicitD2 = 0;
		int autoImplicitD2 = 0;
		int autoEntRelD2 = 0;
		int autoAltLexD2 = 0;

		for (ParseResultFile aFile : autoResults) {
			String fileName = aFile.getFileName();
			if (aFile.isPDTB1() && fileName.contains("draft1")) {
				List<PipeUnit> units = aFile.getPipes();
				for (PipeUnit unit : units) {
					if (unit.getElementType().equals("Explicit")) {
						autoExplicitD1++;
					} else if (unit.getElementType().equals("Implicit")) {
						autoImplicitD1++;
					} else if (unit.getElementType().equals("EntRel")) {
						autoEntRelD1++;
					} else if (unit.getElementType().equals("AltLex")) {
						autoAltLexD1++;
					}
				}
			} else if (aFile.isPDTB1() && fileName.contains("draft2")) {
				List<PipeUnit> units = aFile.getPipes();
				for (PipeUnit unit : units) {
					if (unit.getElementType().equals("Explicit")) {
						autoExplicitD2++;
					} else if (unit.getElementType().equals("Implicit")) {
						autoImplicitD2++;
					} else if (unit.getElementType().equals("EntRel")) {
						autoEntRelD2++;
					} else if (unit.getElementType().equals("AltLex")) {
						autoAltLexD2++;
					}
				}
			}
		}

		System.out.println("KFR -  Draft 1");
		System.out.println("Explicit: " + explicitD1);
		System.out.println("Implicit: " + implicitD1);
		System.out.println("EntRel: " + entRelD1);
		System.out.println("AltLex: " + altLexD1);

		System.out.println("KFR -  Draft 2");
		System.out.println("Explicit: " + explicitD2);
		System.out.println("Implicit: " + implicitD2);
		System.out.println("EntRel: " + entRelD2);
		System.out.println("AltLex: " + altLexD2);

		System.out.println("Auto -  Draft 1");
		System.out.println("Explicit: " + autoExplicitD1);
		System.out.println("Implicit: " + autoImplicitD1);
		System.out.println("EntRel: " + autoEntRelD1);
		System.out.println("AltLex: " + autoAltLexD1);

		System.out.println("Auto -  Draft 2");
		System.out.println("Explicit: " + autoExplicitD2);
		System.out.println("Implicit: " + autoImplicitD2);
		System.out.println("EntRel: " + autoEntRelD2);
		System.out.println("AltLex: " + autoAltLexD2);

	}

	public static void countSenses(String manualFolderPath,
			String autoFolderPath) throws IOException {
		List<ManualParseResultFile> manualResults = ManualParseResultReader
				.readFiles(manualFolderPath);
		List<ParseResultFile> autoResults = ParseResultReader
				.readFiles(autoFolderPath);

		int comparisonD1 = 0;
		int contingencyD1 = 0;
		int expansionD1 = 0;
		int temporalD1 = 0;
		int comparisonD2 = 0;
		int contingencyD2 = 0;
		int expansionD2 = 0;
		int temporalD2 = 0;

		String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";

		for (ManualParseResultFile mFile : manualResults) {
			ModificationRemover.feedTxtInfo(mFile, path);
			String fileName = mFile.getFileName();
			if (fileName.contains("draft1")) {
				List<PipeUnit> units = mFile.getPipes();
				for (PipeUnit unit : units) {
					if (unit.getManualRelationType().equals("Comparison")) {
						comparisonD1++;
					} else if (unit.getManualRelationType().equals(
							"Contingency")) {
						contingencyD1++;
					} else if (unit.getManualRelationType().equals("Expansion")) {
						expansionD1++;
					} else if (unit.getManualRelationType().equals("Temporal")) {
						temporalD1++;
					}
				}
			} else if (fileName.contains("draft2")) {
				List<PipeUnit> units = mFile.getPipes();
				for (PipeUnit unit : units) {
					if (unit.getManualRelationType().equals("Comparison")) {
						comparisonD2++;
					} else if (unit.getManualRelationType().equals(
							"Contingency")) {
						contingencyD2++;
					} else if (unit.getManualRelationType().equals("Expansion")) {
						expansionD2++;
					} else if (unit.getManualRelationType().equals("Temporal")) {
						temporalD2++;
					}
				}
			}
		}

		System.out.println("KFR -  Draft 1");
		System.out.println("Comparison: " + comparisonD1);
		System.out.println("Contingency: " + contingencyD1);
		System.out.println("Expansion: " + expansionD1);
		System.out.println("Temporal: " + temporalD1);

		System.out.println("KFR -  Draft 2");
		System.out.println("Comparison: " + comparisonD2);
		System.out.println("Contingency: " + contingencyD2);
		System.out.println("Expansion: " + expansionD2);
		System.out.println("Temporal: " + temporalD2);

	}

	public static void compareV3(String manualFolderPath,
			String autoFolderPath, String outputFolderPath, TotalSum[] sums)
			throws IOException {
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
			if (!aFile.isPDTB1()) {
				ModificationRemover.removeBoundaryCases(aFile, path);
				aFile.setPDTB2Filter();
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
					/*
					 * String outputPath = outputFolderPath + "/" + postFix +
					 * "/" + fileName + ".log"; BufferedWriter writer = new
					 * BufferedWriter(new FileWriter( outputPath));
					 * writer.write(compareStr); writer.close();
					 */
				}
			}
		}
	}

	public static boolean isConnectiveMatch(String connective1, String connective2) {
		int[] range1Indices = ModificationRemover.retrieveRanges(connective1);
		int[] range2Indices = ModificationRemover.retrieveRanges(connective2);
		int start1 = range1Indices[0];
		int end1 = range2Indices[1];
		int start2 = range2Indices[0];
		int end2 = range2Indices[1];
		
		if(start1==end1 || start2==end2) return false;
		if((start1>=start2 && start1<=end2)||(end1>=start2 && end1 <= end2)) {
			return true;
		}
		return false;
	}
	
	public static String compareV3(ManualParseResultFile manualFile,
			ParseResultFile autoFile, TotalSum sum) {
		List<PipeUnit> manualUnits = manualFile.getPipes();
		int manualCount = manualUnits.size();
		
		List<String> connectiveStrs = new ArrayList<String>();
		List<String> connectiveStrsAuto = new ArrayList<String>();
		for(PipeUnit unit: manualUnits) {
			if(unit.getElementType().equals("Explicit"))
			connectiveStrs.add(unit.getManualConnectiveRange());
		}
		
		
		printNonAjacentStr(manualUnits, sum);
		List<PipeUnit> autoUnits = autoFile.getPipes();
		int autoCount = autoUnits.size();
		for(PipeUnit unit: autoUnits) {
			if(unit.getElementType().equals("Explicit"))
			connectiveStrsAuto.add(unit.getAutoConnectiveRange());
		}
		
		sum.real_explicitConnective_KFR += connectiveStrs.size();
		sum.real_explicitConnective_PARSER += connectiveStrsAuto.size();
		
		Iterator<String> it = connectiveStrs.iterator();
		while(it.hasNext()) {
			String str = it.next();
			Iterator<String> it2 = connectiveStrsAuto.iterator();
			while(it2.hasNext()) {
				String str2 = it2.next();
				if(isConnectiveMatch(str, str2)) {
					sum.real_agreedExplicitConnective ++;
					it.remove();
					it2.remove();
					break;
				}
			}
		}
		
		sum.spans_PARSER += autoCount;
		sum.spans_KFR += manualCount;

		int explicitManualCount = 0;
		for (PipeUnit manualUnit : manualUnits) {
			if (manualUnit.getElementType().equals("Explicit")) {
				explicitManualCount++;
			}
		}
		int explicitAutoCount = 0;
		for (PipeUnit autoUnit : autoUnits) {
			if (autoUnit.getElementType().equals("Explicit")) {
				explicitAutoCount++;
			}
		}
		sum.spans_PARSER_Explicit += explicitAutoCount;
		sum.spans_KFR_Explicit += explicitManualCount;

		sum.spans_PARSER_NonExplicit += (autoCount - explicitAutoCount);
		sum.spans_KFR_NonExplicit += (manualCount - explicitManualCount);

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
		printKFRExplicit(agreedExplicitPairsExact);
		addMatrixCount(agreedExplicitPairsExact, sum, true);
		sum.agreedExplicitConnective += agreedExplicitPairsExact.size();
		sum.explicitConnective_KFR += agreedExplicitPairsExact.size();
		sum.explicitConnective_PARSER += agreedExplicitPairsExact.size();
		sum.exactSpanMatch += agreedExplicitPairsExact.size();
		sum.exactSpanMatch_Explicit += agreedExplicitPairsExact.size();
		sum.partialSpanMatch += agreedExplicitPairsExact.size();
		sum.partialSpanMatch_Explicit += agreedExplicitPairsExact.size();

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\exactMatchedExplicit.txt", true));
			for (PipeUnitPair pair : agreedExplicitPairsExact) {
				writer.write(pair.toString() + "\n");
			}
			writer.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		addAgreedOthersExact(manualUnits, autoUnits, agreedImplicitPairsExact,
				"Implicit");
		addMatrixCount(agreedImplicitPairsExact, sum, true);
		sum.exactSpanMatch += agreedImplicitPairsExact.size();
		sum.exactSpanMatch_NonExplicit += agreedImplicitPairsExact.size();
		sum.partialSpanMatch += agreedImplicitPairsExact.size();
		sum.partialSpanMatch_NonExplicit += agreedImplicitPairsExact.size();

		addAgreedOthersExact(manualUnits, autoUnits, agreedEntRelPairsExact,
				"EntRel");
		addMatrixCount(agreedEntRelPairsExact, sum, true);
		sum.exactSpanMatch += agreedEntRelPairsExact.size();
		sum.exactSpanMatch_NonExplicit += agreedEntRelPairsExact.size();
		sum.partialSpanMatch += agreedEntRelPairsExact.size();
		sum.partialSpanMatch_NonExplicit += agreedEntRelPairsExact.size();

		addAgreedOthersExact(manualUnits, autoUnits, agreedAltLexPairsExact,
				"AltLex");
		addMatrixCount(agreedAltLexPairsExact, sum, true);
		sum.exactSpanMatch += agreedAltLexPairsExact.size();
		sum.exactSpanMatch_NonExplicit += agreedAltLexPairsExact.size();
		sum.partialSpanMatch += agreedAltLexPairsExact.size();
		sum.partialSpanMatch_NonExplicit += agreedAltLexPairsExact.size();

		addOnlyRangesExact(manualUnits, autoUnits, notAgreedExact);
		printKFRExplicit(notAgreedExact);
		sum.exactSpanMatch += notAgreedExact.size();
		sum.exactSpanMatch_NonExplicit += notAgreedExact.size();
		sum.partialSpanMatch += notAgreedExact.size();
		sum.partialSpanMatch_NonExplicit += notAgreedExact.size();
		for (PipeUnitPair pair : notAgreedExact) {
			PipeUnit mu = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			if (mu.getElementType().equals("Explicit")) {
				sum.explicitConnective_KFR++;
			}
			if (auto.getElementType().equals("Explicit")) {
				sum.explicitConnective_PARSER++;
			}
		}

		if (manualFile.getFileName().contains("redrose")) {
			System.out.println("Not agreed: exact");
			for (PipeUnitPair pair : notAgreedExact) {
				System.out.println(pair.toString());
			}
		}
		addMatrixCount(notAgreedExact, sum, true);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\typenotagreed.txt", true));
			for (PipeUnitPair pair : notAgreedExact) {
				writer.write(pair.toString() + "\n");
			}
			writer.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		addAgreedExplicits(manualUnits, autoUnits, agreedExplicitPairsPartial);
		addMatrixCount(agreedExplicitPairsPartial, sum, false);
		printKFRExplicit(agreedExplicitPairsPartial);
		sum.agreedExplicitConnective += agreedExplicitPairsPartial.size();
		sum.explicitConnective_KFR += agreedExplicitPairsPartial.size();
		sum.explicitConnective_PARSER += agreedExplicitPairsPartial.size();
		sum.partialSpanMatch += agreedExplicitPairsPartial.size();
		sum.partialSpanMatch_Explicit += agreedExplicitPairsPartial.size();

		addAgreedOthers(manualUnits, autoUnits, agreedImplicitPairsPartial,
				"Implicit");
		addMatrixCount(agreedImplicitPairsPartial, sum, false);
		sum.partialSpanMatch += agreedImplicitPairsPartial.size();
		sum.partialSpanMatch_NonExplicit += agreedImplicitPairsPartial.size();

		addAgreedOthers(manualUnits, autoUnits, agreedEntRelPairsPartial,
				"EntRel");
		addMatrixCount(agreedEntRelPairsPartial, sum, false);
		sum.partialSpanMatch += agreedEntRelPairsPartial.size();
		sum.partialSpanMatch_NonExplicit += agreedEntRelPairsPartial.size();

		addAgreedOthers(manualUnits, autoUnits, agreedAltLexPairsPartial,
				"AltLex");
		addMatrixCount(agreedAltLexPairsPartial, sum, false);
		sum.partialSpanMatch += agreedAltLexPairsPartial.size();
		sum.partialSpanMatch_NonExplicit += agreedAltLexPairsPartial.size();

		addOnlyRangeMatches(manualUnits, autoUnits, notAgreedPartial);
		addMatrixCount(notAgreedPartial, sum, false);
		printKFRExplicit(notAgreedPartial);
		sum.partialSpanMatch += notAgreedPartial.size();
		for (PipeUnitPair pair : notAgreedPartial) {
			PipeUnit mu = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			if (mu.getElementType().equals("Explicit")) {
				sum.explicitConnective_KFR++;
			}
			if (auto.getElementType().equals("Explicit")) {
				sum.explicitConnective_PARSER++;
			}
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\typenotagreedPartial.txt", true));
			for (PipeUnitPair pair : notAgreedPartial) {
				writer.write(pair.toString() + "\n");
			}
			writer.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		addAgreedArg2OnlyExplicit(manualUnits, autoUnits,
				agreedExplicitPairsArg2);
		sum.agreedExplicitConnective += agreedExplicitPairsArg2.size();
		sum.explicitConnective_KFR += agreedExplicitPairsArg2.size();
		sum.explicitConnective_PARSER += agreedExplicitPairsArg2.size();
		// printKFRExplicit(agreedExplicitPairsArg2);

		addAgreedOthersArg2Only(manualUnits, autoUnits,
				agreedImplicitPairsArg2, "Implicit");
		addAgreedOthersArg2Only(manualUnits, autoUnits, agreedEntRelPairsArg2,
				"EntRel");
		addAgreedOthersArg2Only(manualUnits, autoUnits, agreedAltLexPairsArg2,
				"AltLex");
		addOnlyRangeMatchesArg2(manualUnits, autoUnits, notAgreedArg2);
		// printKFRExplicit(notAgreedArg2);
		for (PipeUnitPair pair : notAgreedArg2) {
			PipeUnit manual = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			if (manual.getElementType().equals("Explicit")) {
				sum.explicitConnective_KFR++;
			}
			if (auto.getElementType().equals("Explicit")) {
				sum.explicitConnective_PARSER++;
			}
		}

		addAgreedArg1OnlyExplicit(manualUnits, autoUnits,
				agreedExplicitPairsArg1);
		sum.agreedExplicitConnective += agreedExplicitPairsArg1.size();
		sum.explicitConnective_KFR += agreedExplicitPairsArg1.size();
		sum.explicitConnective_PARSER += agreedExplicitPairsArg1.size();
		// printKFRExplicit(agreedExplicitPairsArg1);

		addAgreedOthersArg1Only(manualUnits, autoUnits,
				agreedImplicitPairsArg1, "Implicit");
		addAgreedOthersArg1Only(manualUnits, autoUnits, agreedEntRelPairsArg1,
				"EntRel");
		addAgreedOthersArg1Only(manualUnits, autoUnits, agreedAltLexPairsArg1,
				"AltLex");
		addOnlyRangeMatchesArg1(manualUnits, autoUnits, notAgreedArg1);
		// printKFRExplicit(notAgreedArg1);
		for (PipeUnitPair pair : notAgreedArg2) {
			PipeUnit manual = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			if (manual.getElementType().equals("Explicit")) {
				sum.explicitConnective_KFR++;
			}
			if (auto.getElementType().equals("Explicit")) {
				sum.explicitConnective_PARSER++;
			}
		}

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
		printKFRExplicitOnly(manualUnits);
		printAutoExplicitOnly(autoUnits);

		for (PipeUnit autoUnit : autoUnits) {
			if (autoUnit.getElementType().equals("Explicit")) {
				sum.explicitConnective_PARSER++;
			}
		}

		for (PipeUnit manualUnit : manualUnits) {
			if (manualUnit.getElementType().equals("Explicit")) {
				sum.explicitConnective_KFR++;
			}
		}

		// addCount(agreedExplicitPairsExact, "ExactMatch-Explicit", temp);
		// addCount(agreedExplicitPairsPartial, "PartialMatch-Explicit", temp);
		// addCount(agreedExplicitPairsArg1, "Arg1Match-Arg2NotMatch-Explicit",
		// temp);
		// addCount(agreedExplicitPairsArg2, "Arg2Match-Arg1NotMatch-Explicit",
		// temp);
		// addCountNotAgreed(notAgreedExact, "ExactMatch-NotAgreed", temp);
		//
		// addCount(agreedImplicitPairsExact, "ExactMatch-Implicit", temp);
		// addCount(agreedImplicitPairsPartial, "PartialMatch-Implicit", temp);
		// addCount(agreedImplicitPairsArg1, "Arg1Match-Arg2NotMatch-Implicit",
		// temp);
		// addCount(agreedImplicitPairsArg2, "Arg2Match-Arg1NotMatch-Implicit",
		// temp);
		// addCountNotAgreed(notAgreedPartial, "PartialMatch-NotAgreed", temp);
		//
		// addCount(agreedEntRelPairsExact, "ExactMatch-EntRel", temp);
		// addCount(agreedEntRelPairsPartial, "PartialMatch-EntRel", temp);
		// addCount(agreedEntRelPairsArg1, "Arg1Match-Arg2NotMatch-EntRel",
		// temp);
		// addCount(agreedEntRelPairsArg2, "Arg2Match-Arg1NotMatch-EntRel",
		// temp);
		// addCountNotAgreed(notAgreedArg1, "Arg1Match-Arg2NotMatch-NotAgreed",
		// temp);
		//
		// addCount(agreedAltLexPairsExact, "ExactMatch-AltLex", temp);
		// addCount(agreedAltLexPairsPartial, "PartialMatch-AltLex", temp);
		// addCount(agreedAltLexPairsArg1, "Arg1Match-Arg2NotMatch-AltLex",
		// temp);
		// addCount(agreedAltLexPairsArg2, "Arg2Match-Arg1NotMatch-AltLex",
		// temp);
		// addCountNotAgreed(notAgreedArg2, "Arg2Match-Arg1NotMatch-NotAgreed",
		// temp);
		//
		// addCountParserOnly(autoUnits, temp);
		// addCountKFROnly(manualUnits, temp);
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
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\PARSERONLY.txt", true));
			for (PipeUnit unit : autoUnits) {
				String key = "PARSER ONLY|" + unit.getElementType() + "|"
						+ unit.getRelationType();
				sum.addCount(key);
				// System.out.println(key + "|" + unit.getRange1TxtAuto() + "|"
				// + unit.getRange2TxtAuto() + "\n");

				writer.write(key + "|" + unit.getRange1TxtAuto() + "|"
						+ unit.getRange2TxtAuto() +"|"+unit.getConnectiveAuto()+ "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addCountKFROnly(List<PipeUnit> manualUnits, TotalSum sum) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\KFRONLY.txt", true));
			for (PipeUnit unit : manualUnits) {
				String key = "KFR ONLY|" + unit.getElementType() + "|"
						+ unit.getManualRelationTypeStr();
				sum.addCount(key);
				writer.write(key + "|" + unit.getRange1Txt() + "|"
						+ unit.getRange2Txt() +"|"+unit.getConnectiveManual()+ "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printNonAjacentStr(List<PipeUnit> manualUnits,
			TotalSum sum) {
		try {
			BufferedWriter manualWriter = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\Nonadjacent.txt", true));

			for (PipeUnit manualUnit : manualUnits) {
				if (manualUnit.getElementType().equals("Implicit")
						&& manualUnit.isNonajacent()
						&& manualUnit.getStrInMiddle() != null) {
					if (manualUnit.getStrInMiddle().trim().length() > 1) {
						String str = "";
						str += "\n";
						str += "KFR|" + manualUnit.getElementType() + "|"
								+ manualUnit.getManualRelationTypeStr() + "|"
								+ manualUnit.getRange1Txt() + "|"
								+ manualUnit.getRange2Txt();
						if (manualUnit.getElementType().equals("Explicit"))
							str += "|" + manualUnit.getConnectiveManual();
						str += "|" + manualUnit.getStrInMiddle();
						str += "\n";
						manualWriter.write(str + "\n");
						sum.nonadjacentCnt = sum.nonadjacentCnt + 1;
					}
				}
			}
			manualWriter.close();
		} catch (Exception exp) {
			exp.printStackTrace();
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

	public static void addMatrixCount(List<PipeUnitPair> pairs, TotalSum sum,
			boolean isExact) {
		for (PipeUnitPair pair : pairs) {
			PipeUnit manual = pair.manualUnit;
			PipeUnit auto = pair.autoUnit;
			if (!auto.getElementType().equals("EntRel")
					&& manual.getManualRelationTypes().contains(
							auto.getRelationType())) {
				sum.addCountCM(manual.getElementType(), auto.getRelationType(),
						auto.getElementType(), auto.getRelationType());
				sum.countCorrect(manual.getElementType(),
						auto.getRelationType(), auto.getElementType(),
						auto.getRelationType(), isExact);
			} else {
				sum.addCountCM(manual.getElementType(),
						manual.getManualRelationType(), auto.getElementType(),
						auto.getRelationType());
				sum.countCorrect(manual.getElementType(),
						manual.getManualRelationType(), auto.getElementType(),
						auto.getRelationType(), isExact);
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
							// System.out.println(pair);
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
						// System.out.println(pair);
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
		return isConnectiveMatch(manualUnit.getManualConnectiveRange(), autoUnit.getAutoConnectiveRange());
	}
	
	public static boolean isConnectiveMatch2(PipeUnit manualUnit,
			PipeUnit autoUnit) {
		String connectiveManual = manualUnit.getConnectiveManual();
		String connectiveAuto = autoUnit.getConnectiveAuto();
		if (connectiveAuto.trim().length() > 0) {
			if (connectiveManual.trim().length() > 0) {
				/*if (!connectiveManual.trim().contains(connectiveAuto.trim())
						&& !connectiveAuto.trim().contains(
								connectiveManual.trim())) {
					return false;
				}*/
				return isConnectiveMatch(manualUnit.getManualConnectiveRange(), autoUnit.getAutoConnectiveRange());
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
				 * range1TxtManual = range1TxtManual.replaceAll("�", "\"");
				 * range2TxtManual = range2TxtManual.replaceAll("�", "\"");
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

	public static void printKFRExplicit(List<PipeUnitPair> pairs) {
		try {
			BufferedWriter manualWriter = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\KFRExplicit.txt", true));
			BufferedWriter autoWriter = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\AutoExplicit.txt", true));
			for (PipeUnitPair pair : pairs) {
				PipeUnit manual = pair.manualUnit;
				if (manual.getElementType().equals("Explicit")) {
					manualWriter.write(pair.toString() + "\n");
				}
				PipeUnit auto = pair.autoUnit;
				if (auto.getElementType().equals("Explicit")) {
					autoWriter.write(pair.toString() + "\n");
				}
			}
			manualWriter.close();
			autoWriter.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public static void printKFRExplicitOnly(List<PipeUnit> manualUnits) {
		try {
			BufferedWriter manualWriter = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\KFRExplicit.txt", true));

			for (PipeUnit manualUnit : manualUnits) {
				if (manualUnit.getElementType().equals("Explicit")) {
					String str = "";
					str += "\n";
					str += "KFR|" + manualUnit.getElementType() + "|"
							+ manualUnit.getManualRelationTypeStr() + "|"
							+ manualUnit.getRange1Txt() + "|"
							+ manualUnit.getRange2Txt();
					if (manualUnit.getElementType().equals("Explicit"))
						str += "|" + manualUnit.getConnectiveManual();
					str += "\n";
					manualWriter.write(str + "\n");
				}
			}
			manualWriter.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public static void printAutoExplicitOnly(List<PipeUnit> autoUnits) {
		try {
			BufferedWriter autoWriter = new BufferedWriter(new FileWriter(
					"C:\\Not Backed Up\\temp\\AutoExplicit.txt", true));

			for (PipeUnit autoUnit : autoUnits) {
				if (autoUnit.getElementType().equals("Explicit")) {
					String str = "";
					str += "\n";
					str += "PARSER|" + autoUnit.getElementType() + "|"
							+ autoUnit.getRelationType() + "|"
							+ autoUnit.getRange1Txt() + "|"
							+ autoUnit.getRange2Txt();
					if (autoUnit.getElementType().equals("Explicit"))
						str += "|" + autoUnit.getConnectiveAuto();
					str += "\n";
					autoWriter.write(str + "\n");
				}
			}
			autoWriter.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
}

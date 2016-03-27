package edu.pitt.cs.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeUnit;

class TotalSum {
	int agreedAll = 0;
	int totalAgreed = 0;
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

	Hashtable<String, Integer> disagreedEntTable = new Hashtable<String, Integer>();
	Hashtable<String, Integer> disagreedTypeTable = new Hashtable<String, Integer>();

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
		str += "Relation Type: " + agreedAll + "/" + totalAgreed + "\n";
		str += "KFR:" + kfrTotal + " annotated relations\n";
		str += "PARSER:" + parserTotal + " annotated relations\n";
		str += "AGREED:\n";
		str += agreedExplicit + " Explicit \n";
		str += agreedImplicit + " Implicit \n";
		str += agreedEnt + " EntRel \n";
		str += agreedNoRel + " NoRel \n";
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
		compare(manualFolderPath, autoFolderPath, outputFolderPath, sums);

		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outputFolderPath + "/" + "compareSumD1.txt"));
		writer.write(sums[0].toString());
		writer.close();
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(
				outputFolderPath + "/" + "compareSumD2.txt"));
		writer2.write(sums[1].toString());
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
					String compareStr = compare(mFile, aFile, sum);
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

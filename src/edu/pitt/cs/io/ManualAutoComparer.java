package edu.pitt.cs.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeUnit;

public class ManualAutoComparer {
	public static void main(String[] args) throws IOException {
		String manualFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
		String autoFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		String outputFolderPath = "C:\\Not Backed Up\\discourse_parse_results\\compareOutput";
		compare(manualFolderPath, autoFolderPath, outputFolderPath);
	}

	public static void compare(String manualFolderPath, String autoFolderPath,
			String outputFolderPath) throws IOException {
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
				String compareStr = compare(mFile, aFile);
				String outputPath = outputFolderPath + "/" + postFix + "/"
						+ fileName + ".log";
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputPath));
				writer.write(compareStr);
				writer.close();
			}
		}
	}

	public static String compare(ManualParseResultFile manualFile,
			ParseResultFile autoFile) {
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

				String range1TxtManual = manualUnit.getRange1Txt();
				String range2TxtManual = manualUnit.getRange2Txt();
				String range1TxtAuto = autoUnit.getRange1TxtAuto();
				String range2TxtAuto = autoUnit.getRange2TxtAuto();

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

				if (isRange1Match && isRange2Match) {
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
					} else {
						disagreedDetails.add(str);
					}

					if (mSenseType.equals(aSenseType)) {
						if (mSenseType.equals("Comparison")) {
							agreedComparison++;
						} else if (mSenseType.equals("Contingency")) {
							agreedContingency++;
						} else if (mSenseType.equals("Expansion")) {
							agreedExpansion++;
						} else if (mSenseType.equals("Temporal")) {
							agreedTemporal++;
						}
						agreedTypeCount++;
						typeAgreedDetails.add(str);
					} else {
						typeDisagreedDetails.add(str);
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

				String range1TxtManual = manualUnit.getRange1Txt();
				String range2TxtManual = manualUnit.getRange2Txt();
				String range1TxtAuto = autoUnit.getRange1TxtAuto();
				String range2TxtAuto = autoUnit.getRange2TxtAuto();

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

				if (isRange1Match && isRange2Match)
					found = true;
			}
			if (found == false) {
				String str = "PARSER ONLY|" + autoUnit.getElementType() + "|"
						+ autoUnit.getRelationType() + "|" +  autoUnit.getRange1TxtAuto() + "|"
						+ autoUnit.getRange2TxtAuto();
				disagreedDetails.add(str);
			}
		}
		int senseCount = agreedExplicit + agreedImplicit;
		int agreedCorrect = agreedExplicit + agreedImplicit + agreedEnt
				+ agreedNoEnt;

		String start = "Relation Type: " + agreedCorrect + " common/"
				+ agreedCount + " total\n";
		String kfrCountStr = "KFR: " + manualCount + " annotated revisions\n";
		String autoCountStr = "PDTB: " + autoCount + " annotated revisions\n";
		String agreedStr = "AGREED: \n" + agreedExplicit + " Explicit\n"
				+ agreedImplicit + " Implicit\n" + agreedEnt + " EntRel\n"
				+ agreedNoEnt + " NoRel\n";
		
		String agreedElement = "AGREED:\n";
		for (String str: agreedDetails) {
			agreedElement += str + "\n";
		}
		String agreedType = "AGREED:\n";
		for (String str: typeAgreedDetails) {
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

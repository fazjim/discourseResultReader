package edu.pitt.cs.io;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeUnit;

public class ManualAutoComparer {
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
					if (mType.equals(aType)) {
						if (mType.equals("Explicit")) {
							agreedExplicit++;
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
							} else {
								String str = "Auto: " + aSenseType + ", Kate: "
										+ mSenseType + ", Explicit";
								typeDisagreedDetails.add(str);
							}
						} else if (mType.equals("Implicit")) {
							agreedImplicit++;
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
							} else {
								String str = "Auto: " + aSenseType + ", Kate: "
										+ mSenseType + ", Implicit";
								typeDisagreedDetails.add(str);
							}
						} else if (mType.equals("EntRel")) {
							agreedEnt++;
						} else {
							agreedNoEnt++;
						}
					} else {
						String str = "Auto:" + aType + ", Kate: " + mType;
						disagreedDetails.add(str);
					}

				} else {

				}
			}
			if (found == false) {
				String str = "Kate annotated while auto does not: |"
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

				if(isRange1Match && isRange2Match) found = true;
			}
			if (found == false) {
				String str = "Auto annotated while Kate does not: |"
						+ autoUnit.getRange1TxtAuto() + "|"
						+ autoUnit.getRange2TxtAuto();
				disagreedDetails.add(str);
			}
		}
		int senseCount = agreedExplicit + agreedImplicit;

		String start = "Relation Type: " + agreedCount + " common(agreed)/"
				+ biggerCount + " total\n";
		String kfrCountStr = "KFR: " + manualCount + " annotated revisions\n";
		String autoCountStr = "PDTB: " + autoCount + " annotated revisions\n";
		String agreedStr = "Agreed: \n" + agreedExplicit + "Explicit\n"
				+ agreedImplicit + "Implicit\n" + agreedEnt + "EntRel\n"
				+ agreedNoEnt + "NoRel\n";
		String disAgreedStr = "Disagreed: ";
		for (String str : disagreedDetails) {
			disAgreedStr += str + "\n";
		}

		String disAgreedTypeStr = "Disagreed: ";
		for (String str : typeDisagreedDetails) {
			disAgreedTypeStr += str + "\n";
		}
		String secondStart = "4 Class Relation Sense: " + agreedTypeCount + "/"
				+ senseCount + "Agreed\n";
		secondStart += agreedComparison + " Comparison\n";
		secondStart += agreedContingency + " Contingency\n";
		secondStart += agreedExpansion + " Expansion\n";
		secondStart += agreedTemporal + "Temporal";

		result += start;
		result += kfrCountStr;
		result += autoCountStr;
		result += agreedStr;
		result += disAgreedStr;
		result += "\n";
		result += secondStart;
		result += disAgreedTypeStr;
		return result;
	}
}

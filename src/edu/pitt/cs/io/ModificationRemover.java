package edu.pitt.cs.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.model.ManualParseResultFile;
import edu.pitt.cs.model.PipeUnit;

public class ModificationRemover {
	static String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";

	public static void main(String[] args) throws IOException {
		List<ManualParseResultFile> resultFiles = ManualParseResultReader
				.readFiles("C:\\Not Backed Up\\discourse_parse_results\\manual");

		Hashtable<String, ManualParseResultFile> d1UnModified = new Hashtable<String, ManualParseResultFile>();
		Hashtable<String, ManualParseResultFile> d2UnModified = new Hashtable<String, ManualParseResultFile>();
		for (ManualParseResultFile result : resultFiles) {
			String fileName = result.getFileName();
			String shortName = (new File(fileName)).getName();
			if (fileName.contains(" - "))
				shortName = shortName.substring(0, shortName.indexOf(" - "))
						.trim();
			shortName = shortName.replaceAll("\\.txt", "");

			System.out.println(shortName);
			ManualParseResultFile fixedFile = removeModified(result, path);
			if (fileName.contains("draft1")) {
				if (fixedFile != null)
					d1UnModified.put(shortName, fixedFile);
			} else if (fileName.contains("draft2")) {
				if (fixedFile != null)
					d2UnModified.put(shortName, fixedFile);
			}
		}

		Iterator<String> it = d1UnModified.keySet().iterator();
		while (it.hasNext()) {
			String txtName = it.next();
			ManualParseResultFile d1File = d1UnModified.get(txtName);
			ManualParseResultFile d2File = d2UnModified.get(txtName);

			if (d1File != null && d2File != null) {
				List<PipeUnit> pipes = d1File.getPipes();
				List<PipeUnit> pipes2 = d2File.getPipes();
				Hashtable<String, PipeUnit> pipeTable = new Hashtable<String, PipeUnit>();
				// for (PipeUnit pipe : pipes2) {
				// pipeTable.put(pipe.getRange1Txt(), pipe);
				// }
				String logFile = d1File.getFileName() + "_LOG_UNCHANGED";
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						logFile));
				System.out.println("Write file:"+logFile);
				for (PipeUnit pipe : pipes) {
					for (PipeUnit pipe2 : pipes2) {
						if (pipe.getRange1Txt().contains(pipe2.getRange1Txt())
								|| pipe2.getRange1Txt().contains(
										pipe.getRange1Txt())) {
							writer.write("Draft 1" + pipe.getRelationType()
									+ "|" + pipe.getElementType() + "|"
									+ pipe.getRange1Txt() + "|"
									+ pipe.getRange2Txt() + "\n");
							writer.write("Draft 2" + pipe2.getRelationType()
									+ "|" + pipe2.getElementType() + "|"
									+ pipe2.getRange1Txt() + "|"
									+ pipe2.getRange2Txt() + "\n");
						}
					}
					// if (pipeTable.containsKey(pipe.getRange1Txt())) {
					// PipeUnit pipe2 = pipeTable.get(pipe.getRange1Txt());
					// //if (pipe2.getRange2Txt().equals(pipe.getRange2Txt())) {
					// if (!(pipe.getRelationType().equals(pipe2
					// .getRelationType()))
					// || !(pipe.getElementType().equals(pipe2
					// .getElementType()))) {
					// writer.write("Draft 1" + pipe.getRelationType()
					// + "|" + pipe.getElementType() + "|"
					// + pipe.getRange1Txt() + "|"
					// + pipe.getRange2Txt() + "\n");
					// writer.write("Draft 2"
					// + pipe2.getRelationType() + "|"
					// + pipe2.getElementType() + "|"
					// + pipe2.getRange1Txt() + "|"
					// + pipe2.getRange2Txt() + "\n");
					// }
					// }
					// }
				}
				writer.close();
			}
		}
	}

	public static ManualParseResultFile removeModified(
			ManualParseResultFile file, String referencePath)
			throws IOException {
		String name = file.getFileName();
		System.out.println("Processing...." + name);
		File f = new File(name);
		String fileName = f.getName();
		if (fileName.contains(" - "))
			fileName = fileName.substring(0, fileName.indexOf(" - ")).trim();
		fileName = fileName.replaceAll("\\.txt", "").trim();
		System.out.println("Trimmed name:" + fileName);

		String d1FolderPath = referencePath + "/draft1";
		String d2FolderPath = referencePath + "/draft2";
		File d1Folder = new File(d1FolderPath);
		File d2Folder = new File(d2FolderPath);
		File d1File = null;
		File d2File = null;
		File[] subsD1 = d1Folder.listFiles();
		File[] subsD2 = d2Folder.listFiles();
		for (File d1Temp : subsD1) {
			if (d1Temp.isFile()) {
				if (d1Temp.getName().contains(fileName)) {
					d1File = d1Temp;
					break;
				}
			}
		}
		for (File d2Temp : subsD2) {
			if (d2Temp.isFile()) {
				if (d2Temp.getName().contains(fileName)) {
					d2File = d2Temp;
					break;
				}
			}
		}

		if (d1File == null) {
			System.out.println("D1 file is null");
			return null;
		} else if (d2File == null) {
			System.out.println("D2 file is null");
			return null;
		} else {

			String d1Txt = readTxt(d1File);
			String d2Txt = readTxt(d2File);

			ManualParseResultFile newFile = new ManualParseResultFile();
			newFile.setFileName(name);
			if (name.contains("draft 1")) {
				// retrieve txt in draft 1 and go to find draft 2
				List<PipeUnit> units = file.getPipes();
				for (PipeUnit unit : units) {
					String range1 = unit.getManualRange1();
					String range2 = unit.getManualRange2();
					int[] range1Indices = retrieveRanges(range1);
					int[] range2Indices = retrieveRanges(range2);

					String range1Txt = d1Txt.substring(range1Indices[0],
							range1Indices[1]);
					String range2Txt = d1Txt.substring(range2Indices[0],
							range2Indices[1]);
					unit.setRange1Txt(range1Txt);
					unit.setRange2Txt(range2Txt);
					if (d2Txt.contains(range1Txt) && d2Txt.contains(range2Txt)) {
						newFile.addUnit(unit);
					}
				}
			} else {
				List<PipeUnit> units = file.getPipes();
				for (PipeUnit unit : units) {
					String range1 = unit.getManualRange1();
					String range2 = unit.getManualRange2();

					// int[] mids = new int[2];
					// if(unit.getAttr(1).contains("..")) {
					// String val = unit.getAttr(1);
					// String[] splits = val.split("\\.\\.");
					// mids[0] = Integer.parseInt(splits[0]);
					// mids[1] = Integer.parseInt(splits[1]);
					// }
					//
					int[] range1Indices = retrieveRanges(range1);
					int[] range2Indices = retrieveRanges(range2);

					String range1Txt = d2Txt.substring(range1Indices[0],
							range1Indices[1]);
					String range2Txt = d2Txt.substring(range2Indices[0],
							range2Indices[1]);
					unit.setRange1Txt(range1Txt);
					unit.setRange2Txt(range2Txt);
					if (d1Txt.contains(range1Txt) && d1Txt.contains(range2Txt)) {
						newFile.addUnit(unit);
					}
				}
			}
			return newFile;
		}
	}

	public static int[] retrieveRangesShort(String range) {
		String[] splits = range.split("\\.\\.");
		int start = Integer.parseInt(splits[0]);
		int end = Integer.parseInt(splits[1]);
		int[] ranges = new int[2];
		if (start == -1)
			start++;
		ranges[0] = start;
		ranges[1] = end;
		return ranges;
	}

	public static int[] retrieveRanges(String range) {
		int start = -1;
		int end = -1;
		if (range.contains(";")) {
			String[] splits = range.split(";");
			for (String split : splits) {
				int[] vals = retrieveRangesShort(split);
				if (start == -1)
					start = vals[0];
				if (end == -1)
					end = vals[1];
				if (vals[0] < start)
					start = vals[0];
				if (vals[1] > end)
					end = vals[1];
			}
			int[] ranges = new int[2];
			ranges[0] = start;
			ranges[1] = end;
			return ranges;
		} else {
			return retrieveRangesShort(range);
		}
	}

	public static String readTxt(File f) throws IOException {
		String txt = "";
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = reader.readLine();
		while (line != null) {
			txt += line + "\n";
			line = reader.readLine();
		}
		reader.close();
		return txt;
	}
}

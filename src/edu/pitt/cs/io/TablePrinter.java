package edu.pitt.cs.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.cs.model.Table;

/**
 * Printing the table to screen, txt file or excel files
 * 
 * @author zhangfan
 *
 */
public class TablePrinter {
	/**
	 * Print to screen
	 * 
	 * @param table
	 * @throws IOException
	 */
	public static void printScreen(Table table) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				System.out));
		print(writer, table);
		writer.flush();
		writer.close();
	}

	/**
	 * Print to txt files
	 * 
	 * @param table
	 * @param fileName
	 * @throws IOException
	 */
	public static void printFile(Table table, String fileName)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		print(writer, table);
		writer.close();
	}

	public static void print(BufferedWriter writer, Table table)
			throws IOException {
		List<String> cols = table.getColumns();

		for (int i = 0; i < cols.size(); i++) {
			writer.write(cols.get(i) + "\t");
		}

		writer.write("\n");
		List<List<String>> rows = table.getRows();
		for (int i = 0; i < rows.size(); i++) {
			List<String> row = rows.get(i);
			for (int j = 0; j < row.size(); j++) {
				writer.write(row.get(j) + "\t");
			}
			writer.write("\n");
		}
	}

	public static void printCSV(Table table, String csvFile) throws IOException {
		List<String> cols = table.getColumns();
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

		fileWriter = new FileWriter(csvFile);
		csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
		csvFilePrinter.printRecord(cols);
		List<List<String>> rows = table.getRows();
		for (int i = 0; i < rows.size(); i++) {
			csvFilePrinter.printRecord(rows.get(i));
		}
		fileWriter.flush();
		fileWriter.close();
		csvFilePrinter.close();
	}

	/**
	 * Print to excel files
	 * 
	 * @param table
	 * @param excelFile
	 * @throws IOException
	 */
	public static void printExcel(Table table, String excelFile)
			throws IOException {
		FileOutputStream fileOut = new FileOutputStream(excelFile);
		XSSFWorkbook xwb = new XSSFWorkbook();

		XSSFSheet sheet = xwb.createSheet(table.getTableName());
		XSSFRow header = sheet.createRow(0);

		List<String> cols = table.getColumns();
		for (int i = 0; i < cols.size(); i++) {
			header.createCell(i).setCellValue(cols.get(i));
		}
		List<List<String>> rows = table.getRows();
		for (int i = 0; i < rows.size(); i++) {
			XSSFRow xRow = sheet.createRow(i + 1);
			List<String> row = rows.get(i);
			for (int j = 0; j < row.size(); j++) {
				xRow.createCell(j).setCellValue(row.get(j));
			}
		}
		xwb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
}

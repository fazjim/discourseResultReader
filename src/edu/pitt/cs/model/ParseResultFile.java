package edu.pitt.cs.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseResultFile {
	private String fileName;
	private List<PipeUnit> pipes;

	private boolean isPDTB1 = false;

	public void choosePDTB1() {
		isPDTB1 = true;
	}
	public void choosePDTB2() {
		isPDTB1 = false;
	}
	
	public boolean isPDTB1() {
		return isPDTB1;
	}

	public ParseResultFile(String fileName) throws IOException {
		this.fileName = fileName;
		if(fileName.contains("pdtb_1")) {
			choosePDTB1();
		} else {
			choosePDTB2();
		}
		pipes = new ArrayList<PipeUnit>();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		while (line != null) {
			PipeUnit pu = new PipeUnit(line);
			pipes.add(pu);
			line = reader.readLine();
		}
		reader.close();
	}

	public String getFileName() {
		return this.fileName;
	}

	public List<PipeUnit> getPipes() {
		return this.pipes;
	}
}

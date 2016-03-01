package edu.pitt.cs.main;

import java.util.List;

import edu.pitt.cs.model.ParseResultFile;
import edu.pitt.cs.model.PipeUnit;

public class ResultComparor {
	public void compareResult(ParseResultFile manual, ParseResultFile automatic) {
		
	}
	
	public ParseResultFile removeModified(ParseResultFile file, String[] unmodifiedSentences) {
		ParseResultFile newFile = new ParseResultFile();
		List<PipeUnit> pipes = file.getPipes();
		for(PipeUnit pipe: pipes) {
		
		}
		return newFile;
		
	}
}

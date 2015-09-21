package edu.pitt.cs.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import edu.pitt.cs.model.ParseResultFile;

import java.util.List;

public class ParseResultReader {
	public static List<ParseResultFile> readFiles(String path) throws IOException {
		List<ParseResultFile> files = new ArrayList<ParseResultFile>();
		File folder = new File(path);
		Stack<File> root = new Stack<File>();
		root.push(folder);
		while(!root.empty()) {
			File temp = root.pop();
			if(temp.isDirectory()) {
				File[] subs = temp.listFiles();
				for(File sub: subs) {
					root.push(sub);
				}
			} else {
				if(temp.getName().endsWith(".pipe")) {
					ParseResultFile psf = new ParseResultFile(temp.getAbsolutePath());
					files.add(psf);
				}
			}
		}
		return files;
	}
}

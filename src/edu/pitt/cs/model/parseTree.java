package edu.pitt.cs.model;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.StringUtils;

public class parseTree {
	
	public static LinkedList<String> revertToTokens(String parse) {
		Tree T = Tree.valueOf(parse);		
		List<Tree> Leaves = T.getLeaves();
		
		LinkedList<String> Tokens = new LinkedList<>();
        for (Tree l : Leaves) {
            Tokens.add(l.nodeString());
        }
        return Tokens;
	}
	
	public static String revertToString(String parse) {
		LinkedList<String> Tokens = revertToTokens(parse);
		return StringUtils.join(Tokens, " ");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String parse = "(ROOT   (S     (PP (IN In)       (NP (NNP Hell)))     (NP (PRP I))     (VP (MD would)       (VP (VB have)         (NP           (NP (DT the) (NN uncommitted))           (CC or)           (NP (DT the) (NNS people)))         (PP (IN with)           (NP             (NP (DT no) (NN purpose))             (PP (IN in)               (NP                 (NP (NN life))                 (PP (IN in)                   (NP (DT the) (NN vestibule)))))))))     (. .)))";
		System.out.print(revertToString(parse));
	}
}

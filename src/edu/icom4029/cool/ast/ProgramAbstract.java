package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;

/** Defines simple phylum Program */
public abstract class ProgramAbstract extends TreeNode {
	
	public ProgramAbstract(int lineNumber) {
		super(lineNumber);
	}
	public abstract void dump_with_types(PrintStream out, int n);
	public abstract void semant();
	public abstract void cgen(PrintStream s);
}

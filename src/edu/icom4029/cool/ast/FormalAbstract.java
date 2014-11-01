package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;

/** Defines simple phylum Formal */
public abstract class FormalAbstract extends TreeNode {
	public FormalAbstract(int lineNumber) {
		super(lineNumber);
	}
	public abstract void dump_with_types(PrintStream out, int n);
}

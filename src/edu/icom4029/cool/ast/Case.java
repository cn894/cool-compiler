package edu.icom4029.cool.ast;

import java.io.PrintStream;
import edu.icom4029.cool.ast.base.TreeNode;

/** Defines simple phylum Case */
public abstract class Case extends TreeNode {
	public Case(int lineNumber) {
		super(lineNumber);
	}
	public abstract void dump_with_types(PrintStream out, int n);
}

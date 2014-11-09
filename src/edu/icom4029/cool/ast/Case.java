package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.lexer.AbstractSymbol;

/** Defines simple phylum Case */
public abstract class Case extends TreeNode {
	private AbstractSymbol type = null;
	public Case(int lineNumber) {
		super(lineNumber);
	}
	public AbstractSymbol get_type() { return type; }
	public Case set_type(AbstractSymbol s) { type = s; return this; }
	public abstract void dump_with_types(PrintStream out, int n);
}

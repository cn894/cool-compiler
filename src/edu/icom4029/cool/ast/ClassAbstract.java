package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.lexer.AbstractSymbol;

/** Defines simple phylum Class_ */
public abstract class ClassAbstract extends TreeNode {
	public ClassAbstract(int lineNumber) {
		super(lineNumber);
	}
	public abstract void           dump_with_types(PrintStream out, int n);
	public abstract AbstractSymbol getName();
	public abstract AbstractSymbol getParent();
	public abstract AbstractSymbol getFilename();
	public abstract Features       getFeatures();
}
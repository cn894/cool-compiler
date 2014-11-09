package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines simple phylum Feature */
public abstract class Feature extends TreeNode {
	public Feature(int lineNumber) {
		super(lineNumber);
	}
	public abstract void dump_with_types(PrintStream out, int n);
	public abstract void semant(ClassTable classTable, class_ c, SymbolTable symbolTable);
	public abstract String fillMethodTable(SymbolTable methodTable);
	public abstract String fillAttrTable(SymbolTable attrTable);
}

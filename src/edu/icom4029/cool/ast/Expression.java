package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines simple phylum Expression */
public abstract class Expression extends TreeNode {
	public Expression(int lineNumber) {
		super(lineNumber);
	}
	private AbstractSymbol type = null;

	public  AbstractSymbol get_type() { return type; }           
	public  Expression     set_type(AbstractSymbol s) { type = s; return this; } 

	public abstract void dump_with_types(PrintStream out, int n);

	public void dump_type(PrintStream out, int n) {
		if (type != null) { 
			out.println(Utilities.pad(n) + ": " + type.getString()); 
		}
		else { 
			out.println(Utilities.pad(n) + ": _no_type");
		}
	}
	public abstract void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable);
	public abstract void code(PrintStream s);
}

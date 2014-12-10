package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

public class or extends Expression {
	protected Expression e1;
	protected Expression e2;
	
	public or(int lineNumber, Expression a1, Expression a2) {
		super(lineNumber);
		e1 = a1;
		e2 = a2;
	}

	@Override
	public void dump_with_types(PrintStream out, int n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void code(PrintStream s) {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeNode copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dump(PrintStream out, int n) {
		// TODO Auto-generated method stub

	}

}

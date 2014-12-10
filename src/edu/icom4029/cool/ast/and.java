package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

public class and extends Expression {
	protected Expression e1;
	protected Expression e2;
	
	public and(int lineNumber, Expression a1, Expression a2) {
		super(lineNumber);
		e1 = a1;
		e2 = a2;
	}

	@Override
	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_and");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		e1.semant(classTable, cl, symbolTable);
		e2.semant(classTable, cl, symbolTable);
		
		if (e1.get_type() != TreeConstants.Bool || e2.get_type() != TreeConstants.Bool) {
			classTable.semantError(cl).println("non-Bool arguments: " + e1.get_type().getString() + " + " + e2.get_type().getString());
			set_type(TreeConstants.No_type);
		}
		else {
			set_type(TreeConstants.Bool);
		}
	}

	@Override
	public void code(PrintStream s) {
		CgenSupport.emitAnd(e1, e2, s);
	}

	@Override
	public TreeNode copy() {
		return new and(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
	}

	@Override
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "and\n");
		e1.dump(out, n+2);
		e2.dump(out, n+2);
	}

}

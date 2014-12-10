package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
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

	public TreeNode copy() {
		return new or(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
	}

	public Expression getLHS() { return e1; }
	public Expression getRHS() { return e2; }

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "or\n");
		e1.dump(out, n+2);
		e2.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_or");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
		dump_type(out, n);
	}
	
	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		e1.semant(classTable, cl, symbolTable); // Perform semantic analysis on the LHS expression
		e2.semant(classTable, cl, symbolTable); // Perform semantic analysis on the RHS expression
		
		if (e1.get_type() != TreeConstants.Bool || e2.get_type() != TreeConstants.Bool) {
			classTable.semantError(cl).println("non-Bool arguments: " + e1.get_type().getString() + " or " + e2.get_type().getString());
			set_type(TreeConstants.No_type);
		}
		else {
			set_type(TreeConstants.Bool);
		}
	}
	
	@Override
	public void code(PrintStream s) {
		e1.code(s);
		CgenSupport.emitFetchInt(CgenSupport.T1, CgenSupport.ACC, s);
		CgenSupport.emitPush(CgenSupport.T1, s);
		e2.code(s);
		CgenSupport.emitJal("Object.copy", s);
		CgenSupport.emitFetchInt(CgenSupport.T2, CgenSupport.ACC, s);
		CgenSupport.emitPop(CgenSupport.T1, s);
		// Here, we have e1 in T1 and e2 in T2, represented as 0 or 1
		
		int labelTrue = CgenSupport.genLabelNum();
		int labelEnd   = CgenSupport.genLabelNum();
		
		CgenSupport.emitBne(CgenSupport.T1, CgenSupport.ZERO, labelTrue, s);
		CgenSupport.emitBne(CgenSupport.T2, CgenSupport.ZERO, labelTrue, s);
		
		CgenSupport.emitLoadFalse(CgenSupport.ACC, s);
		
		CgenSupport.emitBranch(labelEnd, s);
		
		CgenSupport.emitLabelDef(labelTrue, s);
		CgenSupport.emitLoadTrue(CgenSupport.ACC, s);
		
		CgenSupport.emitLabelDef(labelEnd, s);
	}
}

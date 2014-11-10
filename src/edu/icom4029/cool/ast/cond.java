package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'cond'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class cond extends Expression {
	protected Expression pred;
	protected Expression then_exp;
	protected Expression else_exp;
	
	/** Creates "cond" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for pred
	 * @param a1 initial value for then_exp
	 * @param a2 initial value for else_exp
	 */
	public cond(int lineNumber, Expression a1, Expression a2, Expression a3) {
		super(lineNumber);
		pred = a1;
		then_exp = a2;
		else_exp = a3;
	}

	public TreeNode copy() {
		return new cond(lineNumber, (Expression)pred.copy(), (Expression)then_exp.copy(), (Expression)else_exp.copy());
	}
	
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "cond\n");
		pred.dump(out, n+2);
		then_exp.dump(out, n+2);
		else_exp.dump(out, n+2);
	}
	
	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_cond");
		pred.dump_with_types(out, n + 2);
		then_exp.dump_with_types(out, n + 2);
		else_exp.dump_with_types(out, n + 2);
		dump_type(out, n);
	}
	
	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		pred.semant(classTable, cl, symbolTable);
		then_exp.semant(classTable, cl, symbolTable);
		else_exp.semant(classTable, cl, symbolTable);
		
		AbstractSymbol thenType = then_exp.get_type();
		if (thenType == TreeConstants.SELF_TYPE) {
			thenType = cl.getName();
		}
		
		AbstractSymbol elseType = else_exp.get_type();
		if (elseType == TreeConstants.SELF_TYPE) {
			elseType = cl.getName();
		}
		set_type(classTable.leastCommonAncestor(thenType, elseType));
	}
}

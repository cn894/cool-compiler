package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'eq'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class eq extends Expression {
	protected Expression e1;
	protected Expression e2;
	/** Creates "eq" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for e1
	 * @param a1 initial value for e2
	 */
	public eq(int lineNumber, Expression a1, Expression a2) {
		super(lineNumber);
		e1 = a1;
		e2 = a2;
	}

	public TreeNode copy() {
		return new eq(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
	}
	
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "eq\n");
		e1.dump(out, n+2);
		e2.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_eq");
		e1.dump_with_types(out, n + 2);
		e2.dump_with_types(out, n + 2);
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
		e1.semant(classTable, cl, symbolTable); // Perform semantic analysis on the LHS expression
		e2.semant(classTable, cl, symbolTable); // Perform semantic analysis on the RHS expression
		
		if (e1.get_type() != e2.get_type() && classTable.hasBasicClass(e2.get_type().getString())) {
			classTable.semantError(cl).println("Illegal comparison with a basic type.");
		}
		set_type(TreeConstants.Bool);
	}
}

package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'no_expr'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class no_expr extends Expression {

	/** Creates "no_expr" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 */
	public no_expr(int lineNumber) {
		super(lineNumber);
	}

	public TreeNode copy() {
		return new no_expr(lineNumber);
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "no_expr\n");
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_no_expr");
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
		// TODO Auto-generated method stub
		
	}
}

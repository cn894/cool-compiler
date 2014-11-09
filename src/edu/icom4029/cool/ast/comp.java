package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;

/** Defines AST constructor 'comp'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class comp extends Expression {
	protected Expression e1;
	
	/** Creates "comp" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for e1
	 */
	public comp(int lineNumber, Expression a1) {
		super(lineNumber);
		e1 = a1;
	}
	
	public void semant() {
		
	}
	
	public TreeNode copy() {
		return new comp(lineNumber, (Expression)e1.copy());
	}
	
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "comp\n");
		e1.dump(out, n+2);
	}

	public Expression getExpression() { return e1; }

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_comp");
		e1.dump_with_types(out, n + 2);
		dump_type(out, n);
	}
	
	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
	}
}

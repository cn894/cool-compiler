package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.Enumeration;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'block'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class block extends Expression {
	protected Expressions body;
	/** Creates "block" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for body
	 */
	public block(int lineNumber, Expressions a1) {
		super(lineNumber);
		body = a1;
	}

	public TreeNode copy() {
		return new block(lineNumber, (Expressions)body.copy());
	}
	
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "block\n");
		body.dump(out, n+2);
	}


	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_block");
		for (Enumeration e = body.getElements(); e.hasMoreElements();) {
			((Expression)e.nextElement()).dump_with_types(out, n + 2);
		}
		dump_type(out, n);
	}
	
	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		for (Enumeration e = body.getElements(); e.hasMoreElements();) {
			Expression expr = (Expression) e.nextElement();
			expr.code(s);
		}
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		for (Enumeration e = body.getElements(); e.hasMoreElements();) {
			Expression expr = (Expression) e.nextElement();
			expr.semant(classTable, cl, symbolTable);
			set_type(expr.get_type());	// Type is the type of the last expression in the block.
		}
	}
}

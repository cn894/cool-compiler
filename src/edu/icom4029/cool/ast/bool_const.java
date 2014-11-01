package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.BoolConst;

/** Defines AST constructor 'bool_const'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class bool_const extends Expression {
	protected Boolean val;
	/** Creates "bool_const" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for val
	 */
	public bool_const(int lineNumber, Boolean a1) {
		super(lineNumber);
		val = a1;
	}
	public TreeNode copy() {
		return new bool_const(lineNumber, copy_Boolean(val));
	}
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "bool_const\n");
		dump_Boolean(out, n+2, val);
	}


	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_bool");
		dump_Boolean(out, n + 2, val);
		dump_type(out, n);
	}
	/** Generates code for this expression.  This method method is provided
	 * to you as an example of code generation.
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		CgenSupport.emitLoadBool(CgenSupport.ACC, new BoolConst(val), s);
	}
}

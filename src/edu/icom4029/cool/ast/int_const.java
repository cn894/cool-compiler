package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.lexer.IntSymbol;

/** Defines AST constructor 'int_const'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class int_const extends Expression {
	protected AbstractSymbol token;

	/** Creates "int_const" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for token
	 */
	public int_const(int lineNumber, AbstractSymbol a1) {
		super(lineNumber);
		token = a1;
	}
	
	public void semant() {
		
	}

	public TreeNode copy() {
		return new int_const(lineNumber, copy_AbstractSymbol(token));
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "int_const\n");
		dump_AbstractSymbol(out, n+2, token);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_int");
		dump_AbstractSymbol(out, n + 2, token);
		dump_type(out, n);
	}

	/** Generates code for this expression.  This method method is provided
	 * to you as an example of code generation.
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		CgenSupport.emitLoadInt(CgenSupport.ACC, (IntSymbol) AbstractTable.inttable.lookup(token.getString()), s);
	}
}

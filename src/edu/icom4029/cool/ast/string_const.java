package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.lexer.StringSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'string_const'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class string_const extends Expression {
	protected AbstractSymbol token;

	/** Creates "string_const" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for token
	 */
	public string_const(int lineNumber, AbstractSymbol a1) {
		super(lineNumber);
		token = a1;
	}

	public TreeNode copy() {
		return new string_const(lineNumber, copy_AbstractSymbol(token));
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "string_const\n");
		dump_AbstractSymbol(out, n+2, token);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_string");
		out.print(Utilities.pad(n + 2) + "\"");
		Utilities.printEscapedString(out, token.getString());
		out.println("\"");
		dump_type(out, n);
	}

	/** Generates code for this expression.  This method method is provided
	 * to you as an example of code generation.
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		CgenSupport.emitLoadString(CgenSupport.ACC, (StringSymbol) AbstractTable.stringtable.lookup(token.getString()), s);
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		set_type(TreeConstants.Str);
	}
}

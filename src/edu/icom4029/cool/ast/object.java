package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'object'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class object extends Expression {
	protected AbstractSymbol name;

	/** Creates "object" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 */
	public object(int lineNumber, AbstractSymbol a1) {
		super(lineNumber);
		name = a1;
	}

	public TreeNode copy() {
		return new object(lineNumber, copy_AbstractSymbol(name));
	}

	public AbstractSymbol getName() { return name; }

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "object\n");
		dump_AbstractSymbol(out, n+2, name);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_object");
		dump_AbstractSymbol(out, n + 2, name);
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

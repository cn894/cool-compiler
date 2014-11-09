package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'assign'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class assign extends Expression {
	protected AbstractSymbol name;
	protected Expression expr;
	/** Creates "assign" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 * @param a1 initial value for expr
	 */
	public assign(int lineNumber, AbstractSymbol a1, Expression a2) {
		super(lineNumber);
		name = a1;
		expr = a2;
	}

	public TreeNode copy() {
		return new assign(lineNumber, copy_AbstractSymbol(name), (Expression)expr.copy());
	}

	public AbstractSymbol getName() { return name; }
	public Expression     getExpr() { return expr; }

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "assign\n");
		dump_AbstractSymbol(out, n+2, name);
		expr.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_assign");
		dump_AbstractSymbol(out, n + 2, name);
		expr.dump_with_types(out, n + 2);
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

package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.Enumeration;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;

/** Defines AST constructor 'method'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class method extends Feature {
	protected AbstractSymbol name;
	protected Formals        formals;
	protected AbstractSymbol return_type;
	protected Expression     expr;

	/** Creates "method" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 * @param a1 initial value for formals
	 * @param a2 initial value for return_type
	 * @param a3 initial value for expr
	 */
	public method(int lineNumber, AbstractSymbol a1, Formals a2, AbstractSymbol a3, Expression a4) {
		super(lineNumber);
		name        = a1;
		formals     = a2;
		return_type = a3;
		expr        = a4;
	}

	public TreeNode copy() {
		return new method(lineNumber, copy_AbstractSymbol(name), (Formals)formals.copy(), copy_AbstractSymbol(return_type), (Expression)expr.copy());
	}

	public AbstractSymbol getName()       { return name; }
	public Formals        getFormals()    { return formals; }
	public AbstractSymbol getReturnType() { return return_type; }
	public Expression     getExpr()       {return expr; }

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "method\n");
		dump_AbstractSymbol(out, n+2, name);
		formals.dump(out, n+2);
		dump_AbstractSymbol(out, n+2, return_type);
		expr.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_method");
		dump_AbstractSymbol(out, n + 2, name);
		for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
			((FormalAbstract)e.nextElement()).dump_with_types(out, n + 2);
		}
		dump_AbstractSymbol(out, n + 2, return_type);
		expr.dump_with_types(out, n + 2);
	}
}

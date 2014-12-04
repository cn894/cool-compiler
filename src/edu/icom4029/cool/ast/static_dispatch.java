package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.Enumeration;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'static_dispatch'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class static_dispatch extends Expression {
	protected Expression expr;
	protected AbstractSymbol type_name;
	protected AbstractSymbol name;
	protected Expressions actual;
	/** Creates "static_dispatch" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for expr
	 * @param a1 initial value for type_name
	 * @param a2 initial value for name
	 * @param a3 initial value for actual
	 */
	public static_dispatch(int lineNumber, Expression a1, AbstractSymbol a2, AbstractSymbol a3, Expressions a4) {
		super(lineNumber);
		expr = a1;
		type_name = a2;
		name = a3;
		actual = a4;
	}

	public TreeNode copy() {
		return new static_dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(type_name), copy_AbstractSymbol(name), (Expressions)actual.copy());
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "static_dispatch\n");
		expr.dump(out, n+2);
		dump_AbstractSymbol(out, n+2, type_name);
		dump_AbstractSymbol(out, n+2, name);
		actual.dump(out, n+2);
	}


	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_static_dispatch");
		expr.dump_with_types(out, n + 2);
		dump_AbstractSymbol(out, n + 2, type_name);
		dump_AbstractSymbol(out, n + 2, name);
		out.println(Utilities.pad(n + 2) + "(");
		for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
			((Expression)e.nextElement()).dump_with_types(out, n + 2);
		}
		out.println(Utilities.pad(n + 2) + ")");
		dump_type(out, n);
	}

	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		CgenSupport.codeActuals(actual, s);
		expr.code(s);
		CgenSupport.emitCheckVoidCallDispAbort(lineNumber, s);
		CgenSupport.emitLoadAddress(CgenSupport.T1, type_name + CgenSupport.DISPTAB_SUFFIX, s);
		CgenSupport.emitLoad(CgenSupport.T1, AbstractTable.classTable.getMethodOffset(type_name, name, actual), CgenSupport.T1, s);
		CgenSupport.emitJalr(CgenSupport.T1, s);
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		expr.semant(classTable, cl, symbolTable);
		AbstractSymbol exprType = expr.get_type();

		if (exprType == TreeConstants.SELF_TYPE) {
			exprType = (AbstractSymbol)symbolTable.lookup(TreeConstants.SELF_TYPE);
		}

		if (!classTable.isBase(type_name, exprType)) {
			classTable.semantError(cl).println("Expression type " + expr.get_type().getString() + 
					" does not conform to declared static dispatch type " + type_name.getString());
		}

		for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
			((Expression)e.nextElement()).semant(classTable, cl, symbolTable);
		}

		SymbolTable methodsTable = classTable.getMethodTable(type_name);
		method methodInfo = (method)methodsTable.lookup(name);
		AbstractSymbol nameType = methodInfo.getReturnType();
		set_type(nameType);
	}


}

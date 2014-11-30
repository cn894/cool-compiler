package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'isvoid'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class isvoid extends Expression {
	protected Expression e1;

	/** Creates "isvoid" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for e1
	 */
	public isvoid(int lineNumber, Expression a1) {
		super(lineNumber);
		e1 = a1;
	}

	public Expression getExpression() {
		return e1;
	}

	public TreeNode copy() {
		return new isvoid(lineNumber, (Expression)e1.copy());
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "isvoid\n");
		e1.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_isvoid");
		e1.dump_with_types(out, n + 2);
		dump_type(out, n);
	}

	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		e1.code(s);
		
		int labelVoid = CgenSupport.genLabelNum();
		int labelEnd = CgenSupport.genLabelNum();
		CgenSupport.emitBeqz(CgenSupport.ACC, labelVoid, s);

		// not void
		CgenSupport.emitLoadFalse(CgenSupport.ACC, s);
		CgenSupport.emitBranch(labelEnd, s);

		// void
		CgenSupport.emitLabelDef(labelVoid, s);
		CgenSupport.emitLoadTrue(CgenSupport.ACC, s);

		// end
		CgenSupport.emitLabelDef(labelEnd, s);
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		e1.semant(classTable, cl, symbolTable); // Perform semantic analysis on the expression
		set_type(TreeConstants.Bool);
	}
}

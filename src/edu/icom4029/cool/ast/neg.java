package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'neg'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class neg extends Expression {
	protected Expression e1;
	
	/** Creates "neg" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for e1
	 */
	public neg(int lineNumber, Expression a1) {
		super(lineNumber);
		e1 = a1;
	}

	public TreeNode copy() {
		return new neg(lineNumber, (Expression)e1.copy());
	}
	
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "neg\n");
		e1.dump(out, n+2);
	}
	
	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_neg");
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

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		e1.semant(classTable, cl, symbolTable); // Perform semantic analysis on the expression
		
		if (e1.get_type() != TreeConstants.Int) {
			classTable.semantError(cl).println("non-Int argument: ~" + e1.get_type().getString());
			set_type(TreeConstants.No_type);
		}
		else {
			set_type(TreeConstants.Int);
		}
	}
}

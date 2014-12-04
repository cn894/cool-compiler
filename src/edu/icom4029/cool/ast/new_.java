package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'new_'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class new_ extends Expression {
	protected AbstractSymbol type_name;
	/** Creates "new_" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for type_name
	 */
	public new_(int lineNumber, AbstractSymbol a1) {
		super(lineNumber);
		type_name = a1;
	}

	public TreeNode copy() {
		return new new_(lineNumber, copy_AbstractSymbol(type_name));
	}

	public AbstractSymbol getTypeName() { return type_name; }

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "new_\n");
		dump_AbstractSymbol(out, n+2, type_name);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_new");
		dump_AbstractSymbol(out, n + 2, type_name);
		dump_type(out, n);
	}
	
	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		if (type_name == TreeConstants.SELF_TYPE) {
            CgenSupport.emitLoadAddress(CgenSupport.T1, CgenSupport.CLASSOBJTAB, s);
            CgenSupport.emitLoad(CgenSupport.T2, 0, CgenSupport.SELF, s);
            CgenSupport.emitSll(CgenSupport.T2, CgenSupport.T2, 3, s);
            CgenSupport.emitAddu(CgenSupport.T1, CgenSupport.T1, CgenSupport.T2, s);
            CgenSupport.emitLoad(CgenSupport.ACC, 0, CgenSupport.T1, s);
            CgenSupport.emitPush(CgenSupport.T1, s);
            CgenSupport.emitJal("Object.copy", s);
            CgenSupport.emitPop(CgenSupport.T1, s);
            CgenSupport.emitLoad(CgenSupport.T1, 1, CgenSupport.T1, s);
            CgenSupport.emitJalr(CgenSupport.T1, s);
        }
		else {
            CgenSupport.emitLoadAddress(CgenSupport.ACC, type_name.toString() + CgenSupport.PROTOBJ_SUFFIX, s);
            CgenSupport.emitJal("Object.copy", s);
            CgenSupport.emitJal(type_name.toString() + CgenSupport.CLASSINIT_SUFFIX, s);
        }
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		if (type_name != TreeConstants.SELF_TYPE && !classTable.hasClass(type_name.getString())) {
			classTable.semantError(cl).println("'new' used with undefined class " + type_name.getString());
			set_type(TreeConstants.No_type);
		}
		else {
			set_type(type_name);
		}
	}
}

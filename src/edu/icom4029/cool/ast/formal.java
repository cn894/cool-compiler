package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'formal'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class formal extends FormalAbstract {
	protected AbstractSymbol name;
	protected AbstractSymbol type_decl;

	/** Creates "formal" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 * @param a1 initial value for type_decl
	 */
	public formal(int lineNumber, AbstractSymbol a1, AbstractSymbol a2) {
		super(lineNumber);
		name      = a1;
		type_decl = a2;
	}

	public TreeNode copy() {
		return new formal(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl));
	}

	public AbstractSymbol getName() { return name; }
	public AbstractSymbol getType() { return type_decl; }


	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "formal\n");
		dump_AbstractSymbol(out, n+2, name);
		dump_AbstractSymbol(out, n+2, type_decl);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_formal");
		dump_AbstractSymbol(out, n + 2, name);
		dump_AbstractSymbol(out, n + 2, type_decl);
	}
	
	public void semant(ClassTable classTable, class_ c) {
		if (type_decl == TreeConstants.SELF_TYPE) {
			classTable.semantError(c).println("Formal parameter " + name.getString() + " cannot have type SELF_TYPE.");
		} 
		if (name == TreeConstants.self) {
			classTable.semantError(c).println("'self' cannot be the name of a formal parameter.");
		}
	}
	
	public void fillSymbolTable(SymbolTable symbolTable) {
		symbolTable.addId(name, type_decl);
	}
}

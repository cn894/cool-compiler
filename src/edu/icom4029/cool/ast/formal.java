package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;

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
	
	public void semant() {
		
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
}

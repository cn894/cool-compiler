package edu.icom4029.cool.ast;

import java.io.PrintStream;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;


/** Defines AST constructor 'attr'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class attr extends Feature {
	protected AbstractSymbol name;
	protected AbstractSymbol type_decl;
	protected Expression init;

	/** Creates "attr" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 * @param a1 initial value for type_decl
	 * @param a2 initial value for init
	 */
	public attr(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
		super(lineNumber);
		name      = a1;
		type_decl = a2;
		init      = a3;
	}

	public TreeNode copy() {
		return new attr(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)init.copy());
	}

	public AbstractSymbol getName()       { return name; }
	public Expression     getExpression() { return init; }
	public AbstractSymbol getType()	      { return type_decl; }

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "attr\n");
		dump_AbstractSymbol(out, n+2, name);
		dump_AbstractSymbol(out, n+2, type_decl);
		init.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_attr");
		dump_AbstractSymbol(out, n + 2, name);
		dump_AbstractSymbol(out, n + 2, type_decl);
		init.dump_with_types(out, n + 2);
	}

	@Override
	public void semant(ClassTable classTable, class_ c, SymbolTable symbolTable) {
		if (name == TreeConstants.self) {
			classTable.semantError(c).println("'self' cannot be the name of an attribute");
		}
		init.semant(classTable, c, symbolTable);
	}

	@Override
	public String fillMethodTable(SymbolTable methodTable) {
		return "";
	}

	@Override
	public String fillAttrTable(SymbolTable attrTable) {
		if (attrTable.lookup(name) != null) {
			return "Attribute " + name.getString() + " is an attribute from an inherited class";
		}
		else {
			attrTable.addId(name, type_decl);
			return "";
		}
	}
}

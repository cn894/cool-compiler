package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.Enumeration;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'class_'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class class_ extends ClassAbstract {
	protected AbstractSymbol name;
	protected AbstractSymbol parent;
	protected Features       features;
	protected AbstractSymbol filename;
	private   SymbolTable    methodTable;
	private   SymbolTable    attrTable;

	/** Creates "class_" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 * @param a1 initial value for parent
	 * @param a2 initial value for features
	 * @param a3 initial value for filename
	 */
	public class_(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Features a3, AbstractSymbol a4) {
		super(lineNumber);
		name     = a1;
		parent   = a2;
		features = a3;
		filename = a4;
	}
	
	public void semant(ClassTable classTable, SymbolTable symbolTable) {
		
	}

	public TreeNode copy() {
		return new class_(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(parent), (Features)features.copy(), copy_AbstractSymbol(filename));
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "class_\n");
		dump_AbstractSymbol(out, n+2, name);
		dump_AbstractSymbol(out, n+2, parent);
		features.dump(out, n+2);
		dump_AbstractSymbol(out, n+2, filename);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_class");
		dump_AbstractSymbol(out, n + 2, name);
		dump_AbstractSymbol(out, n + 2, parent);
		out.print(Utilities.pad(n + 2) + "\"");
		Utilities.printEscapedString(out, filename.getString());
		out.println("\"\n" + Utilities.pad(n + 2) + "(");
		for (Enumeration e = features.getElements(); e.hasMoreElements();) {
			((Feature)e.nextElement()).dump_with_types(out, n + 2);
		}
		out.println(Utilities.pad(n + 2) + ")");
	}
	
	public AbstractSymbol getName()        { return name; }
	public AbstractSymbol getParent()      { return parent; }
	public AbstractSymbol getFilename()    { return filename; }
	public Features       getFeatures()    { return features; }
	public SymbolTable    getMethodTable() { return methodTable; }
	public SymbolTable    getAttrTable()   { return attrTable; }
}

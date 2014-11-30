package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenClassTable;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'program'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class program extends ProgramAbstract {
	private ClassTable classTable;

	protected Classes classes;

	/** Creates "program" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for classes
	 */
	public program(int lineNumber, Classes a1) {
		super(lineNumber);
		classes = a1;
	}

	public TreeNode copy() {
		return new program(lineNumber, (Classes)classes.copy());
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "program\n");
		classes.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_program");
		for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
			((ClassAbstract)e.nextElement()).dump_with_types(out, n+2);
		}
	}

	/** This method is the entry point to the semantic checker.  You will
    need to complete it in programming assignment 4.
<p>
    Your checker should do the following two things:
<ol>
<li>Check that the program is semantically correct
<li>Decorate the abstract syntax tree with type information
    by setting the type field in each Expression node.
    (see tree.h)
</ol>
<p>
You are free to first do (1) and make sure you catch all semantic
	errors. Part (2) can be done in a second stage when you want
to test the complete compiler.
	 */
	public void semant() {
		// Semantic Analysis: First-pass
		// Build the inheritance graph and check for multiply defined classes and cycles in the inheritance graph
		classTable = new ClassTable(classes);
		
		if (classTable.errors()) {
			System.err.println("Compilation halted due to static semantic errors.");
			System.exit(1);
		}
		
		// Check that the program has a class called Main
		if (!classTable.hasClass(TreeConstants.Main.getString())) {
			classTable.semantError().println("Class Main is not defined.");
		}
		
		// Semantic Analysis: Second-pass
		// Add all of the methods and attributes to their corresponding classes
		classTable.fillMethodAttrTable();
		
		// Semantic Analysis: Third-pass
		// Type check everything
		SymbolTable symbolTable = new SymbolTable();
		symbolTable.enterScope();
//		symbolTable.addId(TreeConstants.self, TreeConstants.SELF_TYPE);
		
		// Perform type checking on each class that composes the program
		for (class_ c : classTable.getClassList()) {
			c.semant(classTable, symbolTable);
		}
		
		if (classTable.errors()) {
			System.err.println("Compilation halted due to static semantic errors.");
			System.exit(1);
		}
	}

	/** This method is the entry point to the code generator.  All of the work
	 * of the code generator takes place within CgenClassTable constructor.
	 * @param s the output stream 
	 * @see CgenClassTable
	 * */
	public void cgen(PrintStream s) {
		CgenClassTable codegen_classtable = new CgenClassTable(classes, s);
	}
}

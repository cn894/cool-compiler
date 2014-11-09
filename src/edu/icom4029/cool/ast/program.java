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
	HashMap<String, SymbolTable> objectEnvironments = new HashMap<String, SymbolTable>(); // A map that defines the object environment for each class. Each class can have many levels of scope due to methods, lets, etc

	HashMap<String, SymbolTable> methodSymTabMap    = new HashMap<String, SymbolTable>();
	HashMap<String, HashMap<String, ArrayList<AbstractSymbol>>> methodEnvironment;
	ClassTable classTable;

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
		/* ClassTable constructor may do some semantic analysis */
		classTable = new ClassTable(classes);

		/* some semantic analysis code may go here */
		if (classTable.errors()) {
			System.err.println("Compilation halted due to static semantic errors.");
			System.exit(1);
		}
		
		if (!classTable.hasClass(TreeConstants.Main.getString())) {
			classTable.semantError().println("Class Main is not defined.");
		}
		
		classTable.fillMethodAttrTable();
		
		SymbolTable symbolTable = new SymbolTable();
		symbolTable.enterScope();
		symbolTable.addId(TreeConstants.self, TreeConstants.SELF_TYPE);
		
		for (class_ c : classTable.getClassList()) {
			c.semant(classTable, symbolTable);
		}
		
		if (classTable.errors()) {
			System.err.println("Compilation halted due to static semantic errors.");
			System.exit(1);
		}

//		Classes basicClasses = classTable.getBasicClassList();

		// Traversing the AST formed by the basic classes: Int, Bool, String, IO to add them all to the environment when checking all other user defined classes.
//		traverseAST(basicClasses);
		// Traverse the program's entire AST to perform semantic analysis on it.
//		traverseAST(classes);

	}

//	private void traverseAST(Classes classes) {
//		// Loop through each class defined in the program
//		class_ currentClass = null;
//		Enumeration e = classes.getElements();
//
//		while(e.hasMoreElements()) {
//			currentClass = (class_) e.nextElement(); // get next class in class list that comprises program
//
//			objectEnvironments.put(currentClass.getName().toString(), new SymbolTable());                          // Define the object environment for the current class
//			SymbolTable currentClassObjectEnvironment = objectEnvironments.get(currentClass.getName().toString()); // Get that environment
//			currentClassObjectEnvironment.enterScope();                                                            // Enter a scope. Necessary before doing anything with a SymbolTable.
//
//			// NEW POOP ALERT
//			//		methodEnvironment.put(currentClass.getName().toString(), new HashMap<String,ArrayList<AbstractSymbol>>());
//			//        HashMap<String, ArrayList<AbstractSymbol>> methodSymTab = methodEnvironment.get(currentClass.getName().toString());
//			//	    // NEW POOP ALERT
//
//			// get the features of the current class
//			Features features = currentClass.getFeatures();
//			Enumeration fe    = features.getElements();
//
//			// Loop through all of the features in the class body
//			while(fe.hasMoreElements()) {
//				Feature currentFeature = (Feature) fe.nextElement(); // get next feature in the class body
//
//				if (currentFeature instanceof attr) {
//					// Found attribute
//					AbstractSymbol attributeName = ((attr) currentFeature).getName();       // AbstractSymbol representing the identifier of the attribute
//					AbstractSymbol attributeType = ((attr) currentFeature).getType();       // AbstractSymbol representing the type of the attribute
//					Expression     attributeExpr = ((attr) currentFeature).getExpression(); // The attribute expression
//
//					// if the attribute had been declared previously, throw a semamtic error
//					if (currentClassObjectEnvironment.lookup(attributeName) != null) {
//						classTable.semantError(currentClass.getFilename(), currentFeature).println("Attribute " + attributeName.toString() + " is defined more than once.");
//					}
//
//					// add attribute and its type to the current class's environment, will get overwritten if previously defined
//					// we still add it to keep going with the semantic analysis, rather than quit with the error.
//					currentClassObjectEnvironment.addId(attributeName, attributeType);
//
//					// Traverse the attribute expression.
//					// Recall that it can be either a declaration or assignment
//					// Also, the right-hand side is an expression within the attribute expression. See Parser.cup
//					traverseExpr(currentClass, attributeExpr, currentClassObjectEnvironment, null);
//
//				}
//				else {
//					// Found method
//					AbstractSymbol methodName = ((method) currentFeature).getName(); // AbstractSymbol representing the identifier of the method
//
//					// Add method to method Symbol Table,if already present, scope error
//					//			    if (methodSymTab.containsKey(methodName.toString())) {
//					//			    	classTable.semantError(currentClass.getFilename(), currentFeature).println("Method " + methodName.toString() + " is defined more than once.");
//					//			    }
//					//			    
//					//			    // Traverse the method definition
//					//			    traverseMethod(currentClass, ((method) currentFeature), currentClassObjectEnvironment);
//				}
//
//
//			}	
//		}
//	}

	/** Traverse method. Check formal parameters, return type and expressions **/
//	private void traverseMethod(class_ currentClass, method currentMethod, SymbolTable currentClassObjectEnvironment) {
//		// Traverse formal arguments, adding them to scope
//		String  className  = currentClass.getName().toString();  // Get the name of the class in which the current method is defined
//		String  methodname = currentMethod.getName().toString(); // Get the identifier of the current method
//		Formals formals    = currentMethod.getFormals();         // Get the parameter definitions of the current method
//		currentClassObjectEnvironment.enterScope();              // Starts a new scope. Recall that functions also define their own scope.
//
//
//		if (methodEnvironment.get(className).get(methodname) == null) {
//			methodEnvironment.get(className).put(methodname, new ArrayList<AbstractSymbol>());
//		}
//
//		// Loop through the formal parameters of the method definition and assign types to each one
//		Enumeration e = formals.getElements();
//		while(e.hasMoreElements()) {
//			formal currentFormal = (formal) e.nextElement();
//
//			// Throw an error if there is more than one formal parameter with the same name
//			if (currentClassObjectEnvironment.probe(currentFormal.getName()) != null) {
//				classTable.semantError(currentClass.getFilename(), currentFormal).println("Formal parameter " + currentFormal.getName().toString() + " is defined more than once");
//			}
//
//			// Add the current formal parameter into the environment.
//			currentClassObjectEnvironment.addId(currentFormal.getName(), currentFormal.getType());
//			methodEnvironment.get(className).get(methodname).add(currentFormal.getType());
//		}
//
//		// Add the return type of the current method in the environment
//		methodEnvironment.get(className).get(methodname).add(currentMethod.getReturnType());
//
//		// Traverse the function body, which is an expression, to assign it a type
//		traverseExpr(currentClass, currentMethod.getExpr(), currentClassObjectEnvironment, null);
//		currentClassObjectEnvironment.exitScope(); // Exit the current method's scope
//	}

//	private void traverseExpr(class_ currentClass, Expression expr, SymbolTable currentClassObjectEnvironment, SymbolTable currentClassMethodEnvironment) {
//		System.out.println("You are traversing the Expression: " + expr.toString());
//
//		if (expr instanceof string_const) {
//			// Expression is a String constant
//			expr.set_type(TreeConstants.Str);
//		} else if (expr instanceof bool_const) {
//			// Expression is a Boolean constant
//			expr.set_type(TreeConstants.Bool);
//		} else if (expr instanceof int_const) {
//			// Expression is an Integer constant
//			expr.set_type(TreeConstants.Int);
//		} else if (expr instanceof plus) {
//			// Expression is a sum of expressions: e1 + e2
//			expr.set_type(TreeConstants.Int);
//			traverseExpr(currentClass, ((plus) expr).getLHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//			traverseExpr(currentClass, ((plus) expr).getRHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//		} else if (expr instanceof sub) {
//			// Expression is a subtraction of expressions: e1 - e2
//			expr.set_type(TreeConstants.Int);
//			traverseExpr(currentClass, ((sub) expr).getLHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//			traverseExpr(currentClass, ((sub) expr).getRHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//		} else if (expr instanceof mul) {
//			// Expression is a multiplication of expressions: e1 * e2
//			expr.set_type(TreeConstants.Int);
//			traverseExpr(currentClass, ((mul) expr).getLHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//			traverseExpr(currentClass, ((mul) expr).getRHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//		} else if (expr instanceof divide) {
//			// Expression is a division of expressions: e1 * e2
//			expr.set_type(TreeConstants.Int);
//			traverseExpr(currentClass, ((divide) expr).getLHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//			traverseExpr(currentClass, ((divide) expr).getRHS(), currentClassObjectEnvironment, currentClassMethodEnvironment);
//		} else if (expr instanceof new_) {
//			// Expression is a new object instantiation
//			expr.set_type(((new_)expr).getTypeName());
//		} else if (expr instanceof assign) {
//			Expression e = ((assign) expr).getExpr();
//			traverseExpr( currentClass, e, currentClassObjectEnvironment, currentClassMethodEnvironment);
//			// The type of the expression
//			expr.set_type(e.get_type());
//		}
//	}

	/** This method is the entry point to the code generator.  All of the work
	 * of the code generator takes place within CgenClassTable constructor.
	 * @param s the output stream 
	 * @see CgenClassTable
	 * */
	public void cgen(PrintStream s) {
		CgenClassTable codegen_classtable = new CgenClassTable(classes, s);
	}
}

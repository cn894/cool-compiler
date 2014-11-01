package edu.icom4029.cool.parser;

/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import edu.icom4029.cool.ast.Classes;
import edu.icom4029.cool.ast.Features;
import edu.icom4029.cool.ast.Formals;
import edu.icom4029.cool.ast.attr;
import edu.icom4029.cool.ast.class_;
import edu.icom4029.cool.ast.formal;
import edu.icom4029.cool.ast.method;
import edu.icom4029.cool.ast.no_expr;
import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
public class ClassTable {
    private int         semantErrors;
    private PrintStream errorStream;

    private HashMap<String, ArrayList<String>> adjacencyList;
    private HashMap<String, class_>            nameToClass;
    private HashMap<String, Integer>           depthList;

    private Classes basicClassList;

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make it do what
     * you want.
     * */
    private void installBasicClasses() {
        AbstractSymbol filename = AbstractTable.stringtable.addString("<basic class>");
	
        // The following demonstrates how to create dummy parse trees to
        // refer to basic Cool classes.  There's no need for method
		// bodies -- these are already built into the runtime system.
	
		// IMPORTANT: The results of the following expressions are
		// stored in local variables.  You will want to do something
		// with those variables at the end of this method to make this
		// code meaningful.
	
		// The Object class has no parent class. Its methods are
		//        cool_abort() : Object    aborts the program
		//        type_name() : Str        returns a string representation 
		//                                 of class name
		//        copy() : SELF_TYPE       returns a copy of the object

		class_ Object_class = 
		    new class_(0, 
		    		TreeConstants.Object_, 
		    		TreeConstants.No_class,
		    		new Features(0)
		    			.appendElement(new method(0, TreeConstants.cool_abort, new Formals(0), TreeConstants.Object_,   new no_expr(0)))
		    			.appendElement(new method(0, TreeConstants.type_name,  new Formals(0), TreeConstants.Str,       new no_expr(0)))
		    			.appendElement(new method(0, TreeConstants.copy,       new Formals(0), TreeConstants.SELF_TYPE, new no_expr(0))),
		    filename);
	
		// The IO class inherits from Object. Its methods are
		//        out_string(Str) : SELF_TYPE  writes a string to the output
		//        out_int(Int) : SELF_TYPE     writes an int   to the output
		//
		//        in_string() : Str            reads a string from the input
		//        in_int() : Int               reads an int   from the input
	
		class_ IO_class = 
			new class_(0,
					TreeConstants.IO,
					TreeConstants.Object_,
					new Features(0)
						.appendElement(new method(0, TreeConstants.out_string, 
							new Formals(0)
								.appendElement(new formal(0, TreeConstants.arg, TreeConstants.Str)), TreeConstants.SELF_TYPE, new no_expr(0)))
							
						.appendElement(new method(0, TreeConstants.out_int, 
							new Formals(0)
								.appendElement(new formal(0, TreeConstants.arg, TreeConstants.Int)), TreeConstants.SELF_TYPE, new no_expr(0)))
							
						.appendElement(new method(0, TreeConstants.in_string, new Formals(0), TreeConstants.Str, new no_expr(0)))
						.appendElement(new method(0, TreeConstants.in_int,    new Formals(0), TreeConstants.Int, new no_expr(0))),
			filename);
	
		// The Int class has no methods and only a single attribute, the
		// "val" for the integer.
	
		class_ Int_class = 
			new class_(0,
					TreeConstants.Int,
					TreeConstants.Object_,
					new Features(0)
						.appendElement(new attr(0, TreeConstants.val, TreeConstants.prim_slot, new no_expr(0))),
			filename);
	
		// Bool also has only the "val" slot.
		class_ Bool_class = 
			new class_(0,
					TreeConstants.Bool,
					TreeConstants.Object_,
					new Features(0)
						.appendElement(new attr(0, TreeConstants.val, TreeConstants.prim_slot, new no_expr(0))),
			filename);
	
		// The class Str has a number of slots and operations:
		//       val                              the length of the string
		//       str_field                        the string itself
		//       length() : Int                   returns length of the string
		//       concat(arg: Str) : Str           performs string concatenation
		//       substr(arg: Int, arg2: Int): Str substring selection
	
		class_ Str_class =
			new class_(0,
					TreeConstants.Str,
					TreeConstants.Object_,
					new Features(0)
						.appendElement(new attr(0, TreeConstants.val,       TreeConstants.Int,       new no_expr(0)))
						.appendElement(new attr(0, TreeConstants.str_field, TreeConstants.prim_slot, new no_expr(0)))
						
						.appendElement(new method(0, TreeConstants.length, new Formals(0), TreeConstants.Int, new no_expr(0)))
						
						.appendElement(new method(0, TreeConstants.concat, 
							new Formals(0)
								.appendElement(new formal(0, TreeConstants.arg, TreeConstants.Str)), 
							TreeConstants.Str, 
							new no_expr(0)))
							
						.appendElement(new method(0, TreeConstants.substr, 
							new Formals(0)
								.appendElement(new formal(0, TreeConstants.arg,  TreeConstants.Int))
								.appendElement(new formal(0, TreeConstants.arg2, TreeConstants.Int)),
							TreeConstants.Str,
							new no_expr(0))),
			filename);

		/* Do somethind with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */
		
		nameToClass.put(TreeConstants.Object_.getString(), Object_class);
		nameToClass.put(TreeConstants.IO.getString(),      IO_class);
		nameToClass.put(TreeConstants.Int.getString(),     Int_class);
		nameToClass.put(TreeConstants.Bool.getString(),    Bool_class);
		nameToClass.put(TreeConstants.Str.getString(),     Str_class);
		
		adjacencyList.put(TreeConstants.Object_.getString(), new ArrayList<String>());
		adjacencyList.put(TreeConstants.IO.getString(),      new ArrayList<String>());
		adjacencyList.put(TreeConstants.Int.getString(),     new ArrayList<String>());
		adjacencyList.put(TreeConstants.Bool.getString(),    new ArrayList<String>());
		adjacencyList.put(TreeConstants.Str.getString(),     new ArrayList<String>());
		
		basicClassList = new Classes(0);
		basicClassList.appendElement(Object_class);
		basicClassList.appendElement(IO_class);
		basicClassList.appendElement(Int_class);
		basicClassList.appendElement(Bool_class);
		basicClassList.appendElement(Str_class);
    }

    public ClassTable(Classes cls) {
    	semantErrors = 0;
    	errorStream  = System.err;
		
		// Build graph first out of class declarations
    	adjacencyList = new HashMap<String, ArrayList<String>>();
    	nameToClass   = new HashMap<String, class_>();
    	depthList     = new HashMap<String, Integer>();
		
		installBasicClasses();
		buildGraph(cls);
		assignDepths("Object", 0);
    }

    /**
     * Returns the depth of a node given its name.
     * @param node the name of the desired node
     * @return the depth of the given node
     */
    public int getDepth(String node) {
    	return depthList.get(node);
    }
    
    /**
     * Assign depths to every node in the inheritance tree.
     * @param node the starting node of the tree
     * @param depth the depth to be assigned
     */
    private void assignDepths(String node, int depth) {
    	depthList.put(node, depth);
    	ArrayList<String> children = adjacencyList.get(node);
    	
    	if (children == null) return;
    	if (!children.isEmpty()) {
    		// Update the depth of each child class recursively.
    		depth = depth + 1;
    		for (String child : children) {
    			assignDepths(child, depth);
    		}
    	}
    }
    
    /**
     * Builds an adjacency list of the inheritance graph
     * while making sure there are no cycles
     * @param classList list of classes generated by the parser
     */
    private void buildGraph(Classes classList) {
    	adjacencyList.put(TreeConstants.Object_.getString(), new ArrayList<String>());
    	
    	// Add primitive classes as children of Object.
    	ArrayList<String> objectList = adjacencyList.get(TreeConstants.Object_.getString());
    	objectList.add(TreeConstants.IO.getString());
    	objectList.add(TreeConstants.Int.getString());
    	objectList.add(TreeConstants.Bool.getString());
    	objectList.add(TreeConstants.Str.getString());
    	adjacencyList.put(TreeConstants.Object_.getString(), objectList);
    	
    	// Generate adjacency lists for each class.
    	for (Enumeration e = classList.getElements(); e.hasMoreElements(); ) {
    		class_ currentClass = ((class_) e.nextElement());
    		
    		// Check that the class has not yet been defined. Else, produce a semant error.
    		String className = currentClass.getName().toString();
    		if (!nameToClass.containsKey(className)) {
    			nameToClass.put(className, currentClass);
    		}
    		else {
    			semantError(currentClass).println("Class " + className + " was previously defined.");
    			continue;
    		}
    		
    		// If parent already exists, append the child to the list of children.
    		String parent = currentClass.getParent().toString();
    		if (!adjacencyList.containsKey(parent)) {
    			adjacencyList.put(parent, new ArrayList<String>());
    		}
    		
    		// Add the child class to the parent's adjacency list.
    		adjacencyList.get(parent).add(currentClass.getName().toString());
    	}
    	
    	// Check that each parent in a parent-child inheritance is valid.
    	HashSet<String> bogusClasses = new HashSet<String>();
    	for (String parent : adjacencyList.keySet()) {
    		if (!nameToClass.containsKey(parent)) {
    			for (String child : adjacencyList.get(parent)) {
    				semantError(nameToClass.get(child)).println("Class " + child +" inherits from an undefined class "+ parent);
    			}
    			// Add the bogus parent class to bogusClasses so it can be removed later.
    			bogusClasses.add(parent);
    		}
    	}
    	
    	// Remove all the bogus classes from the inheritance graph.
    	for (String bogusClass : bogusClasses) {
    		adjacencyList.remove(bogusClass);
    	}
    	
    	// Check if any class is inheriting from Int, Bool or String
    	for (String child : adjacencyList.get(TreeConstants.Int.getString())) {
    		semantError(nameToClass.get(child)).println("Class " + child + " illegally inherits from class Int");
    	}
    	for (String child : adjacencyList.get(TreeConstants.Bool.getString())) {
    		semantError(nameToClass.get(child)).println("Class " + child + " illegally inherits from class Bool");
    	}
    	for (String child : adjacencyList.get(TreeConstants.Str.getString())) {
    		semantError(nameToClass.get(child)).println("Class " + child + " illegally inherits from class String");
    	}
    	
    	// Return before checking for cycles of there are errors.
    	if (errors()) {
    		return;
    	}
    	
    	// Now check for cycles, using a depth first search starting from Object
    	HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
    	for (String key : adjacencyList.keySet()) {
    		visited.put(key, false);
    		for (String value : adjacencyList.get(key)) {
    			visited.put(value, false);
    		}
    	}
    	
    	depthFirstSearch(visited, TreeConstants.Object_.getString());
    	
    	// Check if the depth first search resulted in cycles.
    	for (String key : visited.keySet()) {
    		if (!visited.get(key)) {
    			semantError(nameToClass.get(key)).println("Class " + key + ", or its ancestors, is involved in a cycle");
    		}
    	}
    }
    
    private boolean depthFirstSearch(HashMap<String, Boolean> visited, String node) {
    	if (visited.get(node)) {
    		// This shouldn't happen, but let's be safe.
    		semantError(nameToClass.get(node)).println("Class " + node + ", or its ancestors, is involved in a cycle");
    		return false;
    	}
    	visited.put(node, true);
    	if (adjacencyList.get(node) == null) {
    		return true;
    	}
    	for (String child : adjacencyList.get(node)) {
    		depthFirstSearch(visited, child);
    	}
    	return true;
    }
    
    /**
     * Checks if the given AbstractSymbol corresponds to a valid type of class.
     * @param type an AbstractSymbol object corresponding to a type or class
     * @return true if the symbol is a valid type or class
     */
    public boolean isValidType(AbstractSymbol type) {
    	return nameToClass.containsKey(type.getString());
    }
    
    /**
     * Checks if C1 <= C2
     * @param C1
     * @param C2
     * @param currentClass
     * @return
     */
    public boolean checkConformance(AbstractSymbol C1, AbstractSymbol C2, AbstractSymbol currentClass) {
    	if (C1 == TreeConstants.SELF_TYPE) {
    		C1 = currentClass;
    	}
    	if (C2 == TreeConstants.SELF_TYPE) {
    		C2 = currentClass;
    	}
    	if (C1 == C2) {
    		return true;
    	}
    	return isReachable(C1.getString(), C2.getString());
    }
    
    private boolean isReachable(String C1, String C2) {
    	if (adjacencyList.get(C2) == null) {
    		return false;
    	}
    	for (String child : adjacencyList.get(C2)) {
    		if (child.equals(C1)) {
    			return true;
    		}
    		else if (isReachable(C1, child)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Returns the parent class of a given class.
     * @param child the child class
     * @return the name of the parent class
     */
    public String getParent(String child) {
    	if (child.equals(TreeConstants.Object_.getString())) {
    		return TreeConstants.No_class.getString();
    	}
    	for (String parent : adjacencyList.keySet()) {
    		ArrayList<String> listOfChildren = adjacencyList.get(parent);
    		if (listOfChildren.indexOf(child) != -1) {
    			return parent;
    		}
    	}
    	// Shouldn't happen, but it's here to avoid compilation errors anyways.
    	return null;
    }
    
    /**
     * Returns a list containing the child classes of a given class. 
     * @param parent the parent class
     * @return the list containing all the child classes
     */
    public ArrayList<String> getChildren(String parent) {
    	return adjacencyList.get(parent);
    }
    
    /**
     * Returns a list of classes containing the Object, IO, Int, Bool and String classes.
     * @return the basic class list
     */
    public Classes getBasicClassList() {
    	return basicClassList;
    }
    
    /**
     * Returns a class_ object corresponding to a given name.
     * @param className the name of the class
     * @return the corresponding class
     */
    public class_ getClass(String className) {
    	return nameToClass.get(className);
    }
    
    /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(class_ c) {
    	return semantError(c.getFilename(), c);
    }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
    	this.errorStream.print(filename + ":" + t.getLineNumber() + ": ");
		return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
    	this.semantErrors++;
    	return this.errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
    	return this.semantErrors != 0;
    }
}

    

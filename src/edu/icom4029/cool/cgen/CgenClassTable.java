package edu.icom4029.cool.cgen;

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

// This is a project skeleton file

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import edu.icom4029.cool.ast.ClassAbstract;
import edu.icom4029.cool.ast.Classes;
import edu.icom4029.cool.ast.Expressions;
import edu.icom4029.cool.ast.Features;
import edu.icom4029.cool.ast.Formals;
import edu.icom4029.cool.ast.attr;
import edu.icom4029.cool.ast.class_;
import edu.icom4029.cool.ast.formal;
import edu.icom4029.cool.ast.method;
import edu.icom4029.cool.ast.no_expr;
import edu.icom4029.cool.core.Flags;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.lexer.BoolConst;
import edu.icom4029.cool.semant.SymbolTable;

/** This class is used for representing the inheritance tree during code
    generation. You will need to fill in some of its methods and
    potentially extend it in other useful ways. */
public class CgenClassTable extends SymbolTable {

	/** All classes in the program, represented as CgenNode */
	private Vector nds;

	/** This is the stream to which assembly instructions are output */
	private PrintStream str;

	private int stringclasstag;
	private int intclasstag;
	private int boolclasstag;

	private int classTag = 0;


	// The following methods emit code for constants and global
	// declarations.

	/** Emits code to start the .data segment and to
	 * declare the global names.
	 * */
	private void codeGlobalData() {
		// The following global names must be defined first.

		str.print("\t.data\n" + CgenSupport.ALIGN);
		str.println(CgenSupport.GLOBAL + CgenSupport.CLASSNAMETAB);
		str.print(CgenSupport.GLOBAL); 
		CgenSupport.emitProtObjRef(TreeConstants.Main, str);
		str.println("");
		str.print(CgenSupport.GLOBAL); 
		CgenSupport.emitProtObjRef(TreeConstants.Int, str);
		str.println("");
		str.print(CgenSupport.GLOBAL); 
		CgenSupport.emitProtObjRef(TreeConstants.Str, str);
		str.println("");
		str.print(CgenSupport.GLOBAL); 
		BoolConst.falsebool.codeRef(str);
		str.println("");
		str.print(CgenSupport.GLOBAL); 
		BoolConst.truebool.codeRef(str);
		str.println("");
		str.println(CgenSupport.GLOBAL + CgenSupport.INTTAG);
		str.println(CgenSupport.GLOBAL + CgenSupport.BOOLTAG);
		str.println(CgenSupport.GLOBAL + CgenSupport.STRINGTAG);

		// We also need to know the tag of the Int, String, and Bool classes
		// during code generation.

		str.println(CgenSupport.INTTAG + CgenSupport.LABEL 
				+ CgenSupport.WORD + intclasstag);
		str.println(CgenSupport.BOOLTAG + CgenSupport.LABEL 
				+ CgenSupport.WORD + boolclasstag);
		str.println(CgenSupport.STRINGTAG + CgenSupport.LABEL 
				+ CgenSupport.WORD + stringclasstag);

	}

	/** Emits code to start the .text segment and to
	 * declare the global names.
	 * */
	private void codeGlobalText() {
		str.println(CgenSupport.GLOBAL + CgenSupport.HEAP_START);
		str.print(CgenSupport.HEAP_START + CgenSupport.LABEL);
		str.println(CgenSupport.WORD + 0);
		str.println("\t.text");
		str.print(CgenSupport.GLOBAL);
		CgenSupport.emitInitRef(TreeConstants.Main, str);
		str.println("");
		str.print(CgenSupport.GLOBAL);
		CgenSupport.emitInitRef(TreeConstants.Int, str);
		str.println("");
		str.print(CgenSupport.GLOBAL);
		CgenSupport.emitInitRef(TreeConstants.Str, str);
		str.println("");
		str.print(CgenSupport.GLOBAL);
		CgenSupport.emitInitRef(TreeConstants.Bool, str);
		str.println("");
		str.print(CgenSupport.GLOBAL);
		CgenSupport.emitMethodRef(TreeConstants.Main, TreeConstants.main_meth, str);
		str.println("");
	}

	/** Emits code definitions for boolean constants. */
	private void codeBools(int classtag) {
		BoolConst.falsebool.codeDef(classtag, str);
		BoolConst.truebool.codeDef(classtag, str);
	}

	/** Generates GC choice constants (pointers to GC functions) */
	private void codeSelectGc() {
		str.println(CgenSupport.GLOBAL + "_MemMgr_INITIALIZER");
		str.println("_MemMgr_INITIALIZER:");
		str.println(CgenSupport.WORD 
				+ CgenSupport.gcInitNames[Flags.cgen_Memmgr]);

		str.println(CgenSupport.GLOBAL + "_MemMgr_COLLECTOR");
		str.println("_MemMgr_COLLECTOR:");
		str.println(CgenSupport.WORD 
				+ CgenSupport.gcCollectNames[Flags.cgen_Memmgr]);

		str.println(CgenSupport.GLOBAL + "_MemMgr_TEST");
		str.println("_MemMgr_TEST:");
		str.println(CgenSupport.WORD 
				+ ((Flags.cgen_Memmgr_Test == Flags.GC_TEST) ? "1" : "0"));
	}

	/** Emits code to reserve space for and initialize all of the
	 * constants.  Class names should have been added to the string
	 * table (in the supplied code, is is done during the construction
	 * of the inheritance graph), and code for emitting string constants
	 * as a side effect adds the string's length to the integer table.
	 * The constants are emmitted by running through the stringtable and
	 * inttable and producing code for each entry. */
	private void codeConstants() {
		// Add constants that are required by the code generator.
		AbstractTable.stringtable.addString("");
		AbstractTable.inttable.addString("0");

		AbstractTable.stringtable.codeStringTable(stringclasstag, str);
		AbstractTable.inttable.codeStringTable(intclasstag, str);
		codeBools(boolclasstag);
	}


	/** Creates data structures representing basic Cool classes (Object,
	 * IO, Int, Bool, String).  Please note: as is this method does not
	 * do anything useful; you will need to edit it to make if do what
	 * you want.
	 * */
	private void installBasicClasses() {
		AbstractSymbol filename 
		= AbstractTable.stringtable.addString("<basic class>");

		// A few special class names are installed in the lookup table
		// but not the class list.  Thus, these classes exist, but are
		// not part of the inheritance hierarchy.  No_class serves as
		// the parent of Object and the other special classes.
		// SELF_TYPE is the self class; it cannot be redefined or
		// inherited.  prim_slot is a class known to the code generator.

		addId(TreeConstants.No_class,
				new CgenNode(new class_(0,
						TreeConstants.No_class,
						TreeConstants.No_class,
						new Features(0),
						filename),
						CgenNode.Basic, this, -1));

		addId(TreeConstants.SELF_TYPE,
				new CgenNode(new class_(0,
						TreeConstants.SELF_TYPE,
						TreeConstants.No_class,
						new Features(0),
						filename),
						CgenNode.Basic, this, -1));

		addId(TreeConstants.prim_slot,
				new CgenNode(new class_(0,
						TreeConstants.prim_slot,
						TreeConstants.No_class,
						new Features(0),
						filename),
						CgenNode.Basic, this, -1));

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
				.appendElement(new method(0, 
						TreeConstants.cool_abort, 
						new Formals(0), 
						TreeConstants.Object_, 
						new no_expr(0)))
						.appendElement(new method(0,
								TreeConstants.type_name,
								new Formals(0),
								TreeConstants.Str,
								new no_expr(0)))
								.appendElement(new method(0,
										TreeConstants.copy,
										new Formals(0),
										TreeConstants.SELF_TYPE,
										new no_expr(0))),
										filename);

		installClass(new CgenNode(Object_class, CgenNode.Basic, this, 0));

		// The IO class inherits from Object. Its methods are
		//        out_string(Str) : SELF_TYPE  writes a string to the output
		//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
		//        in_string() : Str            reads a string from the input
		//        in_int() : Int                "   an int     "  "     "

		class_ IO_class = 
				new class_(0,
						TreeConstants.IO,
						TreeConstants.Object_,
						new Features(0)
				.appendElement(new method(0,
						TreeConstants.out_string,
						new Formals(0)
				.appendElement(new formal(0,
						TreeConstants.arg,
						TreeConstants.Str)),
						TreeConstants.SELF_TYPE,
						new no_expr(0)))
						.appendElement(new method(0,
								TreeConstants.out_int,
								new Formals(0)
						.appendElement(new formal(0,
								TreeConstants.arg,
								TreeConstants.Int)),
								TreeConstants.SELF_TYPE,
								new no_expr(0)))
								.appendElement(new method(0,
										TreeConstants.in_string,
										new Formals(0),
										TreeConstants.Str,
										new no_expr(0)))
										.appendElement(new method(0,
												TreeConstants.in_int,
												new Formals(0),
												TreeConstants.Int,
												new no_expr(0))),
												filename);

		installClass(new CgenNode(IO_class, CgenNode.Basic, this, 1));

		// The Int class has no methods and only a single attribute, the
		// "val" for the integer.

		class_ Int_class = 
				new class_(0,
						TreeConstants.Int,
						TreeConstants.Object_,
						new Features(0)
				.appendElement(new attr(0,
						TreeConstants.val,
						TreeConstants.prim_slot,
						new no_expr(0))),
						filename);

		installClass(new CgenNode(Int_class, CgenNode.Basic, this, intclasstag));

		// Bool also has only the "val" slot.
		class_ Bool_class = 
				new class_(0,
						TreeConstants.Bool,
						TreeConstants.Object_,
						new Features(0)
				.appendElement(new attr(0,
						TreeConstants.val,
						TreeConstants.prim_slot,
						new no_expr(0))),
						filename);

		installClass(new CgenNode(Bool_class, CgenNode.Basic, this, boolclasstag));

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
				.appendElement(new attr(0,
						TreeConstants.val,
						TreeConstants.Int,
						new no_expr(0)))
						.appendElement(new attr(0,
								TreeConstants.str_field,
								TreeConstants.prim_slot,
								new no_expr(0)))
								.appendElement(new method(0,
										TreeConstants.length,
										new Formals(0),
										TreeConstants.Int,
										new no_expr(0)))
										.appendElement(new method(0,
												TreeConstants.concat,
												new Formals(0)
										.appendElement(new formal(0,
												TreeConstants.arg, 
												TreeConstants.Str)),
												TreeConstants.Str,
												new no_expr(0)))
												.appendElement(new method(0,
														TreeConstants.substr,
														new Formals(0)
												.appendElement(new formal(0,
														TreeConstants.arg,
														TreeConstants.Int))
														.appendElement(new formal(0,
																TreeConstants.arg2,
																TreeConstants.Int)),
																TreeConstants.Str,
																new no_expr(0))),
																filename);

		installClass(new CgenNode(Str_class, CgenNode.Basic, this, stringclasstag));
		classTag = stringclasstag;
	}

	// The following creates an inheritance graph from
	// a list of classes.  The graph is implemented as
	// a tree of `CgenNode', and class names are placed
	// in the base class symbol table.

	private void installClass(CgenNode nd) {
		AbstractSymbol name = nd.getName();
		if (probe(name) != null) return;
		nds.addElement(nd);
		addId(name, nd);
	}

	private void installClasses(Classes cs) {
		for (Enumeration e = cs.getElements(); e.hasMoreElements(); ) {
			installClass(new CgenNode((ClassAbstract)e.nextElement(), 
					CgenNode.NotBasic, this, ++classTag));
		}
	}

	private void buildInheritanceTree() {
		for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
			setRelations((CgenNode)e.nextElement());
		}
	}

	private void setRelations(CgenNode nd) {
		CgenNode parent = (CgenNode)probe(nd.getParent());
		nd.setParentNd(parent);
		parent.addChild(nd);
	}

	/** Constructs a new class table and invokes the code generator */
	public CgenClassTable(Classes cls, PrintStream str) {
		AbstractTable.classTable = this;
		nds = new Vector();

		this.str = str;

		stringclasstag = 2 /* Change to your String class tag here */;
		intclasstag =    3 /* Change to your Int class tag here */;
		boolclasstag =   4 /* Change to your Bool class tag here */;

		enterScope();
		if (Flags.cgen_debug) System.out.println("Building CgenClassTable");

		installBasicClasses();
		installClasses(cls);
		buildInheritanceTree();

		code();

		exitScope();
	}

	/** This method is the meat of the code generator.  It is to be
        filled in programming assignment 5 */
	public void code() {
		if (Flags.cgen_debug) System.out.println("coding global data");
		codeGlobalData();

		if (Flags.cgen_debug) System.out.println("choosing gc");
		codeSelectGc();

		if (Flags.cgen_debug) System.out.println("coding constants");
		codeConstants();

		//                 Add your code to emit
		//                   - prototype objects
		//                   - class_nameTab
		//                   - dispatch tables

		codeNameTable();
		codeObjTable();
		codeDispatchTables();
		codeProtoObjects();

		if (Flags.cgen_debug) System.out.println("coding global text");
		codeGlobalText();

		//                 Add your code to emit
		//                   - object initializer
		//                   - the class methods
		//                   - etc...

		codeClasses();
	}

	private void codeNameTable() {
		str.print(CgenSupport.CLASSNAMETAB + CgenSupport.LABEL);
		for (Enumeration e = nds.elements(); e.hasMoreElements();) {
			CgenNode node = (CgenNode) e.nextElement();
			str.println(CgenSupport.WORD + CgenSupport.getStringRef(node.getName().getString()));
		}
	}

	private void codeObjTable() {
		str.print(CgenSupport.CLASSNAMETAB + CgenSupport.LABEL);
		for (Enumeration e = nds.elements(); e.hasMoreElements();) {
			CgenNode node = (CgenNode) e.nextElement();
			str.println(CgenSupport.WORD + node.getName().getString() + CgenSupport.PROTOBJ_SUFFIX);
			str.println(CgenSupport.WORD + node.getName().getString() + CgenSupport.CLASSINIT_SUFFIX);
		}
	}

	private void codeDispatchTables() {
		for (Enumeration e = nds.elements(); e.hasMoreElements();) {
			CgenNode node = (CgenNode) e.nextElement();
			node.codeDispatchTable(str);
		}
	}

	private void codeProtoObjects() {
		for (Enumeration e = nds.elements(); e.hasMoreElements();) {
			CgenNode node = (CgenNode) e.nextElement();
			node.codeProtoObject(str);
		}
	}

	private void codeClasses() {
		for (Enumeration e = nds.elements(); e.hasMoreElements();) {
			CgenNode node = (CgenNode) e.nextElement();
			node.code(str);
		}
	}

	/** Gets the root of the inheritance tree */
	public CgenNode root() {
		return (CgenNode) probe(TreeConstants.Object_);
	}

	public int getMethodOffset(AbstractSymbol type, AbstractSymbol name, Expressions actuals) {
		if (type == TreeConstants.SELF_TYPE) {
			type = CgenSupport.currentClass.getName();
		}
		CgenNode node = (CgenNode)AbstractTable.classTable.lookup(type);
		return node.getMethodOffset(name, actuals);
	}

	public boolean isParent(AbstractSymbol parent, AbstractSymbol child) {
		return leastCommonAncestor(parent, child) == parent;
	}

	public int depth(class_ cl) {
		if (cl.getName() == TreeConstants.SELF_TYPE) {
			return 0;
		}
		return cl.getName() == TreeConstants.Object_ ? 0 : depth(getClass(cl.getParent())) + 1;
	}

	public int depth(AbstractSymbol name) {
		return depth(getClass(name));
	}

	private AbstractSymbol leastCommonAncestor(AbstractSymbol first, AbstractSymbol second) {
		class_ u = (class_) getClass(first).copy();
		class_ v = (class_) getClass(second).copy();
		int h1 = depth(u);
		int h2 = depth(v);

		while (h1 != h2) {
			if (h1 > h2) {
				u = (class_) getClass(u.getParent()).copy();
				--h1;
			} else {
				v = (class_) getClass(v.getParent()).copy();
				--h2;
			}
		}

		while (u.getName() != v.getName()) {
			u = (class_) getClass(u.getParent()).copy();
			v = (class_) getClass(v.getParent()).copy();
		}

		return u.getName();
	}

	private class_ getClass(AbstractSymbol name) {
		return (class_) lookup(name);
	}

	public List<Integer> getChildrenTags(AbstractSymbol className) {
		CgenNode node = (CgenNode) lookup(className);
		return node.getChildrenTags();
	}
}



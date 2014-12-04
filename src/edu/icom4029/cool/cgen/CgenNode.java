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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import edu.icom4029.cool.ast.ClassAbstract;
import edu.icom4029.cool.ast.Expressions;
import edu.icom4029.cool.ast.Feature;
import edu.icom4029.cool.ast.attr;
import edu.icom4029.cool.ast.class_;
import edu.icom4029.cool.ast.method;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.lexer.BoolConst;
import edu.icom4029.cool.lexer.IntSymbol;
import edu.icom4029.cool.lexer.StringSymbol;
import edu.icom4029.cool.semant.SymbolTable;

public class CgenNode extends class_ {
	/** The parent of this node in the inheritance tree */
	private CgenNode parent;

	/** The children of this node in the inheritance tree */
	private Vector children;

	/** Indicates a basic class */
	final static int Basic = 0;

	/** Indicates a class that came from a Cool program */
	final static int NotBasic = 1;

	/** Does this node correspond to a basic class? */
	private int basic_status;

	private int tag;

	private List<MethodInfo> methodInfos;

	/** Constructs a new CgenNode to represent class "c".
	 * @param c the class
	 * @param basic_status is this class basic or not
	 * @param table the class table
	 * */
	CgenNode(ClassAbstract c, int basic_status, CgenClassTable table, int tag) {
		super(0, c.getName(), c.getParent(), c.getFeatures(), c.getFilename());
		this.parent = null;
		this.children = new Vector();
		this.basic_status = basic_status;
		this.tag = tag;
		this.methodInfos = new ArrayList<MethodInfo>();
		AbstractTable.stringtable.addString(name.getString());
	}

	void addChild(CgenNode child) {
		children.addElement(child);
	}

	/** Gets the children of this class
	 * @return the children
	 * */
	Enumeration getChildren() {
		return children.elements(); 
	}

	/** Sets the parent of this class.
	 * @param parent the parent
	 * */
	void setParentNd(CgenNode parent) {
		if (this.parent != null) {
			Utilities.fatalError("parent already set in CgenNode.setParent()");
		}
		if (parent == null) {
			Utilities.fatalError("null parent in CgenNode.setParent()");
		}
		this.parent = parent;
	}    


	/** Gets the parent of this class
	 * @return the parent
	 * */
	CgenNode getParentNd() {
		return parent; 
	}

	/** Returns true is this is a basic class.
	 * @return true or false
	 * */
	boolean basic() { 
		return basic_status == Basic; 
	}

	public int getTag() {
		return tag;
	}

	public List<attr> getAttrs() {
		List<attr> attrs = (parent == null ? new ArrayList<attr>() : parent.getAttrs());
		attrs.addAll(getOwnAttrs());
		return attrs;
	}

	private List<attr> getOwnAttrs() {
		List<attr> attrs = new ArrayList<attr>();
		for (Enumeration e = features.getElements(); e.hasMoreElements();) {
			Feature feature = (Feature) e.nextElement();
			if (feature instanceof attr) {
				attrs.add((attr) feature);
			}
		}
		return attrs;
	}

	private int getSize() {
		return CgenSupport.DEFAULT_OBJFIELDS + getAttrs().size();
	}

	private List<MethodInfo> getMethodInfos() {
		List<MethodInfo> methodInfos = (parent == null ? new ArrayList<MethodInfo>() : parent.getMethodInfos());

		List<method> methods = getMethods();
		for (int i = 0; i < methods.size(); i++) {
			method m = methods.get(i);
			MethodInfo methodInfo = new MethodInfo(this, m);

			boolean isOverriden = false;
			for (int j = 0; j < methodInfos.size(); j++) {
				MethodInfo previousMethodInfo = methodInfos.get(j);
				if (previousMethodInfo.isOverriden(methodInfo)) {
					methodInfos.set(j, methodInfo);
					isOverriden = true;
					break;
				}
			}
			if (!isOverriden) {
				methodInfos.add(methodInfo);
			}
		}
		return methodInfos;
	}

	public void codeProtoObject(PrintStream str) {
		str.println(CgenSupport.WORD + "-1");
		str.print(name.getString() + CgenSupport.PROTOBJ_SUFFIX + CgenSupport.LABEL);
		str.println(CgenSupport.WORD + tag);
		str.println(CgenSupport.WORD + getSize());
		str.println(CgenSupport.WORD + name.getString() + CgenSupport.DISPTAB_SUFFIX);

		for (attr a : getAttrs()) {
			a.codeProtoObject(str);
		}
	}

	public void emitProtoObjRef(PrintStream str) {
		if (name == TreeConstants.Int) {
			IntSymbol sym = (IntSymbol) AbstractTable.inttable.lookup("0");
			sym.codeRef(str);
		}
		else if (name == TreeConstants.Bool) {
			BoolConst.falsebool.codeRef(str);
		}
		else if (name == TreeConstants.Str) {
			StringSymbol sym = (StringSymbol) AbstractTable.stringtable.lookup("");
			sym.codeRef(str);
		}
		else {
			str.print(CgenSupport.EMPTYSLOT);
		}
		str.println("");
	}

	public void codeDispatchTable(PrintStream str) {
		str.print(name.getString() + CgenSupport.DISPTAB_SUFFIX + CgenSupport.LABEL);
		methodInfos = getMethodInfos();
		for (MethodInfo mi : methodInfos) {
			str.println(CgenSupport.WORD + mi);
		}
	}

	public void codeInit(PrintStream str) {
		str.print(name + CgenSupport.CLASSINIT_SUFFIX + CgenSupport.LABEL);
		CgenSupport.emitStartMethod(str);
		CgenSupport.emitMove(CgenSupport.SELF, CgenSupport.ACC, str);

		if (parent != null && parent.name != TreeConstants.No_class) {
			CgenSupport.emitJal(parent.name + CgenSupport.CLASSINIT_SUFFIX, str);
		}

		List<attr> attrs = getOwnAttrs();
		for (int i = 0; i < attrs.size(); ++i) {
			attrs.get(i).codeInit(str);
		}

		CgenSupport.emitMove(CgenSupport.ACC, CgenSupport.SELF, str);
		CgenSupport.emitEndMethod(0, str);
	}

	public void code(PrintStream str) {
		CgenSupport.currentFilename = filename;
		CgenSupport.currentClass = this;
		AbstractTable.varTable = new SymbolTable();
		AbstractTable.varTable.enterScope();

		fillAttrTable();		
		codeInit(str);
		if (!basic()) {
			codeMethods(str);
		}
	}

	private void fillAttrTable() {
		List<attr> attrs = getAttrs();
		for (int i = 0; i < attrs.size(); i++) {
			AbstractTable.varTable.addId(attrs.get(i).getName(), new AttrVariable(i));
		}
	}

	private void codeMethods(PrintStream str) {
		for (method m : getMethods()) {
			str.print(name.getString() + CgenSupport.METHOD_SEP + m.getName().getString());
			if (!basic()) {
				for (AbstractSymbol paramType : m.getParamTypes()) {
					str.print(CgenSupport.PARAM_SEP + paramType.getString());
				}
			}
			str.print(CgenSupport.LABEL);
			m.code(str);
		}
	}

	public int getMethodOffset(AbstractSymbol method, Expressions actuals) {
		List<AbstractSymbol> paramTypes = actuals.getTypes();
		for (int offset = 0; offset < methodInfos.size(); ++offset) {
			MethodInfo methodInfo = methodInfos.get(offset);
			if (methodInfo.getName() != method) {
				continue;
			}
			List<AbstractSymbol> methodParamTypes = methodInfo.getParamTypes();
			if (methodParamTypes.size() != paramTypes.size()) {
				continue;
			}
			boolean doTypesMatch = true;
			for (int i = 0; i < paramTypes.size(); ++i) {
				AbstractSymbol paramType = paramTypes.get(i);
				AbstractSymbol methodParamType = methodParamTypes.get(i);
				if (paramType == TreeConstants.SELF_TYPE || methodParamType == TreeConstants.SELF_TYPE) {
					continue;
				}
				if (!AbstractTable.classTable.isParent(methodParamType, paramType)) {
					doTypesMatch = false;
					break;
				}
			}
			if (doTypesMatch) {
				return offset;
			}
		}
		return -1;
	}

	public List<Integer> getChildrenTags() {
		//System.err.println("get tags for " + name);
		List<Integer> tags = new ArrayList<Integer>();
		tags.add(tag);
		for (Enumeration e = children.elements(); e.hasMoreElements();) {
			CgenNode child = (CgenNode)e.nextElement();
			tags.addAll(child.getChildrenTags());
		}
		return tags;
	}

	private class MethodInfo {
		private AbstractSymbol _class;
		private AbstractSymbol name;
		private List<AbstractSymbol> paramTypes;
		private boolean isBasicClass;

		public MethodInfo(CgenNode c, method m) {
			_class = c.getName();
			name = m.getName();
			paramTypes = m.getParamTypes();
			isBasicClass = c.basic();
		}

		public AbstractSymbol getName() {
			return name;
		}

		public List<AbstractSymbol> getParamTypes() {
			return paramTypes;
		}

		public String toString() {
			String string = _class.getString() + CgenSupport.METHOD_SEP + name.getString();
			if (!isBasicClass) {
				for (AbstractSymbol p : paramTypes) {
					string += CgenSupport.PARAM_SEP + p.getString();
				}
			}
			return string;
		}

		public boolean isOverriden(MethodInfo other) {
			if (!name.equals(other.name)) {
				return false;
			}
			if (paramTypes.size() != other.paramTypes.size()) {
				return false;
			}			
			for (int i = 0; i < paramTypes.size(); i++) {
				if (!paramTypes.get(i).equals(other.paramTypes.get(i))) {
					return false;
				}
			}
			return true;
		}
	}
}




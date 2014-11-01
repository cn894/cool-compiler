package edu.icom4029.cool.ast;

import java.util.Vector;

import edu.icom4029.cool.ast.base.ListNode;
import edu.icom4029.cool.ast.base.TreeNode;

/** Defines list phylum Formals
<p>
See <a href="ListNode.html">ListNode</a> for full documentation. */
public class Formals extends ListNode {
	public final static Class elementClass = FormalAbstract.class;
	/** Returns class of this lists's elements */
	public Class getElementClass() {
		return elementClass;
	}
	
	public Formals(int lineNumber, Vector elements) {
		super(lineNumber, elements);
	}
	
	/** Creates an empty "Formals" list */
	public Formals(int lineNumber) {
		super(lineNumber);
	}
	
	/** Appends "Formal" element to this list */
	public Formals appendElement(TreeNode elem) {
		addElement(elem);
		return this;
	}
	
	public TreeNode copy() {
		return new Formals(lineNumber, copyElements());
	}
}

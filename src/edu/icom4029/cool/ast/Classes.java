package edu.icom4029.cool.ast;

import java.util.Vector;

import edu.icom4029.cool.ast.base.ListNode;
import edu.icom4029.cool.ast.base.TreeNode;

/** Defines list phylum Classes
<p>
See <a href="ListNode.html">ListNode</a> for full documentation. */
public class Classes extends ListNode {
	public final static Class elementClass = ClassAbstract.class;
	/** Returns class of this lists's elements */
	public Class getElementClass() {
		return elementClass;
	}
	public Classes(int lineNumber, Vector elements) {
		super(lineNumber, elements);
	}
	/** Creates an empty "Classes" list */
	public Classes(int lineNumber) {
		super(lineNumber);
	}
	/** Appends "Class_" element to this list */
	public Classes appendElement(TreeNode elem) {
		addElement(elem);
		return this;
	}
	public TreeNode copy() {
		return new Classes(lineNumber, copyElements());
	}
}

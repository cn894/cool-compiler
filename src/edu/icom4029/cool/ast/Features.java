package edu.icom4029.cool.ast;

import java.util.Vector;

import edu.icom4029.cool.ast.base.ListNode;
import edu.icom4029.cool.ast.base.TreeNode;

/** Defines list phylum Features
<p>
See <a href="ListNode.html">ListNode</a> for full documentation. */
public class Features extends ListNode {
	public final static Class elementClass = Feature.class;
	/** Returns class of this lists's elements */
	public Class getElementClass() {
		return elementClass;
	}
	public Features(int lineNumber, Vector elements) {
		super(lineNumber, elements);
	}
	/** Creates an empty "Features" list */
	public Features(int lineNumber) {
		super(lineNumber);
	}
	/** Appends "Feature" element to this list */
	public Features appendElement(TreeNode elem) {
		addElement(elem);
		return this;
	}
	public TreeNode copy() {
		return new Features(lineNumber, copyElements());
	}
}

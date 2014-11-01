package edu.icom4029.cool.ast;

import java.util.Vector;

import edu.icom4029.cool.ast.base.ListNode;
import edu.icom4029.cool.ast.base.TreeNode;


/** Defines list phylum Cases
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
public class Cases extends ListNode {
	public final static Class elementClass = Case.class;
	
	/** Returns class of this lists's elements */
	public Class getElementClass() {
		return elementClass;
	}
	
	public Cases(int lineNumber, Vector elements) {
		super(lineNumber, elements);
	}
	
	/** Creates an empty "Cases" list */
	public Cases(int lineNumber) {
		super(lineNumber);
	}
	
	/** Appends "Case" element to this list */
	public Cases appendElement(TreeNode elem) {
		addElement(elem);
		return this;
	}
	
	public TreeNode copy() {
		return new Cases(lineNumber, copyElements());
	}
}

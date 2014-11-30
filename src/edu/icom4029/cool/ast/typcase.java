package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'typcase'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class typcase extends Expression {
	protected Expression expr;
	protected Cases cases;
	
	/** Creates "typcase" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for expr
	 * @param a1 initial value for cases
	 */
	public typcase(int lineNumber, Expression a1, Cases a2) {
		super(lineNumber);
		expr = a1;
		cases = a2;
	}

	public TreeNode copy() {
		return new typcase(lineNumber, (Expression)expr.copy(), (Cases)cases.copy());
	}
	
	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "typcase\n");
		expr.dump(out, n+2);
		cases.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_typcase");
		expr.dump_with_types(out, n + 2);
		for (Enumeration e = cases.getElements(); e.hasMoreElements();) {
			((Case)e.nextElement()).dump_with_types(out, n + 2);
		}
		dump_type(out, n);
	}
	
	/** Generates code for this expression.  This method is to be completed 
	 * in programming assignment 5.  (You may or add remove parameters as
	 * you wish.)
	 * @param s the output stream 
	 * */
	public void code(PrintStream s) {
		expr.code(s);
        CgenSupport.emitCheckVoidCallCaseAbort2(lineNumber, s);
        int labelEnd = CgenSupport.genLabelNum();
        List<branch> branches = getBranches();
        
        Collections.sort(branches, new Comparator<branch>() {
            public int compare(branch first, branch second) {
                return AbstractTable.classTable.depth(second.getTypeDecl()) - AbstractTable.classTable.depth(first.getTypeDecl());
            }
        });
        
        for (branch br: branches) {
            br.code(labelEnd, s);
        }
        
        CgenSupport.emitCaseAbort(s);
        CgenSupport.emitLabelDef(labelEnd, s);
	}

	@Override
	public void semant(ClassTable classTable, class_ cl, SymbolTable symbolTable) {
		expr.semant(classTable, cl, symbolTable);
		List<AbstractSymbol> types = new ArrayList<AbstractSymbol>();
		Set<AbstractSymbol> declTypes = new HashSet<AbstractSymbol>();
		for (Enumeration e = cases.getElements(); e.hasMoreElements();) {
			branch br = (branch) e.nextElement();
			AbstractSymbol typeDecl = br.getTypeDecl();
			if (declTypes.contains(typeDecl)) {
				classTable.semantError(cl).println("Duplicate branch " + typeDecl.getString() + " in case statement.");
			}
			declTypes.add(typeDecl);
			br.semant(classTable, cl, symbolTable);
			types.add(br.get_type());
		}
		set_type(classTable.leastCommonAncestor(types));
	}
	
	private List<branch> getBranches() {
		List<branch> branches = new ArrayList<branch>();
        for (Enumeration e = cases.getElements(); e.hasMoreElements();) {
            branch br = (branch) e.nextElement();
            branches.add(br);
        }
        return branches;
	}
}

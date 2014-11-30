package edu.icom4029.cool.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.icom4029.cool.ast.base.TreeNode;
import edu.icom4029.cool.cgen.CgenSupport;
import edu.icom4029.cool.cgen.FormalVariable;
import edu.icom4029.cool.core.TreeConstants;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.AbstractSymbol;
import edu.icom4029.cool.lexer.AbstractTable;
import edu.icom4029.cool.semant.ClassTable;
import edu.icom4029.cool.semant.SymbolTable;

/** Defines AST constructor 'method'.
<p>
See <a href="TreeNode.html">TreeNode</a> for full documentation. */
public class method extends Feature {
	protected AbstractSymbol name;
	protected Formals        formals;
	protected AbstractSymbol return_type;
	protected Expression     expr;

	/** Creates "method" AST node. 
	 *
	 * @param lineNumber the line in the source file from which this node came.
	 * @param a0 initial value for name
	 * @param a1 initial value for formals
	 * @param a2 initial value for return_type
	 * @param a3 initial value for expr
	 */
	public method(int lineNumber, AbstractSymbol a1, Formals a2, AbstractSymbol a3, Expression a4) {
		super(lineNumber);
		name        = a1;
		formals     = a2;
		return_type = a3;
		expr        = a4;
	}

	public TreeNode copy() {
		return new method(lineNumber, copy_AbstractSymbol(name), (Formals)formals.copy(), copy_AbstractSymbol(return_type), (Expression)expr.copy());
	}

	public AbstractSymbol getName()       { return name; }
	public Formals        getFormals()    { return formals; }
	public AbstractSymbol getReturnType() { return return_type; }
	public Expression     getExpr()       { return expr; }

	public List<AbstractSymbol> getParamTypes() {
		List<AbstractSymbol> paramTypes = new ArrayList<AbstractSymbol>();
		for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
			formal f = (formal) e.nextElement();
			paramTypes.add(f.getType());
		}
		return paramTypes;
	}

	public void dump(PrintStream out, int n) {
		out.print(Utilities.pad(n) + "method\n");
		dump_AbstractSymbol(out, n+2, name);
		formals.dump(out, n+2);
		dump_AbstractSymbol(out, n+2, return_type);
		expr.dump(out, n+2);
	}

	public void dump_with_types(PrintStream out, int n) {
		dump_line(out, n);
		out.println(Utilities.pad(n) + "_method");
		dump_AbstractSymbol(out, n + 2, name);
		for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
			((FormalAbstract)e.nextElement()).dump_with_types(out, n + 2);
		}
		dump_AbstractSymbol(out, n + 2, return_type);
		expr.dump_with_types(out, n + 2);
	}

	@Override
	public void semant(ClassTable classTable, class_ c, SymbolTable symbolTable) {
		if (return_type != TreeConstants.SELF_TYPE && !classTable.hasClass(return_type.getString())) {
			classTable.semantError(c).println("Undefined return type " + return_type.getString() + " in " + name.getString());
		}
		symbolTable.enterScope();
		Set<AbstractSymbol> seenFormals = new HashSet<AbstractSymbol>();
		for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
			formal f = (formal) e.nextElement();
			f.semant(classTable, c);
			if (seenFormals.contains(f.getName())) {
				classTable.semantError(c).println("Formal parameter " + f.getName().getString() + " is multiply defined");
			}
			else {
				seenFormals.add(f.getName());
				f.fillSymbolTable(symbolTable);
			}
		}

		expr.semant(classTable, c, symbolTable);
		symbolTable.exitScope();

		if (expr.get_type() == TreeConstants.No_type) {
			return;
		}

		AbstractSymbol exprType = expr.get_type();
		if (exprType == TreeConstants.SELF_TYPE) {
			exprType = c.getName();
		}

		//		if (return_type == TreeConstants.SELF_TYPE && expr.get_type() != TreeConstants.SELF_TYPE) {
		//			classTable.semantError(c).println("Inferred return type " + expr.get_type().getString() + " of method " + name.getString() +
		//					" does not conform to declared return type " + return_type.getString());
		//		}
		if (return_type != TreeConstants.SELF_TYPE && !classTable.isBase(return_type, exprType)) {
			classTable.semantError(c).println("Inferred return type " + expr.get_type().getString() + " of method " + name.getString() +
					" does not conform to declared return type " + return_type.getString());
		}
	}

	@Override
	public String fillMethodTable(SymbolTable methodTable) {
		method parentMethod = (method) methodTable.lookup(name);
		if (parentMethod != null) {
			if (parentMethod.getFormals().getLength() != formals.getLength()) {
				return "Incompatible number of formal parameters in redefined method " + name.getString();
			}
			else {
				for (int i = 0; i < formals.getLength(); i++) {
					formal f = (formal) formals.getNth(i);
					formal parentFormal = (formal) parentMethod.getFormals().getNth(i);
					if (f.getType() != parentFormal.getType()) {
						return "Paremeter type " + f.getType().getString() + " in redefined method " + name.getString() +
								" is different from original type " + parentFormal.getType().getString();
					}
				}
			}
		}
		methodTable.addId(name, this);
		return "";
	}

	@Override
	public String fillAttrTable(SymbolTable attrTable) {
		return "";
	}

	public void code(PrintStream str) {
		AbstractTable.offset = 1;
		AbstractTable.varTable.enterScope();
		for (int i = 0; i < formals.getLength(); ++i) {
			formal formal = (formal)formals.getNth(i);
			int offset = 2 + formals.getLength() - i;
			AbstractTable.varTable.addId(formal.getName(), new FormalVariable(-offset));
		}
		CgenSupport.emitStartMethod(str);
		CgenSupport.emitMove(CgenSupport.SELF, CgenSupport.ACC, str);
		expr.code(str);
		CgenSupport.emitEndMethod(formals.getLength(), str);
		AbstractTable.varTable.exitScope();
	}
}

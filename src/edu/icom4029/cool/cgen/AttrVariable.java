package edu.icom4029.cool.cgen;

import java.io.PrintStream;

public class AttrVariable extends Variable {

	private int offset;

	public AttrVariable(int offset) {
		this.offset = offset + CgenSupport.DEFAULT_OBJFIELDS;
	}

	@Override
	public void emitRef(PrintStream s) {
		CgenSupport.emitLoad(CgenSupport.ACC, offset, CgenSupport.SELF, s);
	}

	@Override
	public void emitAssign(PrintStream s) {
		CgenSupport.emitStore(CgenSupport.ACC, offset, CgenSupport.SELF, s);
	}
}

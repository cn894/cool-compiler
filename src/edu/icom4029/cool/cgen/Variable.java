package edu.icom4029.cool.cgen;

import java.io.PrintStream;

public abstract class Variable {
	public abstract void emitRef(PrintStream s);
	public abstract void emitAssign(PrintStream s);
}

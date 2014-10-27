package edu.icom4029.cool.parser;
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.icom4029.cool.core.Flags;
import edu.icom4029.cool.lexer.Lexer;

/** Static semantics driver class */
class Semant {

	/** Reads AST from from parser output, and outputs the new AST */
	public static void main(String[] args) {
		args = Flags.handleFlags(args);
		
		for (int i = 0; i < args.length; i++) {
			FileReader file = null;

			try {
				file          = new FileReader(args[i]);
				Lexer  lexer  = new Lexer(file);
				Parser parser = new Parser(lexer);
				Object result = parser.parse().value;

				((ProgramAbstract) result).semant();
				((ProgramAbstract) result).dump_with_types(System.out, 0);

			} catch (FileNotFoundException ex) {
				System.out.println("Could not open input file " + args[i]);
	    	} catch (IOException ex) {
				System.out.println("Unexpected exception in parser");
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}
	}
}

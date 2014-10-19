package edu.icom4029.cool.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java_cup.runtime.Symbol;
import edu.icom4029.cool.core.Flags;
import edu.icom4029.cool.core.Utilities;
import edu.icom4029.cool.lexer.Lexer;
import edu.icom4029.cool.parser.TokenConstants;

/* The lexer driver class */
class LexerTest {

    /* Loops over lexed tokens, printing them out to the console */
    public static void main(String[] args) {
    	args = Flags.handleFlags(args);

		for (int i = 0; i < args.length; i++) {
		    FileReader file = null;
		    try {
				file = new FileReader(args[i]);
				
				System.out.println("#name \"" + args[i] + "\"");
				Lexer lexer = new Lexer(file);
				Symbol s;
				
				while ((s = lexer.next_token()).sym != TokenConstants.EOF) {
				    Utilities.dumpToken(System.out, lexer.getLineNumber(), s);
				}
				
		    } catch (FileNotFoundException ex) {
		    	Utilities.fatalError("Could not open input file " + args[i]);
		    } catch (IOException ex) {
		    	Utilities.fatalError("Unexpected exception in lexer");
		    }
		}
    }
}
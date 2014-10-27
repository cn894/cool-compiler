package edu.icom4029.cool.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java_cup.runtime.Symbol;
import edu.icom4029.cool.core.Flags;
import edu.icom4029.cool.lexer.Lexer;

/* The parser driver class */
public class ParserTest {

	public static void main(String[] args) {
		args = Flags.handleFlags(args);
		
		for (int i = 0; i < args.length; i++) {
			FileReader file = null;
			
			try {
				file          = new FileReader(args[i]);
				Parser parser = new Parser(new Lexer(file));
				Symbol result = parser.parse();
				
				((program) result.value).dump_with_types(System.out, 0);
				
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

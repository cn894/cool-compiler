package edu.icom4029.cool.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.icom4029.cool.core.Flags;
import edu.icom4029.cool.lexer.Lexer;
import edu.icom4029.cool.parser.Parser;

public class ParserTest {

	public static void main(String[] args) {
		args = Flags.handleFlags(args);
		
		for (int i = 0; i < args.length; i++) {
			FileReader file = null;
			
			try {
				file = new FileReader(args[i]);
				Parser parser_obj = new Parser(new Lexer(file));
				parser_obj.parse();
				
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

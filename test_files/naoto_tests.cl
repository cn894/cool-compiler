
class Main inherits IO {

	var : Bool;
	
	main(): SELF_TYPE {{
		out_string("Hello, World.\n");
		out_int(10 + 5);
		out_string("\n");
		out_int(10 - 5);
		out_string("\n");
		out_int(10 * 5);
		out_string("\n");
		out_int(10 / 5);
		out_string("\n\n");


		out_string("Testing And operator:\n\n");
		
		out_string("True and True: ");
		out_bool(true and true);
		out_string("\n");
		
		out_string("True and False: ");
		out_bool(true and false);
		out_string("\n");
		
		out_string("False and True: ");
		out_bool(false and true);
		out_string("\n");
		
		out_string("False and False: ");
		out_bool(false and false);
		out_string("\n\n");
		
		
		
		out_string("Testing Or operator:\n\n");
		
		out_string("True or True: ");
		out_bool(true or true);
		out_string("\n");
		
		out_string("True or False: ");
		out_bool(true or false);
		out_string("\n");
		
		out_string("False or True: ");
		out_bool(false or true);
		out_string("\n");
		
		out_string("False or False: ");
		out_bool(false or false);
		out_string("\n\n");
		
		
		out_string("Testing Precedence of And, Or operators:\n\n");
		
		out_string("not False or not False and True: ");
		out_bool(not false or not true and true);
		out_string("\n\n");
		
		out_string("var <- not False or not False and True: \n");
		var <- not false or not true and true;
		out_string("var: ");
		out_bool(var);
		out_string("\n\n");
		
		
		out_string("False and (not False or False) and True: ");
		out_bool(false and (not false or false) and true);
		out_string("\n\n");
		
		out_string("True or True and False: ");
		out_bool(true or true and false);
		out_string("\n\n");
		
		out_string("(True or True) and False: ");
		out_bool((true or true) and false);
		out_string("\n\n");
		
		out_string("True or (True and False): ");
		out_bool(true or (true and false));
		out_string("\n\n");
		
   }};
   
	out_bool(b: Bool): Bool {{
		if b then out_string("True") else
	       out_string("False")
	    fi;
	    b;
	}};
};

class A {
	x   : Int <- 10;
	var : Int <- 200;
};

class B {
	y : Int <- 9000;
	a : Bool;
	lel : Int <- 90 + 30;
};
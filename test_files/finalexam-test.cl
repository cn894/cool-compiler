class Main inherits IO {
	a : Bool <- true;
	b : Bool <- false;

	main() : SELF_TYPE {{
		if a and a then out_string("Passed.\n") else out_string("Failed.\n") fi;
		if a and b then out_string("Failed.\n") else out_string("Passed.\n") fi;
		if b and a then out_string("Failed.\n") else out_string("Passed.\n") fi;
		if b and b then out_string("Failed.\n") else out_string("Passed.\n") fi;

		if a or a then out_string("Passed.\n") else out_string("Failed.\n") fi;
		if a or b then out_string("Passed.\n") else out_string("Failed.\n") fi;
		if b or a then out_string("Passed.\n") else out_string("Failed.\n") fi;
		if b or b then out_string("Failed.\n") else out_string("Passed.\n") fi;
		
		if not b and a then out_string("Passed.\n") else out_string("Failed.\n") fi;
		if not (b and a) then out_string("Passed.\n") else out_string("Failed.\n") fi;
		
		if not a and a then out_string("Failed.\n") else out_string("Passed.\n") fi;
		if not (a and b) then out_string("Passed.\n") else out_string("Failed.\n") fi;
	}};
};
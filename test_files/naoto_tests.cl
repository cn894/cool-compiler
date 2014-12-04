
class Main inherits IO {
   main(): SELF_TYPE {{
	out_string("Hello, World.\n");
	out_int(10 + 5);
	out_string("\n");
	out_int(10 - 5);
	out_string("\n");
	out_int(10 * 5);
	out_string("\n");
	out_int(10 / 5);
	out_string("\n");
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
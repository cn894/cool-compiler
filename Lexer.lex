
package edu.icom4029.cool;

import java_cup.runtime.*;
import java.io.IOException;

// import .LexerSym;
// import static .LexerSym.*;

%%

%class Lexer

%unicode
%line
%column

// %public
%final
// %abstract

// %cupsym LexerSym
// %cup
// %cupdebug

%integer

%init{
	// TODO: code that goes to constructor
%init}

%{
	private Symbol sym(int type)
	{
		return sym(type, yytext());
	}

	private Symbol sym(int type, Object value)
	{
		return new Symbol(type, yyline, yycolumn, value);
	}

	private void error()
	throws IOException
	{
		throw new IOException("illegal text at line = "+yyline+", column = "+yycolumn+", text = '"+yytext()+"'");
	}
%}

/* Macros */

LineTerminator = (\r|\n|\r\n)
InputCharacter = [^\r\n]
Whitespace = (" "|\t|\f|\v|{LineTerminator})+
LowercaseCharacter = [a-z]
UppercaseCharacter = [A-Z]
AlphabeticCharacter = ({LowercaseCharacter} | {UppercaseCharacter})
Digit = [0-9]
AlphanumericCharacter = ({AlphabeticCharacter} | {Digit})

/* Comments */
EndOfLineComment = "--" {InputCharacter}* {LineTerminator}?
MultilineComment = "(*" ~"*)"
Comment = ({EndOfLineComment} | {MultilineComment})

/* Identifiers */
IdentifierCharacter = ({AlphanumericCharacter} | "_")
ObjectIdentifier = {LowercaseCharacter}{IdentifierCharacter}*
TypeIdentifier = {UppercaseCharacter}{IdentifierCharacter}*

/* Numeric Literals */
Integer = {Digit}+

/* Strings */
String = "\"" ({InputCharacter} | "\\"{LineTerminator})* "\""

Any = .

%%
/* Lexical Rules */

/* Keywords */
"class"		{}
"else"		{}
"false"		{}
"fi"		{}
"if"		{}
"in"		{}
"inherits"	{}
"isvoid"	{}
"let"		{}
"loop"		{}
"pool"		{}
"then"		{}
"while"		{}
"case"		{}
"esac"		{}
"new"		{}
"of"		{}
"not"		{}
"true"		{}

/* Operators */
"<-"		{}
"("			{}
")"			{}
"{"			{}
"}"			{}
"["			{}
"]"			{}
":"			{}
";"			{}
"+"			{}
"-"			{}
"*"			{}
"/"			{}
"="			{}
"<"			{}
">"			{}
"<="		{}
">="		{}
"."			{}
"~"			{}

/* Identifiers */
{ObjectIdentifier}	{}
{TypeIdentifier}	{}

/* Integers */
{Integer}	{}

/* Strings */
{String}	{}

/* Comments */
{Comment}	{ /* Ignore. */ }

/* Whitespace */
{Whitespace}	{ /* Ignore. */ }

{Any}		{	/* return sym(ANY) */ }


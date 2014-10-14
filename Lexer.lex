
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

%public
%final
// %abstract

// %cupsym LexerSym
// %cup
// %cupdebug

%function nextToken
%type Symbol

%state STRING, MLCOMMENT

%init{
	// TODO: code that goes to constructor
%init}

%{
	StringBuffer string = new StringBuffer();
	
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
	
	public int getLineNumber() {
		return yyline + 1;
	}
%}

//===MACROS============================================================================================================

LineTerminator        = (\r|\n|\r\n)
Whitespace            = (" "|\t|\f|{LineTerminator})+
LowercaseCharacter    = [a-z]
UppercaseCharacter    = [A-Z]
AlphabeticCharacter   = ({LowercaseCharacter} | {UppercaseCharacter})
Digit                 = [0-9]
AlphanumericCharacter = ({AlphabeticCharacter} | {Digit})

//---IDENTIFIERS-------------------------------------------------------------------------------------------------------

IdentifierCharacter = ({AlphanumericCharacter} | "_")
ObjectIdentifier    = {LowercaseCharacter}{IdentifierCharacter}*
TypeIdentifier      = {UppercaseCharacter}{IdentifierCharacter}*

//---NUMERIC-LITERALS--------------------------------------------------------------------------------------------------

Integer = {Digit}+

//---ETC---------------------------------------------------------------------------------------------------------------

Any = .

%%
//===LEXICAL=RULES=====================================================================================================

/* Initial State */
<YYINITIAL> {

	/* Single-line comment */
	"--" {Any}* {LineTerminator}? { /* Ignore */ }
	
	"*)"		{ return sym(TokenConstants.ERROR, "Unmatched comment terminator"); }
	
	//---Keywords------------------------------------------------------------------------------------------------------
	
	// Currently case sensitive. In reference lexer, it isn't.
	
	"class"		{ return sym(TokenConstants.CLASS); }
	"else"		{ return sym(TokenConstants.ELSE); }
	"false"		{ return sym(TokenConstants.BOOL_CONST); }
	"fi"		{ return sym(TokenConstants.FI); }
	"if"		{ return sym(TokenConstants.IF); }
	"in"		{ return sym(TokenConstants.IN); }
	"inherits"	{ return sym(TokenConstants.INHERITS); }
	"isvoid"	{ return sym(TokenConstants.ISVOID); }
	"let"		{ return sym(TokenConstants.LET); }
	"loop"		{ return sym(TokenConstants.LOOP); }
	"pool"		{ return sym(TokenConstants.POOL); }
	"then"		{ return sym(TokenConstants.THEN); }
	"while"		{ return sym(TokenConstants.WHILE); }
	"case"		{ return sym(TokenConstants.CASE); }
	"esac"		{ return sym(TokenConstants.ESAC); }
	"new"		{ return sym(TokenConstants.NEW); }
	"of"		{ return sym(TokenConstants.OF); }
	"not"		{ return sym(TokenConstants.NOT); }
	"true"		{ return sym(TokenConstants.BOOL_CONST); }
	
	//---Operators-----------------------------------------------------------------------------------------------------
	
	"<-"		{ return sym(TokenConstants.ASSIGN); }
	"=>"		{ return sym(TokenConstants.DARROW); }
	"("			{ return sym(TokenConstants.LPAREN); }
	")"			{ return sym(TokenConstants.RPAREN); }
	"{"			{ return sym(TokenConstants.LBRACE); }
	"}"			{ return sym(TokenConstants.RBRACE); }
	":"			{ return sym(TokenConstants.COLON); }
	";"			{ return sym(TokenConstants.SEMI); }
	"+"			{ return sym(TokenConstants.PLUS); }
	"-"			{ return sym(TokenConstants.MINUS); }
	"*"			{ return sym(TokenConstants.MULT); }
	"/"			{ return sym(TokenConstants.DIV); }
	"="			{ return sym(TokenConstants.EQ); }
	"<"			{ return sym(TokenConstants.LT); }
	"<="		{ return sym(TokenConstants.LE); }
	","			{ return sym(TokenConstants.COMMA); }
	"."			{ return sym(TokenConstants.DOT); }
	
	/* Identifiers */
	{ObjectIdentifier}	{	AbstractSymbol lexValue = AbstractTable.idtable.addString(yytext());
							return sym(TokenConstants.OBJECTID, lexValue);	}
							
	{TypeIdentifier}	{	AbstractSymbol lexValue = AbstractTable.idtable.addString(yytext());
							return sym(TokenConstants.TYPEID, lexValue);	}
	
	/* Integers */
	{Integer}			{	AbstractSymbol lexValue = AbstractTable.inttable.addString(yytext());
							return sym(TokenConstants.INT_CONST, lexValue);	}
	
	/* Strings */
	\"					{	string.setLength(0); yybegin(STRING); }
	
	/* Comments */
	"(*"				{	yybegin(MLCOMMENT); }

}

/* Strings state */
<STRING> {

	\"					{	yybegin(YYINITIAL);
							AbstractSymbol lexValue = AbstractTable.stringtable.addString(string.toString());
							return sym(TokenConstants.STR_CONST, lexValue); }
	[^\n\r\"\\]+		{	string.append(yytext()); }
	\\t					{	string.append('\t'); }
	\\n					{	string.append('\n'); }
	\\r					{	string.append('\r'); }
	\\\"				{	string.append('\"'); }
	\\					{	string.append('\\'); }
	\\{LineTerminator}	{	/* Do nothing. Continue. */ }
	
	{LineTerminator}	{ yybegin(YYINITIAL); return sym(TokenConstants.ERROR, "Unescaped newline in string"); }
	
	<<EOF>>				{ yybegin(YYINITIAL); return sym(TokenConstants.ERROR, "EOF in string"); }
}

/* MultiLine Comments state */
<MLCOMMENT> {

	{LineTerminator}	{ /* Ignore. */ }
	{Any}				{ /* Ignore. */ }
	"*)"				{ yybegin(YYINITIAL); /* Ignore. */ }
	<<EOF>>				{ yybegin(YYINITIAL); return sym(TokenConstants.ERROR, "EOF in comment."); }
}


/* Whitespace */ 
{Whitespace}	{ /* Ignore. */ }

/* Error fallback. */
[^]			{ return sym(TokenConstants.ERROR); }

<<EOF>>		{ return sym(TokenConstants.EOF); }

{Any}		{	/* return sym(ANY) */ }


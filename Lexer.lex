
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
	int commentLevel = 0;
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
	
	/* Unmatched multiline comment terminator. */
	"*)"		{ return sym(ParserSym.ERROR, "Unmatched comment terminator"); }
	
	//---Keywords------------------------------------------------------------------------------------------------------
	
	[Cc][Ll][Aa][Ss][Ss]				{ return sym(ParserSym.CLASS); }
	[Ee][Ll][Ss][Ee]					{ return sym(ParserSym.ELSE); }
	[Ff][Ii]							{ return sym(ParserSym.FI); }
	[Ii][Ff]							{ return sym(ParserSym.IF); }
	[Ii][Nn]							{ return sym(ParserSym.IN); }
	[Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss]	{ return sym(ParserSym.INHERITS); }
	[Ii][Ss][Vv][Oo][Ii][Dd]			{ return sym(ParserSym.ISVOID); }
	[Ll][Ee][Tt]						{ return sym(ParserSym.LET); }
	[Ll][Oo][Oo][Pp]					{ return sym(ParserSym.LOOP); }
	[Pp][Oo][Oo][Ll]					{ return sym(ParserSym.POOL); }
	[Tt][Hh][Ee][Nn]					{ return sym(ParserSym.THEN); }
	[Ww][Hh][Ii][Ll][Ee]				{ return sym(ParserSym.WHILE); }
	[Cc][Aa][Ss][Ee]					{ return sym(ParserSym.CASE); }
	[Ee][Ss][Aa][Cc]					{ return sym(ParserSym.ESAC); }
	[Nn][Ee][Ww]						{ return sym(ParserSym.NEW); }
	[Oo][Ff]							{ return sym(ParserSym.OF); }
	[Nn][Oo][Tt]						{ return sym(ParserSym.NOT); }
	"t"[Rr][Uu][Ee]						{ return sym(ParserSym.BOOL_CONST); }
	"f"[Aa][Ll][Ss][Ee]					{ return sym(ParserSym.BOOL_CONST); }
	
	//---Operators-----------------------------------------------------------------------------------------------------
	
	"<-"		{ return sym(ParserSym.ASSIGN); }
	"=>"		{ return sym(ParserSym.DARROW); }
	"("			{ return sym(ParserSym.LPAREN); }
	")"			{ return sym(ParserSym.RPAREN); }
	"{"			{ return sym(ParserSym.LBRACE); }
	"}"			{ return sym(ParserSym.RBRACE); }
	":"			{ return sym(ParserSym.COLON); }
	";"			{ return sym(ParserSym.SEMI); }
	"+"			{ return sym(ParserSym.PLUS); }
	"-"			{ return sym(ParserSym.MINUS); }
	"*"			{ return sym(ParserSym.MULT); }
	"/"			{ return sym(ParserSym.DIV); }
	"="			{ return sym(ParserSym.EQ); }
	"<"			{ return sym(ParserSym.LT); }
	"<="		{ return sym(ParserSym.LE); }
	","			{ return sym(ParserSym.COMMA); }
	"."			{ return sym(ParserSym.DOT); }
	"@"			{ return sym(ParserSym.AT); }
	"~"			{ return sym(ParserSym.NEG); }
	
	/* Identifiers */
	{ObjectIdentifier}	{	AbstractSymbol lexValue = AbstractTable.idtable.addString(yytext());
							return sym(ParserSym.OBJECTID, lexValue);	}
							
	{TypeIdentifier}	{	AbstractSymbol lexValue = AbstractTable.idtable.addString(yytext());
							return sym(ParserSym.TYPEID, lexValue);	}
	
	/* Integers */
	{Integer}			{	AbstractSymbol lexValue = AbstractTable.inttable.addString(yytext());
							return sym(ParserSym.INT_CONST, lexValue);	}
	
	/* Strings */
	\"					{	string.setLength(0); yybegin(STRING); }
	
	/* Comments */
	"(*"				{	commentLevel++;
							yybegin(MLCOMMENT); }

}

/* Strings state */
<STRING> {

	\"								{	yybegin(YYINITIAL);
										AbstractSymbol lexValue = AbstractTable.stringtable.addString(string.toString());
										return sym(ParserSym.STR_CONST, lexValue); }
	[^\n\r\"\\]+					{	string.append(yytext()); }
	\\t								{	string.append('\t'); }
	\\n								{	string.append('\n'); }
	\\r								{	string.append('\r'); }
	\\\"							{	string.append('\"'); }
	\\\\							{	string.append('\\'); }
	\\{LineTerminator}{Whitespace}*	{	string.append('\n'); }
	
	{LineTerminator}	{ yybegin(YYINITIAL); return sym(ParserSym.ERROR, "Unescaped newline in string"); }
	
	<<EOF>>				{ yybegin(YYINITIAL); return sym(ParserSym.ERROR, "EOF in string"); }
}

/* Multiline Comments state */
<MLCOMMENT> {

	"(*"				{	commentLevel++; }
	"*)"				{	commentLevel--;
							if (commentLevel <= 0) yybegin(YYINITIAL); /* Ignore */ }
	{LineTerminator}	{	/* Ignore. */ }
	<<EOF>>				{	yybegin(YYINITIAL); return sym(ParserSym.ERROR, "EOF in comment."); }
	{Any}				{	/* Ignore. */ }
}


/* Whitespace */ 
{Whitespace}	{ /* Ignore. */ }

/* Error fallback. */
[^]			{ return sym(ParserSym.ERROR); }

<<EOF>>		{ return sym(ParserSym.EOF); }

{Any}		{	/* return sym(ANY) */ }


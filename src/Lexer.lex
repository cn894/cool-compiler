
package edu.icom4029.cool.lexer;

import java_cup.runtime.*;
import java.io.IOException;

import edu.icom4029.cool.parser.TokenConstants;
import edu.icom4029.cool.core.*;

%%

%public
%final 
%class Lexer

%unicode
%line
%column

%cup

%state STRING, MLCOMMENT


%ctorarg String filename

%init{
	this.filename = AbstractTable.stringtable.addString(filename);
%init}

%{
	int commentLevel        = 0;
	AbstractSymbol filename;
	StringBuffer string     = new StringBuffer();
	
	private Symbol sym(int type) {
		return sym(type, yytext());
	}

	private Symbol sym(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}

	private void error() throws IOException {
		throw new IOException("illegal text at line = "+yyline+", column = "+yycolumn+", text = '"+yytext()+"'");
	}
	
	public int getLineNumber() {
		return yyline + 1;
	}
	
	public AbstractSymbol getFilename() {
		return filename;
	}
%}

//===MACROS============================================================================================================

Any                   = .
LineTerminator        = (\r|\n|\r\n)
Whitespace            = (" "|\t|\f|{LineTerminator})+
LowercaseCharacter    = [a-z]
UppercaseCharacter    = [A-Z]
AlphabeticCharacter   = ({LowercaseCharacter} | {UppercaseCharacter})
Digit                 = [0-9]
AlphanumericCharacter = ({AlphabeticCharacter} | {Digit})
Integer               = {Digit}+

//---IDENTIFIERS-------------------------------------------------------------------------------------------------------

IdentifierCharacter = ({AlphanumericCharacter} | "_")
ObjectIdentifier    = {LowercaseCharacter}{IdentifierCharacter}*
TypeIdentifier      = {UppercaseCharacter}{IdentifierCharacter}*

%%
//===LEXICAL=RULES=====================================================================================================

/* Initial State */
<YYINITIAL> {

	/* Single-line comment */
	"--" {Any}* {LineTerminator}? { /* Ignore */ }
	
	/* Unmatched multiline comment terminator. */
	"*)"		{ return sym(TokenConstants.ERROR, "Unmatched comment terminator"); }
	
	//---Keywords------------------------------------------------------------------------------------------------------
	
	[Cc][Ll][Aa][Ss][Ss]				{ return sym(TokenConstants.CLASS); }
	[Ee][Ll][Ss][Ee]					{ return sym(TokenConstants.ELSE); }
	[Ff][Ii]							{ return sym(TokenConstants.FI); }
	[Ii][Ff]							{ return sym(TokenConstants.IF); }
	[Ii][Nn]							{ return sym(TokenConstants.IN); }
	[Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss]	{ return sym(TokenConstants.INHERITS); }
	[Ii][Ss][Vv][Oo][Ii][Dd]			{ return sym(TokenConstants.ISVOID); }
	[Ll][Ee][Tt]						{ return sym(TokenConstants.LET); }
	[Ll][Oo][Oo][Pp]					{ return sym(TokenConstants.LOOP); }
	[Pp][Oo][Oo][Ll]					{ return sym(TokenConstants.POOL); }
	[Tt][Hh][Ee][Nn]					{ return sym(TokenConstants.THEN); }
	[Ww][Hh][Ii][Ll][Ee]				{ return sym(TokenConstants.WHILE); }
	[Cc][Aa][Ss][Ee]					{ return sym(TokenConstants.CASE); }
	[Ee][Ss][Aa][Cc]					{ return sym(TokenConstants.ESAC); }
	[Nn][Ee][Ww]						{ return sym(TokenConstants.NEW); }
	[Oo][Ff]							{ return sym(TokenConstants.OF); }
	[Nn][Oo][Tt]						{ return sym(TokenConstants.NOT); }
	"t"[Rr][Uu][Ee]						{ return sym(TokenConstants.BOOL_CONST, true); }
	"f"[Aa][Ll][Ss][Ee]					{ return sym(TokenConstants.BOOL_CONST, false); }
	
	/*------- Added for final exam -------*/
	[Aa][Nn][Dd]                        { return sym(TokenConstants.AND); }
	[Oo][Rr]                            { return sym(TokenConstants.OR); }
	/*------------------------------------*/
	
	//---Operators-----------------------------------------------------------------------------------------------------
	
	"<-" 	{ return sym(TokenConstants.ASSIGN); }
	"=>" 	{ return sym(TokenConstants.DARROW); }
	"("  	{ return sym(TokenConstants.LPAREN); }
	")"	 	{ return sym(TokenConstants.RPAREN); }
	"{"	 	{ return sym(TokenConstants.LBRACE); }
	"}"	 	{ return sym(TokenConstants.RBRACE); }
	":"		{ return sym(TokenConstants.COLON); }
	";"		{ return sym(TokenConstants.SEMI); }
	"+"		{ return sym(TokenConstants.PLUS); }
	"-"		{ return sym(TokenConstants.MINUS); }
	"*"		{ return sym(TokenConstants.MULT); }
	"/"		{ return sym(TokenConstants.DIV); }
	"="		{ return sym(TokenConstants.EQ); }
	"<"		{ return sym(TokenConstants.LT); }
	"<="	{ return sym(TokenConstants.LE); }
	","		{ return sym(TokenConstants.COMMA); }
	"."		{ return sym(TokenConstants.DOT); }
	"@"		{ return sym(TokenConstants.AT); }
	"~"		{ return sym(TokenConstants.NEG); }
	
	/* Identifiers */
	{ObjectIdentifier}	{ AbstractSymbol lexValue = AbstractTable.idtable.addString(yytext());
						  return sym(TokenConstants.OBJECTID, lexValue); }
							
	{TypeIdentifier}	{ AbstractSymbol lexValue = AbstractTable.idtable.addString(yytext());
						  return sym(TokenConstants.TYPEID, lexValue); }
	
	/* Integers */
	{Integer}			{ AbstractSymbol lexValue = AbstractTable.inttable.addString(yytext());
						  return sym(TokenConstants.INT_CONST, lexValue); }
	
	/* Strings */
	\"					{ string.setLength(0); yybegin(STRING); }
	
	/* Comments */
	"(*"				{ commentLevel++; yybegin(MLCOMMENT); }
}

/* Strings state */
<STRING> {

	\"								{	yybegin(YYINITIAL);
										AbstractSymbol lexValue = AbstractTable.stringtable.addString(string.toString());
										return sym(TokenConstants.STR_CONST, lexValue); }
	[^\n\r\"\\]+					{	string.append(yytext()); }
	\\t								{	string.append('\t'); }
	\\n								{	string.append('\n'); }
	\\r								{	string.append('\r'); }
	\\\"							{	string.append('\"'); }
	\\\\							{	string.append('\\'); }
	\\{LineTerminator}{Whitespace}*	{	string.append('\n'); }
	
	{LineTerminator}				{ yybegin(YYINITIAL); return sym(TokenConstants.ERROR, "Unescaped newline in string"); }
	<<EOF>>							{ yybegin(YYINITIAL); return sym(TokenConstants.ERROR, "EOF in string"); }
}

/* Multiline Comments state */
<MLCOMMENT> {

	"(*"					{ commentLevel++; }
	"*)"					{ commentLevel--; if (commentLevel <= 0) yybegin(YYINITIAL); /* Ignore */ }
	<<EOF>>					{ yybegin(YYINITIAL); return sym(TokenConstants.ERROR, "EOF in comment."); }
	{LineTerminator}|{Any}	{ /* Ignore. */ }
}


/* Whitespace */ 
{Whitespace}	{ /* Ignore. */ }

/* Error fallback. */
[^]			{ return sym(TokenConstants.ERROR); }

<<EOF>>		{ return sym(TokenConstants.EOF); }

//{Any}		{	/* return sym(ANY) */ }

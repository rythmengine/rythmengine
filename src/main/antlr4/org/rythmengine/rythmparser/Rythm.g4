/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 * 
 * this is the Language definition for the Rythm template language
 *
 * it is specified using antlr syntax and uses the ANTLR V4 parser generator
 * see http://www.antlr.org
 * 
 * as of 2016-06-03 this is not in use yet - it's just a start for a new Parser engine that
 * is intended to be in use for the 2.0 release
 * 
 * for Eclipse you might want to install the IDE support:
 * https://github.com/jknack/antlr4ide 
 */
parser grammar Rythm;

options {
// use separate Lexer
	tokenVocab = RythmTokenLexer;
}

/* Grammar Start */
rythm
:
	args? statement* EOF
;

args
:
	ARGS arg?
	(
		COMMA arg
	)*
;

arg
:
	Type VarName
;

statement
:
	atexpression
	| ANY
;

atexpression
:
	AT expression
;

expression
:
	| fullyqualifiedname
	| expression signature
	| LPAREN expression RPAREN
;

signature
:
	LPAREN param?
	(
		COMMA param
	) RPAREN
;

param
:
	fullyqualifiedname
;

fullyqualifiedname
:
	VarName
	| VarName DOT fullyqualifiedname
;
 

   

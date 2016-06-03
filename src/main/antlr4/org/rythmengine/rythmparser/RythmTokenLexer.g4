/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
lexer grammar RythmTokenLexer;

/* Keywords */
ARGS: '@args';

/* SEPARATORS */
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
DOT: '.';

/* OPERATORS */
AT: '@';

fragment IDENTIFIER: [_a-zA-Z][a-zA-Z0-9_]*;

 // please note the needed change in naming conventions see
 // http://stackoverflow.com/questions/24299214/using-antlr-parser-and-lexer-separatly
 // lexer rule start with an upperCas
 /** {@link org.rythmengine.internal.parser.VarName} */
 VarName:
   IDENTIFIER;
 
 /** {@link org.rythmengine.internal.parser.Type} */
Type:
   IDENTIFIER;  

ANY: ~[@]+;

fragment WHITESPACE_CHAR:[ \r\t\n];

/* as of 2016-06-03 We're going to ignore all white space characters @FIXME - whitespace handling is going to
 * be very different in the future ...*/
WHITESPACE: WHITESPACE_CHAR+ -> skip;
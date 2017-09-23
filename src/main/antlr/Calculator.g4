grammar Calculator;

@header {
package antlr;
}

prog:	(expr NEWLINE)* ;
expr:	op1=expr op=('*'|'/') op2=expr #DotExpr
    |	op1=expr op=('+'|'-') op2=expr #LineExpr
    |	INT                 #IntLit
    |	'(' expr ')'        #ParExpr
    ;
NEWLINE : [\r\n]+ ;
INT     : [0-9]+ ;
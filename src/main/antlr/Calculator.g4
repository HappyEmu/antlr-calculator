grammar Calculator;

@header {
package antlr;
}

prog: stat+
    ;

stat:   expr NEWLINE                #PrintStatement
    |   ID '=' expr NEWLINE         #AssignStatement
    |   NEWLINE                     #EmptyStatement
    ;

expr:	<assoc=right> expr '^' expr #PowerExpr
    |   expr op=('*'|'/') expr      #MultiplicativeExpr
    |	expr op=('+'|'-') expr      #AdditiveExpr
    |	INT                         #IntLitExpr
    |   ID                          #IdExpr
    |	'(' expr ')'                #ParensExpr
    ;

ID  : [a-zA-Z]+ ;
INT : [0-9]+ ;
NEWLINE : '\r'? '\n' ;
WS  : [ \t]+ -> skip ;

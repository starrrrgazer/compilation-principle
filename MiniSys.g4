grammar MiniSys;

//parser


LINE_COMMENT : '//' .*? '\r'?'\n' -> skip;

COMMENT : '/*' .*? '*/' -> skip;

WS : [ \t\r\n]+ -> skip;

compUnit    :   compUnit (decl | funcDef)
                | (decl | funcDef);
decl        :   constDecl | varDecl;
constDecl   :   'const' bType constDef (',' constDef)* ';';
bType       :   'int';
constDef    :   ident ('[' constExp ']')* '=' constInitVal;
constInitVal:   constExp
                | '{' (constInitVal (','constInitVal)*)? '}';
varDecl     :   bType varDef (',' varDef )* ';';
varDef      :   ident ('[' constExp ']')*
                |ident ('[' constExp ']')* '=' initVal;
initVal     :   exp
                |'{'(initVal (',' initVal)*)? '}';
funcDef     :   funcType ident '(' (funcFParams)? ')' block;
funcType    :   'void' | 'int';
funcFParams :   funcFParam ( ',' funcFParam )*;
funcFParam  :   bType ident ('[' ']' ( '[' exp ']' )*)?;
block       :   '{' (blockItem)* '}';
blockItem   :   decl | stmt;
stmt        :   lVal '=' exp ';'
                | (exp)? ';'
                | block
                | 'if' '(' cond ')' stmt ( 'else' stmt )?
                | 'while' '(' cond ')' stmt
                | 'break' ';'
                | 'continue' ';'
                | 'return' (exp)? ';';
exp         :   addExp;
cond        :   lOrExp;
lVal        :   ident('[' exp ']')*;
primaryExp  :   '(' exp ')'
                | lVal
                | Number;
unaryExp    :   primaryExp
                | ident '(' (funcRParams)? ')'
                | unaryOp unaryExp;
unaryOp     :   '+'| '-' | '!';
funcRParams :   exp (',' exp)*;
mulExp      :   unaryExp
                | mulExp ('*'|'/'|'%') unaryExp;
addExp      :   mulExp
                | addExp ('+'|'-') mulExp;
relExp      :   addExp
                | relExp ('<' | '>' | '<=' | '>=') addExp;
eqExp       :   relExp
                | eqExp ('==' | '!=') relExp;
lAndExp     :   eqExp
                | lAndExp '&&' eqExp;
lOrExp      :   lAndExp
                | lOrExp '||' lAndExp;
constExp    :   addExp;
ident       :   nondigit
                | ident nondigit
                | ident digit;
nondigit     :   Nondigit;
digit       :   Number;
//number             : decimalconst | octalconst | hexadecimalconst;
//decimalconst      : nonzerodigit | decimalconst digit;
//octalconst        : '0' | octalconst octaldigit;
//hexadecimalconst  : hexadecimalprefix hexadecimaldigit
//                      | hexadecimalconst hexadecimaldigit;
//hexadecimalprefix : '0x' | '0X';
//nonzerodigit      : '1'..'9';
//octaldigit        : '0'..'7';
//digit              : '0' | nonzerodigit;
//hexadecimaldigit  : '0'-'9'
//                      | 'a' | 'b' | 'c' | 'd' | 'e' | 'f'
//                      | 'A' | 'B' | 'C' | 'D' | 'E' | 'F';
//lexer
Nondigit    :   [a-zA-Z_][_a-zA-Z0-9]*;
Number      :   [1-9][0-9]*           //decimal
                | '0'                 //0
                | '0'[0-7]*           //octal
                | '0x'[a-fA-F0-9]*    //hex
                | '0X'[a-fA-F0-9]*;
//"=", ";", "(", ")", "{", "}", "+", "*", "/", "<", ">", "==",
  //            "if", "else", "while", "break", "continue", "return"
//Assign      :   '=';
//Semicolon   :   ';';
//LPar        :   '(';
//RPar        :   ')';
//LBrace      :   '{';
//RBrace      :   '}';
//Plus        :   '+';
//Mult        :   '*';
//Div         :   '/';
//Lt          :   '<';
//Gt          :   '>';
//Eq          :   '==';
//If          :   'if';
//Else        :   'else';
//While       :   'while';
//Break       :   'break';
//Continue    :   'continue';
//Return      :   'return';
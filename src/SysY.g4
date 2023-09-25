grammar SysY;
/*
goal: 消除左递归文法
*/
CompUnit:
    (decl | funcDef)* mainFuncDef;

decl:
    constDecl
    | varDecl;

constDecl:
    CONSTTK bType constDef (COMMA constDef)* SEMICN;

bType:
    INTTK;

constDef:
    Ident (LBRACK constExp RBRACK)* ASSIGN constInitVal; // 仅包含普通变量、一维变量、二维变量

constInitVal:
    constExp
    | LBRACE (constInitVal (COMMA constInitVal)*)? RBRACE; // 仅包含一维数组、二维数组

varDecl:
    bType varDef (COMMA varDef)* SEMICN;

varDef:
    Ident (LBRACK constExp RBRACK)*
    | Ident (LBRACK constExp RBRACK)* ASSIGN initVal;

initVal:
    exp
    | LBRACE (initVal (COMMA initVal)*)? RBRACE;

funcDef:
    funcType Ident LPARENT (funcFParams)? RPARENT block;

mainFuncDef:
    INTTK MAINTK LPARENT RPARENT block;

funcType:
    VOIDTK
    | INTTK;

funcFParams:
    funcFParam (COMMA funcFParam)*;

funcFParam:
    bType Ident (LBRACK RBRACK (LBRACK constExp RBRACK)*)?;

block:
    LBRACE (blockItem)* RBRACE;

blockItem:
    decl
    | stmt;

stmt:
    lVal ASSIGN exp SEMICN
    | (exp)? SEMICN
    | block
    | IFTK LPARENT cond RPARENT stmt (ELSETK stmt)?
    | FORTK LPARENT (forStmt)? SEMICN (cond)? SEMICN (forStmt)? RPARENT stmt
    | BREAKTK SEMICN
    | CONTINUETK SEMICN
    | RETURNTK (exp)? SEMICN
    | lVal ASSIGN GETINTTK LPARENT RPARENT SEMICN
    | PRINTFTK LPARENT FormatString (COMMA exp)* RPARENT SEMICN;

forStmt:
    lVal ASSIGN exp;

exp:
    addExp;

cond:
    lOrExp;

lVal:
    Ident (LBRACK exp RBRACK)*;

primaryExp:
    LBRACE exp RBRACE
    | lVal
    | number;

number:
    IntConst;

unaryExp:
    primaryExp
    | Ident LPARENT (funcRParams)? RPARENT
    | unaryOp unaryExp;

funcRParams:
    exp (COMMA exp)*;

mulExp:
    unaryExp (MULT | DIV | MOD unaryExp)*;

addExp:
    mulExp (PLUS | MINUS mulExp)*;

relExp:
    addExp (LSS | LEQ | GRE | GEQ addExp)*;

eqExp:
    relExp (EQL | NEQ relExp)*;

lAndExp:
    eqExp (AND eqExp)*;

lOrExp:
    lAndExp (OR lAndExp)*;

constExp:
    addExp; // 使用的Ident必须是常量

unaryOp: PLUS | MINUS | NOT;

PLUS: '+';
MINUS: '-';
NOT: '!';
MULT: '*';
DIV: '/';
MOD: '%';
LSS: '<';
LEQ: '<=';
GRE: '>';
GEQ: '>=';
EQL: '==';
NEQ: '!=';
AND: '&&';
OR: '||';
ASSIGN: '=';

COMMA: ',';
SEMICN: ';';
LBRACK: '[';
RBRACK: ']';
LBRACE: '{';
RBRACE: '}';
LPARENT: '(';
RPARENT: ')';
CONSTTK: 'const';
INTTK: 'int';
MAINTK: 'main';
VOIDTK: 'void';
BREAKTK: 'break';
CONTINUETK: 'continue';
IFTK: 'if';
ELSETK: 'else';
FORTK: 'for';
RETURNTK: 'return';
GETINTTK: 'getInt';
PRINTFTK: 'printf';

Ident: [a-zA-Z_][a-zA-Z_0-9]*;
IntConst: [1-9][0-9]* | '0';
FormatString: '"' (Char)* '"';
Char: FormatChar | NormalChar;
FormatChar: '%d';
NormalChar
    : ' '   // 32 空格
    | NOT   // 33 感叹号
    | [(-~] // 40-126
    ;
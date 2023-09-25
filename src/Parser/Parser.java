package Parser;

import Lexer.WordType;
import Parser.GrammarElements.*;
import Parser.GrammarElements.Number;
import Settings.Configure;

public class Parser {
    private final TokenManager tokenManager;

    public Parser() {
        this.tokenManager = new TokenManager();
    }

    /*
    * CompUnit -> (Decl | FuncDef)* MainFuncDef
    */
    public gElement parseCompUnit() {
        /* Decl */
        CompUnit compUnit = new CompUnit();
        while(!tokenManager.checkTokenType(2,WordType.LPARENT)) {
            compUnit.addElement(parseDecl());
        }
        /* FuncDef */
        while(!tokenManager.checkTokenType(1, WordType.MAINTK)) {
            compUnit.addElement(parseFuncDef());
        }
        /* MainFuncDef
        * @Error: 缺少main函数 */
        if (tokenManager.checkTokenType(1, WordType.MAINTK)) {
            compUnit.addElement(parseMainFuncDef());
        } else {
            Error.handleError("缺少main函数");
        }
        Configure.parseDisplay(compUnit.toString());
        return compUnit;
    }

    /* Decl -> ConstDecl | VarDecl */
    public gElement parseDecl() {
        Decl decl = new Decl();
        if (tokenManager.checkTokenType(0, WordType.CONSTTK)) {
            decl.addElement(parseConstDecl());
        } else {
            decl.addElement(parseVarDecl());
        }
        return decl;
    }

    /* FuncDef -> FuncType Ident '(' FuncFParams* ')' Block */
    public gElement parseFuncDef() {
        FuncDef funcDef = new FuncDef();
        funcDef.addElement(parseFuncType()); // FuncType
        funcDef.addElement(tokenManager.getNextToken()); // Ident
        funcDef.addElement(tokenManager.getNextToken()); // '('
        while(!tokenManager.checkTokenType(0, WordType.RPARENT)) { // FuncFParams
            funcDef.addElement(parseFuncFParams());
        }
        funcDef.addElement(tokenManager.getNextToken()); // ')'
        funcDef.addElement(parseBlock()); // Block
        Configure.parseDisplay(funcDef.toString());
        return funcDef;
    }

    /* MainFuncDef -> 'int' 'main' '(' ')' Block */
    public gElement parseMainFuncDef() {
        MainFuncDef mainFuncDef = new MainFuncDef();
        mainFuncDef.addElement(tokenManager.getNextToken()); // 'int'
        mainFuncDef.addElement(tokenManager.getNextToken()); // 'main'
        mainFuncDef.addElement(tokenManager.getNextToken()); // '('
        mainFuncDef.addElement(tokenManager.getNextToken()); // ')'
        mainFuncDef.addElement(parseBlock()); // Block
        Configure.parseDisplay(mainFuncDef.toString());
        return mainFuncDef;
    }

    /* ConstDecl -> 'const' BType ConstDef (',' ConstDef )* ';' */
    public gElement parseConstDecl() {
        ConstDecl constDecl = new ConstDecl();
        constDecl.addElement(tokenManager.getNextToken()); // 'const'
        constDecl.addElement(parseBType()); // BType
        constDecl.addElement(parseConstDef()); // ConstDef
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' ConstDef )*
            constDecl.addElement(tokenManager.getNextToken());
            constDecl.addElement(parseConstDef());
        }
        constDecl.addElement(tokenManager.getNextToken()); // ';'
        Configure.parseDisplay(constDecl.toString());
        return constDecl;
    }

    /* ConstDef -> Ident ('[' ConstExp ']')* '=' ConstInitVal */
    public gElement parseConstDef() {
        ConstDef constDef = new ConstDef();
        constDef.addElement(tokenManager.getNextToken()); // Ident
        while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ConstExp ']')*
            constDef.addElement(tokenManager.getNextToken());
            constDef.addElement(parseConstExp());
            constDef.addElement(tokenManager.getNextToken());
        }
        constDef.addElement(tokenManager.getNextToken()); // '='
        constDef.addElement(parseConstInitVal());
        Configure.parseDisplay(constDef.toString());
        return constDef;
    }

    /* VarDecl -> BType VarDef (',' VarDef)* ';' */
    public gElement parseVarDecl() {
        VarDecl varDecl = new VarDecl();
        varDecl.addElement(parseBType()); // BType
        varDecl.addElement(parseVarDef()); // VarDef
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' VarDef)*
            varDecl.addElement(tokenManager.getNextToken());
            varDecl.addElement(parseVarDef());
        }
        varDecl.addElement(tokenManager.getNextToken()); // ';'
        Configure.parseDisplay(varDecl.toString());
        return varDecl;
    }

    /* VarDef -> Ident ('[' ConstExp ']')* | Ident ('[' ConstExp ']')* '=' InitVal */
    public gElement parseVarDef() {
        VarDef varDef = new VarDef();
        varDef.addElement(tokenManager.getNextToken()); // Ident
        while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ConstExp ']')*
            varDef.addElement(tokenManager.getNextToken());
            varDef.addElement(parseConstExp());
            varDef.addElement(tokenManager.getNextToken());
        }
        if (tokenManager.checkTokenType(0, WordType.ASSIGN)) {
            varDef.addElement(tokenManager.getNextToken()); // '='
            varDef.addElement(parseInitVal()); // InitVal
        }
        Configure.parseDisplay(varDef.toString());
        return varDef;
    }

    /* BType -> 'int' */
    public gElement parseBType() {
        BType bType = new BType();
        bType.addElement(tokenManager.getNextToken()); // 'int'
        return bType;
    }

    /* ConstInitVal -> ConstExp | '{' (ConstInitVal (',' ConstInitVal)*)? '}' */
    public gElement parseConstInitVal() {
        ConstInitVal constInitVal = new ConstInitVal();
        if (!tokenManager.checkTokenType(0, WordType.LBRACE)) { // ConstExp
            constInitVal.addElement(parseConstExp());
        } else {
            constInitVal.addElement(tokenManager.getNextToken()); // '{'
            if (!tokenManager.checkTokenType(0, WordType.RBRACE)) {
                constInitVal.addElement(parseConstInitVal());
                while (tokenManager.checkTokenType(0, WordType.COMMA)) {
                    constInitVal.addElement(tokenManager.getNextToken());
                    constInitVal.addElement(parseConstInitVal());
                }
            }
            constInitVal.addElement(tokenManager.getNextToken()); // '}'
        }
        Configure.parseDisplay(constInitVal.toString());
        return constInitVal;
    }

    /* ConstExp -> AddExp */
    public gElement parseConstExp() {
        ConstExp constExp = new ConstExp();
        constExp.addElement(parseAddExp());
        Configure.parseDisplay(constExp.toString());
        return constExp;
    }

    /* InitVal -> Exp | '{' (InitVal (',' InitVal)*)? '}' */
    public gElement parseInitVal() {
        InitVal initVal = new InitVal();
        if (!tokenManager.checkTokenType(0, WordType.LBRACE)) { // Exp
            initVal.addElement(parseExp());
        } else {
            initVal.addElement(tokenManager.getNextToken()); // '{'
            if (!tokenManager.checkTokenType(0, WordType.RBRACE)) {
                initVal.addElement(parseInitVal()); // InitVal
                while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' InitVal)*
                    initVal.addElement(tokenManager.getNextToken());
                    initVal.addElement(parseInitVal());
                }
            }
            initVal.addElement(tokenManager.getNextToken()); // '}'
        }
        Configure.parseDisplay(initVal.toString());
        return initVal;
    }

    /* Exp -> AddExp */
    public gElement parseExp() {
        Exp exp = new Exp();
        exp.addElement(parseAddExp());
        Configure.parseDisplay(exp.toString());
        return exp;
    }

    /* FuncType -> 'void' | 'int' */
    public gElement parseFuncType() {
        FuncType funcType = new FuncType();
        funcType.addElement(tokenManager.getNextToken());
        Configure.parseDisplay(funcType.toString());
        return funcType;
    }

    /* FuncFParams -> FuncFParam (',' FuncFParam)* */
    public gElement parseFuncFParams() {
        FuncFParams funcFParams = new FuncFParams();
        funcFParams.addElement(parseFuncFParam()); // FuncFParam
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' FuncFParam)*
            funcFParams.addElement(tokenManager.getNextToken());
            funcFParams.addElement(parseFuncFParam());
        }
        Configure.parseDisplay(funcFParams.toString());
        return funcFParams;
    }

    /* Block -> '{' (BlockItem)* '}' */
    public gElement parseBlock() {
        Block block = new Block();
        block.addElement(tokenManager.getNextToken()); // '{'
        while (!tokenManager.checkTokenType(0, WordType.RBRACE)) { // (BlockItem)*
            block.addElement(parseBlockItem());
        }
        block.addElement(tokenManager.getNextToken()); // '}'
        Configure.parseDisplay(block.toString());
        return block;
    }

    /* FuncFParam -> BType Ident ('[' ']' ('[' ConstExp ']')*)? */
    public gElement parseFuncFParam() {
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.addElement(parseBType()); // BType
        funcFParam.addElement(tokenManager.getNextToken()); // Ident
        if (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ']' ('[' ConstExp ']')*)?
            funcFParam.addElement(tokenManager.getNextToken()); // '['
            funcFParam.addElement(tokenManager.getNextToken()); // ']'
            while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ConstExp ']')*
                funcFParam.addElement(tokenManager.getNextToken()); // '['
                funcFParam.addElement(parseConstExp()); // ConstExp
                funcFParam.addElement(tokenManager.getNextToken()); // ']'
            }
        }
        Configure.parseDisplay(funcFParam.toString());
        return funcFParam;
    }

    /* BlockItem -> Decl | Stmt */
    public gElement parseBlockItem() {
        BlockItem blockItem = new BlockItem();
        if (tokenManager.checkTokenType(0, WordType.CONSTTK) || tokenManager.checkTokenType(0, WordType.INTTK)) {
            blockItem.addElement(parseDecl()); // Decl
        } else {
            blockItem.addElement(parseStmt()); // Stmt
        }
        return blockItem;
    }

    /* Stmt -> LVal '=' Exp ';' ①
    *        | LVal '=' 'getint' '(' ')' ';'  ②
    *        | [Exp] ';'  ③
    *        | Block  ④
    *        | 'if' '(' Cond ')' Stmt ('else' Stmt)?  ⑤
    *        | 'for' '(' (ForStmt)? ';' (Cond)? ';' (ForStmt)? ')' Stmt  ⑥
    *        | 'break' ';'  ⑦
    *        | 'continue' ';'  ⑧
    *        | 'return' (Exp)? ';'  ⑨
    *        | 'printf' '(' FormatString (',' Exp)* ')' ';'   ⑩ */
    public gElement parseStmt() {
        Stmt stmt = new Stmt();
        if (tokenManager.checkTokenType(0, WordType.LBRACE)) { // ④
            stmt.addElement(parseBlock());
        } else if (tokenManager.checkTokenType(0, WordType.IFTK)) { // ⑤
            stmt.addElement(tokenManager.getNextToken()); // 'if'
            stmt.addElement(tokenManager.getNextToken()); // '('
            stmt.addElement(parseCond()); // Cond
            stmt.addElement(tokenManager.getNextToken()); // ')'
            stmt.addElement(parseStmt()); // Stmt
            if (tokenManager.checkTokenType(0, WordType.ELSETK)) { // ('else' Stmt)?
                stmt.addElement(tokenManager.getNextToken()); // 'else'
                stmt.addElement(parseStmt()); // Stmt
            }
        } else if (tokenManager.checkTokenType(0, WordType.FORTK)) { // ⑥
            stmt.addElement(tokenManager.getNextToken()); // 'for'
            stmt.addElement(tokenManager.getNextToken()); // '('
            if (!tokenManager.checkTokenType(0, WordType.SEMICN)) { // (ForStmt)?
                stmt.addElement(parseForStmt()); // ForStmt
            }
            stmt.addElement(tokenManager.getNextToken()); // ';'
            if (!tokenManager.checkTokenType(0, WordType.SEMICN)) { // (Cond)?
                stmt.addElement(parseCond()); // Cond
            }
            stmt.addElement(tokenManager.getNextToken()); // ';'
            if (!tokenManager.checkTokenType(0, WordType.RPARENT)) { // (ForStmt)?
                stmt.addElement(parseForStmt()); // ForStmt
            }
            stmt.addElement(tokenManager.getNextToken()); // ')'
            stmt.addElement(parseStmt()); // Stmt
        } else if (tokenManager.checkTokenType(0, WordType.WHILETK)) { // while '(' Cond ')' Stmt
            stmt.addElement(tokenManager.getNextToken()); // 'while'
            stmt.addElement(tokenManager.getNextToken()); // '('
            stmt.addElement(parseCond()); // Cond
            stmt.addElement(tokenManager.getNextToken()); // ')'
            stmt.addElement(parseStmt()); // Stmt
        } else if (tokenManager.checkTokenType(0, WordType.BREAKTK)) { // ⑦
            stmt.addElement(tokenManager.getNextToken()); // 'break'
            stmt.addElement(tokenManager.getNextToken()); // ';'
        } else if (tokenManager.checkTokenType(0, WordType.CONTINUETK)) { // ⑧
            stmt.addElement(tokenManager.getNextToken()); // 'continue'
            stmt.addElement(tokenManager.getNextToken()); // ';'
        } else if (tokenManager.checkTokenType(0, WordType.RETURNTK)) { // ⑨
            stmt.addElement(tokenManager.getNextToken()); // 'return'
            if (!tokenManager.checkTokenType(0, WordType.SEMICN)) { // (Exp)?
                stmt.addElement(parseExp()); // Exp
            }
            stmt.addElement(tokenManager.getNextToken()); // ';'
        } else if (tokenManager.checkTokenType(0, WordType.PRINTFTK)) { // ⑩
            stmt.addElement(tokenManager.getNextToken()); // 'printf'
            stmt.addElement(tokenManager.getNextToken()); // '('
            stmt.addElement(tokenManager.getNextToken()); // FormatString
            while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' Exp)*
                stmt.addElement(tokenManager.getNextToken()); // ','
                stmt.addElement(parseExp()); // Exp
            }
            stmt.addElement(tokenManager.getNextToken()); // ')'
            stmt.addElement(tokenManager.getNextToken()); // ';'
        } else { // ① ② ③
            int index = 0, type = 0;
            while (!tokenManager.checkTokenType(index, WordType.SEMICN)) {
                if (tokenManager.checkTokenType(index, WordType.ASSIGN)) {
                    type = 1;
                    break;
                }
                index++;
            }
            if (type == 0) { // ③
                if (!tokenManager.checkTokenType(0, WordType.SEMICN)) {
                    stmt.addElement(parseExp()); // Exp
                }
                stmt.addElement(tokenManager.getNextToken()); // ';'
            } else {
                stmt.addElement(parseLVal()); // LVal
                stmt.addElement(tokenManager.getNextToken()); // '='
                if (tokenManager.checkTokenType(0, WordType.GETINTTK)) { // ②
                    stmt.addElement(tokenManager.getNextToken()); // 'getint'
                    stmt.addElement(tokenManager.getNextToken()); // '('
                    stmt.addElement(tokenManager.getNextToken()); // ')'
                    stmt.addElement(tokenManager.getNextToken()); // ';'
                } else { // ①
                    stmt.addElement(parseExp()); // Exp
                    stmt.addElement(tokenManager.getNextToken()); // ';'
                }
            }
        }
        Configure.parseDisplay(stmt.toString());
        return stmt;
    }

    /* LVal -> Ident ('[' Exp ']')* */
    public gElement parseLVal() {
        LVal lVal = new LVal();
        lVal.addElement(tokenManager.getNextToken()); // Ident
        while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' Exp ']')*
            lVal.addElement(tokenManager.getNextToken()); // '['
            lVal.addElement(parseExp()); // Exp
            lVal.addElement(tokenManager.getNextToken()); // ']'
        }
        Configure.parseDisplay(lVal.toString());
        return lVal;
    }

    /* Cond -> LOrExp */
    public gElement parseCond() {
        Cond cond = new Cond();
        cond.addElement(parseLOrExp());
        Configure.parseDisplay(cond.toString());
        return cond;
    }

    /* ForStmt -> LVal '=' Exp */
    public gElement parseForStmt() {
        ForStmt forStmt = new ForStmt();
        forStmt.addElement(parseLVal()); // LVal
        forStmt.addElement(tokenManager.getNextToken()); // '='
        forStmt.addElement(parseExp()); // Exp
        Configure.parseDisplay(forStmt.toString());
        return forStmt;
    }

    /* AddExp -> MulExp (('+'|'-' MulExp))* */
    public gElement parseAddExp() {
        AddExp addExp = new AddExp();
        addExp.addElement(parseMulExp()); // MulExp
        Configure.parseDisplay(addExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.PLUS, WordType.MINU)) { // (('+'|'-' MulExp))*
            addExp.addElement(tokenManager.getNextToken()); // '+'|'-'
            addExp.addElement(parseMulExp()); // MulExp
            Configure.parseDisplay(addExp.toString()); // 消除左递归的副作用
        }
        return addExp;
    }

    /* MulExp -> UnaryExp (('*' | '/' | '%') UnaryExp)* */
    public gElement parseMulExp() {
        MulExp mulExp = new MulExp();
        mulExp.addElement(parseUnaryExp()); // UnaryExp
        Configure.parseDisplay(mulExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.MULT, WordType.DIV, WordType.MOD)) { // (('*' | '/' | '%') UnaryExp)*
            mulExp.addElement(tokenManager.getNextToken()); // '*' | '/' | '%'
            mulExp.addElement(parseUnaryExp()); // UnaryExp
            Configure.parseDisplay(mulExp.toString()); // 消除左递归的副作用
        }
        return mulExp;
    }

    /* PrimaryExp -> '(' Exp ')' | LVal | Number */
    public gElement parsePrimaryExp() {
        PrimaryExp primaryExp = new PrimaryExp();
        if (tokenManager.checkTokenType(0, WordType.LPARENT)) { // '(' Exp ')'
            primaryExp.addElement(tokenManager.getNextToken()); // '('
            primaryExp.addElement(parseExp()); // Exp
            primaryExp.addElement(tokenManager.getNextToken()); // ')'
        } else if (tokenManager.checkTokenType(0, WordType.INTCON)) { // Number
            primaryExp.addElement(parseNumber()); // Number
        } else { // LVal
            primaryExp.addElement(parseLVal()); // LVal
        }
        Configure.parseDisplay(primaryExp.toString());
        return primaryExp;
    }

    /* Number -> IntConst */
    public gElement parseNumber() {
        Number number = new Number();
        number.addElement(tokenManager.getNextToken());
        Configure.parseDisplay(number.toString());
        return number;
    }

    /* UnaryExp -> PrimaryExp | Ident '(' (FuncRParams)? ')' | UnaryOp UnaryExp */
    public gElement parseUnaryExp() {
        UnaryExp unaryExp = new UnaryExp();
        if (tokenManager.checkTokenType(0, WordType.IDENFR) && tokenManager.checkTokenType(1, WordType.LPARENT)) { // Ident '(' (FuncRParams)? ')'
            unaryExp.addElement(tokenManager.getNextToken()); // Ident
            unaryExp.addElement(tokenManager.getNextToken()); // '('
            if (!tokenManager.checkTokenType(0, WordType.RPARENT)) { // (FuncRParams)?
                unaryExp.addElement(parseFuncRParams()); // FuncRParams
            }
            unaryExp.addElement(tokenManager.getNextToken()); // ')'
        } else if (tokenManager.checkTokenType(0, WordType.PLUS, WordType.MINU, WordType.NOT)) { // UnaryOp UnaryExp
            unaryExp.addElement(parseUnaryOp()); // UnaryOp
            unaryExp.addElement(parseUnaryExp()); // UnaryExp
        } else { // PrimaryExp
            unaryExp.addElement(parsePrimaryExp()); // PrimaryExp
        }
        Configure.parseDisplay(unaryExp.toString());
        return unaryExp;
    }

    /* UnaryOp -> '+' | '-' | '!' */
    public gElement parseUnaryOp() {
        UnaryOp unaryOp = new UnaryOp();
        unaryOp.addElement(tokenManager.getNextToken());
        Configure.parseDisplay(unaryOp.toString());
        return unaryOp;
    }

    /* FuncRParams -> Exp (',' Exp)* */
    public gElement parseFuncRParams() {
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.addElement(parseExp()); // Exp
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' Exp)*
            funcRParams.addElement(tokenManager.getNextToken()); // ','
            funcRParams.addElement(parseExp()); // Exp
        }
        Configure.parseDisplay(funcRParams.toString());
        return funcRParams;
    }

    /* RelExp -> AddExp (('<' | '<=' | '>' | '>=') AddExp)* */
    public gElement parseRelExp() {
        RelExp relExp = new RelExp();
        relExp.addElement(parseAddExp()); // AddExp
        Configure.parseDisplay(relExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.LSS, WordType.LEQ, WordType.GRE, WordType.GEQ)) { // (('<' | '<=' | '>' | '>=') AddExp)*
            relExp.addElement(tokenManager.getNextToken()); // '<' | '<=' | '>' | '>='
            relExp.addElement(parseAddExp()); // AddExp
            Configure.parseDisplay(relExp.toString()); // 消除左递归的副作用
        }
        return relExp;
    }

    /* EqExp -> RelExp (('==' | '!=') RelExp)* */
    public gElement parseEqExp() {
        EqExp eqExp = new EqExp();
        eqExp.addElement(parseRelExp()); // RelExp
        Configure.parseDisplay(eqExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.EQL, WordType.NEQ)) { // (('==' | '!=') RelExp)*
            eqExp.addElement(tokenManager.getNextToken()); // '==' | '!='
            eqExp.addElement(parseRelExp()); // RelExp
            Configure.parseDisplay(eqExp.toString()); // 消除左递归的副作用
        }
        return eqExp;
    }

    /* LAndExp -> EqExp ('&&' EqExp)* */
    public gElement parseLAndExp() {
        LAndExp lAndExp = new LAndExp();
        lAndExp.addElement(parseEqExp()); // EqExp
        Configure.parseDisplay(lAndExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.AND)) { // ('&&' EqExp)*
            lAndExp.addElement(tokenManager.getNextToken()); // '&&'
            lAndExp.addElement(parseEqExp()); // EqExp
            Configure.parseDisplay(lAndExp.toString()); // 消除左递归的副作用
        }
        return lAndExp;
    }

    /* LOrExp -> LAndExp ('||' LAndExp)* */
    public gElement parseLOrExp() {
        LOrExp lOrExp = new LOrExp();
        lOrExp.addElement(parseLAndExp()); // LAndExp
        Configure.parseDisplay(lOrExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.OR)) { // ('||' LAndExp)*
            lOrExp.addElement(tokenManager.getNextToken()); // '||'
            lOrExp.addElement(parseLAndExp()); // LAndExp
            Configure.parseDisplay(lOrExp.toString()); // 消除左递归的副作用
        }
        return lOrExp;
    }

}

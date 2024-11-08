package frontend.parser;

import frontend.LexParseLog;
import frontend.lexer.Token;
import frontend.lexer.WordType;
import frontend.parser.astNode.*;
import frontend.parser.astNode.Number;
import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;

public class Parser {
    private final TokenManager tokenManager;

    public Parser() {
        this.tokenManager = new TokenManager();
    }

    /*
     * CompUnit -> (Decl | FuncDef)* MainFuncDef
     */
    public CompUnit parseCompUnit() throws ParserException {
        /* Decl */
        CompUnit compUnit = new CompUnit();
        while(!tokenManager.checkTokenType(2,WordType.LPARENT)) {
            compUnit.addDecl(parseDecl());
        }
        /* FuncDef */
        while(!tokenManager.checkTokenType(1, WordType.MAINTK)) {
            compUnit.addFuncDef(parseFuncDef());
        }
        /* MainFuncDef */
        if (tokenManager.checkTokenType(1, WordType.MAINTK)) {
            compUnit.addFuncDef(parseMainFuncDef());
        }
        LexParseLog.add(compUnit.toString());
        return compUnit;
    }

    /* Decl -> ConstDecl | VarDecl */
    public Decl parseDecl() throws ParserException {
        Decl decl = new Decl();
        if (tokenManager.checkTokenType(0, WordType.CONSTTK)) {
            decl.setConstDecl(parseConstDecl());
        } else {
            decl.setVarDecl(parseVarDecl());
        }
        return decl;
    }

    /* FuncDef -> FuncType Ident '(' FuncFParams? ')' Block */
    public FuncDef parseFuncDef() throws ParserException {
        FuncDef funcDef = new FuncDef();
        funcDef.setFuncType(parseFuncType()); // FuncType
        funcDef.setIdent(tokenManager.getNextToken(WordType.IDENFR)); // Ident
        tokenManager.getNextToken(WordType.LPARENT); // '('
        if(!tokenManager.checkTokenType(0, WordType.RPARENT)) { // FuncFParams
            try {
                tokenManager.openBackup();
                FuncFParams funcFParams = parseFuncFParams();
                funcDef.setFuncFParams(funcFParams);
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
            }
        }
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        funcDef.setBlock(parseBlock()); // Block
        LexParseLog.add(funcDef.toString());
        return funcDef;
    }

    /* MainFuncDef -> 'int' 'main' '(' ')' Block */
    public FuncDef parseMainFuncDef() throws ParserException {
        FuncDef funcDef = new FuncDef();
        FuncType funcType = new FuncType();
        funcType.addElement(tokenManager.getNextToken(WordType.INTTK)); // 'int'
        funcDef.setFuncType(funcType); // FuncType
        funcDef.setIdent(tokenManager.getNextToken(WordType.MAINTK)); // Main
        tokenManager.getNextToken(WordType.LPARENT); // '('
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        funcDef.setBlock(parseBlock()); // Block
        LexParseLog.add("<" + GrammarType.MainFuncDef + ">");
        return funcDef;
    }

    /* ConstDecl -> 'const' BType ConstDef (',' ConstDef )* ';' */
    public ConstDecl parseConstDecl() throws ParserException {
        ConstDecl constDecl = new ConstDecl();
        tokenManager.getNextToken(WordType.CONSTTK); // 'const'
        constDecl.setBType(parseBType()); // BType
        constDecl.addConstDef(parseConstDef()); // ConstDef
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' ConstDef )*
            tokenManager.getNextToken();
            constDecl.addConstDef(parseConstDef());
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        LexParseLog.add(constDecl.toString());
        return constDecl;
    }

    /* ConstDef -> Ident ('[' ConstExp ']')* '=' ConstInitVal */
    public ConstDef parseConstDef() throws ParserException {
        ConstDef constDef = new ConstDef();
        constDef.setIdent(tokenManager.getNextToken(WordType.IDENFR)); // Ident
        while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ConstExp ']')*
            tokenManager.getNextToken();
            constDef.addConstExp(parseConstExp());
            try {
                tokenManager.getNextToken(WordType.RBRACK); // ']'
            } catch (ParserException e) {
                Token token = tokenManager.makeupToken(WordType.RBRACK);
                ErrorLog.addError(ErrorType.RBRACK_MISSED, token.getLine());
            }
        }
        tokenManager.getNextToken(WordType.ASSIGN); // '='
        constDef.setConstInitVal(parseConstInitVal());
        LexParseLog.add(constDef.toString());
        return constDef;
    }

    /* VarDecl -> BType VarDef (',' VarDef)* ';' */
    public VarDecl parseVarDecl() throws ParserException {
        VarDecl varDecl = new VarDecl();
        varDecl.setBType(parseBType()); // BType
        varDecl.addVarDef(parseVarDef()); // VarDef
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' VarDef)*
            varDecl.addElement(tokenManager.getNextToken());
            varDecl.addVarDef(parseVarDef());
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        LexParseLog.add(varDecl.toString());
        return varDecl;
    }

    /* VarDef -> Ident ('[' ConstExp ']')* ('=' InitVal) */
    public VarDef parseVarDef() throws ParserException {
        VarDef varDef = new VarDef();
        varDef.setIdent(tokenManager.getNextToken(WordType.IDENFR)); // Ident
        while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ConstExp ']')*
            tokenManager.getNextToken();
            varDef.addConstExp(parseConstExp());
            try {
                tokenManager.getNextToken(WordType.RBRACK); // ']'
            } catch (ParserException e) {
                Token token = tokenManager.makeupToken(WordType.RBRACK);
                ErrorLog.addError(ErrorType.RBRACK_MISSED, token.getLine());
            }
        }
        if (tokenManager.checkTokenType(0, WordType.ASSIGN)) {
            tokenManager.getNextToken(); // '='
            varDef.setInitVal(parseInitVal()); // InitVal
        }
        LexParseLog.add(varDef.toString());
        return varDef;
    }

    /* BType -> 'int' */
    public BType parseBType() throws ParserException {
        BType bType = new BType();
        bType.addElement(tokenManager.getNextToken(WordType.INTTK)); // 'int'
        return bType;
    }

    /* ConstInitVal -> ConstExp | '{' (ConstInitVal (',' ConstInitVal)*)? '}' */
    public ConstInitVal parseConstInitVal() throws ParserException {
        ConstInitVal constInitVal = new ConstInitVal();
        if (!tokenManager.checkTokenType(0, WordType.LBRACE)) { // ConstExp
            constInitVal.setConstExp(parseConstExp());
        } else {
            tokenManager.getNextToken(); // '{'
            if (!tokenManager.checkTokenType(0, WordType.RBRACE)) {
                constInitVal.addConstInitVal(parseConstInitVal());
                while (tokenManager.checkTokenType(0, WordType.COMMA)) {
                    tokenManager.getNextToken();
                    constInitVal.addConstInitVal(parseConstInitVal());
                }
            }
            tokenManager.getNextToken(WordType.RBRACE); // '}'
        }
        LexParseLog.add(constInitVal.toString());
        return constInitVal;
    }

    /* ConstExp -> AddExp */
    public ConstExp parseConstExp() throws ParserException {
        ConstExp constExp = new ConstExp();
        constExp.addElement(parseAddExp());
        LexParseLog.add(constExp.toString());
        return constExp;
    }

    /* InitVal -> Exp | '{' (InitVal (',' InitVal)*)? '}' */
    public InitVal parseInitVal() throws ParserException {
        InitVal initVal = new InitVal();
        if (!tokenManager.checkTokenType(0, WordType.LBRACE)) { // Exp
            initVal.setExp(parseExp());
        } else {
            tokenManager.getNextToken(); // '{'
            if (!tokenManager.checkTokenType(0, WordType.RBRACE)) {
                initVal.addInitVal(parseInitVal()); // InitVal
                while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' InitVal)*
                    tokenManager.getNextToken();
                    initVal.addInitVal(parseInitVal());
                }
            }
            tokenManager.getNextToken(WordType.RBRACE); // '}'
        }
        LexParseLog.add(initVal.toString());
        return initVal;
    }

    /* Exp -> AddExp */
    public Exp parseExp() throws ParserException {
        Exp exp = new Exp();
        exp.addElement(parseAddExp());
        LexParseLog.add(exp.toString());
        return exp;
    }

    /* FuncType -> 'void' | 'int' */
    public FuncType parseFuncType() throws ParserException {
        FuncType funcType = new FuncType();
        funcType.addElement(tokenManager.getNextToken(WordType.VOIDTK, WordType.INTTK));
        LexParseLog.add(funcType.toString());
        return funcType;
    }

    /* FuncFParams -> FuncFParam (',' FuncFParam)* */
    public FuncFParams parseFuncFParams() throws ParserException {
        FuncFParams funcFParams = new FuncFParams();
        funcFParams.addFuncFParam(parseFuncFParam()); // FuncFParam
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' FuncFParam)*
            tokenManager.getNextToken();
            funcFParams.addFuncFParam(parseFuncFParam());
        }
        LexParseLog.add(funcFParams.toString());
        return funcFParams;
    }

    /* Block -> '{' (BlockItem)* '}' */
    public Block parseBlock() throws ParserException {
        Block block = new Block();
        tokenManager.getNextToken(WordType.LBRACE); // '{'
        while (!tokenManager.checkTokenType(0, WordType.RBRACE)) { // (BlockItem)*
            block.addBlockItem(parseBlockItem());
        }
        block.setRbraceLine(tokenManager.getNextToken(WordType.RBRACE).getLine()); // '}'
        LexParseLog.add(block.toString());
        return block;
    }

    /* FuncFParam -> BType Ident ('[' ']' ('[' ConstExp ']')*)? */
    public FuncFParam parseFuncFParam() throws ParserException {
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.setBType(parseBType()); // BType
        funcFParam.setIdent(tokenManager.getNextToken(WordType.IDENFR)); // Ident
        if (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ']' ('[' ConstExp ']')*)?
            tokenManager.getNextToken(); // '['
            funcFParam.addConstExp(new ConstExp());
            try {
                tokenManager.getNextToken(WordType.RBRACK); // ']'
            } catch (ParserException e) {
                Token token = tokenManager.makeupToken(WordType.RBRACK);
                ErrorLog.addError(ErrorType.RBRACK_MISSED, token.getLine());
            }
            while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' ConstExp ']')*
                tokenManager.getNextToken(); // '['
                funcFParam.addConstExp(parseConstExp()); // ConstExp
                try {
                    tokenManager.getNextToken(WordType.RBRACK); // ']'
                } catch (ParserException e) {
                    Token token = tokenManager.makeupToken(WordType.RBRACK);
                    ErrorLog.addError(ErrorType.RBRACK_MISSED, token.getLine());
                }
            }
        }
        LexParseLog.add(funcFParam.toString());
        return funcFParam;
    }

    /* BlockItem -> Decl | Stmt */
    public BlockItem parseBlockItem() throws ParserException {
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
    public Stmt parseStmt() throws ParserException {
        Stmt stmt;
        if (tokenManager.checkTokenType(0, WordType.LBRACE)) { // ④
            stmt = parseStmtBlock();
        } else if (tokenManager.checkTokenType(0, WordType.IFTK)) { // ⑤
            stmt = parseStmtIf();
        } else if (tokenManager.checkTokenType(0, WordType.FORTK)) { // ⑥
            stmt = parseStmtFor();
        } else if (tokenManager.checkTokenType(0, WordType.WHILETK)) { // while '(' Cond ')' Stmt
            stmt = parseStmtWhile();
        } else if (tokenManager.checkTokenType(0, WordType.BREAKTK)) { // ⑦
            stmt = parseStmtBrkCon();
        } else if (tokenManager.checkTokenType(0, WordType.CONTINUETK)) { // ⑧
            stmt = parseStmtBrkCon();
        } else if (tokenManager.checkTokenType(0, WordType.RETURNTK)) { // ⑨
            stmt = parseStmtReturn();
        } else if (tokenManager.checkTokenType(0, WordType.PRINTFTK)) { // ⑩
            stmt = parseStmtPrintf();
        } else { // ① ② ③
            try {
                tokenManager.openBackup();
                stmt = parseStmtGetint();
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
                try {
                    tokenManager.openBackup();
                    stmt = parseStmtAssign();
                    tokenManager.closeBackup();
                } catch (ParserException e1) {
                    tokenManager.rollBack();
                    try {
                        tokenManager.openBackup();
                        stmt = parseStmtExp();
                        tokenManager.closeBackup();
                    } catch (ParserException e2) {
                        tokenManager.rollBack();
                        throw new ParserException(ParserException.ParserExcType.OTHER);
                    }
                }
            }
        }
        LexParseLog.add(stmt.toString());
        return stmt;
    }

    public StmtAssign parseStmtAssign() throws ParserException {
        StmtAssign stmtAssign = new StmtAssign();
        stmtAssign.setLVal(parseLVal()); // LVal
        tokenManager.getNextToken(WordType.ASSIGN); // '='
        stmtAssign.setExp(parseExp()); // Exp
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        return stmtAssign;
    }
    public StmtBlock parseStmtBlock() throws ParserException {
        StmtBlock stmtBlock = new StmtBlock();
        stmtBlock.setBlock(parseBlock());
        return stmtBlock;
    }
    public StmtBrkCon parseStmtBrkCon() throws ParserException {
        StmtBrkCon stmtBrkCon = new StmtBrkCon(tokenManager.getNextToken(WordType.BREAKTK, WordType.CONTINUETK));
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        return stmtBrkCon;
    }
    public StmtExp parseStmtExp() throws ParserException {
        StmtExp stmtExp = new StmtExp();
        if (!tokenManager.checkTokenType(0, WordType.SEMICN)) {
            try {
                tokenManager.openBackup();
                stmtExp.setExp(parseExp()); // Exp
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
            }
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        return stmtExp;
    }
    // stmt -> 'for' '(' (ForStmt)? ';' (Cond)? ';' (ForStmt)? ')' Stmt
    public StmtFor parseStmtFor() throws ParserException {
        StmtFor stmtFor = new StmtFor();
        tokenManager.getNextToken(WordType.FORTK); // 'for'
        tokenManager.getNextToken(WordType.LPARENT); // '('
        if (!tokenManager.checkTokenType(0, WordType.SEMICN)) { // (ForStmt)?
            try {
                tokenManager.openBackup();
                ForStmt forStmt1 = parseForStmt();
                stmtFor.setForStmt1(forStmt1); // ForStmt
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
            }
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        if (!tokenManager.checkTokenType(0, WordType.SEMICN)) { // (Cond)?
            try {
                tokenManager.openBackup();
                Cond cond = parseCond();
                stmtFor.setCond(cond); // Cond
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
            }
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        if (!tokenManager.checkTokenType(0, WordType.RPARENT)) { // (ForStmt)?
            try {
                tokenManager.openBackup();
                ForStmt forStmt2 = parseForStmt();
                stmtFor.setForStmt2(forStmt2); // ForStmt
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
            }
        }
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        stmtFor.setStmt(parseStmt()); // Stmt
        return stmtFor;
    }
    public StmtWhile parseStmtWhile() throws ParserException {
        StmtWhile stmtWhile = new StmtWhile();
        tokenManager.getNextToken(WordType.WHILETK); // 'while'
        tokenManager.getNextToken(WordType.LPARENT); // '('
        stmtWhile.setCond(parseCond()); // Cond
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        stmtWhile.setStmt(parseStmt()); // Stmt
        return stmtWhile;
    }
    public StmtGetint parseStmtGetint() throws ParserException {
        StmtGetint stmtGetint = new StmtGetint();
        stmtGetint.setLVal(parseLVal()); // LVal
        tokenManager.getNextToken(WordType.ASSIGN); // '='
        tokenManager.getNextToken(WordType.GETINTTK); // 'getint'
        tokenManager.getNextToken(WordType.LPARENT); // '('
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        return stmtGetint;
    }
    public StmtIf parseStmtIf() throws ParserException {
        StmtIf stmtIf = new StmtIf();
        tokenManager.getNextToken(WordType.IFTK); // 'if'
        tokenManager.getNextToken(WordType.LPARENT); // '('
        stmtIf.setCond(parseCond()); // Cond
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        stmtIf.setStmtIf(parseStmt()); // Stmt
        if (tokenManager.checkTokenType(0, WordType.ELSETK)) { // ('else' Stmt)?
            tokenManager.getNextToken(); // 'else'
            stmtIf.setStmtElse(parseStmt()); // Stmt
        }
        return stmtIf;
    }
    public StmtPrintf parseStmtPrintf() throws ParserException {
        StmtPrintf stmtPrintf = new StmtPrintf();
        stmtPrintf.setPrintfLine(tokenManager.getNextToken(WordType.PRINTFTK).getLine()); // 'printf'
        tokenManager.getNextToken(WordType.LPARENT); // '('
        stmtPrintf.setFormatString(tokenManager.getNextToken(WordType.STRCON)); // FormatString
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' Exp)*
            tokenManager.getNextToken(); // ','
            stmtPrintf.addExp(parseExp()); // Exp
        }
        try {
            tokenManager.getNextToken(WordType.RPARENT); // ')'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.RPARENT);
            ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        return stmtPrintf;
    }
    public StmtReturn parseStmtReturn() throws ParserException {
        StmtReturn stmtReturn = new StmtReturn();
        stmtReturn.setReturnLine(tokenManager.getNextToken(WordType.RETURNTK).getLine()); // 'return'
        if (!tokenManager.checkTokenType(0, WordType.SEMICN)) { // (Exp)?
            try {
                tokenManager.openBackup();
                stmtReturn.setExp(parseExp()); // Exp
                tokenManager.closeBackup();
            } catch (ParserException e) {
                tokenManager.rollBack();
            }
        }
        try {
            tokenManager.getNextToken(WordType.SEMICN); // ';'
        } catch (ParserException e) {
            Token token = tokenManager.makeupToken(WordType.SEMICN);
            ErrorLog.addError(ErrorType.SEMICN_MISSED, token.getLine());
        }
        return stmtReturn;
    }

    /* LVal -> Ident ('[' Exp ']')* */
    public LVal parseLVal() throws ParserException {
        LVal lVal = new LVal();
        lVal.setIdent(tokenManager.getNextToken(WordType.IDENFR)); // Ident
        while (tokenManager.checkTokenType(0, WordType.LBRACK)) { // ('[' Exp ']')*
            tokenManager.getNextToken(); // '['
            lVal.addExp(parseExp()); // Exp
            try {
                tokenManager.getNextToken(WordType.RBRACK); // ']'
            } catch (ParserException e) {
                Token token = tokenManager.makeupToken(WordType.RBRACK);
                ErrorLog.addError(ErrorType.RBRACK_MISSED, token.getLine());
            }
        }
        LexParseLog.add(lVal.toString());
        return lVal;
    }

    /* Cond -> LOrExp */
    public Cond parseCond() throws ParserException {
        Cond cond = new Cond();
        cond.addElement(parseLOrExp());
        LexParseLog.add(cond.toString());
        return cond;
    }

    /* ForStmt -> LVal '=' Exp */
    public ForStmt parseForStmt() throws ParserException {
        ForStmt forStmt = new ForStmt();
        forStmt.setLVal(parseLVal()); // LVal
        tokenManager.getNextToken(WordType.ASSIGN); // '='
        forStmt.setExp(parseExp()); // Exp
        LexParseLog.add(forStmt.toString());
        return forStmt;
    }

    /* AddExp -> MulExp (('+'|'-' MulExp))* */
    public AddExp parseAddExp() throws ParserException {
        AddExp addExp = new AddExp();
        addExp.addElement(parseMulExp()); // MulExp
        LexParseLog.add(addExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.PLUS, WordType.MINU)) { // (('+'|'-' MulExp))*
            addExp.addElement(tokenManager.getNextToken()); // '+'|'-'
            addExp.addElement(parseMulExp()); // MulExp
            LexParseLog.add(addExp.toString()); // 消除左递归的副作用
        }
        return addExp;
    }

    /* MulExp -> UnaryExp (('*' | '/' | '%') UnaryExp)* */
    public MulExp parseMulExp() throws ParserException {
        MulExp mulExp = new MulExp();
        mulExp.addElement(parseUnaryExp()); // UnaryExp
        LexParseLog.add(mulExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.MULT, WordType.DIV, WordType.MOD)) { // (('*' | '/' | '%') UnaryExp)*
            mulExp.addElement(tokenManager.getNextToken()); // '*' | '/' | '%'
            mulExp.addElement(parseUnaryExp()); // UnaryExp
            LexParseLog.add(mulExp.toString()); // 消除左递归的副作用
        }
        return mulExp;
    }

    /* PrimaryExp -> '(' Exp ')' | LVal | Number */
    public PrimaryExp parsePrimaryExp() throws ParserException {
        PrimaryExp primaryExp = new PrimaryExp();
        if (tokenManager.checkTokenType(0, WordType.LPARENT)) { // '(' Exp ')'
            tokenManager.getNextToken(); // '('
            primaryExp.addElement(parseExp()); // Exp
            try {
                tokenManager.getNextToken(WordType.RPARENT); // ')'
            } catch (ParserException e) {
                Token token = tokenManager.makeupToken(WordType.RPARENT);
                ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
            }
        } else if (tokenManager.checkTokenType(0, WordType.INTCON)) { // Number
            primaryExp.addElement(parseNumber()); // Number
        } else { // LVal
            primaryExp.addElement(parseLVal()); // LVal
        }
        LexParseLog.add(primaryExp.toString());
        return primaryExp;
    }

    /* Number -> IntConst */
    public Number parseNumber() throws ParserException {
        Number number = new Number();
        number.addElement(tokenManager.getNextToken(WordType.INTCON));
        LexParseLog.add(number.toString());
        return number;
    }

    /* UnaryExp -> PrimaryExp | Ident '(' (FuncRParams)? ')' | UnaryOp UnaryExp */
    public UnaryExp parseUnaryExp() throws ParserException {
        UnaryExp unaryExp = new UnaryExp();
        if (tokenManager.checkTokenType(0, WordType.IDENFR) && tokenManager.checkTokenType(1, WordType.LPARENT)) { // Ident '(' (FuncRParams)? ')'
            unaryExp.setCallFunc();
            unaryExp.setIdent(tokenManager.getNextToken()); // Ident
            tokenManager.getNextToken(); // '('
            if (!tokenManager.checkTokenType(0, WordType.RPARENT)) { // (FuncRParams)?
                try {
                    tokenManager.openBackup();
                    unaryExp.setFuncRParams(parseFuncRParams()); // FuncRParams
                    tokenManager.closeBackup();
                } catch (ParserException e) {
                    tokenManager.rollBack();
                }
            }
            try {
                tokenManager.getNextToken(WordType.RPARENT); // ')'
            } catch (ParserException e) {
                Token token = tokenManager.makeupToken(WordType.RPARENT);
                ErrorLog.addError(ErrorType.RPARENT_MISSED, token.getLine());
            }
        } else if (tokenManager.checkTokenType(0, WordType.PLUS, WordType.MINU, WordType.NOT)) { // UnaryOp UnaryExp
            unaryExp.setUnaryType();
            unaryExp.setUnaryOp(parseUnaryOp()); // UnaryOp
            unaryExp.setUnaryExp(parseUnaryExp()); // UnaryExp
        } else { // PrimaryExp
            unaryExp.setPrimaryExpType();
            unaryExp.setPrimaryExp(parsePrimaryExp()); // PrimaryExp
        }
        LexParseLog.add(unaryExp.toString());
        return unaryExp;
    }

    /* UnaryOp -> '+' | '-' | '!' */
    public UnaryOp parseUnaryOp() throws ParserException {
        UnaryOp unaryOp = new UnaryOp();
        unaryOp.addElement(tokenManager.getNextToken(WordType.PLUS, WordType.MINU, WordType.NOT));
        LexParseLog.add(unaryOp.toString());
        return unaryOp;
    }

    /* FuncRParams -> Exp (',' Exp)* */
    public FuncRParams parseFuncRParams() throws ParserException {
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.addExp(parseExp()); // Exp
        while (tokenManager.checkTokenType(0, WordType.COMMA)) { // (',' Exp)*
            tokenManager.getNextToken(); // ','
            funcRParams.addExp(parseExp()); // Exp
        }
        LexParseLog.add(funcRParams.toString());
        return funcRParams;
    }

    /* RelExp -> AddExp (('<' | '<=' | '>' | '>=') AddExp)* */
    public RelExp parseRelExp() throws ParserException {
        RelExp relExp = new RelExp();
        relExp.addElement(parseAddExp()); // AddExp
        LexParseLog.add(relExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.LSS, WordType.LEQ, WordType.GRE, WordType.GEQ)) { // (('<' | '<=' | '>' | '>=') AddExp)*
            relExp.addElement(tokenManager.getNextToken()); // '<' | '<=' | '>' | '>='
            relExp.addElement(parseAddExp()); // AddExp
            LexParseLog.add(relExp.toString()); // 消除左递归的副作用
        }
        return relExp;
    }

    /* EqExp -> RelExp (('==' | '!=') RelExp)* */
    public EqExp parseEqExp() throws ParserException {
        EqExp eqExp = new EqExp();
        eqExp.addElement(parseRelExp()); // RelExp
        LexParseLog.add(eqExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.EQL, WordType.NEQ)) { // (('==' | '!=') RelExp)*
            eqExp.addElement(tokenManager.getNextToken()); // '==' | '!='
            eqExp.addElement(parseRelExp()); // RelExp
            LexParseLog.add(eqExp.toString()); // 消除左递归的副作用
        }
        return eqExp;
    }

    /* LAndExp -> EqExp ('&&' EqExp)* */
    public LAndExp parseLAndExp() throws ParserException {
        LAndExp lAndExp = new LAndExp();
        lAndExp.addElement(parseEqExp()); // EqExp
        LexParseLog.add(lAndExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.AND)) { // ('&&' EqExp)*
            lAndExp.addElement(tokenManager.getNextToken()); // '&&'
            lAndExp.addElement(parseEqExp()); // EqExp
            LexParseLog.add(lAndExp.toString()); // 消除左递归的副作用
        }
        return lAndExp;
    }

    /* LOrExp -> LAndExp ('||' LAndExp)* */
    public LOrExp parseLOrExp() throws ParserException {
        LOrExp lOrExp = new LOrExp();
        lOrExp.addElement(parseLAndExp()); // LAndExp
        LexParseLog.add(lOrExp.toString()); // 消除左递归的副作用
        while (tokenManager.checkTokenType(0, WordType.OR)) { // ('||' LAndExp)*
            lOrExp.addElement(tokenManager.getNextToken()); // '||'
            lOrExp.addElement(parseLAndExp()); // LAndExp
            LexParseLog.add(lOrExp.toString()); // 消除左递归的副作用
        }
        return lOrExp;
    }
}
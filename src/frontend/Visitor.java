package frontend;

import frontend.lexer.Token;
import frontend.parser.astNode.*;
import frontend.parser.astNode.Number;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.Argument;
import ir.BasicBlock;
import ir.Function;
import ir.GlobalVariable;
import ir.Module;
import ir.Value;
import ir.constants.Constant;
import ir.constants.ConstantArray;
import ir.constants.ConstantInt;
import ir.constants.ConstantStr;
import ir.instrs.Alloca;
import ir.instrs.Alu;
import ir.instrs.Br;
import ir.instrs.Call;
import ir.instrs.GetElementPtr;
import ir.instrs.Icmp;
import ir.instrs.Instr;
import ir.instrs.Load;
import ir.instrs.Ret;
import ir.instrs.Store;
import ir.instrs.Trunc;
import ir.instrs.Zext;
import ir.types.ArrayType;
import ir.types.DataType;
import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.Type;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visitor {
    private Module module;
    private SymbolTable rootTab;
    private SymbolTable curTab;
    private Function curFunc = null;
    private BasicBlock curBB = null;
    private LinkedList<Value> loopStack;
    public Visitor(SymbolTable rootTab) {
        this.rootTab = rootTab;
        this.curTab = this.rootTab;
        this.module = new Module();
        this.loopStack = new LinkedList<>();
    }
    public Module getModule() {
        return module;
    }
    public void visitCompUnit(CompUnit compUnit) {
        if (compUnit.hasDecls())
            for (Decl decl : compUnit.getDecls())
                visitDecl(decl);
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
    }
    private void visitDecl(Decl decl) {
        if (decl.isConstDecl())
            for (ConstDef constDef : decl.getConstDecl().getConstDefs())
                visitConstDef(constDef);
        else
            for (VarDef varDef : decl.getVarDecl().getVarDefs())
                visitVarDef(varDef);
    }
    private void visitConstDef(ConstDef constDef) {
        String name = constDef.getIdent().getValue();
        Symbol symbol = curTab.getSymbol(name);
        if (symbol.isGlobal()) {
            GlobalVariable globalVariable = new GlobalVariable(name, new PointerType(symbol.getType()), true, symbol.getConstantInit());
            module.addGlobalVariable(globalVariable);
            symbol.setIrPtr(globalVariable);
        }
        else {
            Alloca alloca = new Alloca(new PointerType(symbol.getType()), curBB);
            curBB.addInstr(alloca);
            symbol.setIrPtr(alloca);
            if (symbol.getType().isIntegerTy()) {
                ConstantInt constantInt = (ConstantInt) symbol.getConstantInit();
                curBB.addInstr(new Store(curBB, constantInt, symbol.getIrPtr()));
            } else {
                ConstantArray constantArray = (ConstantArray) symbol.getConstantInit();
                ArrayType arrayType = (ArrayType) constantArray.getType();
                ArrayList<Integer> dims = arrayType.getDims();
                if (dims.size() == 1) {
                    for (int i = 0; i < dims.get(0); i++) {
                        ConstantInt constantInt = constantArray.getBase(i);
                        GetElementPtr getElementPtr = new GetElementPtr(new PointerType(constantInt.getType()),
                                curBB, symbol.getIrPtr(), new ConstantInt(new IntegerType(32), 0),
                                new ConstantInt(new IntegerType(32), i));
                        curBB.addInstr(getElementPtr);
                        curBB.addInstr(new Store(curBB, constantInt, getElementPtr));
                    }
                } else {
                    for (int i = 0; i < dims.get(1); i++)
                        for (int j = 0; j < dims.get(0); j++) {
                            ConstantInt constantInt = constantArray.getBase(i, j);
                            GetElementPtr getElementPtr = new GetElementPtr(new PointerType(constantInt.getType()),
                                    curBB, symbol.getIrPtr(), new ConstantInt(new IntegerType(32), 0),
                                    new ConstantInt(new IntegerType(32), i), new ConstantInt(new IntegerType(32), j));
                            curBB.addInstr(getElementPtr);
                            curBB.addInstr(new Store(curBB, constantInt, getElementPtr));
                        }
                }
            }
        }
    }
    private void visitVarDef(VarDef varDef) {
        String name = varDef.getIdent().getValue();
        Symbol symbol = curTab.getSymbol(name);
        if (symbol.isGlobal()) {
            Constant constant;
            if (null == symbol.getConstantInit()) {
                if (symbol.getType().isIntegerTy())
                    constant = new ConstantInt(new IntegerType(32), 0);
                else
                    constant = new ConstantArray(symbol.getType());
            } else
                constant = symbol.getConstantInit();
            GlobalVariable globalVariable = new GlobalVariable(name, new PointerType(symbol.getType()), false, constant);
            module.addGlobalVariable(globalVariable);
            symbol.setIrPtr(globalVariable);
        } else {
            Value ptr = new Alloca(new PointerType(symbol.getType()), curBB);
            curBB.addInstr((Instr) ptr);
            symbol.setIrPtr(ptr);
            if (!varDef.hasInitVal()) return ;
            InitVal initVal = varDef.getInitVal();
            if (initVal.isExpType()) {
                Value value = visitExp(initVal.getExp());
                curBB.addInstr(new Store(curBB, value, ptr));
            } else {
                ArrayList<InitVal> initVals = initVal.getInitVals();
                if (initVals.get(0).isExpType()) {
                    for (int i = 0; i < initVals.size(); i++) {
                        Value tmp = visitExp(initVals.get(i).getExp());
                        GetElementPtr getElementPtr = new GetElementPtr(new PointerType(tmp.getType()), curBB, ptr,
                                new ConstantInt(new IntegerType(32), 0), new ConstantInt(new IntegerType(32), i));
                        curBB.addInstr(getElementPtr);
                        curBB.addInstr(new Store(curBB, tmp, getElementPtr));
                    }
                } else {
                    for (int i = 0; i < initVals.size(); i++) {
                        InitVal sonInitVal = initVals.get(i);
                        ArrayList<InitVal> sonInitVals = sonInitVal.getInitVals();
                        for (int j = 0; j < sonInitVals.size(); j++) {
                            Value tmp = visitExp(sonInitVals.get(j).getExp());
                            GetElementPtr getElementPtr = new GetElementPtr(new PointerType(tmp.getType()), curBB, ptr,
                                    new ConstantInt(new IntegerType(32), 0),
                                    new ConstantInt(new IntegerType(32), i),
                                    new ConstantInt(new IntegerType(32), j));
                            curBB.addInstr(getElementPtr);
                            curBB.addInstr(new Store(curBB, tmp, getElementPtr));
                        }
                    }
                }
            }

        }
    }
    private void visitFuncDef(FuncDef funcDef) {
        // 创建function的初始化
        String name = funcDef.getIdent().getValue();
        Symbol symbol = curTab.getSymbol(name);
        curFunc = new Function(name, (FunctionType) symbol.getType(), false);
        module.addFunction(curFunc);
        symbol.setIrPtr(curFunc);
        curBB = new BasicBlock(curFunc);
        // 创建function的参数
        curTab = curTab.getSonNextTab();
        if (funcDef.hasFuncFParams()) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                String  paramName = funcFParam.getIdent().getValue();
                Symbol paramSymbol = curTab.getSymbol(paramName);
                Argument argument = new Argument((DataType) paramSymbol.getType());
                curFunc.addArgument(argument);
                Alloca alloca = new Alloca(new PointerType(paramSymbol.getType()), curBB);
                curBB.addInstr(alloca);
                paramSymbol.setIrPtr(alloca);
                curBB.addInstr(new Store(curBB, argument, paramSymbol.getIrPtr()));
            }
        }
        // 创建function的block
        visitBlock(funcDef.getBlock());
        curTab = curTab.getParent();
    }
    private void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems())
            visitBlockItem(blockItem);
    }
    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem.isDecl())
            visitDecl(blockItem.getDecl());
        else
            visitStmt(blockItem.getStmt());
    }
    private Value visitExp(Exp exp) { return visitAddExp(exp.getAddExp()); }
    private Value visitAddExp(AddExp addExp) {
        Value value = visitMulExp((MulExp) addExp.get(0));
        for (int i = 2; i < addExp.size(); i += 2) {
            Value tmp = visitMulExp((MulExp) addExp.get(i));
            value = typeConversion(value, new IntegerType(32));
            tmp = typeConversion(tmp, new IntegerType(32));
            switch (((Token) addExp.get(i - 1)).getType()) {
                case PLUS -> {
                    value = new Alu(Value.ValueType.add, (IntegerType) value.getType(), curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                case MINU -> {
                    value = new Alu(Value.ValueType.sub, (IntegerType) value.getType(), curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                default -> {}
            }
        }
        return value;
    }
    private Value visitMulExp(MulExp mulExp) {
        Value value = visitUnaryExp((UnaryExp) mulExp.get(0));
        for (int i = 2; i < mulExp.size(); i += 2) {
            Value tmp = visitUnaryExp((UnaryExp) mulExp.get(i));
            value = typeConversion(value, new IntegerType(32));
            tmp = typeConversion(tmp, new IntegerType(32));
            switch (((Token) mulExp.get(i - 1)).getType()) {
                case MULT -> {
                    value = new Alu(Value.ValueType.mul, (IntegerType) value.getType(), curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                case DIV -> {
                    value = new Alu(Value.ValueType.sdiv, (IntegerType) value.getType(), curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                case MOD -> {
                    value = new Alu(Value.ValueType.srem, (IntegerType) value.getType(), curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                default -> {}
            }
        }
        return value;
    }
    private Value visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.isPrimaryExpType())
            return visitPrimaryExp(unaryExp.getPrimaryExp());
        else if (unaryExp.isUnaryType()) {
            UnaryOp unaryOp = unaryExp.getUnaryOp();
            Value value = visitUnaryExp(unaryExp.getUnaryExp());
            switch (unaryOp.getUnaryOp().getType()) {
                case PLUS -> {}
                case MINU -> {
                    value = new Alu(Value.ValueType.sub, (IntegerType) value.getType(), curBB,
                            new ConstantInt(new IntegerType(32), 0), value);
                    curBB.addInstr((Instr) value);
                }
                case NOT -> {
                    value = new Icmp(Icmp.IcmpOp.eq, curBB, value, new ConstantInt(new IntegerType(32), 0));
                    curBB.addInstr((Instr) value);
                }
                default -> {}
            }
            return value;
        } else
            return visitCall(unaryExp);
    }
    private Value visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.isExpType())
            return visitExp(primaryExp.getExp());
        else if (primaryExp.isLValType()) {
            LVal lVal = primaryExp.getLVal();
            Value value = visitLVal(lVal, false);
            return value;
        }
        else
            return visitNumber(primaryExp.getNumber());
    }
    private Value visitLVal(LVal lVal, boolean isAddr) {
        String name = lVal.getIdent().getValue();
        Symbol symbol = curTab.getSymbol(name);
        if (null == symbol.getIrPtr())
            symbol = curTab.getParent().getSymbol(name);
        Value ptr = symbol.getIrPtr();
        if (symbol.getType().isPointerTy()) {
            ptr = new Load((PointerType) symbol.getType(), curBB, ptr);
            curBB.addInstr((Instr) ptr);
        }
        if (!lVal.hasExps()) {
            if (symbol.getType().isArrayTy()) {
                GetElementPtr getElementPtr = new GetElementPtr(new PointerType(((ArrayType) symbol.getType()).getElementType()), curBB, ptr,
                        new ConstantInt(new IntegerType(32), 0), new ConstantInt(new IntegerType(32), 0));
                curBB.addInstr(getElementPtr);
                return getElementPtr;
            } else{
                if (isAddr || symbol.getType().isPointerTy()) return ptr;
                else {
                    Load load = new Load((DataType) (symbol.getType()), curBB, ptr);
                    curBB.addInstr(load);
                    return load;
                }
            }
        } else {
            ArrayList<Value> values = new ArrayList<>();
            for (Exp exp : lVal.getExps())
                values.add(visitExp(exp));
            Type type = symbol.getType();
            GetElementPtr getElementPtr;
            if (type.isArrayTy()) {
                for (int i = 0; i < values.size(); i++)
                    type = ((ArrayType) type).getElementType();
                values.add(0, new ConstantInt(new IntegerType(32), 0));
                values.add(0, ptr);
                if (isAddr) {
                    getElementPtr = new GetElementPtr(new PointerType(type), curBB, values.toArray(new Value[values.size()]));
                    curBB.addInstr(getElementPtr);
                    return getElementPtr;
                } else {
                    if (type.isIntegerTy()) {
                        getElementPtr = new GetElementPtr(new PointerType(type), curBB, values.toArray(new Value[values.size()]));
                        curBB.addInstr(getElementPtr);
                        Load load = new Load((IntegerType) type, curBB, getElementPtr);
                        curBB.addInstr(load);
                        return load;
                    } else {
                        values.add(new ConstantInt(new IntegerType(32), 0));
                        type = ((ArrayType)type).getElementType();
                        getElementPtr = new GetElementPtr(new PointerType(type), curBB, values.toArray(new Value[values.size()]));
                        curBB.addInstr(getElementPtr);
                        return getElementPtr;
                    }
                }
            } else {
                type = ((PointerType) type).getReferencedType();
                if (lVal.getExps().size() == 2) {
                    type = ((ArrayType) type).getElementType();
                }
                values.add(0, ptr);
                if (isAddr) {
                    getElementPtr = new GetElementPtr(new PointerType(type), curBB, values.toArray(new Value[values.size()]));
                    curBB.addInstr(getElementPtr);
                    return getElementPtr;
                } else {
                    if (type.isIntegerTy()) {
                        getElementPtr = new GetElementPtr(new PointerType(type), curBB, values.toArray(new Value[values.size()]));
                        curBB.addInstr(getElementPtr);
                        Load load = new Load((DataType) type, curBB, getElementPtr);
                        curBB.addInstr(load);
                        return load;
                    } else {
                        values.add(new ConstantInt(new IntegerType(32), 0));
                        type = ((ArrayType)type).getElementType();
                        getElementPtr = new GetElementPtr(new PointerType(type), curBB, values.toArray(new Value[values.size()]));
                        curBB.addInstr(getElementPtr);
                        return getElementPtr;
                    }
                }
            }

        }
    }
    private Value visitNumber(Number number) {
        return new ConstantInt(new IntegerType(32), number.getOpResult());
    }
    private Value visitCall(UnaryExp unaryExp) {
        FuncRParams funcRParams = unaryExp.getFuncRParams();
        String funcName = unaryExp.getIdent().getValue();
        Symbol symbol = curTab.getSymbol(funcName);
        FunctionType functionType = (FunctionType) symbol.getType();
        ArrayList<Value> args = new ArrayList<>();
        args.add(symbol.getIrPtr());
        for (Exp exp : funcRParams.getExps()) {
            args.add(visitExp(exp));
        }
        Call call = new Call(functionType.getReturnType(), curBB, args.toArray(new Value[args.size()]));
        curBB.addInstr(call);
        return call;
    }
    private Value visitLOrExp(LOrExp lOrExp, BasicBlock trueBB, BasicBlock falseBB) { // todo: 短路求值debug重点
        Value value;
        BasicBlock nextBB;
        for (int i = 0; i < lOrExp.size() - 1; i += 2) {
            nextBB = new BasicBlock(curFunc);
            value = visitLAndExp((LAndExp) lOrExp.get(i), nextBB);
            curBB.addInstr(new Br(curBB, value, trueBB, nextBB));
            curBB = nextBB;
        }
        value = visitLAndExp((LAndExp) lOrExp.get(lOrExp.size() - 1), falseBB);
        return value;
    }
    private Value visitLAndExp(LAndExp lAndExp, BasicBlock falseBB) { // todo: 短路求值debug重点
        Value value;
        BasicBlock nextBB;
        for (int i = 0; i < lAndExp.size() - 1; i += 2) {
            value = visitEqExp((EqExp) lAndExp.get(i));
            nextBB = new BasicBlock(curFunc);
            curBB.addInstr(new Br(curBB, value, nextBB, falseBB));
            curBB = nextBB;
        }
        value = visitEqExp((EqExp) lAndExp.get(lAndExp.size() - 1));
        return value;
    }
    private Value visitEqExp(EqExp eqExp) {
        Value value = visitRelExp((RelExp) eqExp.get(0));
        for (int i = 2; i < eqExp.size(); i += 2) {
            Value tmp = visitRelExp((RelExp) eqExp.get(i));
            value = typeConversion(value, new IntegerType(32));
            tmp = typeConversion(tmp, new IntegerType(32));
            switch (((Token) eqExp.get(i - 1)).getType()) {
                case EQL -> {
                    value = new Icmp(Icmp.IcmpOp.eq, curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                case NEQ -> {
                    value = new Icmp(Icmp.IcmpOp.ne, curBB, value, tmp);
                    curBB.addInstr((Instr) value);
                }
                default -> {}
            }
        }
        if (value.getType().isIntegerTy(32)) {
            value = new Icmp(Icmp.IcmpOp.ne, curBB, value,
                    new ConstantInt(new IntegerType(32), 0));
            curBB.addInstr((Instr) value);
        }
        return value;
    }
    private Value visitRelExp(RelExp relExp) {
        Value value = visitAddExp((AddExp) relExp.get(0));
        for (int i = 2; i < relExp.size(); i += 2) {
            Value tmp = visitAddExp((AddExp) relExp.get(i));
            value = typeConversion(value, new IntegerType(32));
            tmp = typeConversion(tmp, new IntegerType(32));
            switch (((Token) relExp.get(i - 1)).getType()) {
                case LSS -> value = new Icmp(Icmp.IcmpOp.slt, curBB, value, tmp);
                case LEQ -> value = new Icmp(Icmp.IcmpOp.sle, curBB, value, tmp);
                case GRE -> value = new Icmp(Icmp.IcmpOp.sgt, curBB, value, tmp);
                case GEQ -> value = new Icmp(Icmp.IcmpOp.sge, curBB, value, tmp);
                default -> {}
            }
            curBB.addInstr((Instr) value);
        }
        return value;
    }
    private void visitStmt(Stmt stmt) {
        switch (stmt.getStmtType()) {
            case StmtAssign -> visitStmtAssign((StmtAssign) stmt);
            case StmtExp -> visitStmtExp((StmtExp) stmt);
            case StmtIf -> visitStmtIf((StmtIf) stmt);
            case StmtFor -> visitStmtFor((StmtFor) stmt);
            case StmtWhile -> visitStmtWhile((StmtWhile) stmt);
            case StmtBreak -> visitStmtBreak();
            case StmtContinue -> visitStmtContinue();
            case StmtGetint -> visitStmtGetInt((StmtGetint) stmt);
            case StmtReturn -> visitStmtReturn((StmtReturn) stmt);
            case StmtPrintf -> visitStmtPrintf((StmtPrintf) stmt);
            case StmtBlock -> {
                Block block = ((StmtBlock) stmt).getBlock();
                curTab = curTab.getSonNextTab();
                visitBlock(block);
                curTab = curTab.getParent();
            }
        }
    }
    private void visitStmtAssign(StmtAssign stmtAssign) {
        Value value = visitExp(stmtAssign.getExp());
        Value ptr = visitLVal(stmtAssign.getLVal(), true);
        value = typeConversion(value, ((PointerType) ptr.getType()).getReferencedType());
        curBB.addInstr(new Store(curBB, value, ptr));
    }
    private void visitStmtExp(StmtExp stmtExp) {
        if (stmtExp.hasExp())
            visitExp(stmtExp.getExp());
    }
    private void visitStmtIf(StmtIf stmtIf) {
        BasicBlock thenBB = new BasicBlock(curFunc);
        BasicBlock mergeBB = new BasicBlock(curFunc);
        if (stmtIf.hasElse()) {
            BasicBlock elseBB = new BasicBlock(curFunc);
            Value cond = visitLOrExp(stmtIf.getCond().getlOrExp(), thenBB, elseBB);
            curBB.addInstr(new Br(curBB, cond, thenBB, elseBB));
            curBB = thenBB;
            visitStmt(stmtIf.getStmtIf());
            curBB.addInstr(new Br(curBB, mergeBB));
            curBB = elseBB;
            visitStmt(stmtIf.getStmtElse());
        } else {
            Value cond = visitLOrExp(stmtIf.getCond().getlOrExp(), thenBB, mergeBB);
            curBB.addInstr(new Br(curBB, cond, thenBB, mergeBB));
            curBB = thenBB;
            visitStmt(stmtIf.getStmtIf());
        }
        curBB.addInstr(new Br(curBB, mergeBB));
        curBB = mergeBB;
    }
    private void visitStmtForAssign(LVal lVal, Exp exp) {
        Value value = visitExp(exp);
        Value ptr = visitLVal(lVal, true);
        value = typeConversion(value, ((PointerType) ptr.getType()).getReferencedType());
        curBB.addInstr(new Store(curBB, value, ptr));
    }
    private void visitStmtFor(StmtFor stmtFor) {
        BasicBlock condBB = new BasicBlock(curFunc);
        BasicBlock bodyBB = new BasicBlock(curFunc);
        BasicBlock stepBB = new BasicBlock(curFunc);
        BasicBlock mergeBB = new BasicBlock(curFunc);
        loopStack.add(mergeBB);
        loopStack.add(stepBB);
        if (stmtFor.hasForStmt1())
            visitStmtForAssign(stmtFor.getForStmt1().getLVal(), stmtFor.getForStmt1().getExp());
        curBB.addInstr(new Br(curBB, condBB));
        curBB = condBB;
        if (stmtFor.hasCond()) {
            Value cond = visitLOrExp(stmtFor.getCond().getlOrExp(), bodyBB, mergeBB);
            curBB.addInstr(new Br(curBB, cond, bodyBB, mergeBB));
        } else
            curBB.addInstr(new Br(curBB, bodyBB));
        curBB = bodyBB;
        visitStmt(stmtFor.getStmt());
        curBB.addInstr(new Br(curBB, stepBB));
        loopStack.removeLast();
        loopStack.removeLast();
        curBB = stepBB;
        if (stmtFor.hasForStmt2())
            visitStmtForAssign(stmtFor.getForStmt2().getLVal(), stmtFor.getForStmt2().getExp());
        curBB.addInstr(new Br(curBB, condBB));
        curBB = mergeBB;
    }
    private void visitStmtWhile(StmtWhile stmtWhile) {
        BasicBlock condBB = new BasicBlock(curFunc);
        BasicBlock bodyBB = new BasicBlock(curFunc);
        BasicBlock mergeBB = new BasicBlock(curFunc);
        loopStack.add(mergeBB);
        loopStack.add(condBB);
        curBB.addInstr(new Br(curBB, condBB));
        curBB = condBB;
        Value cond = visitLOrExp(stmtWhile.getCond().getlOrExp(), bodyBB, mergeBB);
        curBB.addInstr(new Br(curBB, cond, bodyBB, mergeBB));
        curBB = bodyBB;
        visitStmt(stmtWhile.getStmt());
        curBB.addInstr(new Br(curBB, condBB));
        loopStack.removeLast();
        loopStack.removeLast();
        curBB = mergeBB;
    }
    private void visitStmtBreak() {
        curBB.addInstr(new Br(curBB, loopStack.get(loopStack.size() - 2)));
    }
    private void visitStmtContinue() {
        curBB.addInstr(new Br(curBB, loopStack.getLast()));
    }
    private void visitStmtGetInt(StmtGetint stmtGetint) {
        Function function = module.getFunction("getint");
        Value value = new Call(((FunctionType) function.getType()).getReturnType(), curBB, function);
        curBB.addInstr((Instr) value);
        Value ptr = visitLVal(stmtGetint.getLVal(), true);
        curBB.addInstr(new Store(curBB, value, ptr));
    }
    private void visitStmtReturn(StmtReturn stmtReturn) {
        if (stmtReturn.hasExp()) {
            Value value = visitExp(stmtReturn.getExp());
            curBB.addInstr(new Ret(curBB, value));
        } else
            curBB.addInstr(new Ret(curBB));
    }
    public void printStr(String str) {
        String regrex = "\\\\n";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        String str1 = str.replaceAll("\\\\n", "\\\\0A");
        str1 = str1 + "\\00";
        ArrayType arrayType = new ArrayType(new IntegerType(8), str.length() + 1 - count);
        LinkedList<GlobalVariable> globalVariables = module.getGlobalVariables();
        GlobalVariable gv = null;
        for (GlobalVariable globalVariable : globalVariables) {
            if (globalVariable.isConstant()) {
                Constant constant = globalVariable.getInitializer();
                if (constant instanceof ConstantStr) {
                    ConstantStr constantStr = (ConstantStr) constant;
                    if (constantStr.getVal().equals(str1))
                        gv = globalVariable;
                }
            }
        }
        if (null == gv) {
            ConstantStr constantStr = new ConstantStr(str1, arrayType);
            gv = new GlobalVariable(new PointerType(arrayType), constantStr);
            module.addGlobalVariable(gv);
        }
        Function putstr = module.getFunction("putstr");
        GetElementPtr getElementPtr = new GetElementPtr(new PointerType(new IntegerType(8)), curBB, gv, new ConstantInt(new IntegerType(32), 0), new ConstantInt(new IntegerType(32), 0));
        curBB.addInstr(getElementPtr);
        curBB.addInstr(new Call(((FunctionType) putstr.getType()).getReturnType(), curBB, putstr, getElementPtr));
    }
    private void visitStmtPrintf(StmtPrintf stmtPrintf) {
        Function putint = module.getFunction("putint");
        Function putch = module.getFunction("putch");
        String formatString = stmtPrintf.getFormatString().getValue();
        formatString = formatString.substring(1, formatString.length() - 1);
        ArrayList<Exp> exps = stmtPrintf.getExps();
        String regrex = "%d";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(formatString);
        ArrayList<Value> values = new ArrayList<>();
        for (Exp exp : exps)
            values.add(visitExp(exp));
        int i = 0, j, cnt = 0;
        while (matcher.find()) {
            j = matcher.start();
            if (i == j - 1) {
                Value value = new ConstantInt(new IntegerType(32), formatString.charAt(i));
                curBB.addInstr(new Call(((FunctionType) putch.getType()).getReturnType(), curBB, putch, value));
            } else if (i < j) {
                if (i + 2 == j && formatString.charAt(i) == '\\') {
                    Value value = new ConstantInt(new IntegerType(32), 10);
                    curBB.addInstr(new Call(((FunctionType) putch.getType()).getReturnType(), curBB, putch, value));
                } else
                    printStr(formatString.substring(i, j));
            }
            curBB.addInstr(new Call(((FunctionType) putint.getType()).getReturnType(), curBB, putint, values.get(cnt++)));
            i = matcher.end();
        }
        if (i == formatString.length() - 1) {
            Value value = new ConstantInt(new IntegerType(32), formatString.charAt(i));
            curBB.addInstr(new Call(((FunctionType) putch.getType()).getReturnType(), curBB, putch, value));
        } else if (i == formatString.length() - 2 && formatString.charAt(i) == '\\') {
            Value value = new ConstantInt(new IntegerType(32), 10);
            curBB.addInstr(new Call(((FunctionType) putch.getType()).getReturnType(), curBB, putch, value));
        } else if (i < formatString.length() - 1)
            printStr(formatString.substring(i));
    }
    private Value typeConversion(Value value, Type type) {
        if (value.getType().isIntegerTy(1)) {
            if (type.isIntegerTy(32)) {
                Zext zext = new Zext(new IntegerType(32), curBB, value);
                curBB.addInstr(zext);
                return zext;
            } else
                return value;
        }
        else {
            if (type.isIntegerTy(1)) {
                Trunc trunc = new Trunc(new IntegerType(1), curBB, value);
                curBB.addInstr(trunc);
                return trunc;
            } else
                return value;
        }
    }
}
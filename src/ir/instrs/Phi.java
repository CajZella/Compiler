package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.Type;

import java.util.ArrayList;
import java.util.HashMap;

/*
    <result> = phi <ty> [ <val0>, <label0>], ...
    phi指令用于多个基本块之间的数据流，它的结果是基本块中的一个值，这个值是从它的前驱基本块中选择的。
    phi指令的第一个操作数是它的结果类型，后面的操作数是一组值和基本块的对，这些值和基本块的对表示了
    phi指令的前驱基本块中的值和基本块。
    phi指令的结果是它的第一个操作数的类型，它的值是从它的前驱基本块中选择的，选择的规则是：
        1. 如果前驱基本块中没有phi指令的操作数，则选择第一个基本块中的值
        2. 如果前驱基本块中有phi指令的操作数，则选择与当前基本块相同的基本块中的值
 */
//todo: 或许生成中间代码时可以先不管phi指令
public class Phi extends Instr {
    private static int num = 0;
    private ArrayList<BasicBlock> phiBBs = new ArrayList<>();
    // private HashMap<Value, BasicBlock> phiMap = new HashMap<>();
    public Phi(Type type, BasicBlock pBB) {
        super(ValueType.phi, "%p" + num++, type, pBB);
    }
    public void addIncoming(Value value, BasicBlock block) {
        phiBBs.add(block);
        use(value);
    }
    public boolean hasIncomingFrom(BasicBlock block) {
        return phiBBs.contains(block);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s = phi %s ", name, getType()));
        for (int i = 0; i < operandsSize(); i++) {
            builder.append(String.format("[ %s, %s ]", getOperand(i).getName(), phiBBs.get(i).getName()));
            if (i != operandsSize() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }

}

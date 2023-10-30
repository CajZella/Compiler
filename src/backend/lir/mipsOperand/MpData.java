package backend.lir.mipsOperand;

import ir.constants.Constant;
import ir.constants.ConstantArray;
import ir.constants.ConstantInt;
import ir.constants.ConstantStr;
import ir.types.DataType;
import ir.types.Type;

public class MpData extends MpOpd {
    // int, array, string
    private String name;
    private int type = 0; // 0: int; 1: array .word; 2: array .space; 3: string .asciiz
    private Constant initial;
    private Type dataType;
    public MpData(String name, Constant constant, Type dataType) {
        this.name = name;
        this.initial = constant;
        this.dataType = dataType;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (initial instanceof ConstantInt) {
            ConstantInt constantInt = (ConstantInt) initial;
            builder.append(String.format("%s:\n\t.word %d\n", name, constantInt.getVal()));
        } else if (initial instanceof ConstantArray) {
            ConstantArray constantArray = (ConstantArray) initial;
            if (constantArray.getVals().isEmpty())
                builder.append(String.format("%s:\n\t.space\t%d\n", name, dataType.size()));
            else
                builder.append(String.format("%s:%s\n", name, constantArray.toMipsString()));
        } else {
            builder.append(String.format("%s:\n\t.asciiz %s\n", name, ((ConstantStr)initial).toMipsString()));
        }
        return builder.toString();
    }
}

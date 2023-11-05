package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpSyscall extends MpInstr {
    public MpSyscall(MpBlock block) {
        super(MipsInstrType.syscall, block);
    }
    public String toString() { return String.format("%s", MipsInstrType.syscall); }
}

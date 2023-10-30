package backend.lir.mipsInstr;

import backend.lir.MpBlock;

public class MpSyscall extends MpInstr {
    public MpSyscall(MpBlock block) {
        super(MipsInstrType.syscall, block);
    }
    public String toString() { return String.format("%s", MipsInstrType.syscall); }
}

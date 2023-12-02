package backend.Optimize;

import backend.lir.MpModule;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class Peephole {
    private final MpModule module;
    private final ArrayList<MpReg> phyRegs;
    public Peephole(MpModule module, ArrayList<MpReg> phyRegs) {
        this.module = module;
        this.phyRegs = phyRegs;
    }
    public void run() {

    }
}

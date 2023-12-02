package backend.lir.mipsInstr;

import backend.BackEnd;
import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;
import java.util.HashSet;

public class MpJump extends MpInstr {
    private MpLabel label = null;
    private ArrayList<MpReg> Kregs = new ArrayList<>();
    private int argNum = 0;
    public MpJump(MpBlock block, MpLabel label) {
        super(MipsInstrType.j, block);
        this.label = label;
    }
    public MpJump(MpBlock block, MpLabel label, int argNum) {
        super(MipsInstrType.jal, block);
        this.argNum = argNum;
        this.label = label;
        replaceDst(BackEnd.mipsPhyRegs.get(2));
    }
    public MpJump(MpBlock block, MpReg sourceReg) {
        super(MipsInstrType.jr, block);
        replaceSrc1(sourceReg);
    }
    public void setKregs(ArrayList<MpReg> Kregs) { this.Kregs = Kregs; }
    public ArrayList<MpReg> getKregs() { return Kregs; }
    public void dealJal() {
        int t = -1;
        for (MpReg reg : Kregs) {
            MpInstr instr = new MpStore(block, reg, BackEnd.mipsPhyRegs.get(27), new MpImm(t * 4));
            this.insertBefore(instr);
            t--;
        }
        this.insertBefore(new MpAlu(MipsInstrType.addiu, block, BackEnd.mipsPhyRegs.get(27), BackEnd.mipsPhyRegs.get(27), new MpImm(-4 * (13 + argNum))));
        t = -1;
        for (MpReg reg : Kregs) {
            this.insertAfter(new MpLoad(block, reg, BackEnd.mipsPhyRegs.get(27), new MpImm(t * 4)));
            t--;
        }
        this.insertAfter(new MpAlu(MipsInstrType.addiu, block, BackEnd.mipsPhyRegs.get(27), BackEnd.mipsPhyRegs.get(27), new MpImm(4 * (13 + argNum))));
    }
    public MpLabel getLabel() { return label; }
    public void replaceLabel(MpLabel label) { this.label = label; }
    public String toString() {
        return instrType == MipsInstrType.jr
                ? String.format("%s %s", instrType, src1Reg)
                : String.format("%s %s", instrType, label);
    }
}

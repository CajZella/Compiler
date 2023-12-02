package backend.Optimize;

import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.MpModule;
import backend.lir.mipsInstr.MpAlu;
import backend.lir.mipsInstr.MpBranch;
import backend.lir.mipsInstr.MpCmp;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpJump;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Peephole {
    private final MpModule module;
    private final ArrayList<MpReg> phyRegs;
    public Peephole(MpModule module, ArrayList<MpReg> phyRegs) {
        this.module = module;
        this.phyRegs = phyRegs;
    }
    public void run() {
        boolean finished = false;
        while (!finished) {
            finished = true;
            finished &= redundantPreserveWhenCall();
            finished &= removeRedundantMove();
            finished &= removeUselessBlock();
            replaceZeroWithZeroReg();
        }
    }

    /*
     * 删除函数调用时多余的保存和恢复寄存器指令
     */
    private boolean redundantPreserveWhenCall() {
        boolean finished = true;
        for (MpFunction function : module.getMpFunctions()) {
            if (function.isMain()) continue;
            HashSet<MpReg> regUsed = function.getRegUsed();
            for (MpBlock block : function.getMpBlocks()) {
                for (MpInstr instr : block.getMpInstrs()) {
                    regUsed.addAll(instr.getDefRegs());
                    if (instr.getInstrType() == MpInstr.MipsInstrType.jal)
                        regUsed.addAll(((MpJump)instr).getKregs());
                }
            }
            for (int i = 0; i < 3; i++)
                if (regUsed.contains(phyRegs.get(i)))
                    regUsed.remove(phyRegs.get(i));
            for (int i = 16; i < 30; i++)
                if (regUsed.contains(phyRegs.get(i)))
                    regUsed.remove(phyRegs.get(i));
        }
        for (MpFunction function : module.getMpFunctions()) {
            for (MpBlock block : function.getMpBlocks()) {
                for (MpInstr instr : block.getMpInstrs()) {
                    if (instr.getInstrType() == MpInstr.MipsInstrType.jal) {
                        MpJump jal = (MpJump) instr;
                        ArrayList<MpReg> Kregs = jal.getKregs();
                        jal.setKregs(new ArrayList<>(jal.getLabel().getFunction().getRegUsed()));
                        if (!equalArray(Kregs, jal.getKregs()))
                            finished = false;
                    }
                }
            }
        }
        return finished;
    }
    private boolean equalArray(ArrayList<MpReg> regs1, ArrayList<MpReg> regs2) {
        if (regs1.size() != regs2.size()) return false;
        for (int i = 0; i < regs1.size(); i++)
            if (!regs1.contains(regs2.get(i)))
                return false;
        return true;
    }
    /*
     * 删除源寄存器和目的寄存器相同的 move 指令
     */
    private boolean removeRedundantMove() {
        boolean finished = true;
        for (MpFunction function : module.getMpFunctions()) {
            for (MpBlock block : function.getMpBlocks()) {
                for (MpInstr instr : block.getMpInstrs()) {
                    if (instr.getInstrType() == MpInstr.MipsInstrType.move) {
                        MpReg dst = instr.getDstReg();
                        MpReg src = instr.getSrc1Reg();
                        if (dst == src) {
                            instr.remove();
                            finished = false;
                        }
                    }
                }
            }
        }
        return finished;
    }

    /*
     * 删除只有一条跳转指令的基本块
     */
    private boolean removeUselessBlock() {
        boolean finished = true;
        for (MpFunction function : module.getMpFunctions()) {
            Iterator<MpBlock> iterator = function.getMpBlocks().iterator();
            while (iterator.hasNext()) {
                MpBlock block = iterator.next();
                if (block.getFirstMpInstr().getInstrType() == MpInstr.MipsInstrType.j
                        && block.getMpInstrs().size() == 1 && block.getPrecMBs().size() == 1) { // todo: 具体判断还是需要等到中间优化完成后
                    finished = false;
                    MpJump jump = (MpJump) block.getFirstMpInstr();
                    for (MpBlock precBlock : block.getPrecMBs()) {
                        precBlock.replaceBJlabel(block.getLabel(), jump.getLabel());
                        precBlock.getSuccMBs().remove(block);
                        precBlock.addSuccMB(jump.getLabel().getBlock());
                        MpBlock succBlock = jump.getLabel().getBlock();
                        succBlock.getPrecMBs().remove(block);
                        succBlock.addPrecMB(precBlock);
                    }
                    iterator.remove();
                }
            }
        }
        return finished;
    }

    /*
     * 在一定位置中将立即数0换成寄存器$zero
     */
    private void replaceZeroWithZeroReg() {
        for (MpFunction function : module.getMpFunctions()) {
            for (MpBlock block : function.getMpBlocks()) {
                for (MpInstr instr : block.getMpInstrs()) {
                    if (instr instanceof MpAlu)
                        ((MpAlu) instr).replaceZeroWithReg();
                    else if (instr instanceof MpBranch)
                        ((MpBranch) instr).replaceZeroWithReg();
                    else if (instr instanceof MpCmp)
                        ((MpCmp) instr).replaceZeroWithReg();
                }
            }
        }
    }
}

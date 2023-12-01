package backend;

import backend.Optimize.RegAlloc;
import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.MpModule;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpJump;
import backend.lir.mipsOperand.MpPhyReg;
import backend.lir.mipsOperand.MpReg;
import ir.Module;
import settings.Config;
import util.MyIO;

import java.util.ArrayList;

public class BackEnd {
    private static Module module;
    private static MpModule mipsModule;
    public static ArrayList<MpReg> mipsPhyRegs = new ArrayList<>();
    public static void setModule(Module module) { BackEnd.module = module; }
    public static void run() {
        for (int i = 0; i < 30; i++)
            mipsPhyRegs.add(new MpReg(MpPhyReg.getReg(i)));
        CodeGen codeGen = new CodeGen(module, mipsPhyRegs);
        codeGen.genModule();
        mipsModule = codeGen.getMipsModule();
        if (Config.isMIPSVROutput)
            MyIO.writeFile(Config.MIPSVRFile, mipsModule.toString());
        RegAlloc regAlloc = new RegAlloc(mipsModule, mipsPhyRegs);
        regAlloc.run();
        for (MpFunction mipsFunction : mipsModule.getMpFunctions())
            for (MpBlock mipsBlock : mipsFunction.getMpBlocks())
                for (MpInstr mipsInstr : mipsBlock.getMpInstrs())
                    if (mipsInstr.getInstrType() == MpInstr.MipsInstrType.jal)
                        ((MpJump)mipsInstr).dealJal();
    }
    public static MpModule getMipsModule() { return mipsModule; }
}

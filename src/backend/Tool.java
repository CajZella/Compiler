package backend;

import backend.lir.MpBlock;
import backend.lir.mipsInstr.MpAlu;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpMove;
import backend.lir.mipsOperand.MpPhyReg;
import backend.lir.mipsOperand.MpReg;

public class Tool {
    public static class DivByConst {
        /*
         * 除以常数的优化
         * dividend:被除数
         * divisor: 为常数的除数
         * dis: 目标寄存器
         */
        public void run(MpReg dividend, int divisor, MpReg dst, MpBlock curMB) {
            /* step1. 若除数是+1 or -1 */
            if (divisor == 1) {
                new MpMove(curMB, dst, dividend);
            } else if (divisor == -1) {
                new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, new MpReg(MpPhyReg.$zero), dividend);
            }
            /* step2. 若除数是2的幂次 */
            else if ((divisor & (divisor - 1)) == 0) {
                
            }
            /* step3. 转化为除以无符号常数的除法优化 */
            int absDivisor = divisor > 0 ? divisor : -divisor;

        }
    }
}

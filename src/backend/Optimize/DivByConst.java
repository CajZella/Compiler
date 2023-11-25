package backend.Optimize;

import backend.lir.MpBlock;
import backend.lir.mipsInstr.MpAlu;
import backend.lir.mipsInstr.MpCmp;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpLoadImm;
import backend.lir.mipsInstr.MpMfhi;
import backend.lir.mipsInstr.MpMove;
import backend.lir.mipsInstr.MpShift;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpPhyReg;
import backend.lir.mipsOperand.MpReg;
import ir.instrs.Br;

import java.math.BigInteger;
/*
 * C语言的取整方式为向零取整
 */
public class DivByConst {
    private int N = 32;
    /*
     * 除以常数的优化
     * dividend:被除数
     * divisor: 为常数的除数
     * dis: 目标寄存器
     */
    public void run(MpReg dividend, int divisor, MpReg dst, MpBlock curMB) {
        /* step1. 若除数是+1 or -1 */
        if (divisor == 1) {
            curMB.addMpInstr(new MpMove(curMB, dst, dividend));
        } else if (divisor == -1) {
            curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, new MpReg(MpPhyReg.$zero), dividend));
        }
        /* step2. 若除数是2的幂次 */
        else if ((divisor & (divisor - 1)) == 0) {
            int shift = (Integer.numberOfTrailingZeros(divisor));
            curMB.addMpInstr(new MpShift(MpInstr.MipsInstrType.srl, curMB, dst, dividend, new MpImm(shift)));
        }
        /* step3. 转化为除以无符号常数的除法优化 */
        else {
            generateDivision(dividend, divisor, dst, curMB);
        }
    }
    // x二进制表示从最高位开始（左起）的连续的0的个数
    private int clz(long x) {
        int cnt = 0;
        for (int i = 31; i >= 0; i--) {
            if ((x & (1L << i)) != 0) {
                break;
            }
            cnt++;
        }
        return cnt;
    }
    private Multiplier chooseMultiplier(long d, int prec) {
        assert (d >= 1 && d < (1L << N));
        assert (prec >= 1 && prec <= N);
        int l = N - clz(d - 1); // l = log2(d) 上取整
        int post = l;
        BigInteger low = BigInteger.valueOf(1).shiftLeft(N + l).divide(BigInteger.valueOf(d));
        BigInteger high = BigInteger.valueOf(1).shiftLeft(N + l).add(BigInteger.valueOf(1).shiftLeft(N + l-prec)).divide(BigInteger.valueOf(d));
        while ((low.shiftRight(1).compareTo(high.shiftRight(1)) < 0) && post > 0) {
            low = low.shiftRight(1);
            high = high.shiftRight(1);
            --post;
        }
        return new Multiplier(high, post, l);
    }
    private void generateDivision(MpReg n, long d, MpReg dst, MpBlock curMB) {
        assert (d != 0);
        Multiplier multiplier = chooseMultiplier(Math.abs(d), N-1);
        if (multiplier.m.compareTo(BigInteger.valueOf(1).shiftLeft(N-1)) < 0) {
            curMB.addMpInstr(new MpLoadImm(curMB, dst, new MpImm(multiplier.m.intValue())));
            curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.mult, curMB, dst, n));
            curMB.addMpInstr(new MpMfhi(curMB, dst));
        } else {
            multiplier.m = multiplier.m.subtract(BigInteger.valueOf(1).shiftLeft(N));
            curMB.addMpInstr(new MpLoadImm(curMB, dst, new MpImm(multiplier.m.intValue())));
            curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.mult, curMB, dst, n));
            curMB.addMpInstr(new MpMfhi(curMB, dst));
            curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, dst, dst, n));
        }
        if (multiplier.post > 0)
            curMB.addMpInstr(new MpShift(MpInstr.MipsInstrType.sra, curMB, dst, dst, new MpImm(multiplier.post)));
        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.slt, curMB, new MpReg(MpPhyReg.$v0), n, new MpReg(MpPhyReg.$zero)));
        curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, dst, dst, new MpReg(MpPhyReg.$v0)));
        if (d < 0)
            curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, new MpReg(MpPhyReg.$zero), dst));
    }
    public class Multiplier {
        public BigInteger m;
        public int l;
        public int post;
        public Multiplier(BigInteger m, int post, int l) {
            this.m = m;
            this.post = post;
            this.l = l;
        }
    }
}

package backend.lir.mipsOperand;

public enum MpPhyReg {
        $zero,
        $at,
        $v0,
        $v1,
        $a0,
        $a1,
        $a2,
        $a3,
        $t0,
        $t1,
        $t2,
        $t3,
        $t4,
        $t5,
        $t6,
        $t7,
        $s0,
        $s1,
        $s2,
        $s3,
        $s4,
        $s5,
        $s6,
        $s7,
        $t8,
        $t9,
        $gp,
        $sp,
        $fp,
        $ra,
        ;
        public static MpPhyReg getReg(int index) {
                switch (index) {
                        case 0: return $zero;
                        case 1: return $at;
                        case 2: return $v0;
                        case 3: return $v1;
                        case 4: return $a0;
                        case 5: return $a1;
                        case 6: return $a2;
                        case 7: return $a3;
                        case 8: return $t0;
                        case 9: return $t1;
                        case 10: return $t2;
                        case 11: return $t3;
                        case 12: return $t4;
                        case 13: return $t5;
                        case 14: return $t6;
                        case 15: return $t7;
                        case 16: return $s0;
                        case 17: return $s1;
                        case 18: return $s2;
                        case 19: return $s3;
                        case 20: return $s4;
                        case 21: return $s5;
                        case 22: return $s6;
                        case 23: return $s7;
                        case 24: return $t8;
                        case 25: return $t9;
                        case 26: return $gp;
                        case 27: return $sp;
                        case 28: return $fp;
                        case 29: return $ra;
                        default: throw new RuntimeException("Invalid register index");
                }
        }
}

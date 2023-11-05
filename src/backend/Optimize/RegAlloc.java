package backend.Optimize;

import backend.lir.MpModule;

/*
 * step1. 全局活跃变量分析（pre:控制流）
 */
public class RegAlloc {
    private MpModule mipsModule;

    public RegAlloc(MpModule mipsModule) {
        this.mipsModule = mipsModule;
    }
    public void run() {}
    private void build() {}
    private void makeWorklist() {}
    private void simplify() {}
    private void coalesce() {}
    private void freeze() {}
    private void selectSpill() {}
    private void assignColors() {}
    private void rewriteProgram() {}
}

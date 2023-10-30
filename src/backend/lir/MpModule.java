package backend.lir;

import backend.lir.mipsOperand.MpData;

import java.util.LinkedList;

public class MpModule {
    private LinkedList<MpData> mpDatas = new LinkedList<>();
    private LinkedList<MpFunction> mpFunctions = new LinkedList<>();
    public MpModule() {}
    public void addMpData(MpData data) { this.mpDatas.add(data); }
    public void addMpFunction(MpFunction mpFunction) { this.mpFunctions.add(mpFunction); }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(".data\n");
        for (MpData data : mpDatas)
            builder.append(data);
        builder.append("\n\n.text\n");
        for (MpFunction function : mpFunctions) {
            builder.append(function + "\n");
        }
        return builder.toString();
    }
}

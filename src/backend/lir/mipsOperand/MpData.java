package backend.lir.mipsOperand;

import java.util.ArrayList;

public class MpData extends MpOpd {
    // int, array, string
    private String name;
    private int size;
    private String str = null;
    private ArrayList<Integer> vals = null;
    public MpData(String name, String str) {
        this.name = name;
        this.str = str.replaceAll("\\\\0A", "\\\\n");
        this.str = this.str.substring(0, this.str.length() - 3);
        this.str = "\"" + this.str + "\"";
    }
    public MpData(String name, int size) {
        this.name = name;
        this.size = size;
    }
    public MpData(String name, ArrayList<Integer> vals) {
        this.name = name;
        this.vals = vals;
    }
    public String toDataString() {
        StringBuilder builder = new StringBuilder();
        if (null != str)
            builder.append(String.format("%s:\n\t.asciiz %s\n", name, str));
        else if (null != vals) {
            builder.append(String.format("%s:", name));
            for (Integer val : vals)
                builder.append(String.format("\n\t.word %d", val));
            builder.append("\n");
        } else
            builder.append(String.format("%s:\n\t.space\t%d\n", name, size));
        return builder.toString();
    }
    public String toString() {
        return name;
    }
}

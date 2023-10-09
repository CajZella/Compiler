package frontend;

import java.util.ArrayList;

public class LexParseLog {
    private static ArrayList<String> log = new ArrayList<>();
    public static int size() { return log.size(); }
    public static String print() {
        StringBuilder sb = new StringBuilder();
        for (String s : log) {
            sb.append(s).append('\n');
        }
        return sb.toString();
    }
    public static void add(String s) { log.add(s); }
    public static void remain(int num) { log = new ArrayList<>(log.subList(0, num));}
}

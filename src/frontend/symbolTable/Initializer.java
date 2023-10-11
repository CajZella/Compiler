package frontend.symbolTable;

import frontend.parser.astNode.Exp;

import java.util.ArrayList;

// 标识符的初始值
public abstract class Initializer {
    public abstract int getInit(int...idx);
    public static class IntInitializer extends Initializer {
        private int init;
        public IntInitializer(int init) { this.init = init; }
        public int getInit(int...idx) { return init; }
    }
    public static class ExpInitializer extends Initializer {
        private Exp exp;
        public ExpInitializer(Exp exp) { this.exp = exp; }
        public int getInit(int...idx) {
            assert true:"exp can't call getInit.";
            return 0;
        }
        public Exp getExp() { return exp; }
    }
    public static class ArrayInitializer extends Initializer {
        private ArrayList<Initializer> inits;
        public ArrayInitializer() { this.inits = new ArrayList<>(); }
        public void addInit(Initializer init) { this.inits.add(init);}
        public ArrayList<Initializer> getInits() { return inits; }
        public int getInit(int...idxs) {
            Initializer initializer = this;
            for (Integer idx : idxs) {
                initializer = ((ArrayInitializer)initializer).getInits().get(idx);
            }
            return ((IntInitializer)initializer).getInit();
        }
    }
    public static class ZeroInitializer extends Initializer {
        public int getInit(int...idxs) { return 0; }
    }
}

package pass;

import ir.Value;
import ir.instrs.Instr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class PCs {
    public class ParallelCopy {
        public Value src;
        public Value dst;
        public ParallelCopy(Value src, Value dst) {
            this.src = src;
            this.dst = dst;
        }
        public Value getSrc() { return src; }
        public Value getDst() { return dst; }
    }
    private ArrayList<ParallelCopy> parallelCopies = new ArrayList<>();
    public void add(Value src, Value dst) { parallelCopies.add(new ParallelCopy(src, dst)); }
    public ArrayList<ParallelCopy> getParallelCopies() {
        // topological sorting
        ArrayList<ParallelCopy> sortedParallelCopies = new ArrayList<>();
        HashMap<Value, Integer> inDegree = new HashMap<>();
        HashSet<Value> visited = new HashSet<>();
        for (ParallelCopy parallelCopy : parallelCopies) {
            Value src = parallelCopy.getSrc();
            Value dst = parallelCopy.getDst();
            if (!inDegree.containsKey(src))
                inDegree.put(src, 0);
            if (!inDegree.containsKey(dst))
                inDegree.put(dst, 0);
            inDegree.put(dst, inDegree.get(dst) + 1);
            assert inDegree.get(dst) <= 1;
        }
        LinkedList<Value> queue = new LinkedList<>();
        for (Value value : inDegree.keySet())
            if (inDegree.get(value) == 0)
                queue.add(value);
        while (!queue.isEmpty()) {
            Value value = queue.poll();
            visited.add(value);
            Iterator iterator = parallelCopies.iterator();
            while (iterator.hasNext()) {
                ParallelCopy parallelCopy = (ParallelCopy) iterator.next();
                if (parallelCopy.getSrc().equals(value)) {
                    Value dst = parallelCopy.getDst();
                    inDegree.put(dst, inDegree.get(dst) - 1);
                    if (inDegree.get(dst) == 0 && !visited.contains(dst))
                        queue.add(dst);
                    sortedParallelCopies.add(parallelCopy);
                    iterator.remove();
                }
            }
            while (queue.isEmpty() && !parallelCopies.isEmpty()) {
                // has cycle
                Value copy = null;
                ParallelCopy tmp = null;
                for (Value value1 : inDegree.keySet())
                    if (inDegree.get(value1) != 0) {
                        copy = value1;
                        break;
                    }
                for (ParallelCopy parallelCopy : parallelCopies) {
                    if (parallelCopy.getDst().equals(copy)) {
                        tmp = parallelCopy;
                        break;
                    }
                }
                // 自环
                if (tmp.src.equals(tmp.dst)) {
                    sortedParallelCopies.add(tmp);
                    parallelCopies.remove(tmp);
                    inDegree.put(tmp.dst, 0);
                    continue;
                }
                Instr instr = new Instr();
                sortedParallelCopies.add(new ParallelCopy(instr, tmp.dst));
                tmp.dst = instr;
                inDegree.put(instr, 1);
                inDegree.put(tmp.src, inDegree.get(tmp.src) - 1);
                queue.add(tmp.src);
            }
        }
        return sortedParallelCopies;
    }
    public boolean isAssignDst(Value value) {
        for (ParallelCopy parallelCopy : parallelCopies)
            if (parallelCopy.getSrc() == value)
                return true;
        return false;
    }
}

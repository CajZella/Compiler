# 中端优化
## MemToReg- 全局优化
> llvm查看mem2reg效果
>
> ```shell
> clang-10 -S -emit-llvm -Xclang -disable-O0-optnone main.c
> opt-10 -mem2reg -S main.ll -o main_mem2reg.ll
> ```

### 插入PHI指令

1. 删除死基本块和多余的跳转指令

   - 由于类似if语句中的break，基本块中存在多余的跳转指令，直接删除即可。
   - 在删除多余跳转指令的基础上，删除死基本块

2. 计算控制流图CFG

   - 在visitor阶段根据**基本块指令跳转**记录每个基本块的 predecessors 和 successors

   - ```java
     // br
     case ("br label")
     case ("br i1 label1 label2")
     ```

3. 根据CFG图计算支配关系

   - 迭代计算。按照"某基本块的dom <- 某基本块所有前驱的dom的交集加上自己本身" 的策略进行更新，直到该基本块的dom集合不发生变化

   - ```java
     boolean isChanged = true;
     while (isChanged){
         for(BasicBlock bb : function.getBBs()) {
             HashSet doms = new HashSet();
             doms.addAll(bb.getPrevs().getHead());
             for (BasicBlock prevBB : bb.getPrevs()) {
                 doms.retainAll(prevBB);
             }
             doms.add(bb);
             if (!bb.getDoms().equals(doms))
                 isChanged = true;
             bb.setDoms(doms);
         }
     }
     ```

4. 计算直接支配关系和严格支配关系

   - ```java
     // 严格支配sdom
     bb.getDoms().remove(bb);
     //直接支配idom:所有严格支配中离n最近的那一个 bfs
     若bb.sdoms为空，则idom=null
     LinkedList<BasicBlock> bbQueue;
     bbQueue.add(bb);
     while(!bbQueue.isEmpty()) {
         遍历bb的prev，若在bb.sdoms中即为idom；否则继续
     }
     ```

5. 计算支配边界

   -  $DF(n)=\{x|n支配x的前驱节点，n不严格支配x\}$

   - ```java
     for (bb in function.getBBs()) {
         for (next in bb.getSucc()) {
             temp = bb;
             while (!temp.getSdoms().contains(next)) {
                 temp.getDFs().add(b);
                 temp = temp.getIdom();
             }
         }
     }
     ```

6. 插入phi函数

   - ![1.png](https://s2.loli.net/2023/04/15/wvPf9pKkCDtAZy3.png)

   - ```java
     SymTab symTab = function.getSymTab(); // alloca指令
     for (v in symTab) {
         HashSet<BasicBlock> visitedPHI = new...;
         LinkedList<BasicBlock> defBBs = new...;
         遍历function内的instr，若instr.isStore() and instr.getVal = v { // 定义点
             defBBs.add(instr.parent);
         }
         while(!defBBs.isEmpty()) {
             BasicBlock bb = defBBs.pop();
             遍历所有bb的支配边界 dfBB {
                 if (!visitedPHI.contains(dfBB)) {
                     ...
     			}
             }
         }
     }
     ```


### 变量重命名
注意多了undef在global variable内


## 待优化

1. 基本块合并(比如说只有一条跳转语句的基本块)
2. 等死代码删除的bug调出来后，把基本块合并在mem2reg后再干一遍。
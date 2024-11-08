# 代码生成
1. 生成全局数据段
2. 生成全局代码段 
   1. 寄存器分配
   2. 数组的处理
   3. 函数调用
   指令选择、指令调度、寄存器分配
## FIFO 寄存器分配(跨函数寄存器分配)
```java
LinkedList<MpReg> physicalRegs = new LinkedList<>(); // 物理寄存器
HashMap<MpReg, MpReg> phyRegMap = new HashMap<>(); // 虚拟寄存器 -> 物理寄存器
HashMap<MpReg, MpStackOffset> stackMap = new HashMap<>(); // 虚拟寄存器 -> 栈偏移
int stackOffset = 0 + function.getOffset(); // 栈偏移
int p = 0;
for(基本块b) {
    for(指令i) {
        for (使用寄存器useVR) {
            if (useVR在物理寄存器中)
                useVR = regMap.get(useVR);
            else {// useReg在栈中
            	MpReg pr = getPhyReg();
                new Load pr, stackOffset($sp);
                stackMap.remove();
                phyRegMap.put();
                setReg();
            }
        }   
        for (定义寄存器defVR) {
            MpReg pr = getPhyReg();
            phyRegMap.put();
            setReg();
        }
    }
}
MpReg getPhyReg() { // 获取一个物理寄存器
   if (physicalRegs[p]未被分配) {
       phyRegMap.put(vr, physicalRegs[p]);
       return physicalRegs[p];
       p = (p+1)%k;
   }
   else {
       new store physicalRegs[p], stackOffset($sp);
       stackMap.put(MpReg, MpStackOffset);
       offset += 4;
       phyRegMap.remove();
       return physicalRegs[p];
   }
}
```
## 图着色寄存器分配
### 活跃变量分析

沿控制流反方向
$$
in[s]=use[s]\cup(out[s]-def[s])\\
out[s]=\cup_{s的后继基本块p}in[p]
$$


### 冲突图数据结构
查询冲突图数据结构操作：
1. 获得与结点x相邻的所有结点 -> 邻接表
2. 判断x和y是否相邻 -> 邻接矩阵
不需要表示预着色结点的邻接表

计数器：
1. 每个传送有关的结点都有一个计数器，记录该结点涉及的传送指令的条数
2. 所有节点都有记录图中当前与它相邻的结点个数的计数器

其他信息记录：
1. 低度数的传送无关的结点simplify worklist
2. 有可能合并的传送指令 worklist moves
3. 低度数的传送有关的结点 freeze worklist
4. 高度数的结点 spill worklist

## peephole优化
1. 去掉函数跳转前保留的多余的参数 ok
   1. testfile5:  ![img1.png](..\image\img1.png)
2. 去掉只有跳转指令的基本块 ok
   1. 2和4的效果：![img2.png](..\image\img2.png)
3. 去掉跳转到相邻基本块的跳转指令
   1. 在中端做基本块合并
4. move 目标寄存器和源寄存器相同 ok
5. 0 换成 $zero ok
   1. testfile5: ![img3.png](..\image\img3.png)
6. 已经在block内获取过的全局变量，不必再次获取   ==有好有坏，暂时保留==
   1. ![image-20231202223023899](https://s2.loli.net/2023/12/02/HZPQdjcLGUN9EwY.png)
7. 去掉不必要的$ra的保存和恢复 ok
   1. ![image-20231202230327428](https://s2.loli.net/2023/12/02/B76DSeiYkyMJLVt.png)

8. 函数参数等到用到的时候再获取 
   1. 在当前架构上想不到好方法

9. 去掉相邻的store和load


对栈指针的偏移和相关改动写的好蠢
在函数调用时需要修改sp，在函数体内开始和返回处需要修改sp

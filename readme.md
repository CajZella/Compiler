# 编译设计文档

**21373032 程钱佳**

## 编译过程简介

编译过程一般分为5个阶段：

![image-20231019120715214](C:\Users\Michelle\AppData\Roaming\Typora\typora-user-images\image-20231019120715214.png)

1. 词法分析：识别单词及其类别，在本实验中主要分为无意义字符（包括空白字符、注释）、单分界符、双分界符、无符号整数、标识符、字符串常量。
2. 语法分析：根据语法规则识别语法成分，采用递归下降分析，需要消除左递归，通过向前看避免回溯。
3. 语义分析、生成中间代码：基于语法分析得出的抽象语法树进行语义分析，构造符号表，进行错误处理，生成 LLVM IR 中间代码便于优化处理。
4. 代码优化：利用LLVM IR的value-user依赖关系，完成了mem2reg、死代码删除、函数内联、公共子表达式删除等机器无关优化。
5. 生成目标程序：将LLVM IR翻译成与mips相关的LIR，然后进行寄存器分配。

## 参考编译器

### clang + LLVM

clang是将C语言转换成LLVM的前端。前端负责分析源代码，检查错误和把输入代码转换成**抽象语法树（Abstract Syntax Tree，AST）**。通过抽象语法树这种结构化表示，可以实现符号表处理、类型检查以及生成代码，因此AST是前端关注的重点之一。

在clang中AST主要分为declaration、statement和type三类，但是它们并没有继承公共基类，每种节点都有特有的访问方式。我认为设计者主要考虑到这三类没有足够的共性，复杂的层次继承反而会降低AST设计的灵活性和可扩展性。

下为clang的流程图：

![image-20231023153323688](C:\Users\Michelle\AppData\Roaming\Typora\typora-user-images\image-20231023153323688.png)

LLVM IR是一种基于静态单赋值（Static Single Assignment，SSA）的中间表示，我主要参考了LLVM IR的核心类，位于 `include/llvm/IR` 目录下：

![image-20231023155905367](C:\Users\Michelle\AppData\Roaming\Typora\typora-user-images\image-20231023155905367.png)

![image-20231023165204200](C:\Users\Michelle\AppData\Roaming\Typora\typora-user-images\image-20231023165204200.png)

### compiler2022-meowcompiler

另外参考的是2022年参加编译比赛的一支队伍的compiler，架构如下：

![image-20231023163209515](C:\Users\Michelle\AppData\Roaming\Typora\typora-user-images\image-20231023163209515.png)

```shell
├─arg
├─backend # 生成目标代码arm
├─descriptor
├─exception
├─frontend
│  ├─lexer # 词法分析
│  ├─semantic # 语义分析工具（包括符号表等）
│  │  └─symbol
│  ├─visitor.java # 语义分析、生成中间代码
│  └─syntax # 语法分析
├─lir # low IR 进行与机器相关优化
├─manage
├─midend # 中端优化
├─mir # LLVM IR 架构
│  └─type
└─util # 工具
```

## 编译器架构

![image-20231212221514742](https://s2.loli.net/2023/12/12/NskxOL86KAdjzv7.png)

![image-20231212222246473](https://s2.loli.net/2023/12/12/1sFehoJrwZNBASk.png)

## 词法分析

### 编码前的设计

词法分析阶段负责将源代码字符串分解成token。

词法分析一般有3种方式：

1. 有限状态自动机（Finite State Machine，FSM）：
2. 正则表达式：根据不同token的模式（关键字、标识符、运算符、数字）进行匹配从而识别token
3. 使用专门的词法分析工具：如Lex、ANTLR。

考虑到本课程实验采用Sysy语言，采用有限状态自动机实现。首先判断是不是注释，过滤掉注释后，先判断双分界符，再判断单分界符。

词法分析token是存入数据结构还是提供接口给语法分析，随取随用？=>后者

整体词法分析的思路同上，在编码细节处理上做了一些**修改**：
1. 为输入输出流写了一个单例模式进行封装，方便后续的读写，输入的时候采用按行读取，pos记录位置。
2. 将过滤注释和空白字符作为一个单独的方法，将有限状态自动机部分分成`ignoreBlank`和`nextToken`两个方法，对方法职责进行划分。
3. `token`分为双分界符、单分界符、无符号整数、标识符、字符串常量几类，首先进行正则表达式匹配将token划分到相应的类别，再做具体的判断。

### 编码完成后的修改

1. "\s"表示空字符可用于正则表达式匹配，但' '=='\s'是false。
2. formalString 的双引号内容需要进行非贪婪匹配，即 `"\".*\"` -> `"\".*?\"`

## 语法分析

### 编码前的设计

#### 1. 改写文法

带回溯的自顶向下分析方法，试图构造最左推导序列，然而不能处理左递归文法，同时回溯会影响效率。

对课程组提供的SysY文法进行改写，**取消左递归**，以 `AddExp` 推导为例：

```
addExp -> addExp ("+" | "-") mulExp | mulExp
改为：
addExp -> mulExp (("+" | "-") mulExp)*
```

同时语法成分输出也要做相应处理。

为了**减少回溯**，我们需要尽量保证每个非终结符的First集合不相交，这样就可以通过判断First集合来确定使用哪个产生式。然而观察文法，存在First集合相交的情况，这时需要观察相应文法的特殊性，往后继续取标志性单词观察。

#### 2. 构造语法树节点

一开始我仅用一个 `AstNode` 类存储所有语法成分，即：

```java
public class AstNode {
    private AstNodeType type; // 语法树节点类型
    private ArrayList<AstNode> nodes; // 该节点包含的子节点
    // ...
}
```

尽管在语法分析作业中这样写并没有问题，然而所有语法树节点以同一方式存储，AstNode的子节点都视为相同的元素，会丢失语法分析的一部分结果，例如：

```
语句 Stmt → LVal '=' Exp ';'
| [Exp] ';'
| Block
| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
| 'for' '(' [ForStmt] ';' [Cond] ';' [forStmt] ')' Stmt
| 'break' ';' | 'continue' ';'
| 'return' [Exp] ';'
| LVal '=' 'getint''('')'';'
| 'printf''('FormatString{','Exp}')'';'
```

对于 `stmt` 节点，在获取子节点元素时，我们就需要手写判断语句类型，靠文法计算需要获取的子节点的下标，有时候还需要向前查看（遇到可省略语法成分时），几乎相当于又做了一遍语法分析。

因此，为了最大化保留语法分析成果，为每种语法成分建一个类，stmt也细分，每个类里个性化存储子节点，例如对于 `stmt` 中的 `for` 语句：

```java
public class StmtFor extends Stmt {
	private ForStmt forStmt1 = null;
    private Cond cond = null;
    private  ForStmt forStmt2 = null;
    private Stmt stmt;
}
```

#### 3. TokenManager - 将语法分析和与lexer交互解耦

将token的相关操作单独形成一类，与语法分析解耦，符合单一职责原则。另外，在我的设计中词法分析和语法分析一起是一遍，所以在语法分析中实现 `TokenManager` 类来进行token管理，包括控制Lexer读取token、向前查看、获取下一个token。

#### 4. 递归下降进行语法分析

语法分析采用递归下降法，为每个非终结符构造分析函数，用向前看符号指导产生式规则。例如，对于`CompUnit`：

```java
/*
* CompUnit -> (Decl | FuncDef)* MainFuncDef
*/
public CompUnit parseCompUnit() throws ParserException {
    /* Decl */
    CompUnit compUnit = new CompUnit();
    while(!tokenManager.checkTokenType(2,WordType.LPARENT)) {
        compUnit.addDecl(parseDecl());
    }
    /* FuncDef */
    while(!tokenManager.checkTokenType(1, WordType.MAINTK)) {
        compUnit.addFuncDef(parseFuncDef());
    }
    /* MainFuncDef */
    if (tokenManager.checkTokenType(1, WordType.MAINTK)) {
        compUnit.addFuncDef(parseMainFuncDef());
    }
    LexParseLog.add(compUnit.toString());
    return compUnit;
}
```

### 编码完成后的修改

如上所述，为了最大化保留语法分析成果，为每种语法成分建一个类，stmt也细分，每个类里个性化存储子节点。

## 语义分析、错误处理

### 编码前的设计

#### 符号表

SysY是分程序结构的语言，在符号表上需执行**插入**、**查表**、**定位**和**重定位**操作

- 查表是在程序的可执行语句部分读到标识符时，需要判断该标识符是否已经声明：当前所在程序单元 -> **直接外层**符号表
- 定位是在分程序的入口建立一个新的子表
- 重定位是在分程序的出口删除已处理完的分程序的标识符的子表
- 定位与重定位操作的工作方式和栈的压入和弹出操作很像。

1. 符号表存储的信息：

   | ident        | 标识符                                           |
   | ------------ | ------------------------------------------------ |
   | isConst      | 是否用const限定词修饰                            |
   | isGlobal     | 是否为全局变量                                   |
   | type         | 此处直接处理成LLVM IR中的type，用于类型检查      |
   | line         | 符号所在行                                       |
   | constantInit | 常量初值（若有），此处直接处理成LLVM中的Constant |
   | irPtr        | 用于记录IR中符号地址（生成代码时用到）           |

3. 符号表的组织方式

   - 为了便于查找Symbol，我使用`HashMap<String, Symbol>`存储
   - 为了支持符号表向外层查找操作，需要记录符号表的父表 `parent`
   
4. 符号表的操作

   - 定位

     在进入分程序的入口时，创建一个新的子表，将其`parent`设置为当前符号表，将当前符号表设置为新的子表。（初始表的`parent`是`null`）
     ```java
       public void locate() {
           currentTable = new SymbolTable(currentTable);
       }
     ```

   2. 查表分为声明时的查表和使用时的查表
      1. 声明时仅需查找**当前层符号表**
      2. 使用时需**递归查找**当前层及直接外层符号表
   3. 插入
      
      将Symbol插入当前层符号表
   4. 重定位
      
      在分程序的出口时，将当前符号表设置为其`parent`。
      ```java
        public void relocate() {
            currentTable = currentTable.parent;
        }
      ```

#### 语义分析

在**语义分析阶段**，建立符号表以供查询符号表相关错误。建立一张单例模式的错误信息表`errorLog`。为了方便调用语法树各节点内方法，将`checkSema` 方法分散到语法树节点类实现。

##### 非法符号a

在词法分析的过程中，如果创建的 `token` 是 `STRCON` 类型，通过正则表达式匹配是否存在非法字符，以及 `\` 后不是 `d` 或 `n` 的情况。

##### 符号表相关错误b, c, d, e, h

| 错误类别码 | 错误类型                                 | 解决思路                                |
| ---------- | ---------------------------------------- | --------------------------------------- |
| b          | 名字重定义（函数名和变量名重定义）       | 在当前作用域内查找                      |
| c          | 可执行语句使用未定义的名字               | 不断往直接外层查找                      |
| d          | 函数参数个数不匹配                       | Symbol记录的FunctionType的ArgumentTypes |
| e          | 函数参数类型不匹配                       | Symbol记录的FunctionType的ArgumentTypes |
| h          | 不能改变常量的值                         | Symbol记录的isConst                     |
| f          | 无返回值的函数存在不匹配的return语句     | 在`FuncDef` 内进行处理                  |
| g          | 有返回值的函数缺少return语句（函数末尾） |                                         |

在语法分析阶段结束后，处理出符号表。为了处理出常量，如数组维度、全局变量初值、constDecl初值，需要为所有表达式及元素实现 `getOpResult` 方法，以及在节点与子节点之间需要传递 `isGlobal` 等信息。

##### 缺少符号 i, j, k

| 错误类别码 | 错误类型     |
| ---------- | ------------ |
| i          | 缺少分号     |
| j          | 缺少右小括号 |
| k          | 缺少又中括号 |

此处的难点是在语法分析的过程中有向前看的操作，若丢失关键符号，会导致进入分支错误。缺少右小括号不必担心这种情况，只需在需要右小括号但不存在的时候产生错误信息并新建一个分号token放入语法树。`tokenManager.getNextToken()` 方法调用时要和所需要的类型进行匹配，否则报错。下面以缺少分号为例：

```
stmt -> 'return' [exp] ';'
stmt -> Lval '=' 'getint' '(' ')' ';' | Lval '=' Exp ';' | [Exp] ';'
```

在正常情况下，第一句中分号判断是否有exp；第二句中如果在分号前出现了等号则stmt为前两种情况。那么，

1. 在依靠分号进行分支选择的时候，先假设程序正确来分析，保存当前状态；如果进入的分支无法匹配文法，抛出异常；在原分支选择处接收异常，并进行回溯。
2. 为了能完成回溯动作，`tokenManager`新增`backupBuffer` 等备份属性，在尝试匹配的时候打开`isBackup`开关，进行备份。
3. 在我的架构中，在存语法树时，我已经舍弃了一些非必要的token，例如分号、括号等，故我还需要补充一些行号记录。

##### 其他错误 f, g, l, m

| 错误类别码 | 错误类型                                 | 解决思路                                                     |
| ---------- | ---------------------------------------- | ------------------------------------------------------------ |
| f          | 无返回值的函数存在不匹配的return语句     | 从 `FuncDef` 不断往下传 `funcType`，遇到 `return` 语句就和 `funcType` 比对。 |
| g          | 有返回值的函数缺少return语句（函数末尾） | 判断 `block` 的最后一个 `blockIterm` 是否为 `return` 语句。  |
| l          | printf中格式字符与表达式个数不匹配       | 仅需判断`stmtPrintf.getExps().getSize` 是否等于`formatString`中 `%d` 的个数。 |
| m          | 在非循环块中使用break和continue语句      | `for` 设置其 `stmt` 循环块内的 `isInLoop` 均为 `true`。      |

### 编码完成后的修改

语义分析阶段生成的符号表在生成中间代码时需要复用，需要支持从当前符号表依次遍历子符号表，故记录子符号表数组 `childTables`，以及当前遍历的子符号表下标 `index` ，方便程序出口退出子符号表返回父符号表时，获取下一子符号表。

![image-20231023165816922](https://s2.loli.net/2023/12/12/Y9XDaqAZvzxUJd5.png)

## 生成中间代码LLVM IR

### 编码前设计

#### LLVM IR

>参考资料：
>
>[LLVM Programmer Manual](https://llvm.org/docs/ProgrammersManual.html#the-core-llvm-class-hierarchy-reference)
>
>[LLVM release2.0](https://github.com/llvm/llvm-project/tree/release/2.0.x)
>
>[LLVM Lang Ref](https://llvm.org/docs/LangRef.html)

本部分主要通过学习LLVM相关文档和源码实现中间代码。

LLVM IR采用静态单赋值（SSA）形式，类似三地址码，每个值只有一个定义，有助于实现def-use链，有助于后续编译器进行数据流分析和各种优化。同时，LLVM IR实现存储中间代码的结构功能。以下主要关注指针类型，数组对象类型，value、user、use设计。

![image-20231210201059679](https://s2.loli.net/2023/12/10/SX45WoAmkIZYueG.png)

##### 指针类型

给定任何一个Type，都有一个对应的指针类型 Pointer to Type。（没有二维指针的概念）

![image-20231210211142899](https://s2.loli.net/2023/12/10/TrKN4tDu89OmZU5.png)

##### 数组类型ArrayType和数组常量ArrayConstant

数组类型需要记录元素类型 T 和元素个数 N ：[N x T]，要从一维的角度理解。

数组常量的实现需要方便后续语义处理根据下标获取底层元素：

```java
public ConstantInt getBase(int...idxs) {
        Constant constant = this;
        for (int idx : idxs) {
            constant = ((ConstantArray)constant).getVals().get(idx);
        }
        return (ConstantInt)constant;
    }
```

##### value、user、use

记录每个user使用了哪些value，每个value被哪些user使用，其中use类记录每一个user-value的关系。

当某条指令instr 使用了某个value时，需要维护use-value关系，具体的：

```java
public void use(Value value) {
        Use use = new Use(this, value); // 创建use记录该use-value关系
        this.operands.add(use); // this user value
        value.addUser(use); // value used by this
    }
```

### 生成中间代码

采用递归下降子程序分析法，自顶向下对语法成分进行分析，并生成相应的指令，此处重用了语义分析阶段生成的符号表。

##### 重用符号表的设计

在符号表设计部分提到，为了重用符号表的设计，在符号表中记录了子符号表列表以及已遍历到的子符号表下标（即在当前scope中正在处理的子scope下标）。在语义分析阶段，

1. 声明变量，在当前scope查找标识符对应的 `symbol` ，将其属性 `irPtr` 设为声明该变量的指令 `alloca`。
2. 使用变量，在当前scope查找标识符对应的 `symbol`，若找到 `symbol` 但其属性 `irPtr` 为空或未找到 `symbol`，说明在当前状态该变量未声明，继续向外查找符号表。

##### 静态单赋值(Static Single Assignment，SSA)

SSA是一种变量的命名约定，要求程序中每个变量都有且只有一个赋值语句，方便检测可达性优化等。

为了实现SSA，在生成中间代码过程中，我通过alloca， load，store来完成对象的声明和读写（然而内存读写是非常耗时间的，后续会在代码优化中通过alloca+mem2reg技术优化生成phi指令）。这就需要符号表中记录的 `irPtr` 来给出变量地址。

##### 左值LVal的中间代码生成

一共考虑下列情形：

1. 是获取LVal的地址还是获取LVal的值

   ```
   Stmt -> LVal '=' Exp ';'
   Stmt -> LVal '=' 'getint' '(' ')' ';'
   ForStmt -> LVal '=' Exp
   PrimaryExp -> LVal
   // 注意到，前3种情况是获取LVal的地址，后一种是获取LVal的值。
   ```

2. 变量类型是否为指针的指针

   只有当函数形参为数组指针时，函数体内用alloca保存数组指针会出现指向指针的指针。此时需要先load。

3. 如果标识符类型为数组类型，由getelementptr的第一个索引是基于整个数组的偏移，补充第一个索引0即可。
4. 如果想获取指针类型且标识符类型为数组类型，getelementptr补充索引0。

##### 循环

```
for(Initial; Cond; Step) { Body }
```

For循环的关键在于理清循环语句的执行步骤，从而创建基本块处理跳转。同时，在遇到break，continue时需要知道跳转到哪个基本块，故维护了 `loopStack` 用于记录在当前循环中，break和continue跳转的基本块。

![image-20231212202845157](https://s2.loli.net/2023/12/12/z8FoBYxt7h4qRwd.png)

break：跳出当前循环，进入merge_block

continue：跳过本轮循环后续语句，跳转到step_block

```java
void visitStmtFor(StmtFor stmtFor) {
	create condBB, bodyBB, stepBB, mergeBB
	loopStack.add(mergeBB);
	loopStack.add(stepBB);
	complete initial;
	complete cond
	complete body // 此处会遇到break和continue，break则获取次栈顶，continue获取栈顶
	loopStack.pop();
	loopStack.pop();
	complete stepBB
}
```

##### 短路求值

短路求值是或运算中若某个子表达式值为1，则后续子表达式不必计算，整个表达式值为1；与运算中若某个子表达式值为0，则后续子表达式不必计算，整个表达式值为0。由于一些表达式计算存在副作用，所以我们必须实现短路求值。

![image-20231211114211244](https://s2.loli.net/2023/12/11/f4L6ASrNobI7YO9.png)

以生成LorExp的中间代码为例，正如教程图示，在递归处理完当前LAndExp后，若为true，则跳转到trueBB，否则跳转到新生成的nextBB继续处理下一个LAndExp。

```java
private Value visitLOrExp(LOrExp lOrExp, BasicBlock trueBB, BasicBlock falseBB) { 
    Value value;
    BasicBlock nextBB;
    for (int i = 0; i < lOrExp.size() - 1; i += 2) {
        nextBB = new BasicBlock(curFunc);
        value = visitLAndExp((LAndExp) lOrExp.get(i), nextBB);
        curBB.addInstr(new Br(curBB, value, trueBB, nextBB));
        curBB = nextBB;
    }
    value = visitLAndExp((LAndExp) lOrExp.get(lOrExp.size() - 1), falseBB);
    return value;
}
```

### 编码后修改

1. 变量的声明使用可能有一种情况：

   ```c
   int a = 1;
   {
       a = 5; // 虽然该层scope包含a，但此时a还未声明，此处使用的是外层的a
       int a;
   }
   ```

   因此，需要判断 `symbol` 的 `irPtr` 属性是否为空来判断当前状态变量是否声明。

2. 返回值为void的函数最后不一定有`return;` ，然而生成中间代码时只有遇到`return` 才会生成 `ret` 指令，于是在语义分析阶段补充了`return`。

## MIPS目标代码生成

### 编码前设计

目标代码生成关注指令选择、生成全局数据段和代码段、寄存器分配等。本设计构造**LIR架构**存储目标代码并用于机器相关优化。采用**递归下降子程序分析法**生成目标代码，在该阶段使用虚拟寄存器，对于特定用途的寄存器使用直接分配了物理寄存器；维护栈空间；函数跳转前后保存和恢复现场等。在为虚拟寄存器分配物理寄存器时，我暂时先采用**FIFO寄存器分配**，同时预留了图着色寄存器分配的优化。

#### LIR架构

贴合mips特征的IR架构，与中端相比省略了value-use的维护，增加了def-use的维护，用于活跃变量分析。

**使用的mips指令集**：

```
addu,
addiu,
and, // rd = rs and rt
andi,
or,
ori,
subu,
xor,
mul,
mult,
div,
mfhi,
sll,
sra, // 算术右移
seq,
sne,
slt,
slti,
sle,
sgt,
sge,
lw,
sw,
beq, // if(rs=rt) then PC+offset
beqz,
bge, // if(rs>=0) then PC+offset
bgt, // if(rs>0)
ble, // if(rs<=0)
blt,
bne, // if (rs!=rt)
j,
jal,
jr,
syscall,
li,
la,
move
```

**架构图**：

![image-20231212211229679](https://s2.loli.net/2023/12/12/t3AGLflxTW8wopn.png)

#### 目标代码生成

这一步根据LLVM IR生成包含虚拟寄存器的目标代码，需要记录LLVM IR到LIR各数据的映射，方便查找，包括：

```
f2mf: irFunction到mipsFunction的映射
bb2mb: irBasicBlock到mipsBasicBloc的映射
val2opd: irValue到mipsOperand的映射, mipsOperand包括reg，mipsImm，mipsStackOffset
gv2md: irGlobalVariable到mipsData的映射
```

根据irValue查找目标代码中的操作数时，有3种情形：

1. 常数 -> 生成立即数
2. 查找 `val2opd` -> 存在即为查找结果；若不存在，生成虚拟寄存器放入 `val2opd` ，并返回（之所以可能不存在是因为翻译LLVM IR的顺序中不一定先声明再使用，可联想到function和block也如此，需要提前声明好）

在翻译指令时，`alloca`指令变为生成 `MipsStackOffset` 并放入 `val2opd`；`gep` 指令处理 `base` 和 `offset` 生成 `MipsStackOffset` 放入 `val2opd` ，算术指令若两个操作数均为立即数，直接计算生成立即数结果。在这些过程中，若需要中间寄存器则尽可能使用 `$v0` （本设计中 `$v0`不参与后续图着色寄存器分配）。接下来关注目标代码生成中更具挑战性的函数声明和函数调用，两者均涉及栈空间维护，同时具体操作顺序也是值得思考的问题。

##### 函数声明

此处指非内建函数声明，内建函数调用直接内联，不真正调用函数。

1. 计算函数所需栈空间，生成 `addiu $sp, $sp, -offset` 为函数开栈空间
2. 保存函数返回地址 `$ra` ，因为函数内调用函数会覆盖 `$ra`
3. 获取函数形参，前4个形参从寄存器 `$a0-$a3` 获取并 `move`（`move`是因为这四个寄存器也会参与后续寄存器分配），后四个从栈空间加载，此处需要偏移需要加上 `curStackOffset`（因为这是函数调用前的保存）。
4. 生成mips指令

##### 函数调用

1. 保存现场，将为函数分配的寄存器值保存到栈空间中
2. 将函数实参加载到寄存器和栈空间中（注意栈空间方向）
3. 更新栈空间偏移
4. 生成跳转指令
5. 若函数存在返回值，生成 `move` 指令将 `$v0`的值存放到虚拟寄存器中（因为在本设计中 `$v0·` 被认为是一个临时寄存器）
6. 恢复现场和栈空间偏移

#### FIFO寄存器分配（跨函数寄存器分配）

在初版目标代码生成中，我先尝试了FIFO寄存器分配用于调试 LIR 和 codeGen。

在为虚拟寄存器分配物理寄存器的过程中，如果虚拟寄存器是使用，要么分配了物理寄存器，要么在栈中 `load` 即可；如果虚拟寄存器是定义，按先进先出的原则分配物理寄存器。以下是FIFO寄存器分配的伪代码：

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

### 编码后修改

1. 指令中如果使用寄存器，统一存储到抽象类 `MipsInstr` 中方便def-use维护。

2. 观察函数调用执行流程：

   ![image-20231212220351367](https://s2.loli.net/2023/12/12/rBJaFXPs7iVUmZI.png)

​		在函数跳转前保存的实参 `mipsStackOff` 不等于跳转后获取函数参数时刻的 `mipsStackOff`，因为发生了开辟函数所需栈空间，应该更新偏移，本设计的做法是将获取大于4的函数参数的 `load` 指令标记为`SPreference` ，表示 `load` 偏移需要加上函数栈空间大小。

3. 后续将FIFO寄存器分配改为图着色寄存器分配，效率大大提升。

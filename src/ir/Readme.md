# LLVM IR
> 参考资料：
> 
> [BUAA实验教程](https://buaa-se-compiling.github.io/)
> 
> [LLVM Lang Ref](https://llvm.org/docs/LangRef.html)
> 
> [LLVM Programmer Manual](https://llvm.org/docs/ProgrammersManual.html#the-core-llvm-class-hierarchy-reference)

## 结构
1. `model`: 本实验仅涉及但文件编译，单model
2. `model`由`function`和`global variable`组成
3. `function define`至少有一个`basicblock`
4. `basicblock`由多条`instruction`组成，内部必须顺序执行（原子性），都以`terminator instruction`结尾（如`br`, `ret`），实现控制流
5. `instruction`指的是非分支指令，`call`也是非分支指令

## 类型系统
1. void type
2. integer type: i32
3. label type: 代码标签
4. **array type**
5. **pointer type**

## 静态单赋值（Static Single Assignment，SSA）
SSA是一种变量的命名约定，要求程序中每个变量都有且只有一个赋值语句，方便检测可达性优化等。

副作用解决方案：
1. 在中间代码生成时使用phi形式的SSA
2. alloca + memtoreg技术

## 核心类
### Type
抽象类，具体Type类继承Type，Type不能被实例化，在任意时刻具体Type类只能有一个（单例模式）。
# LLVM IR
> 参考资料：
> 
> [BUAA实验教程](https://buaa-se-compiling.github.io/)
> 
> [LLVM Lang Ref](https://llvm.org/docs/LangRef.html)
> 
> [LLVM Programmer Manual](https://llvm.org/docs/ProgrammersManual.html#the-core-llvm-class-hierarchy-reference)

## 结构
1. `model`: 本实验仅涉及单文件编译，单model
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
2. **alloca + memtoreg技术**

## LLVM IR 实例

*通过运行一些测试样例总结的规则*

### 1. 全局变量和局部变量处理比较

```C
const int N = 100;
const int A = 25, B = 1, C = 8;
int x = 2, y = 3;
const int b[B+5] = {(A-B)/C, B, C};
int a[N] = {2, 3};
int main() {
    int x = 4, z = 3;
    int a[5] = {x, z};
}
```

```LLVM
@N = dso_local constant i32 100, align 4
@A = dso_local constant i32 25, align 4
@B = dso_local constant i32 1, align 4
@C = dso_local constant i32 8, align 4
@x = dso_local global i32 2, align 4
@y = dso_local global i32 3, align 4
@b = dso_local constant [6 x i32] [i32 3, i32 1, i32 8, i32 0, i32 0, i32 0], align 16
@a = dso_local global <{ i32, i32, [98 x i32] }> <{ i32 2, i32 3, [98 x i32] zeroinitializer }>, align 16

define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca [5 x i32], align 16
  store i32 0, i32* %1, align 4
  store i32 4, i32* %2, align 4
  store i32 3, i32* %3, align 4
  %5 = getelementptr inbounds [5 x i32], [5 x i32]* %4, i64 0, i64 0
  %6 = load i32, i32* %2, align 4
  store i32 %6, i32* %5, align 4
  %7 = getelementptr inbounds i32, i32* %5, i64 1
  %8 = load i32, i32* %3, align 4
  store i32 %8, i32* %7, align 4
  %9 = getelementptr inbounds i32, i32* %7, i64 1
  %10 = getelementptr inbounds i32, i32* %5, i64 5
  br label %11

11:                                               ; preds = %11, %0
  %12 = phi i32* [ %9, %0 ], [ %13, %11 ]
  store i32 0, i32* %12, align 4
  %13 = getelementptr inbounds i32, i32* %12, i64 1
  %14 = icmp eq i32* %13, %10
  br i1 %14, label %15, label %11

15:                                               ; preds = %11
  %16 = load i32, i32* %1, align 4
  ret i32 %16
}
```

SysY语言要求数组定义维度必须是常量表达式，全局变量声明中指定的初值必须是常量表达式。**在全局变量中涉及的常量表达式都会被显式计算**，<u>目前我需要在常量表达式相关语法树节点新增 `getOpResult` 方法，并在符号表中正确记录数组维度信息。</u>这就又延伸出了一个问题：在计算常量结果的时候，我们会遍历到`LVal->Ident ('[' Exp ']')*` ，所以在`symbol`中我添加一个属性 `Initializer` 。

此处在局部数组变量的初始化中用到了`phi`指令。

## 核心类
### Type
抽象类，具体Type类继承Type，Type不能被实例化，在任意时刻具体Type类只能有一个（单例模式）。
globalValue是为了module内全局变量的内部或外部链接，本编译器仅支持单模块，故不需要。
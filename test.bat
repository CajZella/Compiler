@echo off
setlocal enabledelayedexpansion

REM 设置循环次数
set maxLoops=30

REM 设置源目录和目标目录
set sourceDir=F:\Compiler\2022test
set targetDir=F:\Compiler

REM 开始循环
for /l %%i in (1, 1, %maxLoops%) do (
    REM 复制testfile到src目录下
    copy "%sourceDir%\testfiles-only\A\testfile%%i.txt" "%targetDir%\testfile.txt"

    REM 执行jar文件
    java -jar Compiler.jar 

    REM 比较输出结果
    fc "%sourceDir%\Syntax analysis\A\output%%i.txt" "%targetDir%\output.txt"

    REM 输出比较结果
    if !errorlevel! equ 0 (
        echo Iteration %%i: Files are identical
    ) else (
        echo Iteration %%i: Files are different
    )
)

pause

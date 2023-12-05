@echo off
setlocal enabledelayedexpansion

REM 设置循环次数

REM 设置源目录和目标目录
set sourceDir=F:\Compiler\2022test\full
set targetDir=F:\Compiler\mipsTest

echo > log.txt
REM 开始循环
for /l %%i in (1, 1, 30) do (
    REM 复制testfile到src目录下
    copy "%sourceDir%\C\testfile%%i.txt" "%targetDir%\testfile.txt"
    copy "%sourceDir%\C\input%%i.txt" "%targetDir%\input.txt"
    copy "%sourceDir%\C\output%%i.txt" "%targetDir%\stdans.txt"
    echo Iteration %%i: >> log.txt
    REM 执行jar文件
    java -jar Compiler.jar

    java -jar mars.jar nc mips.txt < input.txt > ans.txt
    echo >> log.txt
    REM 比较输出结果
    fc "%targetDir%\ans.txt" "%targetDir%\stdans.txt"

    REM 输出比较结果
    if !errorlevel! equ 0 (
        echo Iteration %%i: Files are identical
    ) else (
        echo Iteration %%i: Files are different
    )
)
for /l %%i in (1, 1, 30) do (
    REM 复制testfile到src目录下
    copy "%sourceDir%\B\testfile%%i.txt" "%targetDir%\testfile.txt"
    copy "%sourceDir%\B\input%%i.txt" "%targetDir%\input.txt"
    copy "%sourceDir%\B\output%%i.txt" "%targetDir%\stdans.txt"
    echo Iteration %%i: >> log.txt
    REM 执行jar文件
    java -jar Compiler.jar

    java -jar mars.jar nc mips.txt < input.txt > ans.txt
    echo >> log.txt
    REM 比较输出结果
    fc "%targetDir%\ans.txt" "%targetDir%\stdans.txt"

    REM 输出比较结果
    if !errorlevel! equ 0 (
        echo Iteration %%i: Files are identical
    ) else (
        echo Iteration %%i: Files are different
    )
)
for /l %%i in (1, 1, 30) do (
    REM 复制testfile到src目录下
    copy "%sourceDir%\A\testfile%%i.txt" "%targetDir%\testfile.txt"
    copy "%sourceDir%\A\input%%i.txt" "%targetDir%\input.txt"
    copy "%sourceDir%\A\output%%i.txt" "%targetDir%\stdans.txt"
    echo Iteration %%i: >> log.txt
    REM 执行jar文件
    java -jar Compiler.jar

    java -jar mars.jar nc mips.txt < input.txt > ans.txt
    echo >> log.txt
    REM 比较输出结果
    fc "%targetDir%\ans.txt" "%targetDir%\stdans.txt"

    REM 输出比较结果
    if !errorlevel! equ 0 (
        echo Iteration %%i: Files are identical
    ) else (
        echo Iteration %%i: Files are different
    )
)
pause
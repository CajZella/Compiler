#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <cstring>
int main() {
    const char* path = "../2022test/full";
    const char* test_file = "testfile.txt";
    const char* error_file = "error.txt";
    const char* llvm_file = "llvm.txt";
    const char* log_file = "log.txt";
    const char* ans_file = "ans.txt";
    char ch = 'B';
    char command[200];
    int a[3] = {30, 30, 30};
    sprintf(command, "echo -n > %s", log_file);
    system(command);
    for (int i = 0; i < 2; i++)
    {
        char path1[100];
        sprintf(path1, "%s/%c", path, ch);
        for (int j = 1; j <= a[i]; j++) {
            sprintf(command, "cp %s/testfile%d.txt %s", path1, j, test_file);
            system(command);
            sprintf(command, "echo %s/testfile%d.txt >> %s", path1, j, log_file);
            system(command);
            sprintf(command, "java -jar Compiler.jar 2>>%s", log_file);
            system(command);
            sprintf(command, "llvm-link-10 llvm_ir.txt lib.ll -S -o out.ll 2>>%s", log_file);
            system(command);
            sprintf(command, "lli-10 out.ll < %s/input%d.txt > %s", path1, j, ans_file);
            system(command);
            sprintf(command, "diff -ZB %s/output%d.txt %s >> %s", path1, j, ans_file, log_file);
            system(command);
        }
        ch--;
    }
    return 0;
}
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
    char ch = 'A';
    char command[200];
    sprintf(command, "echo -n > %s", log_file);
    system(command);
    for (int i = 0; i < 3; i++) {
        char path1[100];
        sprintf(path1, "%s/%c", path, ch);
        for (int j = 1; j <= 30; j++) {
            sprintf(command, "cp %s/testfile%d.txt %s", path1, j, test_file);
            system(command);
            sprintf(command, "echo %s/testfile%d.txt >> %s", path1, j, log_file);
            system(command);
            sprintf(command, "java -jar Compiler.jar 2>>%s", log_file);
            system(command);
            sprintf(command, "llvm-link-10 llvm.txt lib.ll -S -o out.ll 2>>%s", log_file);
            system(command);
            sprintf(command, "lli-10 out.ll < %s/input%d.txt > %s", path1, j, ans_file);
            system(command);
            sprintf(command, "fc %s/output%d.txt %s >> %s", path1, j, ans_file, log_file);
        }
        ch++;
    }
    return 0;
}
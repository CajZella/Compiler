; ModuleID = 'llvm-link'
source_filename = "llvm-link"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@Mod = dso_local constant i32 389
@N = dso_local constant i32 100005
@a_to_the_a = dso_local global i32 0
@cnt = dso_local global i32 0
@n = dso_local global i32 0
@str.0 = private unnamed_addr constant [26 x i8] c"funcTest: move disk from \00"
@str.1 = private unnamed_addr constant [5 x i8] c" to \00"
@str.2 = private unnamed_addr constant [10 x i8] c"success1\0A\00"
@str.3 = private unnamed_addr constant [17 x i8] c"blockTest: 7 == \00"
@str.4 = private unnamed_addr constant [8 x i8] c", 8 == \00"
@str.5 = private unnamed_addr constant [17 x i8] c"blockTest: 5 == \00"
@str.6 = private unnamed_addr constant [9 x i8] c", 12 == \00"
@str.7 = private unnamed_addr constant [10 x i8] c"Exptest: \00"
@str.8 = private unnamed_addr constant [21 x i8] c"20373569 the mafia~\0A\00"
@str.9 = private unnamed_addr constant [10 x i8] c"Exptest: \00"
@.str = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.1 = private unnamed_addr constant [3 x i8] c"%c\00", align 1
@.str.2 = private unnamed_addr constant [4 x i8] c"%d:\00", align 1
@.str.3 = private unnamed_addr constant [4 x i8] c" %d\00", align 1
@.str.4 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5 = private unnamed_addr constant [3 x i8] c"%s\00", align 1

define dso_local void @move(i32 %ar1, i32 %ar3) {
move_b0:
  %i2 = alloca i32
  store i32 %ar1, i32* %i2
  %i4 = alloca i32
  store i32 %ar3, i32* %i4
  %i5 = load i32, i32* @cnt
  %i6 = add i32 %i5, 1
  store i32 %i6, i32* @cnt
  %i9 = load i32, i32* @cnt
  %i10 = load i32, i32* @Mod
  %i11 = srem i32 %i9, %i10
  %i12 = icmp eq i32 %i11, 0
  br i1 %i12, label %move_b7, label %move_b8

move_b7:                                          ; preds = %move_b0
  %i13 = load i32, i32* %i2
  %i14 = load i32, i32* %i4
  %i15 = getelementptr [26 x i8], [26 x i8]* @str.0, i32 0, i32 0
  call void @putstr(i8* %i15)
  call void @putint(i32 %i13)
  %i16 = getelementptr [5 x i8], [5 x i8]* @str.1, i32 0, i32 0
  call void @putstr(i8* %i16)
  call void @putint(i32 %i14)
  call void @putch(i32 10)
  br label %move_b8

move_b8:                                          ; preds = %move_b7, %move_b0
  ret void
}

define dso_local void @hanoi(i32 %ar1, i32 %ar3, i32 %ar5, i32 %ar7) {
hanoi_b0:
  %i2 = alloca i32
  store i32 %ar1, i32* %i2
  %i4 = alloca i32
  store i32 %ar3, i32* %i4
  %i6 = alloca i32
  store i32 %ar5, i32* %i6
  %i8 = alloca i32
  store i32 %ar7, i32* %i8
  %i11 = load i32, i32* %i2
  %i12 = icmp eq i32 %i11, 1
  br i1 %i12, label %hanoi_b9, label %hanoi_b10

hanoi_b9:                                         ; preds = %hanoi_b0
  %i13 = load i32, i32* %i4
  %i14 = load i32, i32* %i8
  call void @move(i32 %i13, i32 %i14)
  %i15 = getelementptr [10 x i8], [10 x i8]* @str.2, i32 0, i32 0
  call void @putstr(i8* %i15)
  ret void

0:                                                ; No predecessors!
  br label %hanoi_b10

hanoi_b10:                                        ; preds = %0, %hanoi_b0
  %i16 = load i32, i32* %i2
  %i17 = sub i32 %i16, 1
  %i18 = load i32, i32* %i4
  %i19 = load i32, i32* %i8
  %i20 = load i32, i32* %i6
  call void @hanoi(i32 %i17, i32 %i18, i32 %i19, i32 %i20)
  %i21 = load i32, i32* %i4
  %i22 = load i32, i32* %i8
  call void @move(i32 %i21, i32 %i22)
  %i23 = load i32, i32* %i2
  %i24 = sub i32 %i23, 1
  %i25 = load i32, i32* %i6
  %i26 = load i32, i32* %i4
  %i27 = load i32, i32* %i8
  call void @hanoi(i32 %i24, i32 %i25, i32 %i26, i32 %i27)
  ret void
}

define dso_local i32 @qpow(i32 %ar1, i32 %ar3) {
qpow_b0:
  %i2 = alloca i32
  store i32 %ar1, i32* %i2
  %i4 = alloca i32
  store i32 %ar3, i32* %i4
  %i5 = alloca i32
  store i32 1, i32* %i5
  br label %qpow_b6

qpow_b6:                                          ; preds = %qpow_b19, %qpow_b0
  %i9 = load i32, i32* %i4
  %i10 = icmp ne i32 %i9, 0
  br i1 %i10, label %qpow_b7, label %qpow_b8

qpow_b7:                                          ; preds = %qpow_b6
  %i11 = load i32, i32* %i4
  %i12 = sdiv i32 %i11, 2
  store i32 %i12, i32* %i4
  %i13 = load i32, i32* %i2
  %i14 = load i32, i32* %i2
  %i15 = mul i32 %i13, %i14
  %i16 = load i32, i32* @Mod
  %i17 = srem i32 %i15, %i16
  store i32 %i17, i32* %i2
  %i20 = load i32, i32* %i4
  %i21 = srem i32 %i20, 2
  %i22 = icmp ne i32 %i21, 0
  br i1 %i22, label %qpow_b18, label %qpow_b19

qpow_b8:                                          ; preds = %qpow_b6
  %i28 = load i32, i32* %i5
  ret i32 %i28

qpow_b18:                                         ; preds = %qpow_b7
  %i23 = load i32, i32* %i5
  %i24 = load i32, i32* %i2
  %i25 = mul i32 %i23, %i24
  %i26 = load i32, i32* @Mod
  %i27 = srem i32 %i25, %i26
  store i32 %i27, i32* %i5
  br label %qpow_b19

qpow_b19:                                         ; preds = %qpow_b18, %qpow_b7
  br label %qpow_b6
}

define dso_local i32 @gcd(i32 %ar1, i32 %ar3) {
gcd_b0:
  %i2 = alloca i32
  store i32 %ar1, i32* %i2
  %i4 = alloca i32
  store i32 %ar3, i32* %i4
  %i7 = load i32, i32* %i4
  %i8 = icmp eq i32 %i7, 0
  br i1 %i8, label %gcd_b5, label %gcd_b6

gcd_b5:                                           ; preds = %gcd_b0
  %i9 = load i32, i32* %i2
  ret i32 %i9

0:                                                ; No predecessors!
  br label %gcd_b6

gcd_b6:                                           ; preds = %0, %gcd_b0
  %i10 = load i32, i32* %i4
  %i11 = load i32, i32* %i2
  %i12 = load i32, i32* %i4
  %i13 = srem i32 %i11, %i12
  %i14 = call i32 @gcd(i32 %i10, i32 %i13)
  ret i32 %i14
}

define dso_local i32 @testExp() {
testExp_b0:
  %i1 = load i32, i32* @n
  %i2 = load i32, i32* @n
  %i3 = mul i32 %i1, %i2
  store i32 %i3, i32* @a_to_the_a
  %i4 = alloca i32
  %i5 = load i32, i32* @N
  %i6 = load i32, i32* @n
  %i7 = sdiv i32 %i5, %i6
  store i32 %i7, i32* %i4
  %i8 = load i32, i32* @n
  %i9 = load i32, i32* @n
  %i10 = mul i32 %i8, %i9
  %i11 = load i32, i32* @n
  %i12 = sdiv i32 %i10, %i11
  %i13 = load i32, i32* @n
  %i14 = add i32 %i12, %i13
  %i15 = load i32, i32* @n
  %i16 = sub i32 %i14, %i15
  %i17 = alloca i32
  %i18 = load i32, i32* @a_to_the_a
  %i19 = add i32 %i18, 1
  store i32 %i19, i32* %i17
  %i20 = alloca i32
  %i21 = sub i32 0, 2147483647
  %i22 = sub i32 0, %i21
  store i32 %i22, i32* %i20
  %i23 = alloca i32
  %i24 = sub i32 0, 1
  %i25 = load i32, i32* %i20
  %i26 = sub i32 %i24, %i25
  store i32 %i26, i32* %i23
  %i27 = alloca i32
  %i28 = load i32, i32* @a_to_the_a
  %i29 = sub i32 0, %i28
  %i30 = sub i32 1, %i29
  %i31 = load i32, i32* %i17
  %i32 = mul i32 %i30, %i31
  %i33 = sdiv i32 %i32, 3
  %i34 = sub i32 %i33, 2
  %i35 = load i32, i32* @N
  %i36 = add i32 %i34, %i35
  %i37 = load i32, i32* @Mod
  %i38 = srem i32 %i36, %i37
  store i32 %i38, i32* %i27
  %i39 = alloca i32
  %i40 = load i32, i32* @a_to_the_a
  %i41 = load i32, i32* %i17
  %i42 = call i32 @qpow(i32 %i40, i32 %i41)
  store i32 %i42, i32* %i39
  store i32 10, i32* %i17
  store i32 0, i32* %i20
  store i32 7, i32* %i17
  store i32 8, i32* %i20
  %i43 = load i32, i32* %i17
  %i44 = load i32, i32* %i20
  %i45 = getelementptr [17 x i8], [17 x i8]* @str.3, i32 0, i32 0
  call void @putstr(i8* %i45)
  call void @putint(i32 %i43)
  %i46 = getelementptr [8 x i8], [8 x i8]* @str.4, i32 0, i32 0
  call void @putstr(i8* %i46)
  call void @putint(i32 %i44)
  call void @putch(i32 10)
  %i47 = alloca i32
  store i32 0, i32* %i47
  br label %testExp_b48

testExp_b48:                                      ; preds = %testExp_b60, %testExp_b54, %testExp_b0
  %i51 = icmp ne i32 1, 0
  br i1 %i51, label %testExp_b49, label %testExp_b50

testExp_b49:                                      ; preds = %testExp_b48
  %i52 = load i32, i32* %i47
  %i53 = add i32 %i52, 1
  store i32 %i53, i32* %i47
  %i56 = load i32, i32* %i47
  %i57 = srem i32 %i56, 2
  %i58 = icmp ne i32 %i57, 0
  br i1 %i58, label %testExp_b54, label %testExp_b55

testExp_b50:                                      ; preds = %testExp_b59, %testExp_b48
  %i76 = load i32, i32* %i17
  %i77 = load i32, i32* %i20
  %i78 = getelementptr [17 x i8], [17 x i8]* @str.5, i32 0, i32 0
  call void @putstr(i8* %i78)
  call void @putint(i32 %i76)
  %i79 = getelementptr [9 x i8], [9 x i8]* @str.6, i32 0, i32 0
  call void @putstr(i8* %i79)
  call void @putint(i32 %i77)
  call void @putch(i32 10)
  %i80 = alloca i32
  %i81 = load i32, i32* @n
  store i32 %i81, i32* %i80
  %i82 = alloca i32
  store i32 0, i32* %i82
  %i86 = load i32, i32* %i80
  %i87 = icmp slt i32 %i86, 0
  br i1 %i87, label %testExp_b83, label %testExp_b85

testExp_b54:                                      ; preds = %testExp_b49
  br label %testExp_b48

0:                                                ; No predecessors!
  br label %testExp_b55

testExp_b55:                                      ; preds = %0, %testExp_b49
  %i62 = load i32, i32* %i47
  %i63 = load i32, i32* %i17
  %i64 = icmp sge i32 %i62, %i63
  br i1 %i64, label %testExp_b59, label %testExp_b61

testExp_b59:                                      ; preds = %testExp_b55
  br label %testExp_b50

1:                                                ; No predecessors!
  br label %testExp_b60

testExp_b60:                                      ; preds = %testExp_b66, %1
  br label %testExp_b48

testExp_b61:                                      ; preds = %testExp_b55
  %i68 = load i32, i32* %i20
  %i69 = icmp slt i32 %i68, 10
  br i1 %i69, label %testExp_b65, label %testExp_b67

testExp_b65:                                      ; preds = %testExp_b61
  %i70 = load i32, i32* %i20
  %i71 = load i32, i32* %i47
  %i72 = add i32 %i70, %i71
  store i32 %i72, i32* %i20
  br label %testExp_b66

testExp_b66:                                      ; preds = %testExp_b67, %testExp_b65
  br label %testExp_b60

testExp_b67:                                      ; preds = %testExp_b61
  %i73 = load i32, i32* %i20
  %i74 = load i32, i32* %i47
  %i75 = sub i32 %i73, %i74
  store i32 %i75, i32* %i20
  br label %testExp_b66

testExp_b83:                                      ; preds = %testExp_b50
  store i32 10, i32* %i82
  br label %testExp_b84

testExp_b84:                                      ; preds = %testExp_b89, %testExp_b83
  %i100 = load i32, i32* %i80
  %i101 = icmp sle i32 %i100, 10
  br i1 %i101, label %testExp_b98, label %testExp_b99

testExp_b85:                                      ; preds = %testExp_b50
  %i91 = load i32, i32* %i80
  %i92 = icmp sgt i32 %i91, 10
  br i1 %i92, label %testExp_b88, label %testExp_b90

testExp_b88:                                      ; preds = %testExp_b85
  store i32 20, i32* %i82
  br label %testExp_b89

testExp_b89:                                      ; preds = %testExp_b94, %testExp_b88
  br label %testExp_b84

testExp_b90:                                      ; preds = %testExp_b85
  %i95 = load i32, i32* %i80
  %i96 = load i32, i32* @n
  %i97 = icmp eq i32 %i95, %i96
  br i1 %i97, label %testExp_b93, label %testExp_b94

testExp_b93:                                      ; preds = %testExp_b90
  store i32 30, i32* %i82
  br label %testExp_b94

testExp_b94:                                      ; preds = %testExp_b93, %testExp_b90
  br label %testExp_b89

testExp_b98:                                      ; preds = %testExp_b84
  %i102 = load i32, i32* %i82
  %i103 = load i32, i32* %i80
  %i104 = add i32 %i102, %i103
  store i32 %i104, i32* %i82
  br label %testExp_b99

testExp_b99:                                      ; preds = %testExp_b98, %testExp_b84
  %i105 = load i32, i32* @a_to_the_a
  %i106 = load i32, i32* %i17
  %i107 = load i32, i32* %i20
  %i108 = load i32, i32* %i23
  %i109 = load i32, i32* %i39
  %i110 = getelementptr [10 x i8], [10 x i8]* @str.7, i32 0, i32 0
  call void @putstr(i8* %i110)
  call void @putint(i32 %i105)
  call void @putch(i32 32)
  call void @putint(i32 %i106)
  call void @putch(i32 32)
  call void @putint(i32 %i107)
  call void @putch(i32 32)
  call void @putint(i32 %i108)
  call void @putch(i32 32)
  call void @putint(i32 %i109)
  %i111 = load i32, i32* %i80
  %i112 = load i32, i32* %i82
  %i113 = load i32, i32* %i27
  call void @putch(i32 32)
  call void @putint(i32 %i111)
  call void @putch(i32 32)
  call void @putint(i32 %i112)
  call void @putch(i32 32)
  call void @putint(i32 %i113)
  call void @putch(i32 10)
  %i114 = load i32, i32* %i80
  %i115 = load i32, i32* %i82
  %i116 = call i32 @gcd(i32 %i114, i32 %i115)
  ret i32 %i116
}

define dso_local i32 @main() {
main_b0:
  %i1 = getelementptr [21 x i8], [21 x i8]* @str.8, i32 0, i32 0
  call void @putstr(i8* %i1)
  %i2 = call i32 @getint()
  store i32 %i2, i32* @n
  %i3 = load i32, i32* @n
  call void @hanoi(i32 %i3, i32 1, i32 2, i32 3)
  %i4 = call i32 @testExp()
  %i5 = getelementptr [10 x i8], [10 x i8]* @str.9, i32 0, i32 0
  call void @putstr(i8* %i5)
  call void @putint(i32 %i4)
  call void @putch(i32 10)
  ret i32 0
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getint() #0 {
  %1 = alloca i32, align 4
  %2 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i32* %1)
  %3 = load i32, i32* %1, align 4
  ret i32 %3
}

declare dso_local i32 @__isoc99_scanf(i8*, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getch() #0 {
  %1 = alloca i8, align 1
  %2 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1, i64 0, i64 0), i8* %1)
  %3 = load i8, i8* %1, align 1
  %4 = sext i8 %3 to i32
  ret i32 %4
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getarray(i32* %0) #0 {
  %2 = alloca i32*, align 8
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  store i32* %0, i32** %2, align 8
  %5 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i32* %3)
  store i32 0, i32* %4, align 4
  br label %6

6:                                                ; preds = %16, %1
  %7 = load i32, i32* %4, align 4
  %8 = load i32, i32* %3, align 4
  %9 = icmp slt i32 %7, %8
  br i1 %9, label %10, label %19

10:                                               ; preds = %6
  %11 = load i32*, i32** %2, align 8
  %12 = load i32, i32* %4, align 4
  %13 = sext i32 %12 to i64
  %14 = getelementptr inbounds i32, i32* %11, i64 %13
  %15 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i32* %14)
  br label %16

16:                                               ; preds = %10
  %17 = load i32, i32* %4, align 4
  %18 = add nsw i32 %17, 1
  store i32 %18, i32* %4, align 4
  br label %6

19:                                               ; preds = %6
  %20 = load i32, i32* %3, align 4
  ret i32 %20
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putint(i32 %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  %4 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i32 %3)
  ret void
}

declare dso_local i32 @printf(i8*, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putch(i32 %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  %4 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1, i64 0, i64 0), i32 %3)
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putarray(i32 %0, i32* %1) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i32*, align 8
  %5 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  store i32* %1, i32** %4, align 8
  %6 = load i32, i32* %3, align 4
  %7 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.2, i64 0, i64 0), i32 %6)
  store i32 0, i32* %5, align 4
  br label %8

8:                                                ; preds = %19, %2
  %9 = load i32, i32* %5, align 4
  %10 = load i32, i32* %3, align 4
  %11 = icmp slt i32 %9, %10
  br i1 %11, label %12, label %22

12:                                               ; preds = %8
  %13 = load i32*, i32** %4, align 8
  %14 = load i32, i32* %5, align 4
  %15 = sext i32 %14 to i64
  %16 = getelementptr inbounds i32, i32* %13, i64 %15
  %17 = load i32, i32* %16, align 4
  %18 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.3, i64 0, i64 0), i32 %17)
  br label %19

19:                                               ; preds = %12
  %20 = load i32, i32* %5, align 4
  %21 = add nsw i32 %20, 1
  store i32 %21, i32* %5, align 4
  br label %8

22:                                               ; preds = %8
  %23 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4, i64 0, i64 0))
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putstr(i8* %0) #0 {
  %2 = alloca i8*, align 8
  store i8* %0, i8** %2, align 8
  %3 = load i8*, i8** %2, align 8
  %4 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.5, i64 0, i64 0), i8* %3)
  ret void
}

attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.ident = !{!0}
!llvm.module.flags = !{!1}

!0 = !{!"Ubuntu clang version 10.0.1-++20211003085942+ef32c611aa21-1~exp1~20211003090334.2"}
!1 = !{i32 1, !"wchar_size", i32 4}

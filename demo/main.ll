declare i32 @getint()
declare void @putint(i32)
declare void @putstr(i8*)
declare void @putch(i32)
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
define dso_local void @move(i32 %ar1, i32 %ar3) {
move_b0:
  %i5 = load i32, i32* @cnt
  %i6 = add i32 %i5, 1
  store i32 %i6, i32* @cnt
  %i9 = load i32, i32* @cnt
  %i10 = load i32, i32* @Mod
  %i11 = srem i32 %i9, %i10
  %i12 = icmp eq i32 %i11, 0
  br i1 %i12, label %move_b7, label %move_b8
move_b7:
  %i15 = getelementptr [26 x i8], [26 x i8]* @str.0, i32 0, i32 0
  call void @putstr(i8* %i15)
  call void @putint(i32 %ar1)
  %i16 = getelementptr [5 x i8], [5 x i8]* @str.1, i32 0, i32 0
  call void @putstr(i8* %i16)
  call void @putint(i32 %ar3)
  call void @putch(i32 10)
  br label %move_b8
move_b8:
  ret void
}
define dso_local void @hanoi(i32 %ar1, i32 %ar3, i32 %ar5, i32 %ar7) {
hanoi_b0:
  %i12 = icmp eq i32 %ar1, 1
  br i1 %i12, label %hanoi_b9, label %hanoi_b10
hanoi_b9:
  call void @move(i32 %ar3, i32 %ar7)
  %i15 = getelementptr [10 x i8], [10 x i8]* @str.2, i32 0, i32 0
  call void @putstr(i8* %i15)
  ret void
hanoi_b10:
  %i17 = sub i32 %ar1, 1
  call void @hanoi(i32 %i17, i32 %ar3, i32 %ar7, i32 %ar5)
  call void @move(i32 %ar3, i32 %ar7)
  %i24 = sub i32 %ar1, 1
  call void @hanoi(i32 %i24, i32 %ar5, i32 %ar3, i32 %ar7)
  ret void
}
define dso_local i32 @qpow(i32 %ar1, i32 %ar3) {
qpow_b0:
  br label %qpow_b6
qpow_b6:
  %p3 = phi i32 [ 1, %qpow_b0 ], [ %p2, %qpow_b19 ]
  %p1 = phi i32 [ %ar3, %qpow_b0 ], [ %i12, %qpow_b19 ]
  %p0 = phi i32 [ %ar1, %qpow_b0 ], [ %i17, %qpow_b19 ]
  %i10 = icmp ne i32 %p1, 0
  br i1 %i10, label %qpow_b7, label %qpow_b8
qpow_b7:
  %i12 = sdiv i32 %p1, 2
  %i15 = mul i32 %p0, %p0
  %i16 = load i32, i32* @Mod
  %i17 = srem i32 %i15, %i16
  %i21 = srem i32 %i12, 2
  %i22 = icmp ne i32 %i21, 0
  br i1 %i22, label %qpow_b18, label %qpow_b19
qpow_b8:
  ret i32 %p3
qpow_b18:
  %i25 = mul i32 %p3, %i17
  %i26 = load i32, i32* @Mod
  %i27 = srem i32 %i25, %i26
  br label %qpow_b19
qpow_b19:
  %p2 = phi i32 [ %p3, %qpow_b7 ], [ %i27, %qpow_b18 ]
  br label %qpow_b6
}
define dso_local i32 @gcd(i32 %ar1, i32 %ar3) {
gcd_b0:
  %i8 = icmp eq i32 %ar3, 0
  br i1 %i8, label %gcd_b5, label %gcd_b6
gcd_b5:
  ret i32 %ar1
gcd_b6:
  %i13 = srem i32 %ar1, %ar3
  %i14 = call i32 @gcd(i32 %ar3, i32 %i13)
  ret i32 %i14
}
define dso_local i32 @testExp() {
testExp_b0:
  %i1 = load i32, i32* @n
  %i2 = load i32, i32* @n
  %i3 = mul i32 %i1, %i2
  store i32 %i3, i32* @a_to_the_a
  %i5 = load i32, i32* @N
  %i6 = load i32, i32* @n
  %i7 = sdiv i32 %i5, %i6
  %i8 = load i32, i32* @n
  %i9 = load i32, i32* @n
  %i10 = mul i32 %i8, %i9
  %i11 = load i32, i32* @n
  %i12 = sdiv i32 %i10, %i11
  %i13 = load i32, i32* @n
  %i14 = add i32 %i12, %i13
  %i15 = load i32, i32* @n
  %i16 = sub i32 %i14, %i15
  %i18 = load i32, i32* @a_to_the_a
  %i19 = add i32 %i18, 1
  %i21 = sub i32 0, 2147483647
  %i22 = sub i32 0, %i21
  %i24 = sub i32 0, 1
  %i26 = sub i32 %i24, %i22
  %i28 = load i32, i32* @a_to_the_a
  %i29 = sub i32 0, %i28
  %i30 = sub i32 1, %i29
  %i32 = mul i32 %i30, %i19
  %i33 = sdiv i32 %i32, 3
  %i34 = sub i32 %i33, 2
  %i35 = load i32, i32* @N
  %i36 = add i32 %i34, %i35
  %i37 = load i32, i32* @Mod
  %i38 = srem i32 %i36, %i37
  %i40 = load i32, i32* @a_to_the_a
  %i42 = call i32 @qpow(i32 %i40, i32 %i19)
  %i45 = getelementptr [17 x i8], [17 x i8]* @str.3, i32 0, i32 0
  call void @putstr(i8* %i45)
  call void @putint(i32 7)
  %i46 = getelementptr [8 x i8], [8 x i8]* @str.4, i32 0, i32 0
  call void @putstr(i8* %i46)
  call void @putint(i32 8)
  call void @putch(i32 10)
  br label %testExp_b48
testExp_b48:
  %p7 = phi i32 [ 0, %testExp_b0 ], [ %i53, %testExp_b60 ], [ undef, %testExp_b54 ]
  %p5 = phi i32 [ 8, %testExp_b0 ], [ %p4, %testExp_b60 ], [ %p5, %testExp_b54 ]
  %i51 = icmp ne i32 1, 0
  br i1 %i51, label %testExp_b49, label %testExp_b50
testExp_b49:
  %i53 = add i32 %p7, 1
  %i57 = srem i32 %i53, 2
  %i58 = icmp ne i32 %i57, 0
  br i1 %i58, label %testExp_b54, label %testExp_b55
testExp_b50:
  %p6 = phi i32 [ %p7, %testExp_b48 ], [ %i53, %testExp_b59 ]
  %i78 = getelementptr [17 x i8], [17 x i8]* @str.5, i32 0, i32 0
  call void @putstr(i8* %i78)
  call void @putint(i32 7)
  %i79 = getelementptr [9 x i8], [9 x i8]* @str.6, i32 0, i32 0
  call void @putstr(i8* %i79)
  call void @putint(i32 %p5)
  call void @putch(i32 10)
  %i81 = load i32, i32* @n
  %i87 = icmp slt i32 %i81, 0
  br i1 %i87, label %testExp_b83, label %testExp_b85
testExp_b54:
  br label %testExp_b48
testExp_b55:
  %i64 = icmp sge i32 %i53, 7
  br i1 %i64, label %testExp_b59, label %testExp_b61
testExp_b59:
  br label %testExp_b50
testExp_b60:
  br label %testExp_b48
testExp_b61:
  %i69 = icmp slt i32 %p5, 10
  br i1 %i69, label %testExp_b65, label %testExp_b67
testExp_b65:
  %i72 = add i32 %p5, %i53
  br label %testExp_b66
testExp_b66:
  %p4 = phi i32 [ %i75, %testExp_b67 ], [ %i72, %testExp_b65 ]
  br label %testExp_b60
testExp_b67:
  %i75 = sub i32 %p5, %i53
  br label %testExp_b66
testExp_b83:
  br label %testExp_b84
testExp_b84:
  %p11 = phi i32 [ 10, %testExp_b83 ], [ %p8, %testExp_b89 ]
  %i101 = icmp sle i32 %i81, 10
  br i1 %i101, label %testExp_b98, label %testExp_b99
testExp_b85:
  %i92 = icmp sgt i32 %i81, 10
  br i1 %i92, label %testExp_b88, label %testExp_b90
testExp_b88:
  br label %testExp_b89
testExp_b89:
  %p8 = phi i32 [ %p9, %testExp_b94 ], [ 20, %testExp_b88 ]
  br label %testExp_b84
testExp_b90:
  %i96 = load i32, i32* @n
  %i97 = icmp eq i32 %i81, %i96
  br i1 %i97, label %testExp_b93, label %testExp_b94
testExp_b93:
  br label %testExp_b94
testExp_b94:
  %p9 = phi i32 [ 0, %testExp_b90 ], [ 30, %testExp_b93 ]
  br label %testExp_b89
testExp_b98:
  %i104 = add i32 %p11, %i81
  br label %testExp_b99
testExp_b99:
  %p10 = phi i32 [ %p11, %testExp_b84 ], [ %i104, %testExp_b98 ]
  %i105 = load i32, i32* @a_to_the_a
  %i110 = getelementptr [10 x i8], [10 x i8]* @str.7, i32 0, i32 0
  call void @putstr(i8* %i110)
  call void @putint(i32 %i105)
  call void @putch(i32 32)
  call void @putint(i32 7)
  call void @putch(i32 32)
  call void @putint(i32 %p5)
  call void @putch(i32 32)
  call void @putint(i32 %i26)
  call void @putch(i32 32)
  call void @putint(i32 %i42)
  call void @putch(i32 32)
  call void @putint(i32 %i81)
  call void @putch(i32 32)
  call void @putint(i32 %p10)
  call void @putch(i32 32)
  call void @putint(i32 %i38)
  call void @putch(i32 10)
  %i116 = call i32 @gcd(i32 %i81, i32 %p10)
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

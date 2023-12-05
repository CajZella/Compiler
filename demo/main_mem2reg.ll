; ModuleID = 'main.ll'
source_filename = "main.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; Function Attrs: noinline nounwind uwtable
define dso_local i32 @getnum(i32 %0) #0 {
  %2 = mul nsw i32 %0, %0
  %3 = add nsw i32 %2, 1
  ret i32 %3
}

; Function Attrs: noinline nounwind uwtable
define dso_local i32 @main() #0 {
  %1 = icmp sle i32 100, 0
  br i1 %1, label %2, label %14

2:                                                ; preds = %0
  br label %3

3:                                                ; preds = %11, %10, %2
  %.0 = phi i32 [ 0, %2 ], [ %.0, %10 ], [ %12, %11 ]
  %4 = icmp slt i32 %.0, 11
  br i1 %4, label %5, label %13

5:                                                ; preds = %3
  %6 = icmp eq i32 %.0, 10
  br i1 %6, label %7, label %8

7:                                                ; preds = %5
  br label %13

8:                                                ; preds = %5
  %9 = icmp eq i32 %.0, 1
  br i1 %9, label %10, label %11

10:                                               ; preds = %8
  br label %3

11:                                               ; preds = %8
  %12 = add nsw i32 %.0, 1
  br label %3

13:                                               ; preds = %7, %3
  br label %16

14:                                               ; preds = %0
  %15 = call i32 @getnum(i32 100)
  br label %16

16:                                               ; preds = %14, %13
  ret i32 0
}

attributes #0 = { noinline nounwind uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"Ubuntu clang version 10.0.1-++20211003085942+ef32c611aa21-1~exp1~20211003090334.2"}

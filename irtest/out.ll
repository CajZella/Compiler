; ModuleID = 'llvm-link'
source_filename = "llvm-link"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@FIB_M = dso_local constant [2 x [2 x i32]] [[2 x i32] [i32 1, i32 1], [2 x i32] [i32 1, i32 0]]
@N = dso_local constant i32 9
@FIB_V = dso_local constant [2 x i32] [i32 1, i32 1]
@a = dso_local global i32 5
@b = dso_local global i32 7
@c = dso_local global i32 0
@lis = dso_local global [5 x i32] [i32 1, i32 2, i32 3, i32 4, i32 5]
@str.4 = private unnamed_addr constant [10 x i8] c"20373743\0A\00"
@str.5 = private unnamed_addr constant [8 x i8] c"input: \00"
@str.12 = private unnamed_addr constant [17 x i8] c"a and b is true\0A\00"
@str.13 = private unnamed_addr constant [11 x i8] c"c is true\0A\00"
@str.14 = private unnamed_addr constant [16 x i8] c"a || c is true\0A\00"
@str.6 = private unnamed_addr constant [18 x i8] c"get matrix after \00"
@str.7 = private unnamed_addr constant [21 x i8] c" multplFIB_M turns!\0A\00"
@str.0 = private unnamed_addr constant [8 x i8] c"a is [[\00"
@str.1 = private unnamed_addr constant [3 x i8] c", \00"
@str.2 = private unnamed_addr constant [5 x i8] c"], [\00"
@str.3 = private unnamed_addr constant [4 x i8] c"]]\0A\00"
@str.15 = private unnamed_addr constant [19 x i8] c"sum of a, b, c is \00"
@str.8 = private unnamed_addr constant [25 x i8] c"given original vector: [\00"
@str.9 = private unnamed_addr constant [3 x i8] c"]\0A\00"
@str.10 = private unnamed_addr constant [19 x i8] c"get next vector: [\00"
@str.11 = private unnamed_addr constant [4 x i8] c"]!\0A\00"
@str.16 = private unnamed_addr constant [21 x i8] c"sum of lis front id \00"
@.str = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.1 = private unnamed_addr constant [3 x i8] c"%c\00", align 1
@.str.2 = private unnamed_addr constant [4 x i8] c"%d:\00", align 1
@.str.3 = private unnamed_addr constant [4 x i8] c" %d\00", align 1
@.str.4 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5 = private unnamed_addr constant [3 x i8] c"%s\00", align 1

define dso_local i32 @main() {
main_b172:
  %i173 = getelementptr [10 x i8], [10 x i8]* @str.4, i32 0, i32 0
  call void @putstr(i8* %i173)
  %i175 = call i32 @getint()
  %i177 = getelementptr [8 x i8], [8 x i8]* @str.5, i32 0, i32 0
  call void @putstr(i8* %i177)
  call void @putint(i32 %i175)
  call void @putch(i32 10)
  br label %main_b297

main_b203:                                        ; preds = %main_b207
  %i210 = getelementptr [17 x i8], [17 x i8]* @str.12, i32 0, i32 0
  call void @putstr(i8* %i210)
  br label %main_b204

main_b204:                                        ; preds = %main_b400, %main_b207, %main_b203
  %i214 = load i32, i32* @c
  %i215 = icmp eq i32 %i214, 0
  br i1 %i215, label %main_b211, label %main_b212

main_b207:                                        ; preds = %main_b400
  %i208 = load i32, i32* @b
  %i209 = icmp ne i32 %i208, 0
  br i1 %i209, label %main_b203, label %main_b204

main_b211:                                        ; preds = %main_b204
  %i216 = getelementptr [11 x i8], [11 x i8]* @str.13, i32 0, i32 0
  call void @putstr(i8* %i216)
  br label %main_b212

main_b212:                                        ; preds = %main_b211, %main_b204
  %i221 = load i32, i32* @a
  %i222 = icmp ne i32 %i221, 0
  br i1 %i222, label %main_b217, label %main_b220

main_b217:                                        ; preds = %main_b220, %main_b212
  %i225 = getelementptr [16 x i8], [16 x i8]* @str.14, i32 0, i32 0
  call void @putstr(i8* %i225)
  br label %main_b218

main_b218:                                        ; preds = %main_b220, %main_b217
  %i226 = load i32, i32* @a
  %i227 = load i32, i32* @b
  %i228 = load i32, i32* @c
  br label %main_b397

main_b220:                                        ; preds = %main_b212
  %i223 = load i32, i32* @c
  %i224 = icmp ne i32 %i223, 0
  br i1 %i224, label %main_b217, label %main_b218

main_b296:                                        ; preds = %main_b300
  %p6 = phi i32 [ %p4, %main_b300 ]
  %i181 = getelementptr [18 x i8], [18 x i8]* @str.6, i32 0, i32 0
  call void @putstr(i8* %i181)
  call void @putint(i32 %p6)
  %i182 = getelementptr [21 x i8], [21 x i8]* @str.7, i32 0, i32 0
  call void @putstr(i8* %i182)
  %i183 = alloca [2 x i32]
  %i184 = getelementptr [2 x i32], [2 x i32]* %i183, i32 0, i32 0
  store i32 13, i32* %i184
  %i185 = getelementptr [2 x i32], [2 x i32]* %i183, i32 0, i32 1
  store i32 8, i32* %i185
  %i186 = alloca [2 x i32]
  %i187 = getelementptr [2 x i32], [2 x i32]* %i186, i32 0, i32 0
  %i188 = getelementptr [2 x i32], [2 x i32]* %i183, i32 0, i32 0
  br label %main_b401

main_b297:                                        ; preds = %main_b172
  %i306 = alloca [2 x [2 x i32]]
  %i307 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 0, i32 0
  store i32 1, i32* %i307
  %i308 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 0, i32 1
  store i32 0, i32* %i308
  %i309 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 1, i32 0
  store i32 0, i32* %i309
  %i310 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 1, i32 1
  store i32 1, i32* %i310
  %i311 = alloca [2 x [2 x i32]]
  br label %main_b298

main_b298:                                        ; preds = %main_b303, %main_b297
  %p3 = phi i32 [ %p5, %main_b303 ], [ 0, %main_b297 ]
  %p4 = phi i32 [ 0, %main_b297 ], [ %i340, %main_b303 ]
  %i312 = load i32, i32* @N
  %i313 = icmp slt i32 %p4, %i312
  br i1 %i313, label %main_b299, label %main_b300

main_b299:                                        ; preds = %main_b298
  %i314 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i311, i32 0, i32 0
  %i315 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 0
  br label %main_b305

main_b300:                                        ; preds = %main_b298
  br label %main_b296

main_b301:                                        ; preds = %main_b304, %main_b302
  %p5 = phi i32 [ 0, %main_b304 ], [ %i324, %main_b302 ]
  %i316 = icmp slt i32 %p5, 4
  br i1 %i316, label %main_b302, label %main_b303

main_b302:                                        ; preds = %main_b301
  %i317 = sdiv i32 %p5, 2
  %i318 = srem i32 %p5, 2
  %i319 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i311, i32 0, i32 %i317, i32 %i318
  %i320 = load i32, i32* %i319
  %i321 = sdiv i32 %p5, 2
  %i322 = srem i32 %p5, 2
  %i323 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 %i321, i32 %i322
  store i32 %i320, i32* %i323
  %i324 = add i32 %p5, 1
  br label %main_b301

main_b303:                                        ; preds = %main_b301
  %i325 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 0, i32 0
  %i326 = load i32, i32* %i325
  %i327 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 0, i32 1
  %i328 = load i32, i32* %i327
  %i329 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 1, i32 0
  %i330 = load i32, i32* %i329
  %i331 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %i306, i32 0, i32 1, i32 1
  %i332 = load i32, i32* %i331
  %i333 = getelementptr [8 x i8], [8 x i8]* @str.0, i32 0, i32 0
  call void @putstr(i8* %i333)
  call void @putint(i32 %i326)
  %i334 = getelementptr [3 x i8], [3 x i8]* @str.1, i32 0, i32 0
  call void @putstr(i8* %i334)
  call void @putint(i32 %i328)
  %i335 = getelementptr [5 x i8], [5 x i8]* @str.2, i32 0, i32 0
  call void @putstr(i8* %i335)
  call void @putint(i32 %i330)
  %i336 = getelementptr [3 x i8], [3 x i8]* @str.1, i32 0, i32 0
  call void @putstr(i8* %i336)
  call void @putint(i32 %i332)
  %i337 = getelementptr [4 x i8], [4 x i8]* @str.3, i32 0, i32 0
  call void @putstr(i8* %i337)
  %i338 = add i32 %p4, 1
  %i339 = sub i32 0, %i338
  %i340 = sub i32 0, %i339
  br label %main_b298

main_b304:                                        ; preds = %main_b305
  br label %main_b301

main_b305:                                        ; preds = %main_b299
  %i341 = alloca [2 x i32]
  %i342 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 1
  %i343 = load i32, i32* %i342
  %i344 = getelementptr [2 x i32], [2 x i32]* %i341, i32 0, i32 0
  store i32 %i343, i32* %i344
  %i345 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 0
  %i346 = load i32, i32* %i345
  %i347 = getelementptr [2 x i32], [2 x i32]* %i341, i32 0, i32 1
  store i32 %i346, i32* %i347
  %i348 = getelementptr [2 x i32], [2 x i32]* %i315, i32 0, i32 0
  %i349 = load i32, i32* %i348
  %i350 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 0
  %i351 = load i32, i32* %i350
  %i352 = mul i32 %i349, %i351
  %i353 = getelementptr [2 x i32], [2 x i32]* %i315, i32 0, i32 1
  %i354 = load i32, i32* %i353
  %i355 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 1, i32 0
  %i356 = load i32, i32* %i355
  %i357 = mul i32 %i354, %i356
  %i358 = add i32 %i352, %i357
  %i359 = getelementptr [2 x i32], [2 x i32]* %i314, i32 0, i32 0
  store i32 %i358, i32* %i359
  %i360 = getelementptr [2 x i32], [2 x i32]* %i315, i32 0, i32 0
  %i361 = load i32, i32* %i360
  %i362 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 1
  %i363 = load i32, i32* %i362
  %i364 = mul i32 %i361, %i363
  %i365 = getelementptr [2 x i32], [2 x i32]* %i315, i32 0, i32 1
  %i366 = load i32, i32* %i365
  %i367 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 1, i32 1
  %i368 = load i32, i32* %i367
  %i369 = mul i32 %i366, %i368
  %i370 = add i32 %i364, %i369
  %i371 = getelementptr [2 x i32], [2 x i32]* %i314, i32 0, i32 1
  store i32 %i370, i32* %i371
  %i372 = getelementptr [2 x i32], [2 x i32]* %i315, i32 1, i32 0
  %i373 = load i32, i32* %i372
  %i374 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 0
  %i375 = load i32, i32* %i374
  %i376 = mul i32 %i373, %i375
  %i377 = getelementptr [2 x i32], [2 x i32]* %i315, i32 1, i32 1
  %i378 = load i32, i32* %i377
  %i379 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 1, i32 0
  %i380 = load i32, i32* %i379
  %i381 = mul i32 %i378, %i380
  %i382 = add i32 %i376, %i381
  %i383 = getelementptr [2 x i32], [2 x i32]* %i314, i32 1, i32 0
  store i32 %i382, i32* %i383
  %i384 = getelementptr [2 x i32], [2 x i32]* %i315, i32 1, i32 0
  %i385 = load i32, i32* %i384
  %i386 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 1
  %i387 = load i32, i32* %i386
  %i388 = mul i32 %i385, %i387
  %i389 = getelementptr [2 x i32], [2 x i32]* %i315, i32 1, i32 1
  %i390 = load i32, i32* %i389
  %i391 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 1, i32 1
  %i392 = load i32, i32* %i391
  %i393 = mul i32 %i390, %i392
  %i394 = add i32 %i388, %i393
  %i395 = getelementptr [2 x i32], [2 x i32]* %i314, i32 1, i32 1
  store i32 %i394, i32* %i395
  br label %main_b304

main_b396:                                        ; preds = %main_b397
  %p7 = phi i32 [ %i399, %main_b397 ]
  %i230 = getelementptr [19 x i8], [19 x i8]* @str.15, i32 0, i32 0
  call void @putstr(i8* %i230)
  call void @putint(i32 %p7)
  call void @putch(i32 10)
  %i231 = getelementptr [5 x i32], [5 x i32]* @lis, i32 0, i32 0
  %i232 = load i32, i32* %i231
  %i233 = getelementptr [5 x i32], [5 x i32]* @lis, i32 0, i32 1
  %i234 = load i32, i32* %i233
  %i235 = getelementptr [5 x i32], [5 x i32]* @lis, i32 0, i32 2
  %i236 = load i32, i32* %i235
  br label %main_b427

main_b397:                                        ; preds = %main_b218
  %i398 = add i32 %i226, %i227
  %i399 = add i32 %i398, %i228
  br label %main_b396

main_b400:                                        ; preds = %main_b401
  %i189 = getelementptr [2 x i32], [2 x i32]* %i183, i32 0, i32 0
  %i190 = load i32, i32* %i189
  %i191 = getelementptr [2 x i32], [2 x i32]* %i183, i32 0, i32 1
  %i192 = load i32, i32* %i191
  %i193 = getelementptr [25 x i8], [25 x i8]* @str.8, i32 0, i32 0
  call void @putstr(i8* %i193)
  call void @putint(i32 %i190)
  %i194 = getelementptr [3 x i8], [3 x i8]* @str.1, i32 0, i32 0
  call void @putstr(i8* %i194)
  call void @putint(i32 %i192)
  %i195 = getelementptr [3 x i8], [3 x i8]* @str.9, i32 0, i32 0
  call void @putstr(i8* %i195)
  %i196 = getelementptr [2 x i32], [2 x i32]* %i186, i32 0, i32 0
  %i197 = load i32, i32* %i196
  %i198 = getelementptr [2 x i32], [2 x i32]* %i186, i32 0, i32 1
  %i199 = load i32, i32* %i198
  %i200 = getelementptr [19 x i8], [19 x i8]* @str.10, i32 0, i32 0
  call void @putstr(i8* %i200)
  call void @putint(i32 %i197)
  %i201 = getelementptr [3 x i8], [3 x i8]* @str.1, i32 0, i32 0
  call void @putstr(i8* %i201)
  call void @putint(i32 %i199)
  %i202 = getelementptr [4 x i8], [4 x i8]* @str.11, i32 0, i32 0
  call void @putstr(i8* %i202)
  %i205 = load i32, i32* @a
  %i206 = icmp ne i32 %i205, 0
  br i1 %i206, label %main_b207, label %main_b204

main_b401:                                        ; preds = %main_b296
  %i402 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 0
  %i403 = load i32, i32* %i402
  %i404 = getelementptr i32, i32* %i188, i32 0
  %i405 = load i32, i32* %i404
  %i406 = mul i32 %i403, %i405
  %i407 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 0, i32 1
  %i408 = load i32, i32* %i407
  %i409 = getelementptr i32, i32* %i188, i32 1
  %i410 = load i32, i32* %i409
  %i411 = mul i32 %i408, %i410
  %i412 = add i32 %i406, %i411
  %i413 = getelementptr i32, i32* %i187, i32 0
  store i32 %i412, i32* %i413
  %i414 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 1, i32 0
  %i415 = load i32, i32* %i414
  %i416 = getelementptr i32, i32* %i188, i32 0
  %i417 = load i32, i32* %i416
  %i418 = mul i32 %i415, %i417
  %i419 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* @FIB_M, i32 0, i32 1, i32 1
  %i420 = load i32, i32* %i419
  %i421 = getelementptr i32, i32* %i188, i32 1
  %i422 = load i32, i32* %i421
  %i423 = mul i32 %i420, %i422
  %i424 = add i32 %i418, %i423
  %i425 = getelementptr i32, i32* %i187, i32 1
  store i32 %i424, i32* %i425
  br label %main_b400

main_b426:                                        ; preds = %main_b427
  %p8 = phi i32 [ %i429, %main_b427 ]
  %i238 = getelementptr [21 x i8], [21 x i8]* @str.16, i32 0, i32 0
  call void @putstr(i8* %i238)
  call void @putint(i32 %p8)
  call void @putch(i32 10)
  ret i32 0

main_b427:                                        ; preds = %main_b396
  %i428 = add i32 %i232, %i234
  %i429 = add i32 %i428, %i236
  br label %main_b426
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

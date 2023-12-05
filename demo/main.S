	.text
	.abicalls
	.option	pic0
	.section	.mdebug.abi32,"",@progbits
	.nan	legacy
	.module	fp=xx
	.module	nooddspreg
	.text
	.file	"main.c"
	.globl	fib                     # -- Begin function fib
	.p2align	2
	.type	fib,@function
	.set	nomicromips
	.set	nomips16
	.ent	fib
fib:                                    # @fib
	.frame	$fp,40,$ra
	.mask 	0xc0000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	.set	noat
# %bb.0:
	addiu	$sp, $sp, -40
	sw	$ra, 36($sp)            # 4-byte Folded Spill
	sw	$fp, 32($sp)            # 4-byte Folded Spill
	move	$fp, $sp
	sw	$4, 28($fp)
	addiu	$1, $zero, 1
	sw	$1, 24($fp)
	lw	$2, 28($fp)
	bne	$2, $1, $BB0_3
	nop
# %bb.1:
	j	$BB0_2
	nop
$BB0_2:
	addiu	$1, $zero, 1
	sw	$1, 24($fp)
	j	$BB0_8
	nop
$BB0_3:
	lw	$1, 28($fp)
	addiu	$2, $zero, 2
	bne	$1, $2, $BB0_6
	nop
# %bb.4:
	j	$BB0_5
	nop
$BB0_5:
	addiu	$1, $zero, 1
	sw	$1, 24($fp)
	j	$BB0_7
	nop
$BB0_6:
	lw	$1, 28($fp)
	addiu	$4, $1, -1
	jal	fib
	nop
	lw	$1, 28($fp)
	addiu	$4, $1, -2
	sw	$2, 20($fp)             # 4-byte Folded Spill
	jal	fib
	nop
	lw	$1, 20($fp)             # 4-byte Folded Reload
	addu	$2, $1, $2
	sw	$2, 24($fp)
	j	$BB0_7
	nop
$BB0_7:
	j	$BB0_8
	nop
$BB0_8:
	lw	$2, 24($fp)
	move	$sp, $fp
	lw	$fp, 32($sp)            # 4-byte Folded Reload
	lw	$ra, 36($sp)            # 4-byte Folded Reload
	addiu	$sp, $sp, 40
	jr	$ra
	nop
	.set	at
	.set	macro
	.set	reorder
	.end	fib
$func_end0:
	.size	fib, ($func_end0)-fib
                                        # -- End function
	.globl	main                    # -- Begin function main
	.p2align	2
	.type	main,@function
	.set	nomicromips
	.set	nomips16
	.ent	main
main:                                   # @main
	.frame	$fp,40,$ra
	.mask 	0xc0000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	.set	noat
# %bb.0:
	addiu	$sp, $sp, -40
	sw	$ra, 36($sp)            # 4-byte Folded Spill
	sw	$fp, 32($sp)            # 4-byte Folded Spill
	move	$fp, $sp
	sw	$zero, 28($fp)
	addiu	$1, $zero, 100
	sw	$1, 24($fp)
	lw	$4, 24($fp)
	jal	fib
	nop
	addiu	$1, $zero, 0
	sw	$2, 20($fp)             # 4-byte Folded Spill
	move	$2, $1
	move	$sp, $fp
	lw	$fp, 32($sp)            # 4-byte Folded Reload
	lw	$ra, 36($sp)            # 4-byte Folded Reload
	addiu	$sp, $sp, 40
	jr	$ra
	nop
	.set	at
	.set	macro
	.set	reorder
	.end	main
$func_end1:
	.size	main, ($func_end1)-main
                                        # -- End function
	.type	n,@object               # @n
	.section	.rodata,"a",@progbits
	.globl	n
	.p2align	2
n:
	.4byte	1                       # 0x1
	.size	n, 4

	.type	a,@object               # @a
	.globl	a
	.p2align	2
a:
	.4byte	1                       # 0x1
	.size	a, 4

	.type	b,@object               # @b
	.globl	b
	.p2align	2
b:
	.4byte	2                       # 0x2
	.size	b, 4

	.type	c,@object               # @c
	.globl	c
	.p2align	2
c:
	.4byte	3                       # 0x3
	.size	c, 4

	.type	d,@object               # @d
	.globl	d
	.p2align	2
d:
	.4byte	4                       # 0x4
	.size	d, 4

	.type	e,@object               # @e
	.globl	e
	.p2align	2
e:
	.4byte	5                       # 0x5
	.size	e, 4

	.ident	"Ubuntu clang version 10.0.1-++20211003085942+ef32c611aa21-1~exp1~20211003090334.2"
	.section	".note.GNU-stack","",@progbits
	.addrsig
	.addrsig_sym fib
	.text

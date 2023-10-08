package ir.instrs;

/*
    calculate address
    <result> = getelemeterptr <ty>, <ty>* <ptrval>, {<ty> <index>}*
    第一个<ty>表示指针所指向的类型
    第二个<ty>表示后面的指针基址<ptrval>的类型
    <ty> <index> 表示一组索引的类型和值，其中第一个索引不会改变返回的指针的类型，
        其偏移量由索引的值和指针指向的类型共同觉得，接下来每增加一个索引，就会使该
        索引使用的基本类型和返回的指针的类型指向原类型的元素。
 */
public class GetElementPtr extends Instr {

}

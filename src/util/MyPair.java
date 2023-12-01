package util;

public class MyPair<T1, T2>{
    private T1 first;
    private T2 second;
    public MyPair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    public T1 getFirst() { return first; }
    public T2 getSecond() { return second; }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyPair<?, ?>))
            return false;
        MyPair<?, ?> objPair = (MyPair<?, ?>) obj;
        return first.equals(objPair.getFirst()) && second.equals(objPair.getSecond());
    }
    @Override
    public int hashCode() {
        return first.hashCode()*107 + second.hashCode();
    }
}

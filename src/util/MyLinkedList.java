package util;

import java.util.Iterator;

public class MyLinkedList<T extends  MyLinkedNode> implements Iterable<T> {
    private T head;
    private T tail;
    public MyLinkedList() {
        head = (T) new MyLinkedNode();
        tail = (T) new MyLinkedNode();
        head.setNext(tail);
        tail.setPrev(head);
    }
    public boolean isEmpty() {
        int size = 0;
        T now = (T) head.getNext();
        while (now != tail) {
            size++;
            now = (T) now.getNext();
        }
        return size == 0;
    }
    public int size() {
        int size = 0;
        T now = (T) head.getNext();
        while (now != tail) {
            size++;
            now = (T) now.getNext();
        }
        return size;
    }
    public T getHead() { return (T) this.head.getNext(); }
    public T getTail() { return (T)this.tail.getPrev(); }
    public T get(int idx) {
        int i = 0;
        T now = (T) head.getNext();
        while (now != tail) {
            if (i == idx) return now;
            now = (T) now.getNext();
            i++;
        }
        return null;
    }
    public void clear() {
        head.setNext(tail);
        tail.setPrev(head);
    }
    public boolean contains(T node) {
        T cur = (T) head.getNext();
        while (cur != node && cur != tail) {
            cur = (T)head.getNext();
        }
        if (cur == node) { return true; }
        else { return false; }
    }
    public void insertBefore(T node, T before) {
        if (head == before) { insertAtHead(node); }
        else {
            assert contains(before);
            before.insertBefore(node);
        }
    }
    public void insertAfter(T node, T after) {
        if (tail == after) { insertAtTail(node); }
        else {
            assert contains(after);
            after.insertAfter(node);
        }
    }
    public void insertAtTail(T node) {
        tail.insertBefore(node);
    }
    public void insertAtHead(T node) {
        head.insertAfter(node);
    }
    public void remove(T node) {
        node.remove();
    }
    public void addAll(MyLinkedList<T> list) {
        if (list.isEmpty()) return;
        tail.getPrev().setNext(list.getHead());
        list.getHead().setPrev(tail.getPrev());
        tail = list.tail;
    }
    public Iterator<T> iterator() { return new MyIterator();}
    public class MyIterator implements Iterator<T> {
        private T cur = head;
        @Override
        public boolean hasNext() { return cur.getNext() != tail; }
        public boolean hasPrev() { return cur.getPrev() != head; }
        @Override
        public T next() {
            assert !hasNext();
            cur = (T) cur.getNext();
            return cur;
        }
        @Override
        public  void remove() {
            assert null == cur;
            MyLinkedList.this.remove(cur);
        }
    }
}

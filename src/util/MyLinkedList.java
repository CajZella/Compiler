package util;

import java.util.Iterator;

public class MyLinkedList<T extends  MyLinkedNode> implements Iterable<T> {
    private T head;
    private T tail;
    private int size;
    public MyLinkedList() {
        head = (T) new MyLinkedNode();
        tail = (T) new MyLinkedNode();
        head.setNext(tail);
        tail.setPrev(head);
        size = 0;
    }
    public MyLinkedList(MyLinkedList<T> list) {
        addAll(list);
    }
    public boolean isEmpty() { return this.size == 0; }
    public int size() { return this.size; }
    public T getHead() { return (T) this.head.getNext(); }
    public T getTail() { return (T)this.tail.getPrev(); }
    public void clear() {
        head.setNext(tail);
        tail.setPrev(head);
        size = 0;
    }
    public boolean contains(T node) {
        if (size == 0) return false;
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
            size++;
        }
    }
    public void insertAfter(T node, T after) {
        if (tail == after) { insertAtTail(node); }
        else {
            assert contains(after);
            after.insertAfter(node);
            size++;
        }
    }
    public void insertAtTail(T node) {
        tail.insertBefore(node);
        size++;
    }
    public void insertAtHead(T node) {
        head.insertAfter(node);
        size++;
    }
    public void remove(T node) {
        node.remove();
        size--;
    }
    public void addAll(MyLinkedList<T> list) {
        if (list.isEmpty()) return;
        tail.getPrev().setNext(list.getHead().getNext());
        list.getHead().getNext().setPrev(tail.getPrev());
        tail = list.tail;
        size += list.size();

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

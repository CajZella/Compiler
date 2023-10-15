package util;

import java.util.Iterator;

public class MyLinkedList<T extends  MyLinkedNode> implements Iterable<T> {
    private T head = null;
    private T tail = null;
    private int size = 0;
    public MyLinkedList() {}
    public MyLinkedList(MyLinkedList<T> list) {
        addAll(list);
    }
    public boolean isEmpty() { return this.size == 0; }
    public int size() { return this.size; }
    public T getHead() { return this.head; }
    public T getTail() { return this.tail; }
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    public T contains(T node) {
        if (size == 0) return null;
        T cur = head;
        while (cur != node && null != cur) {
            cur = (T)head.getNext();
        }
        if (cur == node) { return cur; }
        else { return null; }
    }
    public void insertBefore(T node, T before) {
        if (head == before) { insertAtHead(node); }
        else {
            assert null != contains(before);
            before.insertBefore(node);
            size++;
        }
    }
    public void insertAfter(T node, T after) {
        if (tail == after) { insertAtTail(node); }
        else {
            assert null != contains(after);
            after.insertAfter(node);
            size++;
        }
    }
    public void insertAtTail(T node) {
        if (isEmpty()) {
            head = tail = node;
        } else {
            tail.insertAfter(node);
            tail = node;
        }
        size++;
    }
    public void insertAtHead(T node) {
        if (isEmpty()) {
            head = tail = node;
        } else {
            head.insertBefore(node);
            head = node;
        }
        size++;
    }
    public void remove(T node) {
        if (head == node) { head = (T)node.getNext(); }
        else if (tail == node) { tail = (T)node.getPrev(); }
        node.remove();
        size--;
    }
    public void addAll(MyLinkedList<T> list) {
        if (list.isEmpty()) return;
        if (isEmpty()) {
            head = list.getHead();
        } else {
            tail.setNext(list.getHead());
            list.getHead().setPrev(tail);
        }
        tail = list.getTail();
        size += list.size();

    }
    public Iterator<T> iterator() { return new MyIterator();}
    public class MyIterator implements Iterator<T> {
        private T next = head;
        private T cur = null;
        @Override
        public boolean hasNext() { return null != next; }
        @Override
        public T next() {
            assert !hasNext();
            cur = next;
            next = (T)next.getNext();
            return cur;
        }
        @Override
        public  void remove() {
            assert null == cur;
            MyLinkedList.this.remove(cur);
            cur = null;
        }
    }
}

package util;

import java.util.Iterator;

public class MyLinkedList<T extends  MyLinkedNode> implements Iterable<T> {
    private T head = null;
    private T tail = null;
    private int size = 0;
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
            head = node;
            tail = node;
        } else {
            tail.insertAfter(node);
            tail = node;
        }
        size++;
    }
    public void insertAtHead(T node) {
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            head.insertBefore(node);
            head = node;
        }
        size++;
    }
    public void remove(T node) {
        if (size == 1 && head == node) { clear(); }
        else if (head == node) { head = (T)node.getNext(); }
        else if (tail == node) { tail = (T)node.getPrev(); }
        node.remove();
        size--;
    }
    public void addAll(MyLinkedList<T> list) {
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            insertAtTail(iterator.next());
        }
    }
    public Iterator<T> iterator() { return new MyIterator();}
    public class MyIterator implements Iterator<T> {
        T cur = head;
        @Override
        public boolean hasNext() {
            return cur.hasNext();
        }
        @Override
        public T next() {
            cur = (T)cur.getNext();
            return cur;
        }
        @Override
        public  void remove() {
            if (size == 1 && head == cur) { clear(); }
            else if (head == cur) head = (T)cur.getNext();
            else if (tail == cur) tail = (T)cur.getPrev();
            cur.remove();
            size--;
        }
    }
}

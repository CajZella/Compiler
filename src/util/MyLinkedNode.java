package util;

public class MyLinkedNode {
    private MyLinkedNode next = null;
    private MyLinkedNode prev = null;
    public boolean hasPrev() { return prev != null; }
    public boolean hasNext() { return next != null; }
    public MyLinkedNode getNext() { return this.next; }
    public MyLinkedNode getPrev() { return this.prev; }
    public void setNext(MyLinkedNode node) { this.next = node; }
    public void setPrev(MyLinkedNode node) { this.prev = node; }
    public void insertBefore(MyLinkedNode node) {
        if (hasPrev())
            this.prev.next = node;
        node.prev = this.prev;
        this.prev = node;
        node.next = this;
    }
    public void insertAfter(MyLinkedNode node) {
        node.prev = this;
        node.next = this.next;
        if (hasNext()) {
            this.next.prev = node;
        }
        this.next = node;
    }
    public void remove() {
        if (hasPrev()) {
            this.prev.next = this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        }
    }
}

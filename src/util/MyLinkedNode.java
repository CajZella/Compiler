package util;

public abstract class MyLinkedNode {
    private MyLinkedNode next = null;
    private MyLinkedNode prev = null;
    public boolean hasNext() { return this.next != null; }
    public boolean hasPrev() { return this.prev != null; }
    public MyLinkedNode getNext() { return this.next; }
    public MyLinkedNode getPrev() { return this.prev; }
    public void insertBefore(MyLinkedNode node) {
        if (this.prev != null) {
            this.prev.next = node;
            node.prev = this.prev;
        }
        this.prev = node;
        node.next = this;
    }
    public void insertAfter(MyLinkedNode node) {
        if (this.next != null) {
            this.next.prev = node;
            node.next = this.next;
        }
        this.next = node;
        node.prev = this;
    }
    public void remove() {
        if (this.prev != null) {
            this.prev.next = this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        }
    }
}

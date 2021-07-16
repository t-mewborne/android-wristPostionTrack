package com.example.bluetoothwristpostiontracker;

public class MyQueueNode<T> {
    private MyQueueNode prev,next;
    private T object;

    public MyQueueNode(MyQueueNode prev, MyQueueNode next, T object) {
        this.prev = prev;
        this.next = next;
        this.object = object;
    }

    public MyQueueNode getNext() {
        return next;
    }

    public MyQueueNode getPrev() {
        return prev;
    }

    public void setPrev(MyQueueNode other) {
        prev = other;
    }

    public void setNext(MyQueueNode other) {
        next = other;
    }

    public T get() {
        return object;
    }
}

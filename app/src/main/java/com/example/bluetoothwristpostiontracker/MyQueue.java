package com.example.bluetoothwristpostiontracker;

public class MyQueue<T> {
    private int size;
    private MyQueueNode<T> front,end;
    private String debugTag="MyQueue";

    public MyQueue() {
        front = null;
        end = null;
        size=0;
    }

    public void enqueue(T object) {
        MyQueueNode<T> node = new MyQueueNode<T>(null,end,object);

        if (!isEmpty()) end.setPrev(node);
        else front = node;

        end = node;
        size++;
    }

    public T dequeue() {
        //Node<T> node = front;
        T object = front.get();
        if(object != null) {
            front = front.getPrev();
            if (front != null) front.setNext(null);
            size--;
        }
        return object;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public int getSize() {
        return size;
    }
}

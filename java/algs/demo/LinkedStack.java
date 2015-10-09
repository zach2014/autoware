/*
 * LinkedStack.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import java.util.Iterator;

public class LinkedStack<T> implements Iterable <T>
{
    private class Node {
        T item;
        Node next;
    }

    private Node first;

	public LinkedStack() {
	    first = null;	
	}

    public T pop(){
        if(isEmpty()){
            throw new IllegalStateException("Empty stack");
        }    
        Node cur = first;
        first = first.next;
        return cur.item;
    }

    public void push(T item){
       Node newFirst  = new Node();
       newFirst.item = item;
       newFirst.next = first;
       first = newFirst;
    }
    
    public boolean isEmpty(){
        return null == first;
    }

    public Iterator<T> iterator(){
        return new LinkedStackIterator();
    }

    class LinkedStackIterator implements Iterator<T>{
        private Node cur = first; 
        public boolean hasNext(){ return cur != null; }
        public T next() {
            T item = cur.item;
            cur = cur.next;
            return item;
        }
    }

}


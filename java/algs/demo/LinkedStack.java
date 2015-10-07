/*
 * LinkedStack.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

public class LinkedStack<T>
{
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

    private class Node {
        T item;
        Node next;
    }
}


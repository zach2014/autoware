/*
 * FixedCapStack.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import java.util.Iterator;

public class FixedCapStack<T> implements Iterable<T>
{
    final private T[] stack;
    private int top;

	public FixedCapStack(int cap) {
        //@SuppressWarnings("unchecked")
	    stack = (T[]) new Object[cap]; 
        top = 0;
	}

    public Iterator<T> iterator(){
        return new ArrayStackIterator();
    }

    class ArrayStackIterator implements Iterator<T> {
        int cur = top - 1;

        public boolean hasNext(){ return cur >= 0; }
        public T next(){
            return stack[cur--];
        }
    }

    public T pop(){
        if(isEmpty())
            throw new IllegalStateException("Stack is empty.");
        return stack[--top];
    }

    public void push(T item){
        if(isFull())
            throw new IllegalStateException("Stack is full.");
        stack[top++] = item;
    }

    private boolean isEmpty(){
        return top == 0; 
    }
    
    private boolean isFull() {
        return top == stack.length;
    }
}


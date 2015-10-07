/*
 * FixedCapStack.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

public class FixedCapStack<T>
{
    final private T[] stack;
    private int top;

	public FixedCapStack(int cap) {
        //@SuppressWarnings("unchecked")
	    stack = (T[]) new Object[cap]; 
        top = 0;
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


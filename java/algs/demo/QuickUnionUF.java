/*
 * QuickUnionUF.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

public class QuickUnionUF
{
    private int[] idTree;
	public QuickUnionUF(int N) {
		idTree = new int[N];
        for(int n = 0; n < idTree.length; n++){
            idTree[n] = n;
        }
	}

    public boolean isConnected(int p, int q){
        return root(p) == root(q);
    }

    public void union(int p , int q){
        if(!inRange(p)){ 
            throw new IllegalArgumentException("Not in range(0"+ (idTree.length-1) + ")" + p); } 
        if(!inRange(q)){ 
            throw new IllegalArgumentException("Not in range(0"+ (idTree.length-1) + ")"+ q); } 
        idTree[p] = root(q); 
    }

    private int root(int n){
        if(!inRange(n)){ 
            throw new IllegalArgumentException("Not in range(0"+ (idTree.length-1) + ")" + n); } 
        while(idTree[n] != n){
            idTree[n] = idTree[idTree[n]]; // compress path to near root
            n = idTree[n];
        }
        return n;
    }

    private boolean inRange(int n){
        return (n >= 0 && n < idTree.length);
    }
}


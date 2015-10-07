/*
 * QuickFindUF.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */

public class QuickFindUF
{
	private int[] all; 
	public QuickFindUF(int size) {
	    all = new int[size];
	    for(int n = 0; n < size; n++){
	    	all[n] = n;
	    }
	}

	public boolean isConnected(int i, int j){
	    if(!inRange(i)){
	    	throw new IllegalArgumentException("Out of range(0--" + all.length + "): " + Integer.toString(i));
	    }
	    if(!inRange(j)){
	    	throw new IllegalArgumentException("Out of range(0--" + all.length + "): " + Integer.toString(j));
	    }
	    return all[i] == all[j];
	}	

	public void union(int i , int j){
	    if(!inRange(i)){
	    	throw new IllegalArgumentException("Out of range(0--" + all.length + "): " + Integer.toString(i));
	    }
	    if(!inRange(j)){
	    	throw new IllegalArgumentException("Out of range(0--" + all.length + "): " + Integer.toString(j));
	    }
	    if(! (i == j)){
	    	if(!(all[i] == all[j])){
		    int oldSet = all[i];
		    int set = all[j];
		    for(int n = 0; n < all.length; n ++ ){
		    	if(oldSet == all[n]){
			    all[n] = set;
			}
		    }
		}
	    }
	}

	private boolean inRange(int r){
	    return (r >= 0) && (r < all.length);
	}

}

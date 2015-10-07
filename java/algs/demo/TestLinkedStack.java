/*
 * TestLinkedStack.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class TestLinkedStack
{
    private static String[] data = { "This", "is", "just", "a", "test"};
    private LinkedStack<String> onTest;
   
    @Before
	public void setUp() {
	    onTest = new LinkedStack<String>();	
	}

    @After
    public void tearDown(){
        onTest = null;
    }

    @Test
    public void should_filo(){
        String tmp = null;
        for(String str: data){
            onTest.push(str);
            tmp = str;
        }
        assertTrue(tmp.equals(onTest.pop()));
    }

    @Test (expected= IllegalStateException.class)
    public void fail_pop_for_empty(){
        onTest.pop();
    }
}


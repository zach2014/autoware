/*
 * TestFixedCapStack.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class TestFixedCapStack
{
    private static String[] data = {"This", "is", "just", "a", "test", "."};
    private static int size = 10;
    private FixedCapStack<String> onTest;
    
    @Before
    public void setUp(){
        onTest = new FixedCapStack<String>(size);
    }

    @After
    public void tearDown(){
        onTest = null;
    }

    @Test
	public void should_filo() {
        for(String str: data) onTest.push(str);
        assertTrue(".".equals(onTest.pop()));
	}
    
    @Test (expected= IllegalStateException.class)
    public void fail_pop_for_empty(){
        for(String str: data){
            onTest.push(str);
            onTest.pop();
        } 
        onTest.pop();
    }
    
    @Test (expected= IllegalStateException.class)
    public void fail_push_for_full(){
        for(String str: data){
            onTest.push(str);
            onTest.push(str);
        }
    }

    @Test (expected= IllegalStateException.class)
    public void should_be_iterable(){
        FixedCapStack<String> copy = new FixedCapStack<String>(size);
        for(String i : onTest){
            copy.push(i);
        }
        boolean allMatched = true;
        for(String str : data){
            allMatched = str.equals(copy.pop());
        }
        assertTrue(allMatched);
        
    }
}


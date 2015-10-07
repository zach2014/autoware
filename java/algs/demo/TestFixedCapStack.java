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
    private FixedCapStack onTest;
    
    @Before
    public void setUp(){
        onTest = new FixedCapStack(size);
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
}


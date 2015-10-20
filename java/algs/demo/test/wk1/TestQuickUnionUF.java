/*
 * TestQuickUnionUF.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class TestQuickUnionUF
{
    private static int size = 10;
    private QuickUnionUF onTest;
	@Before
    public void setUp() {
		onTest = new QuickUnionUF(size);
	}

    @After
    public void tearDown(){
        onTest = null;    
    }

    @Test
    public void all_is_sperated_init(){
        boolean connected= false;
        for(int i = 1; i < size; i++ ){
            connected = onTest.isConnected(i-1, i);
            if(connected) break;
        }
        assertFalse(connected);
    }
    
    @Test
    public void should_connected_aft_union(){
        onTest.union(3, 5);
        assertTrue(onTest.isConnected(3, 5));
    }

    @Test
    public void multiple_union_work(){
        onTest.union(3, 5);
        onTest.union(5, 6);
        assertTrue(onTest.isConnected(3, 6));
    }

    @Test (expected= IllegalArgumentException.class)
    public void should_check_range(){
        onTest.union(6, (size+3));
    }
}


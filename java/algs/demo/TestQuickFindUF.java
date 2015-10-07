/*
 * TestQuickFindUF.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class TestQuickFindUF
{
    private QuickFindUF onTest;

    @Before
    public void init(){
        onTest = new QuickFindUF(10);
    }

    @After
    public void cleanUp(){
        onTest = null;
    }

    @Test
    public void all_is_sperated(){
        assertFalse(onTest.isConnected(2, 8));
    }

    @Test
    public void testConnected(){
        onTest.union(1, 6);
        assertTrue(onTest.isConnected(1, 6));
    }

    @Test (expected= IllegalArgumentException.class) 
    public void invalid_check_for_union(){
        onTest.union(3, 11);
    }

    @Test (expected= IllegalArgumentException.class)
    public void invalid_check_for_isConnected(){
        onTest.isConnected(3, 11);
    } 
}


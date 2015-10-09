/*
 * DemoTestSuite.java
 * Copyright (C) 2015 zach <zacharyzjp@gmail.com>
 *
 * Distributed under terms of the MIT license.
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
    {
        TestLinkedStack.class,
        TestFixedCapStack.class,
        TestQuickFindUF.class,
        TestQuickUnionUF.class
        
    }

)
public class DemoTestSuite
{
	public DemoTestSuite() {
		
	}
}


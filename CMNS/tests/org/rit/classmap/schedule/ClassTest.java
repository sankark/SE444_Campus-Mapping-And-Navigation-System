package org.rit.classmap.schedule;

import java.util.Calendar;

import junit.framework.TestCase;

public class ClassTest extends TestCase 
{
	public void testHasEnded() 
	{
		Calendar now = Calendar.getInstance();
		Class test = new Class( "TestClass-1", now.getTime( ), 
				Calendar.DATE, Frequency.WEEKLY );
		
		now.add( Calendar.DATE, 40 );
		assertTrue( test.hasEnded() );
	}

	public void testNextClass() 
	{
		Calendar now = Calendar.getInstance();
		Class test = new Class( "TestClass-2", now.getTime( ),
				Calendar.HOUR*2, Frequency.WEEKLY );
		
	}
}

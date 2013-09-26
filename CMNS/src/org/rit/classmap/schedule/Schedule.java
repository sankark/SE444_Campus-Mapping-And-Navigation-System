package org.rit.classmap.schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: Schedule assumes repeating every week (identical schedule)
 * @author RyanBelair
 */
public class Schedule
{
	private final List<Class> classes = new ArrayList<Class>( );
	
	public Schedule( )
	{
		
	}
	
	public synchronized boolean addClass( Class c )
	{
		boolean stat = true;
		try
		{
			classes.add( c );
		}
		catch( Exception e )
		{
			stat = false;
		}
		return stat;
	}
	
	public synchronized boolean removeClass( Class c )
	{
		boolean stat = true;
		try
		{
			classes.remove( c );
		}
		catch( Exception e )
		{
			stat = false;
		}
		return stat;
	}
}

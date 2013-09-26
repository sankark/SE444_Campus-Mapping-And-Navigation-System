/**
 * File: Class.java
 * Author(s): Ryan Belair
 * Description: This class represents a class for a user's schedule.
 * 
 * Date: 16 April 2013
 */
package org.rit.classmap.schedule;

import java.util.Calendar;
import java.util.Date;

public class Class 
{
	/**
	 * Private instance data, in case we want to modify the length
	 * or frequency of a Class dynamically
	 */
	private String 		 title;
	private Frequency 	 frequency;
	private long 		 duration;
	private Date		 start;
	
	public Class( String title, Date start, long duration, Frequency frequency )
	{
		this.title 		= title;
		this.start		= start;
		this.duration	= duration;
		this.frequency 	= frequency;
	}
	
	public boolean compareTo( Class c )
	{
		boolean stat = true;
		if( !this.title.equals( c.title ) )
		{
			stat = false;
		}
		if( !this.start.equals( c.start ) );
		{
			stat = false;
		}
		if( !(this.duration == c.duration)  )
		{
			stat = false;
		}
		if( !this.frequency.equals( c.frequency ) )
		{
			stat = false;
		}
		return stat;
	}
	
	/**
	 * Checks the START time of a class and if it is at some point in the 
	 * future return true;
	 * @return
	 */
	public boolean hasEnded( )
	{
		return( !this.start.after( Calendar.getInstance( ).getTime( ) ) );
	}
	
	/**
	 * Careful for nulls here
	 * @return
	 */
	public Class nextClass( )
	{
		Calendar cal = Calendar.getInstance( );
		cal.setTime( start );
		
		Class next = null;
		switch( frequency )
		{
		case DAILY:
			cal.add( Calendar.DATE, 1 );
			next = new Class( this.title, cal.getTime(), 
					( cal.getTimeInMillis() + duration ), Frequency.DAILY );
			break;
		case WEEKLY:
			cal.add( Calendar.WEEK_OF_MONTH, 1 );
			next = new Class( this.title, cal.getTime(), 
					( cal.getTimeInMillis() + duration ), Frequency.WEEKLY );
			break;
		case MONTHLY:
			cal.add( Calendar.MONTH, 1 );
			next = new Class( this.title, cal.getTime(), 
					( cal.getTimeInMillis() + duration ), Frequency.MONTHLY );
			break;
		case YEARLY:
			cal.add( Calendar.YEAR, 1 );
			next = new Class( this.title, cal.getTime(), 
					( cal.getTimeInMillis() + duration ), Frequency.YEARLY );
			break;
		default:
			// nothing
			break;
		}
		return next;
	}
}

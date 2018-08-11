package org.structuredschema;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Errors
{
	private final Errors parent;
	private final List<Object> list;
	private final Object step;
	private final Object value;

	public Errors( Object value )
	{
		this( null, new LinkedList<>( ), null, value );
	}

	public Errors( Errors parent, List<Object> list, Object step, Object value )
	{
		this.parent = parent;
		this.list = list;
		this.step = step;
		this.value = value;
	}

	public List<Object> toList( )
	{
		return list;
	}
	
	public boolean isEmpty( )
	{
		return list.isEmpty( );
	}

	public Errors field( String name, Object val )
	{
		return new Errors( this, list, name, val );
	}

	public Errors item( int index, Object val )
	{
		return new Errors( this, list, index, val );
	}
	
	public Errors detach( )
	{
		return new Errors( parent, new LinkedList<>( ), step, value );
	}
	
	public void repend( Errors errors )
	{
		list.addAll( errors.list );
	}

	public void add( String msg )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "value", value );
		err.put( "msg", msg );
		list.add( err );
	}

	private List<Object> path( )
	{
		if ( parent == null )
		{
			return new LinkedList<>( );
		}
		else
		{
			List<Object> result = parent.path( );
			result.add( step );
			return result;
		}
	}
}

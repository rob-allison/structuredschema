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

	public Errors( )
	{
		this( null, new LinkedList<>( ), null );
	}

	public Errors( Errors parent, List<Object> list, Object step )
	{
		this.parent = parent;
		this.list = list;
		this.step = step;
	}

	public List<Object> toList( )
	{
		return list;
	}
	
	public boolean isEmpty( )
	{
		return list.isEmpty( );
	}

	public Errors field( String name )
	{
		return new Errors( this, list, name );
	}

	public Errors item( int index )
	{
		return new Errors( this, list, index );
	}
	
	public Errors detach( )
	{
		return new Errors( parent, new LinkedList<>( ), step );
	}
	
	public void repend( Errors errors )
	{
		list.addAll( errors.list );
	}
	
	
	public void missingField( String name )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "fieldname", name );
		err.put( "msg", "missing_field" );
		list.add( err );
	}
	
	public void extraField( String name )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "fieldname", name );
		err.put( "msg", "extra_field" );
		list.add( err );
	}
	
	public void missingItem( int index )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "index", index );
		err.put( "msg", "missing_item" );
		list.add( err );
	}
	
	public void extraItem( int index )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "index", index );
		err.put( "msg", "extra_item" );
		list.add( err );
	}
	
	public void invalidValue( Object value, Object type )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "value", value );
		err.put( "type", type );
		err.put( "msg", "invalid_value" );
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

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

	public void missingField( String name )
	{
		Map<String,Object> err = error( "missing_field" );
		err.put( "fieldname", name );
		list.add( err );
	}

	public void extraField( String name )
	{
		Map<String,Object> err = error( "extra_field" );
		err.put( "fieldname", name );
		list.add( err );
	}

	public void missingItem( int index )
	{
		Map<String,Object> err = error( "missing_item" );
		err.put( "index", index );
		list.add( err );
	}

	public void extraItem( int index )
	{
		Map<String,Object> err = error( "extra_item" );
		err.put( "index", index );
		list.add( err );
	}

	public void invalidValue( Object value, Object type )
	{
		Map<String,Object> err = error( "invalid_value" );
		err.put( "value", value );
		err.put( "type", type );
		list.add( err );
	}
	
	public void invalidValue( Object value, Object type, List<Object> union )
	{
		Map<String,Object> err = error( "invalid_value" );
		err.put( "value", value );
		err.put( "type", type );
		err.put( "union", union );
		list.add( err );
	}

	private Map<String,Object> error( String msg )
	{
		Map<String,Object> err = new HashMap<>( );
		err.put( "path", path( ) );
		err.put( "message", msg );
		return err;
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

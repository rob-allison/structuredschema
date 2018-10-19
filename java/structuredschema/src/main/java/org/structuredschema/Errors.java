package org.structuredschema;

import java.util.LinkedHashMap;
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
	
	public void reattach( Errors errors )
	{
		list.addAll( errors.list );
	}

	public void missingField( String name )
	{
		error( "missing-field", name );
	}

	public void extraField( String name )
	{
		error( "extra-field", name );
	}

	public void missingItem( int index )
	{
		error( "missing-item", index );
	}

	public void extraItem( int index )
	{
		error( "extra-item", index );
	}

	public void unmatchedType( Object value, Object type )
	{
		error( "unmatched-type", type );
	}
	
	public void unmatchedUnion( Object value, Object type )
	{
		error( "unmatched-union", type );
	}

	private void error( String code, Object is )
	{
		Map<String,Object> err = new LinkedHashMap<>( );
		err.put( "code", code );
		err.put( "is", is );
		err.put( "path", path( ) );
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

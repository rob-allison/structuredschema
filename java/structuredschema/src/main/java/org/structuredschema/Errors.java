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

	public void missingField( Object value, String name )
	{
		error( "MISSING_FIELD", value, name );
	}

	public void extraField( Object value, String name )
	{
		error( "EXTRA_FIELD", value, name );
	}

	public void missingItem( Object value, int index )
	{
		error( "MISSING_ITEM", value, index );
	}

	public void extraItem( Object value, int index )
	{
		error( "EXTRA_ITEM", value, index );
	}

	public void unmatchedType( Object value, Object type )
	{
		error( "UNMATCHED_TYPE", value, type );
	}
	
	public void badDiscriminator( Object value, String field )
	{
		error( "BAD_DISCRIMINATOR", value, field );
	}
	
	public void objectExpected( Object value )
	{
		unmatchedType( value, "Object" );
	}
	
	public void arrayExpected( Object value )
	{
		unmatchedType( value, "Array" );
	}
	
	public void unmatchedUnion( Object value, String type )
	{
		error( "UNMATCHED_UNION", value, type );
	}

	private void error( String code, Object value, Object is )
	{
		Map<String,Object> err = new LinkedHashMap<>( );
		err.put( "path", pathstring( ) );
		err.put( "code", code );
		err.put( "is", is );
		err.put( "value", StructuredSchema.copy( value ) );
		list.add( err );
	}
	
	private String pathstring( )
	{
		boolean predot = false;
		List<Object> path = path( );
		StringBuilder builder = new StringBuilder( );
		for ( Object step : path )
		{
			if ( step instanceof String )
			{
				String name = (String)step;
				if ( predot )
				{
					builder.append( '.' );
				}
				builder.append( name );
				predot = true;
			}
			else if ( step instanceof Integer )
			{
				Integer index = (Integer)step;
				builder.append( '[' );
				builder.append( index );
				builder.append( ']' );
			}
			else
			{
				throw new RuntimeException( "bad path" );
			}
		}
		return builder.toString( );
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

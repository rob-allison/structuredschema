package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;

public class NamedRange extends RangeExpression
{
	private final String name;

	public NamedRange( String name )
	{
		this.name = name;
	}

	public String getName( )
	{
		return name;
	}

	@Override
	public boolean isNamedRange( )
	{
		return true;
	}

	@Override
	public boolean validate( Object obj )
	{
		throw new UnsupportedOperationException( );
	}

	@Override
	public RangeExpression replaceRangeName( String nm, RangeExpression expression )
	{
		if ( name.equals( nm ) )
		{
			return expression;
		}
		else
		{
			return this;
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( name );
	}
	
	public static String regex( )
	{
		return "[\\p{Alpha}-_]*";
	}
	
	public static NamedRange parseNamed( String str )
	{
		return new NamedRange( str );
	}
	
}

package org.structuredschema;

import java.io.IOException;
import java.io.Writer;

public class BooleanValue extends TypeLiteral
{
	private final boolean value;
	private final Object definition;

	public BooleanValue( boolean value )
	{
		this( value, value );
	}

	public BooleanValue( boolean value, Object definition )
	{
		this.value = value;
		this.definition = definition;
	}

	@Override
	public boolean validate( Object obj )
	{
		if ( obj != null && obj instanceof Boolean )
		{
			boolean b = (Boolean)obj;
			return b == value;
		}
		return false;
	}

	public static String regex( )
	{
		return "true|false";
	}

	public static BooleanValue parseBoolean( String expr )
	{
		if ( expr.equals( "true" ) )
		{
			return new BooleanValue( true, expr );
		}
		else if ( expr.equals( "false" ) )
		{
			return new BooleanValue( false, expr );
		}
		else
		{
			throw new RuntimeException( "bad string" + expr );
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( value ? "true" : "false" );
	}

	@Override
	public Object toDefinition( )
	{
		return definition;
	}
}
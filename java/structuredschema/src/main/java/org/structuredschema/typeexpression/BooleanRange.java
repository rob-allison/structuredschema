package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;

public class BooleanRange extends TypeLiteral
{
	private final Boolean value;

	public BooleanRange( Boolean value )
	{
		this.value = value;
	}

	@Override
	public boolean validate( Object obj )
	{
		boolean b = (Boolean)obj;
		return value != null ? b == value : true;
	}
	
	public static String regex( )
	{
		return "true|false|any";
	}

	public static BooleanRange parseBoolean( String expr )
	{
		if ( expr.equals( "true" ) )
		{
			return new BooleanRange( true );
		}
		else if ( expr.equals( "false" ) )
		{
			return new BooleanRange( false );
		}
		else if ( expr.equals( "any" ) )
		{
			return new BooleanRange( null );
		}
		else
		{
			throw new RuntimeException( "bad string" + expr );
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( value != null ? (value ? "true" : "false") : "any" );
	}
}

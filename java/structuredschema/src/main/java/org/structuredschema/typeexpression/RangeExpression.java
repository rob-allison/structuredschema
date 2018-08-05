package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public abstract class RangeExpression
{
	public abstract boolean isNamedRange( );

	public abstract boolean validate( Object obj );

	public abstract RangeExpression replaceRangeName( String name, RangeExpression expression );
	
	public abstract void compose( Writer writer ) throws IOException;

	public static RangeExpression parse( Reader reader ) throws IOException
	{
		StringBuilder builder = new StringBuilder( );
		int a = reader.read( );
		switch ( a )
		{
			case -1:
				throw new RuntimeException( "} expected" );
			case '}':
				throw new RuntimeException( "empty range expression" );
			case '/':
				return Regex.parseRegex( reader );
			default:
				builder.append( (char)a );
		}

		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case -1:
					throw new RuntimeException( "} expected" );
				case '}':
					String expr = builder.toString( );
					if ( expr.matches( IntegerRange.regex( ) ))
					{
						return IntegerRange.parseRange( expr );
					}
					else if ( expr.matches( DecimalRange.regex( ) ) )
					{
						return DecimalRange.parseRange( expr );
					}
					else if ( expr.matches( NamedRange.regex( ) ) )
					{
						return NamedRange.parseNamed( expr );
					}
					else if ( expr.matches( BooleanRange.regex( ) ) )
					{
						return BooleanRange.parseBoolean( expr );
					}
					else
					{
						throw new RuntimeException( "bad range expression: " + expr );
					}
				default:
					builder.append( (char)c );
			}
		}
	}

}
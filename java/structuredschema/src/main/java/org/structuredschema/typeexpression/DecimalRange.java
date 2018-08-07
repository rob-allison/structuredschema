package org.structuredschema.typeexpression;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class DecimalRange extends TypeLiteral
{
	public static String regex( )
	{
		return "(DECIMAL|INTERVAL)(,(DECIMAL|INTERVAL))*".replace( "DECIMAL", DecimalValue.regex( ) ).replace( "INTERVAL", DecimalInterval.regex( ) );
	}

	public static TypeLiteral parseRange( String expr )
	{
		StringTokenizer toks = new StringTokenizer( expr, "," );
		if ( toks.countTokens( ) == 0 )
		{
			throw new RuntimeException( "bad expr" );
		}
		else
		{
			if ( toks.countTokens( ) == 1 )
			{
				String tok = toks.nextToken( );
				return parseIntervalOrValue( tok );
			}
			else
			{
				List<DecimalRange> ranges = new LinkedList<>( );
				while ( toks.hasMoreTokens( ) )
				{
					String tok = toks.nextToken( );
					ranges.add( parseIntervalOrValue( tok ) );
				}
				return new DecimalRangeList( ranges );
			}
		}
	}

	private static DecimalRange parseIntervalOrValue( String tok )
	{
		if ( tok.matches( DecimalValue.regex( ) ) )
		{
			return DecimalValue.parseValue( tok );
		}
		else
		{
			return DecimalInterval.parseInterval( tok );
		}
	}
}

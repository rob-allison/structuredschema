package org.structuredschema.typeexpression;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class IntegerRange extends TypeLiteral
{
	public static String regex( )
	{
		return "(INTEGER|INTERVAL)(,(INTEGER|INTERVAL))*".replace( "INTEGER", IntegerValue.regex( ) ).replace( "INTERVAL", IntegerInterval.regex( ) );
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
				List<IntegerRange> ranges = new LinkedList<>( );
				while ( toks.hasMoreTokens( ) )
				{
					String tok = toks.nextToken( );
					ranges.add( parseIntervalOrValue( tok ) );
				}
				return new IntegerRangeList( ranges );
			}
		}
	}

	private static IntegerRange parseIntervalOrValue( String tok )
	{
		if ( tok.matches( IntegerValue.regex( ) ) )
		{
			return IntegerValue.parseValue( tok );
		}
		else
		{
			return IntegerInterval.parseInterval( tok );
		}
	}
}

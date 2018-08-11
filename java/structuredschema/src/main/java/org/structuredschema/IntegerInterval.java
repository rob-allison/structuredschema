package org.structuredschema;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerInterval extends TypeLiteral
{
	private final BigInteger low;
	private final BigInteger high;
	private final BigInteger step;

	public IntegerInterval( BigInteger low, BigInteger high, BigInteger step )
	{
		this.low = low;
		this.high = high;
		this.step = step;
	}

	public boolean validate( Object obj )
	{
		BigInteger val = IntegerValue.convert( obj );
		if ( val != null )
		{
			if ( (low != null ? low.compareTo( val ) <= 0 : true) && (high != null ? high.compareTo( val ) >= 0 : true) )
			{
				if ( step != null )
				{
					return val.subtract( low ).remainder( step ).equals( BigInteger.ZERO );
				}
				else
				{
					return true;
				}
			}
		}
		return false;
	}

	public static String regex( )
	{
		return "(INTEGER)?\\.\\.(INTEGER)?(/INTEGER)?".replace( "INTEGER", IntegerValue.regex( ) );
	}

	public static IntegerInterval parseInterval( String str )
	{
		Pattern p = Pattern.compile( "(?<low>\\d*)\\.\\.(?<high>\\d*)(/(?<step>\\d*))?" );
		Matcher m = p.matcher( str );
		if ( m.matches( ) )
		{
			return new IntegerInterval( parseInteger( m.group( "low" ) ), parseInteger( m.group( "high" ) ), parseInteger( m.group( "step" ) ) );
		}
		else
		{
			throw new RuntimeException( "bad str: " + str );
		}
	}

	private static BigInteger parseInteger( String str )
	{
		return str == null || str.isEmpty( ) ? null : new BigInteger( str );
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		if ( low != null )
		{
			writer.write( low.toString( ) );
		}
		writer.write( ".." );
		if ( high != null )
		{
			writer.write( high.toString( ) );
		}
		if ( step != null )
		{
			writer.write( '/' );
			writer.write( step.toString( ) );
		}
	}
}

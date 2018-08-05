package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalInterval extends DecimalRange
{
	private final BigDecimal low;
	private final BigDecimal high;
	private final BigDecimal step;

	public DecimalInterval( BigDecimal low, BigDecimal high, BigDecimal step )
	{
		this.low = low;
		this.high = high;
		this.step = step;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigDecimal val = (BigDecimal)obj;
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
		else
		{
			return false;
		}
	}

	public static DecimalInterval parseInterval( String str )
	{
		Pattern p = Pattern.compile( "(\\d*\\.\\d*)\\.\\.\\.(\\d*\\.\\d*)/(\\d*\\.\\d*)" );
		Matcher m = p.matcher( str );
		if ( m.matches( ) )
		{
			return new DecimalInterval( parseDecimal( m.group( 1 ) ), parseDecimal( m.group( 2 ) ), parseDecimal( m.group( 3 ) ) );
		}
		else
		{
			p = Pattern.compile( "(\\d*\\.\\d*)\\.\\.\\.(\\d*\\.\\d*)" );
			m = p.matcher( str );
			if ( m.matches( ) )
			{
				return new DecimalInterval( parseDecimal( m.group( 1 ) ), parseDecimal( m.group( 2 ) ), null );
			}
			else
			{
				throw new RuntimeException( "bad str: " + str );
			}
		}
	}

	private static BigDecimal parseDecimal( String str )
	{
		return str.isEmpty( ) ? null : new BigDecimal( str );
	}
	
	@Override
	public void compose( Writer writer ) throws IOException
	{
		if ( low != null )
		{
			writer.write( low.toString( ) );
		}
		writer.write( "..." );
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
	
	public static String regex( )
	{
		return "(DECIMAL)?\\.\\.\\.(DECIMAL)?(/DECIMAL)?".replace( "DECIMAL", DecimalValue.regex( ) );
	}
}

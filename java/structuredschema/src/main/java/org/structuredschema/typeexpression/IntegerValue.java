package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;

public class IntegerValue extends TypeLiteral
{
	private final BigInteger value;

	public IntegerValue( BigInteger value )
	{
		this.value = value;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigInteger val = convert( obj );
		if ( val != null )
		{
			return value.equals( val );
		}
		return false;
	}

	public static BigInteger convert( Object obj )
	{
		if ( obj != null )
		{
			if ( obj instanceof BigInteger )
			{
				return (BigInteger)obj;
			}
			if ( obj instanceof Long )
			{
				Long val = (Long)obj;
				return BigInteger.valueOf( val );
			}
			if ( obj instanceof Integer )
			{
				Integer val = (Integer)obj;
				return BigInteger.valueOf( val );
			}
		}
		return null;
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( value.toString( ) );
	}

	public static String regex( )
	{
		return "\\-?\\d+([eE][\\+\\-]?\\d+)?";
	}

	public static IntegerValue parseValue( String str )
	{
		return new IntegerValue( new BigInteger( str ) );
	}
}

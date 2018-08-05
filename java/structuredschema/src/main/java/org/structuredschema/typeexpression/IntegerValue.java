package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;

public class IntegerValue extends IntegerRange
{
	private final BigInteger value;

	public IntegerValue( BigInteger value )
	{
		this.value = value;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigInteger val = (BigInteger)obj;
		return value.equals( val );
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( value.toString( ) );
	}

	public static String regex( )
	{
		return "\\-?\\d+([eE]\\-?\\d+)?";
	}

	public static IntegerValue parseValue( String str )
	{
		return new IntegerValue( new BigInteger( str ) );
	}
}

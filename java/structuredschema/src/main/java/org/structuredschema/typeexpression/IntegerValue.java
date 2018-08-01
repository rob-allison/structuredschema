package org.structuredschema.typeexpression;

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
}

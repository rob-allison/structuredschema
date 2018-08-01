package org.structuredschema.typeexpression;

import java.math.BigInteger;

public class IntegerInterval extends IntegerRange
{
	private final BigInteger low;
	private final BigInteger high;

	public IntegerInterval( BigInteger low, BigInteger high )
	{
		this.low = low;
		this.high = high;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigInteger val = (BigInteger)obj;
		return (low != null ? low.compareTo( val ) <= 0 : true) && (high != null ? high.compareTo( val ) >= 0 : true);
	}
}

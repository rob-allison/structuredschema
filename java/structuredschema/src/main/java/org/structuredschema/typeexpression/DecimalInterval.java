package org.structuredschema.typeexpression;

import java.math.BigDecimal;

public class DecimalInterval extends DecimalRange
{
	private final BigDecimal low;
	private final BigDecimal high;

	public DecimalInterval( BigDecimal low, BigDecimal high )
	{
		this.low = low;
		this.high = high;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigDecimal val = (BigDecimal)obj;
		return (low != null ? low.compareTo( val ) <= 0 : true) && (high != null ? high.compareTo( val ) >= 0 : true);
	}
}

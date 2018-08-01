package org.structuredschema.typeexpression;

import java.math.BigDecimal;

public class DecimalValue extends DecimalRange
{
	private final BigDecimal value;

	public DecimalValue( BigDecimal value )
	{
		this.value = value;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigDecimal val = (BigDecimal)obj;
		return value.equals( val );
	}
}

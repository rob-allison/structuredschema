package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
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

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( value.toString( ) );
	}

	public static String regex( )
	{
		return "\\-?\\d+\\.\\d+([eE][\\+\\-]?\\d+)?";
	}

	public static DecimalValue parseValue( String str )
	{
		return new DecimalValue( new BigDecimal( str ) );
	}
}

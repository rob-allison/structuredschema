package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

public class DecimalValue extends TypeLiteral
{
	private final BigDecimal value;

	public DecimalValue( BigDecimal value )
	{
		this.value = value;
	}

	@Override
	public boolean validate( Object obj )
	{
		BigDecimal val = convert( obj );
		if ( val != null )
		{
			return value.compareTo( val ) == 0;
		}
		return false;
	}

	public static BigDecimal convert( Object obj )
	{
		if ( obj != null )
		{
			if ( obj instanceof BigDecimal )
			{
				return (BigDecimal)obj;
			}
			if ( obj instanceof Double )
			{
				Double val = (Double)obj;
				return BigDecimal.valueOf( val );
			}
			if ( obj instanceof Float )
			{
				Float val = (Float)obj;
				return BigDecimal.valueOf( val );
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
		return "\\-?\\d+\\.\\d+([eE][\\+\\-]?\\d+)?";
	}

	public static DecimalValue parseValue( String str )
	{
		return new DecimalValue( new BigDecimal( str ) );
	}
}

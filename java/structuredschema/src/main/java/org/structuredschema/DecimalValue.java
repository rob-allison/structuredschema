package org.structuredschema;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

public class DecimalValue extends TypeLiteral
{
	private final BigDecimal value;
	private final Object definition;

	public DecimalValue( BigDecimal value )
	{
		this( value, value );
	}

	public DecimalValue( Double value )
	{
		this( BigDecimal.valueOf( value ), value );
	}
	
	public DecimalValue( Float value )
	{
		this( BigDecimal.valueOf( value ), value );
	}
	
	public DecimalValue( BigDecimal value, Object definition )
	{
		this.value = value;
		this.definition = definition;
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
				return new BigDecimal( val.toString( ) );
			}
			if ( obj instanceof Float )
			{
				Float val = (Float)obj;
				return new BigDecimal( val.toString( ) );
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
		return new DecimalValue( new BigDecimal( str ), str );
	}

	@Override
	public Object toDefinition( )
	{
		return definition;
	}
}

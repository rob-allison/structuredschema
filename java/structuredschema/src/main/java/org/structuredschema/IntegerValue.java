package org.structuredschema;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;

public class IntegerValue extends TypeLiteral
{
	private final BigInteger value;
	private final Object definition;

	public IntegerValue( BigInteger value )
	{
		this( value, value );
	}

	public IntegerValue( Long value )
	{
		this( BigInteger.valueOf( value ), value );
	}
	
	public IntegerValue( Integer value )
	{
		this( BigInteger.valueOf( value ), value );
	}
	
	public IntegerValue( BigInteger value, Object definition )
	{
		this.value = value;
		this.definition = definition;
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
		return new IntegerValue( new BigInteger( str ), str );
	}
	
	@Override
	public Object toDefinition( )
	{
		return definition;
	}
}

package org.structuredschema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class StringValue extends TypeLiteral
{
	private final String value;

	public StringValue( String regex )
	{
		this.value = regex;
	}

	public static String regex( )
	{
		return "^_.*_$";
	}

	public static StringValue parseString( String str )
	{
		return new StringValue( str.substring( 1, str.length( ) - 1 ) );
	}

	@Override
	public boolean validate( Object obj )
	{
		if ( obj != null && obj instanceof String )
		{
			String string = (String)obj;
			return value.equals( string );
		}
		return false;
	}

	public static String tokenizeString( Reader reader ) throws IOException
	{
		StringBuilder builder = new StringBuilder( );
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case -1:
					throw new RuntimeException( "_ expected" );

				case '_':
					return builder.toString( );

				default:
					builder.append( (char)c );
			}
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( '_' );
		writer.write( value.replace( "_", "__" ) );
		writer.write( '_' );
	}

}

package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex extends TypeLiteral
{
	private final String regex;

	public Regex( String regex )
	{
		this.regex = regex;
	}

	@Override
	public boolean validate( Object obj )
	{
		if ( obj != null && obj instanceof String )
		{
			Pattern p = Pattern.compile( regex );
			Matcher m = p.matcher( (String)obj );
			return m.matches( );
		}
		return false;
	}

	public static Regex parseRegex( Reader reader ) throws IOException
	{
		StringBuilder builder = new StringBuilder( );
		boolean escaped = false;
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case -1:
					throw new RuntimeException( "/ expected" );

				case '\\':
					escaped = true;
					break;

				case '/':
					if ( escaped )
					{
						escaped = false;
						builder.append( '/' );
						break;
					}
					else
					{
						return new Regex( builder.toString( ) );
					}

				default:
					if ( escaped )
					{
						escaped = false;
						builder.append( '\\' );
					}
					builder.append( (char)c );
			}
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( '/' );
		writer.write( regex.replace( "/", "\\/" ) );
		writer.write( '/' );
	}

}

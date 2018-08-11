package org.structuredschema;

import java.io.IOException;
import java.io.Writer;

public class Discriminator extends TypeLiteral
{
	public static Discriminator instance = new Discriminator( );
	
	@Override
	public boolean validate( Object obj )
	{
		return obj instanceof String;
	}
	
	public static String regex( )
	{
		return "^Discriminator$";
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( "Discriminator" );
	}
}

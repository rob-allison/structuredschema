package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;

public class Null extends TypeLiteral
{
	public static Null instance = new Null( );
	
	public static String regex( )
	{
		return "null";
	}
	
	@Override
	public boolean validate( Object obj )
	{
		return obj == null;
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( "null" );
	}
}

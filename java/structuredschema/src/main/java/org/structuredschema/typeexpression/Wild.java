package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;

public class Wild extends TypeLiteral
{
	public static Wild instance = new Wild( );
	
	public static String regex( )
	{
		return "\\*";
	}
	
	@Override
	public boolean validate( Object obj )
	{
		return true;
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( "*" );
	}
}

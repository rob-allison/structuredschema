package org.structuredschema;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TypeDeclaration
{
	private final String name;
	private final List<String> parameters;
	private final String extnds;
	private final boolean abstrct;
	private final Object definition;

	public static TypeDeclaration read( Object decl )
	{
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String,Object>)decl;

		try
		{
			Reader reader = new StringReader( (String)map.get( "name" ) );
			List<String> params = new LinkedList<String>( );
			String name = parseName( reader, params );
			String ext = (String)map.get( "extends" );
			Object def = StructuredSchema.readDef( map.get( "def" ) );
			Boolean ab = (Boolean)map.get( "abstract" );
			if ( ab == null )
			{
				ab = false;
				if ( def instanceof Map )
				{
					@SuppressWarnings("unchecked")
					Map<String,Object> dmap = (Map<String,Object>)def;
					for ( Object dval : dmap.values( ) )
					{
						if ( dval == Discriminator.instance )
						{
							ab = true;
						}
					}
				}
			}

			return new TypeDeclaration( name, params, ext, ab, def );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	private static String parseName( Reader reader, List<String> params ) throws IOException
	{
		StringBuilder builder = new StringBuilder( );
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case '(':
					parseParameters( reader, params );
				case -1:
					return builder.toString( );
				default:
					builder.append( (char)c );
			}
		}
	}

	private static void parseParameters( Reader reader, List<String> params ) throws IOException
	{
		StringBuilder builder = new StringBuilder( );
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case ',':
					params.add( builder.toString( ) );
					builder = new StringBuilder( );
					break;
				case -1:
					throw new RuntimeException( ") expected" );
				case ')':
					params.add( builder.toString( ) );
					return;
				default:
					builder.append( (char)c );
			}
		}
	}

	private TypeDeclaration( String name, List<String> parameters, String extnds, boolean abstrct, Object definition )
	{
		this.name = name;
		this.parameters = parameters;
		this.extnds = extnds;
		this.abstrct = abstrct;
		this.definition = definition;
	}

	public String getName( )
	{
		return name;
	}

	public List<String> getParameters( )
	{
		return parameters;
	}

	public String getExtnds( )
	{
		return extnds;
	}

	public Object getDefinition( )
	{
		return definition;
	}

	public boolean isAbstract( )
	{
		return abstrct;
	}
}

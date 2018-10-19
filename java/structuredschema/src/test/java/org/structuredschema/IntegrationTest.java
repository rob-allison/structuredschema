package org.structuredschema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

@RunWith(Parameterized.class)
public class IntegrationTest
{
	private static final Yaml yaml = new Yaml( );
	private static final File basedir = new File( System.getProperty( "basedir" ) );

	private final String group;
	private final String sname;
	private final int index;
	private final Object library;
	private final Object schema;
	private final Object test;
	private final Object result;

	public IntegrationTest( String group, String sname, int index, Object library, Object schema, Object test, Object result )
	{
		this.group = group;
		this.sname = sname;
		this.index = index;
		this.library = library;
		this.schema = schema;
		this.test = test;
		this.result = result;
	}

	@Test
	public void test( ) throws IOException
	{
		StructuredContext context = library != null ? StructuredContext.withLibrary( library ) : StructuredContext.core( );
		StructuredSchema sch = context.read( schema );
		Object errors = new LinkedList<Object>( );

		try
		{
			sch.validate( test );
		}
		catch ( ValidationException e )
		{
			errors = e.getErrors( );
		}

		if ( !result.equals( errors ) )
		{
			Map<String,Object> msg = new LinkedHashMap<>( );
			msg.put( "group", group );
			msg.put( "schema", sname );
			msg.put( "test", index );
			msg.put( "expected", result );
			msg.put( "actual", errors );
			Writer writer = new OutputStreamWriter( System.out );
			yaml.dump( msg, writer );
			writer.flush( );
		}

		Assert.assertEquals( result, errors );
	}

	@Parameterized.Parameters(name = "{0}.{1}#{2}")
	public static Collection<Object> schemas( ) throws FileNotFoundException
	{
		List<Object> params = new LinkedList<>( );

		File testdir = new File( basedir, "test" );
		
		for ( File gfile : testdir.listFiles( ) )
		{
			for ( File sfile : gfile.listFiles( new FilenameFilter( )
			{
				@Override
				public boolean accept( File dir, String name )
				{
					return name.endsWith( "schema.yaml" );
				}
			} ) )
			{
				Object schema = yaml.load( new FileReader( sfile ) );

				String sname = sfile.getName( ).replace( ".schema.yaml", "" );
				File tfile = new File( gfile, sname + ".tests.yaml" );
				File rfile = new File( gfile, sname + ".results.yaml" );
				File lfile = new File( gfile, sname + ".library.yaml" );

				List<Object> tests = yaml.load( new FileReader( tfile ) );
				List<Object> results = yaml.load( new FileReader( rfile ) );
				List<Object> library = lfile.exists( ) ? yaml.load( new FileReader( lfile ) ) : null;

				if ( tests.size( ) == results.size( ) )
				{
					for ( int i = 0; i < tests.size( ); i++ )
					{
						Object test = tests.get( i );
						Object result = results.get( i );
						params.add( new Object[] { gfile.getName( ), sname, i, library, schema, test, result } );
					}
				}
				else
				{
					throw new RuntimeException( "size mismatch tests and results" );
				}
			}
		}

		return params;
	}
}

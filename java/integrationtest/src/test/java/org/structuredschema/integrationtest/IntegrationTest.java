package org.structuredschema.integrationtest;

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
import org.structuredschema.StructuredSchema;
import org.yaml.snakeyaml.Yaml;

@RunWith(Parameterized.class)
public class IntegrationTest
{
	private static final Yaml yaml = new Yaml( );
	private static final File dir = new File( System.getProperty( "dir" ) );

	private final String group;
	private final String sname;
	private final int index;
	private final Object schema;
	private final Object test;
	private final Object result;

	public IntegrationTest( String group, String sname, int index, Object schema, Object test, Object result )
	{
		this.group = group;
		this.sname = sname;
		this.index = index;
		this.schema = schema;
		this.test = test;
		this.result = result;
	}

	@Test
	public void test( ) throws IOException
	{
		StructuredSchema sch = StructuredSchema.read( schema );
		Object errors = sch.validate( test );

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

		for ( File gfile : dir.listFiles() )
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

				List<Object> tests = yaml.load( new FileReader( tfile ) );
				List<Object> results = yaml.load( new FileReader( rfile ) );
				if ( tests.size( ) == results.size( ) )
				{
					for ( int i = 0; i < tests.size( ); i++ )
					{
						Object test = tests.get( i );
						Object result = results.get( i );
						params.add( new Object[] { gfile.getName( ), sname, i, schema, test, result } );
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

package org.structuredschema.integrationtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

	private final String sname;
	private final String name;
	private final Object schema;
	private final Object test;
	private final Object result;

	public IntegrationTest( String sname, String name, Object schema, Object test, Object result )
	{
		this.sname = sname;
		this.name = name;
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
			System.out.println( sname + "." + name );
			Writer writer = new OutputStreamWriter( System.out );
			yaml.dump( result, writer );
			yaml.dump( errors, writer );
			writer.flush( );
		}
		
		Assert.assertEquals( result, errors );
	}

	@Parameterized.Parameters(name = "{0}.{1}")
	public static Collection<Object> schemas( ) throws FileNotFoundException
	{
		List<Object> params = new LinkedList<>( );

		for ( File sdir : dir.listFiles( ) )
		{
			String sname = sdir.getName( );
			Object schema = yaml.load( new FileReader( new File( sdir, "schema.yaml" ) ) );

			File[] tfiles = sdir.listFiles( new FilenameFilter( )
			{
				@Override
				public boolean accept( File dir, String name )
				{
					return name.endsWith( ".test.yaml" );
				}
			} );

			for ( File tfile : tfiles )
			{
				String name = tfile.getName( ).replace( ".test.yaml", "" );
				Object test = yaml.load( new FileReader( tfile ) );
				File rfile = new File( sdir, name + ".result.yaml" );
				Object result = yaml.load( new FileReader( rfile ) );
				params.add( new Object[] { sname, name, schema, test, result } );
			}
		}

		return params;
	}
}

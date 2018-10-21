package org.structuredschema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	private static final File rootdir = new File( System.getProperty( "rootdir" ) );

	private final String group;
	private final String sname;
	private final int index;
	private final List<Object> libs;
	private final Object schema;
	private final Object test;
	private final Object result;

	public IntegrationTest( String group, String sname, int index, List<Object> libs, Object schema, Object test, Object result )
	{
		this.group = group;
		this.sname = sname;
		this.index = index;
		this.libs = libs;
		this.schema = schema;
		this.test = test;
		this.result = result;
	}

	@Test
	public void test( ) throws IOException
	{
		StructuredContext context = StructuredContext.withLibs( libs );
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

		File testdir = new File( rootdir, "test" );

		for ( File gfile : testdir.listFiles( ) )
		{
			List<Object> libs = new LinkedList<>( );
			for ( File lfile : gfile.listFiles( new FilenameFilter( )
			{
				@Override
				public boolean accept( File dir, String name )
				{
					return name.endsWith( "library.yaml" );
				}
			} ) )
			{
				libs.add( yaml.load( new FileReader( lfile ) ) );
			}

			for ( File sfile : gfile.listFiles( new FilenameFilter( )
			{
				@Override
				public boolean accept( File dir, String name )
				{
					return name.endsWith( ".tests.yaml" );
				}
			} ) )
			{
				String sname = sfile.getName( ).replace( ".tests.yaml", "" );

				Map<String,Object> testset = yaml.load( new FileReader( sfile ) );
				Object schema = testset.get( "schema" );
				@SuppressWarnings("unchecked")
				List<Object> tests = (List<Object>)testset.get( "tests" );

				for ( int i = 0; i < tests.size( ); i++ )
				{
					@SuppressWarnings("unchecked")
					Map<String,Object> tr = (Map<String,Object>)tests.get( i );
					Object test = tr.get( "test" );
					Object result = tr.get( "result" );
					params.add( new Object[] { gfile.getName( ), sname, i, libs, schema, test, result } );
				}
			}
		}

		return params;
	}

	public static void main( String[] args ) throws IOException
	{
		File testdir = new File( rootdir, "test" );

		for ( File gfile : testdir.listFiles( ) )
		{
			List<Object> libs = new LinkedList<>( );
			for ( File lfile : gfile.listFiles( new FilenameFilter( )
			{
				@Override
				public boolean accept( File dir, String name )
				{
					return name.endsWith( "library.yaml" );
				}
			} ) )
			{
				libs.add( yaml.load( new FileReader( lfile ) ) );
			}
			
			for ( File sfile : gfile.listFiles( new FilenameFilter( )
			{
				@Override
				public boolean accept( File dir, String name )
				{
					return name.endsWith( ".tests.yaml" );
				}
			} ) )
			{
				Map<String,Object> testset = yaml.load( new FileReader( sfile ) );
				
				Object schema = testset.get( "schema" );
				@SuppressWarnings("unchecked")
				List<Object> tests = (List<Object>)testset.get( "tests" );

				StructuredContext context = StructuredContext.withLibs( libs );
				StructuredSchema sch = context.read( schema );

				for ( int i = 0; i < tests.size( ); i++ )
				{
					@SuppressWarnings("unchecked")
					Map<String,Object> tr = (Map<String,Object>)tests.get( i );
					List<Object> result = new LinkedList<>( );
					try
					{
						sch.validate( tr.get( "test" ) );
					}
					catch ( ValidationException e )
					{
						result = e.getErrors( );
					}
					tr.put( "result", result );
				}

				Writer writer = new FileWriter( sfile );
				yaml.dump( testset, writer );
				writer.flush( );
				writer.close( );
			}
		}

	}
	
	public static void mainold( String[] args ) throws IOException
	{
		File testdir = new File( rootdir, "test" );

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
				// File rfile = new File( gfile, sname + ".results.yaml" );
				File lfile = new File( gfile, sname + ".library.yaml" );
				File ufile = new File( gfile, sname + ".utests.yaml" );

				List<Object> tests = yaml.load( new FileReader( tfile ) );
				List<Object> library = lfile.exists( ) ? yaml.load( new FileReader( lfile ) ) : null;

				StructuredContext context = library != null ? StructuredContext.withLibrary( library ) : StructuredContext.core( );
				StructuredSchema sch = context.read( schema );

				Map<String,Object> stmap = new LinkedHashMap<>( );
				stmap.put( "schema", schema );
				List<Object> trs = new LinkedList<>( );
				for ( int i = 0; i < tests.size( ); i++ )
				{
					Object test = tests.get( i );
					List<Object> result = new LinkedList<>( );
					try
					{
						sch.validate( test );
					}
					catch ( ValidationException e )
					{
						result = e.getErrors( );
					}
					Map<String,Object> tr = new LinkedHashMap<>( );
					tr.put( "test", test );
					tr.put( "result", result );
					trs.add( tr );
				}

				stmap.put( "tests", trs );

				Writer writer = new FileWriter( ufile );
				yaml.dump( stmap, writer );
				writer.flush( );
				writer.close( );
			}
		}

	}
}

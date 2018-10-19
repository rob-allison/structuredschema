package org.structuredschema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class CoreTest
{
	private static final Yaml yaml = new Yaml( );
	private static final File basedir = new File( System.getProperty( "basedir" ) );

	@Test
	public void compareCore( ) throws FileNotFoundException
	{
		Object core = StructuredContext.coreLibrary( );
		Object read = yaml.load( new FileReader( new File( basedir, "core.yaml" ) ) );
		Assert.assertEquals( read, core );
	}

	public static void main( String[] args ) throws IOException
	{
		DumperOptions opts = new DumperOptions( );
		opts.setDefaultFlowStyle( FlowStyle.BLOCK );
		Yaml yaml = new Yaml( opts );

		Writer writer = new FileWriter( new File( basedir, "core.yaml" ) );
		Object core = StructuredContext.coreLibrary( );
		yaml.dump( core, writer );
		writer.flush( );
		writer.close( );
	}
}

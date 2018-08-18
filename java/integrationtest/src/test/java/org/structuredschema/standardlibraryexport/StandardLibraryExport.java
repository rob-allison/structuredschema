package org.structuredschema.standardlibraryexport;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.structuredschema.StructuredSchema;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class StandardLibraryExport
{
	public static void main( String[] args ) throws IOException
	{
		DumperOptions opts = new DumperOptions( );
		opts.setDefaultFlowStyle( FlowStyle.BLOCK );
		Yaml yaml = new Yaml(opts);
		
		Writer writer = new OutputStreamWriter( System.out );
		Object core = StructuredSchema.standardLibrary( );
		yaml.dump( core, writer );
		writer.flush( );
	}
}

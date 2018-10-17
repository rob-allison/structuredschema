package org.structuredschema.standardlibraryexport;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.structuredschema.StructuredContext;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class CoreLibraryExport
{
	public static void main( String[] args ) throws IOException
	{
		DumperOptions opts = new DumperOptions( );
		opts.setDefaultFlowStyle( FlowStyle.BLOCK );
		Yaml yaml = new Yaml(opts);
		
		Writer writer = new OutputStreamWriter( System.out );
		Object core = StructuredContext.coreLibrary( );
		yaml.dump( core, writer );
		writer.flush( );
	}
}

package org.structuredschema;

public class SchemaConsistancyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public SchemaConsistancyException( String message )
	{
		super( message );
	}
}

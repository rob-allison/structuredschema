package org.structuredschema;

public class SchemaValidationException extends ValidationException
{
	private static final long serialVersionUID = 1L;

	public SchemaValidationException( ValidationException e )
	{
		super( e.getErrors( ) );
	}
}

package org.structuredschema;

import java.util.List;

public class SchemaException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final List<Object> errors;

	public SchemaException( List<Object> errors )
	{
		this.errors = errors;
	}

	public List<Object> getErrors( )
	{
		return errors;
	}

	@Override
	public String toString( )
	{
		return "BAD SCHEMA! " + errors.toString( );
	}
}

package org.structuredschema;

import java.util.List;

public class ValidationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final List<Object> errors;

	public ValidationException( List<Object> errors )
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
		return "Validation Failed " + errors.toString( );
	}
}

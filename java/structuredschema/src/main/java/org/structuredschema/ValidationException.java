package org.structuredschema;

import java.util.Collections;
import java.util.List;

public class ValidationException extends Exception
{
	private static final long serialVersionUID = 1L;

	private final List<String> errors;

	public ValidationException( String error )
	{
		this( Collections.singletonList( error ) );
	}

	public ValidationException( List<String> errors )
	{
		super( "Validation Errors" );
		this.errors = errors;
	}

	public List<String> getErrors( )
	{
		return errors;
	}

}

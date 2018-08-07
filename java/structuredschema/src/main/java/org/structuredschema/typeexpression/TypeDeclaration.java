package org.structuredschema.typeexpression;

import java.util.List;

public class TypeDeclaration
{
	private final List<String> parameters;
	private final Object definition;
	
	public TypeDeclaration( List<String> parameters, Object definition )
	{
		this.parameters = parameters;
		this.definition = definition;
	}

	public List<String> getParameters( )
	{
		return parameters;
	}

	public Object getDefinition( )
	{
		return definition;
	}
	
	

}

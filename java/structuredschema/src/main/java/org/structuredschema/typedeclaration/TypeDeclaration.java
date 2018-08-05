package org.structuredschema.typedeclaration;

import java.util.List;

public class TypeDeclaration
{
	private final String name;
	private final List<String> typeParameters;
	private final List<String> rangeParameters;
	
	public TypeDeclaration( String name, List<String> typeParameters, List<String> rangeParameters )
	{
		this.name = name;
		this.typeParameters = typeParameters;
		this.rangeParameters = rangeParameters;
	}
	
	public static TypeDeclaration parse( String str )
	{
		return null;
	}
}

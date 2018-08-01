package org.structuredschema.typeexpression;

import java.util.List;

public class ParameterisedType extends TypeExpression
{
	private final String name;
	private final List<TypeExpression> typeParameters;
	private final List<RangeExpression> rangeParameters;

	public ParameterisedType( String name, List<TypeExpression> typeParameters, List<RangeExpression> rangeParameters )
	{
		this.name = name;
		this.typeParameters = typeParameters;
		this.rangeParameters = rangeParameters;
	}

	public String getName( )
	{
		return name;
	}

	public List<TypeExpression> getTypeParameters( )
	{
		return typeParameters;
	}

	public List<RangeExpression> getRangeParameters( )
	{
		return rangeParameters;
	}

	@Override
	public boolean isDeclaration( )
	{
		return typeParameters.stream( ).allMatch( e -> e.isSimpleName( ) ) && rangeParameters.stream( ).allMatch( r -> r.isNamedRange( ) );
	}

	@Override
	public boolean isSimpleName( )
	{
		return typeParameters.isEmpty( ) && rangeParameters.isEmpty( );
	}

}

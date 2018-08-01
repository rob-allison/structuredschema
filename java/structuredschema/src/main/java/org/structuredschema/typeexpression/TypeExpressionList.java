package org.structuredschema.typeexpression;

import java.util.List;

public class TypeExpressionList extends TypeExpression
{
	private final List<TypeExpression> typeExpressions;

	public TypeExpressionList( List<TypeExpression> typeExpressions )
	{
		this.typeExpressions = typeExpressions;
	}

	public List<TypeExpression> getTypeExpressions( )
	{
		return typeExpressions;
	}

	@Override
	public boolean isDeclaration( )
	{
		return false;
	}

	@Override
	public boolean isSimpleName( )
	{
		return false;
	}
}

package org.structuredschema.typeexpression;

import java.util.List;
import java.util.Map;

public abstract class TypeLiteral extends TypeExpression
{
	public abstract boolean validate( Object obj );

	@Override
	public void validate( Object val, Map<String,TypeDeclaration> context, List<String> errors )
	{
		if ( !validate( val ) )
		{
			errors.add( "literal unmatched: " + val + " " + compose( ) );
		}
	}

	@Override
	public TypeExpression replace( String name, TypeExpression expression )
	{
		return this;
	}
}
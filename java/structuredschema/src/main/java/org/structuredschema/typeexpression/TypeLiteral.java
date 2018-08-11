package org.structuredschema.typeexpression;

public abstract class TypeLiteral extends TypeExpression
{
	public abstract boolean validate( Object obj );

	@Override
	public void validate( Object val, StructuredSchema schema, Errors errors )
	{
		if ( !validate( val ) )
		{
			errors.add( "literal unmatched: " + this );
		}
	}

	@Override
	public TypeExpression replace( String name, TypeExpression expression )
	{
		return this;
	}
}
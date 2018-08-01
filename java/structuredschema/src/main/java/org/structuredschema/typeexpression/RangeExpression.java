package org.structuredschema.typeexpression;

public abstract class RangeExpression
{
	public abstract boolean isNamedRange( );
	public abstract boolean validate( Object obj );
}

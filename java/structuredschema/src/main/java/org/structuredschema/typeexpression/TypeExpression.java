package org.structuredschema.typeexpression;

public abstract class TypeExpression
{
	public abstract boolean isDeclaration( );
	public abstract boolean isSimpleName( );
	
	public static TypeExpression parse( String str )
	{
		
	}
}

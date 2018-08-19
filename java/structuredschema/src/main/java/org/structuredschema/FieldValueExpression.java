package org.structuredschema;

public class FieldValueExpression
{
	private final boolean required;
	private final TypeExpression expression;

	public FieldValueExpression( boolean required, TypeExpression expression )
	{
		this.required = required;
		this.expression = expression;
	}

	public boolean isRequired( )
	{
		return required;
	}

	public TypeExpression getExpression( )
	{
		return expression;
	}
	
	public static FieldValueExpression read( String str )
	{
		if ( str.endsWith( "?" ) )
		{
			str = str.substring( 0, str.length( ) - 1 );
			return new FieldValueExpression( false, TypeExpression.parse( str ) );
		}
		else
		{
			return new FieldValueExpression( true, TypeExpression.parse( str ) );
		}
	}

	public Object replace( String pname, TypeExpression pexpr )
	{
		return new FieldValueExpression( required, expression.replace( pname, pexpr ) );
	}

	public Object toDefinition( )
	{
		Object def = expression.toDefinition( );
		if ( !required )
		{
			def = def.toString( ) + "?";
		}
		return def;
	}
}

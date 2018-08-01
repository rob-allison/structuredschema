package org.structuredschema.typeexpression;

public class NamedRange extends RangeExpression
{
	private final String name;

	public NamedRange( String name )
	{
		this.name = name;
	}

	public String getName( )
	{
		return name;
	}

	@Override
	public boolean isNamedRange( )
	{
		return true;
	}

	@Override
	public boolean validate( Object obj )
	{
		throw new UnsupportedOperationException( );
	}
}

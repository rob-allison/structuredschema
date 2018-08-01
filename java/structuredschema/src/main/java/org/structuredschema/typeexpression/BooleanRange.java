package org.structuredschema.typeexpression;

public class BooleanRange extends RangeExpression
{
	private final Boolean value;

	public BooleanRange( Boolean value )
	{
		this.value = value;
	}

	@Override
	public boolean isNamedRange( )
	{
		return false;
	}

	@Override
	public boolean validate( Object obj )
	{
		boolean b = (Boolean)obj;
		return value != null ? b == value : true;
	}

}

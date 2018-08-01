package org.structuredschema.typeexpression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex extends RangeExpression
{
	private final String regex;

	public Regex( String regex )
	{
		this.regex = regex;
	}

	@Override
	public boolean isNamedRange( )
	{
		return false;
	}

	@Override
	public boolean validate( Object obj )
	{
		Pattern p = Pattern.compile( regex );
		Matcher m = p.matcher( (String)obj );
		return m.matches( );
	}
}

package org.structuredschema.typeexpression;

import java.util.List;

public class IntegerRangeList extends IntegerRange
{
	private final List<IntegerRange> ranges;

	public IntegerRangeList( List<IntegerRange> ranges )
	{
		this.ranges = ranges;
	}

	@Override
	public boolean validate( Object obj )
	{
		return ranges.stream( ).anyMatch( r -> r.validate( obj ) );
	}
}

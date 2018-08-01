package org.structuredschema.typeexpression;

import java.util.List;

public class DecimalRangeList extends DecimalRange
{
	private final List<DecimalRange> ranges;

	public DecimalRangeList( List<DecimalRange> ranges )
	{
		this.ranges = ranges;
	}

	@Override
	public boolean validate( Object obj )
	{
		return ranges.stream( ).anyMatch( r -> r.validate( obj ) );
	}
}

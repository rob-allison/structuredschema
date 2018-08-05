package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
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
	
	@Override
	public void compose( Writer writer ) throws IOException
	{
		for ( Iterator<DecimalRange> iter = ranges.iterator( ); iter.hasNext( ); )
		{
			DecimalRange range = iter.next( );
			range.compose( writer );
			if ( iter.hasNext( ) )
			{
				writer.write( ',' );
			}
		}
	}
}

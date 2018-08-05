package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
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

	@Override
	public void compose( Writer writer ) throws IOException
	{
		for ( Iterator<IntegerRange> iter = ranges.iterator( ); iter.hasNext( ); )
		{
			IntegerRange range = iter.next( );
			range.compose( writer );
			if ( iter.hasNext( ) )
			{
				writer.write( ',' );
			}
		}
	}
	
	
}

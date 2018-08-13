package org.structuredschema;

import java.util.List;
import java.util.Map;

public class Structured
{

	@SuppressWarnings("unchecked")
	public static boolean equals( Object a, Object b )
	{
		if ( a instanceof List && b instanceof List )
		{
			List<Object> al = (List<Object>)a;
			List<Object> bl = (List<Object>)b;

			if ( al.size( ) == bl.size( ) )
			{
				for ( int i = 0; i < al.size( ); i++ )
				{
					if ( !equals( al.get( i ), bl.get( i ) ) )
					{
						return false;
					}
				}
				return true;
			}
		}
		
		if ( a instanceof Map && b instanceof Map )
		{
			Map<String,Object> am = (Map<String,Object>)a;
			Map<String,Object> bm = (Map<String,Object>)b;
			
			if ( am.keySet( ).equals( bm.keySet( ) ) )
			{
				for ( String k : am.keySet( ) )
				{
					if ( !equals( am.get( k ), bm.get( k ) ) )
					{
						return false;
					}
				}
				return true;
			}
		}
		
		
		
		return false;
	}
}

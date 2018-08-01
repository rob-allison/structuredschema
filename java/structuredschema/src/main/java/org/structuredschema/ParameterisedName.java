package org.structuredschema;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ParameterisedName
{
	private final String name;
	private final String[] types;
	private final String[] ranges;

	public ParameterisedName( String name, String[] params, String[] ranges )
	{
		this.name = name;
		this.types = params;
		this.ranges = ranges;
	}
	
	public ParameterisedName stripParameters( int tindex, int rindex )
	{
		String[] trem = Arrays.copyOfRange( types, 0, tindex );
		String[] rrem = Arrays.copyOfRange( ranges, 0, rindex );
		return new ParameterisedName( name, trem, rrem );
	}

	public String getName( )
	{
		return name;
	}

	public String[] getTypes( )
	{
		return types;
	}

	public String[] getRanges( )
	{
		return ranges;
	}

	public static ParameterisedName parse( String name )
	{
		return parse( characterIterator( name ) );
	}

	public static ParameterisedName[] parseTypes( String name )
	{
		Iterator<Character> iter = characterIterator( name );
		List<ParameterisedName> names = new LinkedList<ParameterisedName>( );
		while ( iter.hasNext( ) )
		{
			ParameterisedName nm = parse( iter );
			names.add( nm );
		}
		return names.toArray( new ParameterisedName[] {} );
	}

	private static ParameterisedName parse( Iterator<Character> iter )
	{
		List<String> params = new LinkedList<String>( );
		List<String> ranges = new LinkedList<String>( );
		StringBuilder builder = new StringBuilder( );
		while ( iter.hasNext( ) )
		{
			char c = iter.next( );
			switch ( c )
			{
				case '[':
					String ty = parseType( iter );
					params.add( ty );
					break;

				case '{':
					String rg = parseRange( iter );
					ranges.add( rg );
					break;

				case '|':
					return new ParameterisedName( builder.toString( ), params.toArray( new String[] {} ), ranges.toArray( new String[] {} ) );

				default:
					builder.append( c );
			}
		}
		return new ParameterisedName( builder.toString( ), params.toArray( new String[] {} ), ranges.toArray( new String[] {} ) );
	}

	private static String parseType( Iterator<Character> iter )
	{
		StringBuilder builder = new StringBuilder( );
		while ( iter.hasNext( ) )
		{
			char c = iter.next( );
			switch ( c )
			{
				case '{':
					builder.append( c );
					builder.append( parseRange( iter ) );
					builder.append( '}' );
					break;
					
				case '[':
					builder.append( c );
					builder.append( parseType( iter ) );
					builder.append( ']' );
					break;
					
				case ']':
					return builder.toString( );
					
				default:
					builder.append( c );
			}
		}
		throw new RuntimeException( "] expected" );
	}

	private static String parseRange( Iterator<Character> iter )
	{
		StringBuilder builder = new StringBuilder( );
		while ( iter.hasNext( ) )
		{
			char c = iter.next( );
			switch ( c )
			{
				case '^':
					String regex = parseRegex( iter );
					if ( iter.next( ) == '}' )
					{
						return regex;
					}
					else
					{
						throw new RuntimeException( "} expected" );
					}
					
				case '}':
					return builder.toString( );
					
				default:
					builder.append( c );
			}
		}
		throw new RuntimeException( "} expected" );
	}

	private static String parseRegex( Iterator<Character> iter )
	{
		StringBuilder builder = new StringBuilder( );
		builder.append( '^' );

		while ( iter.hasNext( ) )
		{
			char c = iter.next( );
			switch ( c )
			{
				case '$':
					builder.append( c );
					return builder.toString( );
					
				default:
					builder.append( c );
			}
		}
		throw new RuntimeException( "$ expected" );
	}

	@Override
	public String toString( )
	{
		StringBuilder builder = new StringBuilder( );
		builder.append( name );
		for ( String param : types )
		{
			builder.append( '<' );
			builder.append( param );
			builder.append( '>' );
		}
		for ( String range : ranges )
		{
			builder.append( '{' );
			builder.append( range );
			builder.append( '}' );
		}
		return builder.toString( );
	}
	
	private static Iterator<Character> characterIterator( String str )
	{
		return new Iterator<Character>( )
		{
			private int i = 0;
			
			public boolean hasNext( )
			{
				return i < str.length( );
			}

			public Character next( )
			{
				return str.charAt( i++ );
			}

			public void remove( )
			{
				throw new UnsupportedOperationException( );
			}
		};
	}
}
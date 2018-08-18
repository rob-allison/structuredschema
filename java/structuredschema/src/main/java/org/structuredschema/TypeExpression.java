package org.structuredschema;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class TypeExpression
{
	public abstract void validate( Object val, StructuredSchema schema, Errors errors );

	public abstract TypeExpression replace( String name, TypeExpression expression );

	public abstract void compose( Writer writer ) throws IOException;

	public String toString( )
	{
		StringWriter writer = new StringWriter( );
		try
		{
			compose( writer );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
		return writer.toString( );
	}
	
	public Object toDefinition( )
	{
		return toString( );
	}
	
	public static TypeExpression read( Object def )
	{
		if ( def instanceof String )
		{
			String string = (String)def;
			return parse( string );
		}
		else if ( def instanceof Integer )
		{
			Integer val = (Integer)def;
			return new IntegerValue( val );
		}
		else if ( def instanceof Long )
		{
			Long val = (Long)def;
			return new IntegerValue( val );
		}
		else if ( def instanceof BigInteger )
		{
			BigInteger val = (BigInteger)def;
			return new IntegerValue( val );
		}
		else if ( def instanceof Float )
		{
			Float val = (Float)def;
			return new DecimalValue( val );
		}
		else if ( def instanceof Double )
		{
			Double val = (Double)def;
			return new DecimalValue( val );
		}
		else if ( def instanceof BigDecimal )
		{
			BigDecimal val = (BigDecimal)def;
			return new DecimalValue( val );
		}
		else if ( def instanceof Boolean )
		{
			Boolean val = (Boolean)def;
			return new BooleanValue( val );
		}
		else
		{
			throw new RuntimeException( "bad def" );
		}
	}

	public static TypeExpression parse( String string )
	{
		try
		{
			return parse( new StringReader( string ) );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	public static TypeExpression parse( Reader reader ) throws IOException
	{
		List<String> toks = tokenize( reader );
		return parse( toks.iterator( ) );
	}

	public static TypeExpression parse( Iterator<String> iter ) throws IOException
	{
		List<TypeExpression> exprs = new LinkedList<>( );
		List<TypeExpression> tparams = new LinkedList<>( );
		String last = null;

		while ( iter.hasNext( ) )
		{
			String tok = iter.next( );
			switch ( tok )
			{
				case ")":
					if ( exprs.isEmpty( ) )
					{
						return parse( last, tparams );
					}
					else
					{
						exprs.add( parse( last, tparams ) );
						return new TypeExpressionList( exprs );
					}
				case "|":
					exprs.add( parse( last, tparams ) );
					last = null;
					tparams = new LinkedList<>( );
					break;
				case "(":
					TypeExpression tparam = parse( iter );
					tparams.add( tparam );
					break;
				default:
					last = tok;
			}
		}

		if ( exprs.isEmpty( ) )
		{
			return parse( last, tparams );
		}
		else
		{
			exprs.add( parse( last, tparams ) );
			return new TypeExpressionList( exprs );
		}
	}

	private static List<String> tokenize( Reader reader ) throws IOException
	{
		List<String> result = new LinkedList<>( );
		StringBuilder builder = new StringBuilder( );
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case -1:
					if ( !builder.toString( ).isEmpty( ) )
					{
						result.add( builder.toString( ) );
					}
					return result;
				case '(':
				case ')':
				case '|':
					if ( !builder.toString( ).isEmpty( ) )
					{
						result.add( builder.toString( ) );
					}
					result.add( String.valueOf( (char)c ) );
					builder = new StringBuilder( );
					break;
				case ',':
					if ( !builder.toString( ).isEmpty( ) )
					{
						result.add( builder.toString( ) );
					}
					result.add( String.valueOf( ')' ) );
					result.add( String.valueOf( '(' ) );
					builder = new StringBuilder( );
					break;
				case '/':
					if ( builder.toString( ).isEmpty( ) )
					{
						String rg = Regex.tokenizeRegex( reader );
						if ( !result.isEmpty( ) && result.get( result.size( ) - 1 ).startsWith( "/" ) )
						{
							String last = result.remove( result.size( ) - 1 );
							last = last.substring( 1, last.length( ) - 1 );
							rg = last + "/" + rg;
						}
						result.add( "/" + rg + "/" );
					}
					else
					{
						builder.append( (char)c );
					}
					break;
				case '_':
					if ( builder.toString( ).isEmpty( ) )
					{
						String st = StringValue.tokenizeString( reader );
						if ( !result.isEmpty( ) && result.get( result.size( ) - 1 ).startsWith( "_" ) )
						{
							String last = result.remove( result.size( ) - 1 );
							last = last.substring( 1, last.length( ) - 1 );
							st = last + "_" + st;
						}
						result.add( "_" + st + "_" );
					}
					else
					{
						builder.append( (char)c );
					}
					break;
				default:
					builder.append( (char)c );
			}
		}
	}

	private static TypeExpression parse( String expr, List<TypeExpression> tparams )
	{
		if ( expr.matches( IntegerValue.regex( ) ) )
		{
			return IntegerValue.parseValue( expr );
		}
		else if ( expr.matches( IntegerInterval.regex( ) ) )
		{
			return IntegerInterval.parseInterval( expr );
		}
		else if ( expr.matches( DecimalValue.regex( ) ) )
		{
			return DecimalValue.parseValue( expr );
		}
		else if ( expr.matches( DecimalInterval.regex( ) ) )
		{
			return DecimalInterval.parseInterval( expr );
		}
		else if ( expr.matches( BooleanValue.regex( ) ) )
		{
			return BooleanValue.parseBoolean( expr );
		}
		else if ( expr.matches( Regex.regex( ) ) )
		{
			return Regex.parseRegex( expr );
		}
		else if ( expr.matches( StringValue.regex( ) ) )
		{
			return StringValue.parseString( expr );
		}
		else if ( expr.matches( Null.regex( ) ) )
		{
			return Null.instance;
		}
		else if ( expr.matches( Wild.regex( ) ) )
		{
			return Wild.instance;
		}
		else if ( expr.matches( Discriminator.regex( ) ) )
		{
			return Discriminator.instance;
		}
		else
		{
			return new TypeInvocation( expr, tparams );
		}
	}
}

package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
			return new NamedType( expr, tparams );
		}
	}
}

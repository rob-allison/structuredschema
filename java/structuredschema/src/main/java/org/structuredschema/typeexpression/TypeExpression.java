package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public abstract class TypeExpression
{
	public abstract void validate( Object val, StructuredSchema schema, List<String> errors );

	public abstract TypeExpression replace( String name, TypeExpression expression );

	public abstract void compose( Writer writer ) throws IOException;

	public String compose( )
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
		List<TypeExpression> exprs = new LinkedList<>( );
		StringBuilder builder = new StringBuilder( );
		List<TypeExpression> tparams = new LinkedList<>( );

		boolean first = true;
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case -1:
				case ']':
					if ( exprs.isEmpty( ) )
					{
						return parse( builder.toString( ), tparams );
					}
					else
					{
						exprs.add( parse( builder.toString( ), tparams ) );
						return new TypeExpressionList( exprs );
					}
				case '|':
					exprs.add( parse( builder.toString( ), tparams ) );
					builder = new StringBuilder( );
					tparams = new LinkedList<>( );
					break;
				case '[':
					TypeExpression tparam = parse( reader );
					tparams.add( tparam );
					break;
				case '/':
					if ( first )
					{
						Regex regex = Regex.parseRegex( reader );
						int d = reader.read( );
						switch ( d )
						{
							case -1:
							case ']':
								return regex;
							case '|':
								System.out.println( regex.compose( ) );
								exprs.add( regex );
								builder = new StringBuilder( );
								tparams = new LinkedList<>( );
								first = true;
								break;
							default:
								throw new RuntimeException( "unexpected char" );
						}
						break;
					}
				default:
					builder.append( (char)c );
					first = false;
			}
		}
	}

	private static TypeExpression parse( String expr, List<TypeExpression> tparams )
	{
		if ( expr.matches( IntegerRange.regex( ) ) )
		{
			return IntegerRange.parseIntegerRange( expr );
		}
		else if ( expr.matches( DecimalRange.regex( ) ) )
		{
			return DecimalRange.parseDecimalRange( expr );
		}
		else if ( expr.matches( BooleanRange.regex( ) ) )
		{
			return BooleanRange.parseBoolean( expr );
		}
		else if ( expr.matches( Null.regex( ) ) )
		{
			return Null.instance;
		}
		else if ( expr.matches( Wild.regex( ) ) )
		{
			return Wild.instance;
		}
		else
		{
			return new NamedType( expr, tparams );
		}
	}

	public static void main( String[] args )
	{
		TypeExpression expr = TypeExpression.parse( "MyType[Hello[world][null][how][2][/.*/][12.4e10][any]][123.5...123.6/0.01][12..15/2]" );
		System.out.println( expr.compose( ) );
	}

}

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
	public abstract TypeExpression replaceTypeName( String name, TypeExpression expression );

	public abstract TypeExpression replaceRangeName( String name, RangeExpression expression );

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
	
	private static TypeExpression parse( String string )
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
		List<RangeExpression> rparams = new LinkedList<>( );
		while ( true )
		{
			int c = reader.read( );
			switch ( c )
			{
				case -1:
				case ']':
					if ( exprs.isEmpty( ) )
					{
						return new ParameterisedType( builder.toString( ), tparams, rparams );
					}
					else
					{
						exprs.add( new ParameterisedType( builder.toString( ), tparams, rparams ) );
						return new TypeExpressionList( exprs );
					}
				case '|':
					exprs.add( new ParameterisedType( builder.toString( ), tparams, rparams ) );
					builder = new StringBuilder( );
					tparams = new LinkedList<>( );
					rparams = new LinkedList<>( );
					break;
				case '[':
					TypeExpression tparam = parse( reader );
					tparams.add( tparam );
					break;
				case '{':
					RangeExpression rparam = RangeExpression.parse( reader );
					rparams.add( rparam );
					break;
				default:
					builder.append( (char)c );	
			}
		}
	}
	
	 
	public static void main( String[] args )
	{
		TypeExpression expr = TypeExpression.parse( "MyType[Hello[world][how]{2}{X}{12.4e10}{any}]{123.5...123.6/0.01}{12..15/2}" );
		System.out.println( expr.getClass( ) );
		System.out.println( expr.compose( ) );
	}

}

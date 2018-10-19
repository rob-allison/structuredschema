package org.structuredschema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeExpressionUnion extends TypeExpression
{
	private final List<TypeExpression> expressions;

	public TypeExpressionUnion( List<TypeExpression> typeExpressions )
	{
		this.expressions = typeExpressions;
	}

	public List<TypeExpression> getExpressions( )
	{
		return expressions;
	}

	@Override
	public TypeExpression replace( String name, TypeExpression expression )
	{
		return new TypeExpressionUnion( expressions.stream( ).map( e -> e.replace( name, expression ) ).collect( Collectors.toList( ) ) );
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		for ( Iterator<TypeExpression> iter = expressions.iterator( ); iter.hasNext( ); )
		{
			TypeExpression expr = iter.next( );
			expr.compose( writer );
			if ( iter.hasNext( ) )
			{
				writer.write( '|' );
			}
		}
	}

	@Override
	public void validate( Object val, StructuredSchema schema, Errors errors )
	{
		List<Errors> dets = new LinkedList<>( );
		for ( TypeExpression expr : expressions )
		{
			Errors det = errors.detach( );
			expr.validate( val, schema, det );
			if ( det.isEmpty( ) )
			{
				return;
			}
			else
			{
				dets.add( det );
			}
		}

		errors.unmatchedUnion( val, toString( ) );

		for ( Errors det : dets )
		{
			errors.reattach( det );
		}
	}
}

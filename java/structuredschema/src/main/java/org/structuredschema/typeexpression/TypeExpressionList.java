package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeExpressionList extends TypeExpression
{
	private final List<TypeExpression> typeExpressions;

	public TypeExpressionList( List<TypeExpression> typeExpressions )
	{
		this.typeExpressions = typeExpressions;
	}

	public List<TypeExpression> getTypeExpressions( )
	{
		return typeExpressions;
	}
	
	@Override
	public TypeExpression replace( String name, TypeExpression expression )
	{
		return new TypeExpressionList( typeExpressions.stream( ).map( e -> e.replace( name, expression ) ).collect( Collectors.toList( ) ) );
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		for ( Iterator<TypeExpression> iter = typeExpressions.iterator( ); iter.hasNext( ); )
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
	public void validate( Object val, StructuredSchema schema, List<String> errors )
	{
		List<String> errs = new LinkedList<>( );
		for ( TypeExpression expr : typeExpressions )
		{
			expr.validate( val, schema, errs );
			if ( errs.isEmpty( ) )
			{
				return;
			}
			else
			{
				errs = new LinkedList<>( );
			}
		}
		errors.add( "cannot validate union" );
	}
	
	
}

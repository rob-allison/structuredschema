package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterisedType extends TypeExpression
{
	private final String name;
	private final List<TypeExpression> typeParameters;
	private final List<RangeExpression> rangeParameters;

	public ParameterisedType( String name, List<TypeExpression> typeParameters, List<RangeExpression> rangeParameters )
	{
		this.name = name;
		this.typeParameters = typeParameters;
		this.rangeParameters = rangeParameters;
	}

	public String getName( )
	{
		return name;
	}

	public List<TypeExpression> getTypeParameters( )
	{
		return typeParameters;
	}

	public List<RangeExpression> getRangeParameters( )
	{
		return rangeParameters;
	}

	@Override
	public TypeExpression replaceTypeName( String nm, TypeExpression expression )
	{
		if ( typeParameters.isEmpty( ) && rangeParameters.isEmpty( ) )
		{
			if ( name.equals( nm ) )
			{
				return expression;
			}
			else
			{
				return this;
			}
		}
		else
		{
			return new ParameterisedType( name, typeParameters.stream( ).map( e -> e.replaceTypeName( nm, expression ) ).collect( Collectors.toList( ) ), rangeParameters );
		}
	}

	@Override
	public TypeExpression replaceRangeName( String name, RangeExpression expression )
	{
		return new ParameterisedType( name, typeParameters.stream( ).map( e -> e.replaceRangeName( name, expression ) ).collect( Collectors.toList( ) ), rangeParameters.stream( ).map( e -> e.replaceRangeName( name, expression ) ).collect( Collectors.toList( ) ) );
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( name );
		for ( TypeExpression tparam : typeParameters )
		{
			writer.write( '[' );
			tparam.compose( writer );
			writer.write( ']' );
		}
		for ( RangeExpression rparam : rangeParameters )
		{
			writer.write( '{' );
			rparam.compose( writer );
			writer.write( '}' );
		}
	}
}

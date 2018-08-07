package org.structuredschema.typeexpression;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NamedType extends TypeExpression
{
	private final String name;
	private final List<TypeExpression> parameters;

	public NamedType( String name, List<TypeExpression> parameters )
	{
		this.name = name;
		this.parameters = parameters;
	}

	public String getName( )
	{
		return name;
	}

	public List<TypeExpression> getParameters( )
	{
		return parameters;
	}

	@Override
	public TypeExpression replace( String nm, TypeExpression expression )
	{
		if ( parameters.isEmpty( ) )
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
			return new NamedType( name, parameters.stream( ).map( e -> e.replace( nm, expression ) ).collect( Collectors.toList( ) ) );
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( name );
		for ( TypeExpression tparam : parameters )
		{
			writer.write( '[' );
			tparam.compose( writer );
			writer.write( ']' );
		}
	}

	@Override
	public void validate( Object val, Map<String,TypeDeclaration> context, List<String> errors )
	{
		TypeDeclaration decl = context.get( name );
		Object def = decl.getDefinition( );
		for ( int i = 0; i < decl.getParameters( ).size( ); i++ )
		{
			String pname = decl.getParameters( ).get( i );
			TypeExpression pexpr = parameters.get( i );
			def = replace( def, pname, pexpr );
		}
		
		
	}

	private Object replace( Object def, String pname, TypeExpression pexpr )
	{
		if ( def instanceof TypeExpression )
		{
			TypeExpression expr = (TypeExpression)def;
			return expr.replace( pname, pexpr );
		}
		else if ( def instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)def;
			Map<String,Object> result = new HashMap<>( );
			for ( Map.Entry<String,Object> entry : map.entrySet( ) )
			{
				result.put( entry.getKey( ), replace( entry.getValue( ), pname, pexpr ) );
			}
			return result;
		}
		else if ( def instanceof List )
		{
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)def;
			List<Object> result = new LinkedList<>( );
			for ( Object item : list )
			{
				result.add( replace( item, pname, pexpr ) );
			}
			return result;
		}
		else
		{
			throw new RuntimeException( "bad type" );
		}
	}
}
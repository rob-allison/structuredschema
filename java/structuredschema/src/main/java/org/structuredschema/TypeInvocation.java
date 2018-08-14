package org.structuredschema;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeInvocation extends TypeExpression
{
	private final String name;
	private final List<TypeExpression> parameters;

	public TypeInvocation( String name, List<TypeExpression> parameters )
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
			return new TypeInvocation( name, parameters.stream( ).map( e -> e.replace( nm, expression ) ).collect( Collectors.toList( ) ) );
		}
	}

	@Override
	public void compose( Writer writer ) throws IOException
	{
		writer.write( name );
		if ( !parameters.isEmpty( ) )
		{
			writer.write( '(' );
			for ( Iterator<TypeExpression> iter = parameters.iterator( ); iter.hasNext( ); )
			{
				TypeExpression tparam = iter.next( );
				tparam.compose( writer );
				if ( iter.hasNext( ) )
				{
					writer.write( ',' );
				}
			}
			writer.write( ')' );
		}
	}

	@Override
	public void validate( Object val, StructuredSchema schema, Errors errors )
	{
		if ( name.equals( "Object" ) )
		{
			if ( val != null && val instanceof Map )
			{
				Map<String,Object> map = new HashMap<>( );
				TypeExpression vdef = getParameter( 0 );
				for ( Map.Entry<String,Object> entry : map.entrySet( ) )
				{
					String k = entry.getKey( );
					Object v = entry.getValue( );
					schema.validate( v, vdef, errors.field( k ) );
				}
			}
			else
			{
				errors.add( "object expected", val, toString( ) );
			}
		}
		else if ( name.equals( "Array" ) )
		{
			if ( val != null && val instanceof List )
			{
				@SuppressWarnings("unchecked")
				List<Object> vlist = (List<Object>)val;
				TypeExpression idef = getParameter( 0 );
				for ( int i = 0; i < vlist.size( ); i++ )
				{
					Object item = vlist.get( i );
					schema.validate( item, idef, errors.item( i ) );
				}
				TypeExpression ndef = getParameter( 1 );
				schema.validate( vlist.size( ), ndef, errors.field( "!size" ) );
			}
			else
			{
				errors.add( "array expected", val, toString( ) );
			}
		}
		else
		{
			TypeDeclaration decl = schema.get( name );

			decl = subtype( val, decl, schema, errors );

			if ( !decl.isAbstract( ) )
			{
				Object def = extend( decl, schema );

				for ( int i = 0; i < decl.getParameters( ).size( ); i++ )
				{
					String pname = decl.getParameters( ).get( i );
					TypeExpression pexpr = getParameter( i );
					def = replace( def, pname, pexpr );
				}

				schema.validate( val, def, errors );
			}
			else
			{
				throw new RuntimeException( "abstract declaration" );
			}
		}
	}

	private Object extend( TypeDeclaration decl, StructuredSchema schema )
	{
		Object def = decl.getDefinition( );
		String ext = decl.getExtnds( );
		if ( ext != null )
		{
			TypeDeclaration edecl = schema.get( ext );
			@SuppressWarnings("unchecked")
			Map<String,Object> emap = (Map<String,Object>)extend( edecl, schema );
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)def;
			Map<String,Object> result = new HashMap<String,Object>( );
			result.putAll( emap );
			result.putAll( map );
			return result;
		}
		return def;
	}

	private TypeDeclaration subtype( Object val, TypeDeclaration decl, StructuredSchema schema, Errors errors )
	{
		Object def = decl.getDefinition( );

		if ( def instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)def;

			String discrim = null;
			for ( Map.Entry<String,Object> entry : map.entrySet( ) )
			{
				FieldValueExpression fvexpr = (FieldValueExpression)entry.getValue( );
				if ( fvexpr.getExpression( ) == Discriminator.instance )
				{
					discrim = entry.getKey( );
				}
			}

			if ( discrim != null )
			{
				if ( val instanceof Map )
				{
					@SuppressWarnings("unchecked")
					Map<String,Object> vmap = (Map<String,Object>)val;
					String sub = (String)vmap.get( discrim );
					decl = schema.get( sub );
					// subtype( val, decl, schema );
				}
				else
				{
					errors.add( "object expected", val, toString( ) );
				}
			}
		}

		return decl;
	}

	private TypeExpression getParameter( int i )
	{
		if ( i < parameters.size( ) )
		{
			return parameters.get( i );
		}
		else
		{
			return Wild.instance;
		}
	}

	private Object replace( Object def, String pname, TypeExpression pexpr )
	{
		if ( def instanceof TypeExpression )
		{
			TypeExpression expr = (TypeExpression)def;
			return expr.replace( pname, pexpr );
		}
		else if ( def instanceof FieldValueExpression )
		{
			FieldValueExpression fvexpr = (FieldValueExpression)def;
			return fvexpr.replace( pname, pexpr );
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
			throw new RuntimeException( "bad type: " + def );
		}
	}
}

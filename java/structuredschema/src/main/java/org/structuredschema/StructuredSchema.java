package org.structuredschema;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StructuredSchema
{
	private final Map<String,TypeDeclaration> context;
	private final Object def;

	StructuredSchema( Map<String,TypeDeclaration> context, Object def )
	{
		this.context = context;
		this.def = def;
	}

	public void validate( Object document ) throws ValidationException
	{
		Errors errors = new Errors( );
		validate( document, def, errors );
		if ( !errors.isEmpty( ) )
		{
			throw new ValidationException( errors.toList( ) );
		}
	}

	public TypeDeclaration getType( String name )
	{
		return context.get( name );
	}

	public void validate( Object val, Object def, Errors errors )
	{
		if ( def instanceof Map )
		{
			if ( val instanceof Map )
			{
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String,Object>)def;
				@SuppressWarnings("unchecked")
				Map<String,Object> vmap = (Map<String,Object>)val;

				for ( Map.Entry<String,Object> entry : map.entrySet( ) )
				{
					String key = entry.getKey( );
					Object fv = entry.getValue( );

					if ( fv instanceof FieldValueExpression )
					{
						FieldValueExpression fvexpr = (FieldValueExpression)fv;
						if ( vmap.containsKey( key ) )
						{
							Object vval = vmap.get( key );
							fvexpr.getExpression( ).validate( vval, this, errors.field( key ) );
						}
						else
						{
							if ( fvexpr.isRequired( ) )
							{
								errors.missingField( key );
							}
						}
					}
					else
					{
						Object vval = vmap.get( key );
						validate( vval, fv, errors.field( key ) );
					}
				}

				for ( String key : vmap.keySet( ) )
				{
					if ( !map.containsKey( key ) )
					{
						errors.extraField( key );
					}
				}
			}
			else
			{
				errors.unmatchedType( val, writeDef( def ) );
			}
		}
		else if ( def instanceof List )
		{
			if ( val instanceof List )
			{
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>)def;
				@SuppressWarnings("unchecked")
				List<Object> vlist = (List<Object>)val;

				int end = Math.max( list.size( ), vlist.size( ) );

				for ( int i = 0; i < end; i++ )
				{
					if ( i < list.size( ) )
					{
						if ( i < vlist.size( ) )
						{
							Object v = vlist.get( i );
							validate( v, list.get( i ), errors.item( i ) );

						}
						else
						{
							errors.missingItem( i );
						}
					}
					else
					{
						errors.extraItem( i );
					}
				}
			}
			else
			{
				errors.unmatchedType( val, writeDef( def ) );
			}
		}
		else if ( def instanceof TypeExpression )
		{
			TypeExpression expr = (TypeExpression)def;
			expr.validate( val, this, errors );
		}
		else
		{
			throw new RuntimeException( "bad def: " + def );
		}
	}

	public static Object writeDef( Object def )
	{
		if ( def instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)def;
			Map<String,Object> result = new HashMap<>( );
			for ( String key : map.keySet( ) )
			{
				result.put( key, writeDef( map.get( key ) ) );
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
				result.add( writeDef( item ) );
			}
			return result;
		}
		else if ( def instanceof TypeExpression )
		{
			TypeExpression expr = (TypeExpression)def;
			return expr.toString( );
		}
		else if ( def instanceof FieldValueExpression )
		{
			FieldValueExpression fvexpr = (FieldValueExpression)def;
			return fvexpr.toString( );
		}
		else
		{
			throw new RuntimeException( "bad def" );
		}
	}
}

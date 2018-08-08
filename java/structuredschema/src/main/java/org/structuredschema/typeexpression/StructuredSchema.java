package org.structuredschema.typeexpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StructuredSchema
{
	private final Object def;
	private final Map<String,TypeDeclaration> context;
	
	private StructuredSchema( Object def, Map<String,TypeDeclaration> context )
	{
		this.def = def;
		this.context = context;
	}
	
	public List<String> validate( Object val )
	{
		List<String> errors = new LinkedList<>( );
		validate( val, def, errors );
		return errors;
	}
	
	public TypeDeclaration get( String name )
	{
		return context.get( name );
	}

	public void validate( Object val, Object def, List<String> errors )
	{
		if ( def instanceof Map )
		{
			if ( val instanceof Map )
			{
				@SuppressWarnings("unchecked")
				Map<String,Object> map = (Map<String,Object>)def;
				@SuppressWarnings("unchecked")
				Map<String,Object> vmap = (Map<String,Object>)def;
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
							fvexpr.getExpression( ).validate( vval, this, errors );
						}
						else
						{
							if ( fvexpr.isRequired( ) )
							{
								errors.add( "missing field: " + key );
							}
						}
					}
					else
					{
						Object vval = vmap.get( key );
						validate( vval, fv, errors );
					}
				}
			}
			else
			{
				errors.add( "map expected" );
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
				if ( list.size( ) == vlist.size( ) )
				{
					for ( int i = 0; i < list.size( ); i++ )
					{
						validate( vlist.get( i ), list.get( i ), errors );
					}
				}
				else
				{
					errors.add( "mismatched list size" );
				}
			}
			else
			{
				errors.add( "list expected" );
			}
		}
		else if ( def instanceof TypeExpression )
		{
			TypeExpression expr = (TypeExpression)def;
			expr.validate( val, this, errors );
		}
	}
	
	public static void main( String[] args )
	{
		Object def = TypeExpression.parse( "/hello/|List[/.*/][2]" );
		StructuredSchema sch = new StructuredSchema( def, new HashMap<String,TypeDeclaration>( ) );
		List<Object> list = new LinkedList<Object>( );
		list.add( "8" );
		list.add( "7" );
		List<String> errors = sch.validate( list );
		System.out.println( errors );
	}

}

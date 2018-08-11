package org.structuredschema.typeexpression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class StructuredSchema
{
	private final Object def;
	private final Map<String,TypeDeclaration> context;

	public static StructuredSchema read( Object schema )
	{
		if ( schema instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)schema;
			Object def = map.get( "def" );
			Object context = map.get( "context" );
			if ( context == null )
			{
				context = new LinkedList<>( );
			}
			return new StructuredSchema( readDef( def ), readContext( context ) );
		}
		else if ( schema instanceof String )
		{
			return new StructuredSchema( readDef( schema ), new HashMap<String,TypeDeclaration>( ) );
		}
		else
		{
			throw new RuntimeException( "bad schema" );
		}
	}

	private StructuredSchema( Object def, Map<String,TypeDeclaration> context )
	{
		this.def = def;
		this.context = enhance( context );
	}

	private static Map<String,TypeDeclaration> enhance( Map<String,TypeDeclaration> context )
	{
		List<Object> builtins = new LinkedList<Object>( );
		builtins.add( builtin( "Boolean", "true|false" ) );
		builtins.add( builtin( "Integer", ".." ) );
		builtins.add( builtin( "Decimal", "..." ) );
		builtins.add( builtin( "String", "/.*/" ) );
		builtins.add( builtin( "PositiveInteger", "0.." ) );
		builtins.add( builtin( "PositiveDecimal", "0.0..." ) );
		builtins.add( builtin( "Number", "..|..." ) );
		builtins.add( builtin( "PositiveNumber", "0..|0.0..." ) );
		builtins.add( builtin( "WholeNumber", "..|.../1.0" ) );
		builtins.add( builtin( "NonEmptyString", "/.+/" ) );
		builtins.add( builtin( "Scalar", "Boolean|Integer|Decimal|String" ) );
		builtins.add( builtin( "NotNull", "Scalar|Object|Array" ) );

		Map<String,TypeDeclaration> base = readContext( builtins );
		context.putAll( base );
		return context;
	}

	private static Map<String,Object> builtin( String name, String def )
	{
		Map<String,Object> map = new HashMap<String,Object>( );
		map.put( "name", name );
		map.put( "def", def );
		return map;
	}

	public List<Object> validate( Object val )
	{
		Errors errors = new Errors( val );
		validate( val, def, errors );
		return errors.toList( );
	}

	public TypeDeclaration get( String name )
	{
		TypeDeclaration decl = context.get( name );
		if ( decl != null )
		{
			return decl;
		}
		else
		{
			throw new RuntimeException( "name not found: " + name );
		}
	}

	public static Object readDef( Object def )
	{
		if ( def == null )
		{
			return Null.instance;
		}
		else if ( def instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)def;
			Map<String,Object> result = new HashMap<>( );
			for ( Map.Entry<String,Object> entry : map.entrySet( ) )
			{
				if ( entry.getValue( ) instanceof String )
				{
					result.put( entry.getKey( ), FieldValueExpression.read( (String)entry.getValue( ) ) );
				}
				else
				{
					result.put( entry.getKey( ), readDef( entry.getValue( ) ) );
				}
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
				result.add( readDef( item ) );
			}
			return result;
		}
		else if ( def instanceof String )
		{
			String str = (String)def;
			return TypeExpression.parse( str );
		}
		else if ( def instanceof Integer )
		{
			Integer val = (Integer)def;
			return new IntegerValue( BigInteger.valueOf( val ) );
		}
		else if ( def instanceof Long )
		{
			Long val = (Long)def;
			return new IntegerValue( BigInteger.valueOf( val ) );
		}
		else if ( def instanceof BigInteger )
		{
			BigInteger val = (BigInteger)def;
			return new IntegerValue( val );
		}
		else if ( def instanceof Float )
		{
			Float val = (Float)def;
			return new DecimalValue( BigDecimal.valueOf( val ) );
		}
		else if ( def instanceof Double )
		{
			Double val = (Double)def;
			return new DecimalValue( BigDecimal.valueOf( val ) );
		}
		else if ( def instanceof BigDecimal )
		{
			BigDecimal val = (BigDecimal)def;
			return new DecimalValue( val );
		}
		else if ( def instanceof Boolean )
		{
			Boolean val = (Boolean)def;
			return new BooleanValue( val );
		}
		else
		{
			throw new RuntimeException( "bad def" );
		}
	}

	private static Map<String,TypeDeclaration> readContext( Object context )
	{
		Map<String,TypeDeclaration> result = new HashMap<>( );
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>)context;
		for ( Object item : list )
		{
			TypeDeclaration decl = TypeDeclaration.read( item );
			result.put( decl.getName( ), decl );
		}
		return result;
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
							fvexpr.getExpression( ).validate( vval, this, errors.field( key, vval ) );
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

				for ( String key : vmap.keySet( ) )
				{
					if ( !map.containsKey( key ) )
					{
						errors.add( "extra field: " + key );
					}
				}
			}
			else
			{
				errors.add( "object expected" );
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
						Object v = vlist.get( i );
						validate( v, list.get( i ), errors.item( i, v ) );
					}
				}
				else
				{
					errors.add( "unmatched array size" );
				}
			}
			else
			{
				errors.add( "array expected" );
			}
		}
		else if ( def instanceof TypeExpression )
		{
			TypeExpression expr = (TypeExpression)def;
			expr.validate( val, this, errors );
		}
		else
		{
			throw new RuntimeException( "bad def" );
		}
	}

	public static void main( String[] args ) throws FileNotFoundException
	{
		Yaml yaml = new Yaml( );
		Object schema = yaml.load( new FileReader( new File( "/home/rob/structuredschema/schema.yaml") ) );
		//Object schema = schema( );
		
		StructuredSchema sch = StructuredSchema.read( schema );
		//Object doc = test( );
		Object doc = yaml.load( new FileReader( new File( "/home/rob/structuredschema/mv.yaml") ) );

		
		for ( Object err: sch.validate( doc ) )
		{
			System.out.println( err );
		}
	}

	private static List<Object> test( )
	{
		List<Object> list = new LinkedList<Object>( );
		list.add( BigDecimal.valueOf( 5 ) );
		list.add( 5L );
		return list;
	}

	private static Map<String,Object> schema( )
	{
		Map<String,Object> schema = new HashMap<String,Object>( );
		schema.put( "def", "Test(String)" );
		List<Object> context = new LinkedList<Object>( );
		Map<String,Object> decl = new HashMap<String,Object>( );
		decl.put( "name", "Test(T,U)" );
		decl.put( "def", "Array(5.0,2)" );
		context.add( decl );
		schema.put( "context", context );
		return schema;
	}

}

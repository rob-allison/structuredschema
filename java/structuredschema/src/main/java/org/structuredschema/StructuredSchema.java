package org.structuredschema;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StructuredSchema
{
	private final Object def;
	private final Map<String,TypeDeclaration> context;

	public static StructuredSchema readSimple( Object schema )
	{
		return readSimple( new LinkedList<>( ), schema );
	}

	public static StructuredSchema readSimple( List<Object> libraries, Object schema )
	{
		return readSimple( libraries, schema, true );
	}

	private static StructuredSchema readSimple( List<Object> libraries, Object simple, boolean validate )
	{
		Map<String,Object> schema = new HashMap<>( );
		schema.put( "def", simple );
		return read( libraries, schema, validate );
	}

	public static StructuredSchema read( Object schema )
	{
		return read( new LinkedList<>( ), schema );
	}

	public static StructuredSchema read( List<Object> libraries, Object schema )
	{
		return read( libraries, schema, true );
	}

	private static StructuredSchema read( List<Object> libraries, Object schema, boolean validate )
	{
		if ( schema instanceof Map )
		{
			if ( validate )
			{
				StructuredSchema schsch = StructuredSchema.readSimple( libraries, "Schema", false );
				List<Object> errors = schsch.validate( schema );
				if ( !errors.isEmpty( ) )
				{
					throw new SchemaException( errors );
				}
			}

			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)schema;
			Object def = map.get( "def" );

			Object context = map.get( "context" );
			if ( context == null )
			{
				context = new LinkedList<>( );
			}

			Map<String,TypeDeclaration> decls = readContext( context );

			for ( Object lib : libraries )
			{
				if ( validate )
				{
					StructuredSchema libsch = StructuredSchema.readSimple( libraries, "Library", false );
					List<Object> errors = libsch.validate( lib );
					if ( !errors.isEmpty( ) )
					{
						throw new RuntimeException( "bad library:" + errors.toString( ) );
					}
				}

				@SuppressWarnings("unchecked")
				Map<String,Object> lmap = (Map<String,Object>)lib;
				Object lcxt = lmap.get( "context" );
				Map<String,TypeDeclaration> ldecls = readContext( lcxt );
				decls.putAll( ldecls );
			}
			
			
			Map<String,Object> lmap = standardLibrary( );
			Object lcxt = lmap.get( "context" );
			Map<String,TypeDeclaration> ldecls = readContext( lcxt );
			decls.putAll( ldecls );

			return new StructuredSchema( readDef( def ), decls );
		}
		else
		{
			throw new RuntimeException( "bad schema" );
		}
	}

	private StructuredSchema( Object def, Map<String,TypeDeclaration> context )
	{
		this.def = def;
		this.context = context;
	}

	public static Map<String,Object> standardLibrary( )
	{
		Map<String,Object> result = new HashMap<>( );
		
		List<Object> core = new LinkedList<>( );
		core.add( decl( "Boolean", "true|false" ) );
		core.add( decl( "Integer", ".." ) );
		core.add( decl( "Decimal", "..." ) );
		core.add( decl( "String", "/.*/" ) );
		core.add( decl( "PositiveInteger", "0.." ) );
		core.add( decl( "PositiveDecimal", "0.0..." ) );
		core.add( decl( "Number", "..|..." ) );
		core.add( decl( "PositiveNumber", "0..|0.0..." ) );
		core.add( decl( "WholeNumber", "..|.../1.0" ) );
		core.add( decl( "NonEmptyString", "/.+/" ) );
		core.add( decl( "IntegerString", "/\\-?\\d+([eE][\\+\\-]?\\d+)?/" ) );
		core.add( decl( "DecimalString", "/\\-?\\d+\\.\\d+([eE][\\+\\-]?\\d+)?/" ) );
		core.add( decl( "NumberString", "IntegerString|DecimalString" ) );
		core.add( decl( "IsoDate", "/\\d{4}-[01]\\d-[0-3]\\d/" ) );
		core.add( decl( "IsoDateTimeHours", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( decl( "IsoDateTimeMinutes", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( decl( "IsoDateTimeSeconds", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( decl( "IsoDateTimeMillis", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d{3}([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( decl( "IsoDateTime", "IsoDateTimeSeconds" ) );
		core.add( decl( "Scalar", "Boolean|Integer|Decimal|String" ) );
		core.add( decl( "NotNull", "Scalar|Object|Array" ) );
		core.add( decl( "Tree(T,X)", "T|Array(Tree(T),X)" ) );
		core.add( decl( "Graph(T)", "T|Object(Graph(T))" ) );
		core.add( decl( "Grid(T,X,Y)", "Array(Array(T,Y),X)" ) );
		core.add( decl( "Grid3d(T,X,Y,Z)", "Array(Array(Array(T,Z),Y),X)" ) );

		Map<String,Object> keyed = new LinkedHashMap<>( );
		keyed.put( "key", "K" );
		keyed.put( "value", "V" );
		core.add( decl( "Entry(K,V)", keyed ) );
		core.add( decl( "Map(K,V,X)", "Array(Entry(K,V),X)" ) );

		Map<String,Object> directory = new LinkedHashMap<>( );
		directory.put( "name", "String" );
		directory.put( "contents", "Array(T|Directory(T),X)" );
		core.add( decl( "Directory(T,X)", directory ) );

		Map<String,Object> type = new LinkedHashMap<>( );
		type.put( "name", "String" );
		type.put( "abstract", "Boolean?" );
		type.put( "extends", "String?" );
		type.put( "def", "*?" );
		core.add( decl( "Type", type ) );

		Map<String,Object> schema = new LinkedHashMap<>( );
		schema.put( "def", "*" );
		schema.put( "context", "Array(Type)?" );
		core.add( decl( "Schema", schema ) );

		Map<String,Object> library = new LinkedHashMap<>( );
		library.put( "context", "Array(Type)" );
		core.add( decl( "Library", library ) );

		result.put( "context", core );
		
		return result;
	}

	private static Map<String,Object> decl( String name, Object def )
	{
		Map<String,Object> map = new LinkedHashMap<>( );
		map.put( "name", name );
		map.put( "def", def );
		return map;
	}

	public List<Object> validate( Object document )
	{
		Errors errors = new Errors( );
		validate( document, def, errors );
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
				String key = entry.getKey( );
				Object value = entry.getValue( );
				if ( value instanceof String )
				{
					result.put( key, FieldValueExpression.read( (String)value ) );
				}
				else if ( value instanceof Map || value instanceof List )
				{
					result.put( key, readDef( value ) );
				}
				else
				{
					result.put( key, new FieldValueExpression( true, TypeExpression.read( value ) ) );
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
		else
		{
			return TypeExpression.read( def );
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
							fvexpr.getExpression( ).validate( vval, this, errors.field( key ) );
						}
						else
						{
							if ( fvexpr.isRequired( ) )
							{
								errors.add( "missing_field " + key, val, writeDef( def ) );
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
						errors.add( "extra_field " + key, val, writeDef( def ) );
					}
				}
			}
			else
			{
				errors.add( "object_expected", val, writeDef( def ) );
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
						validate( v, list.get( i ), errors.item( i ) );
					}
				}
				else if ( list.size( ) < vlist.size( ) )
				{
					errors.add( "array_oversize", val, writeDef( def ) );
				}
				else
				{
					errors.add( "array_undersize", val, writeDef( def ) );
				}
			}
			else
			{
				errors.add( "array_expected", val, writeDef( def ) );
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
			return expr.toDefinition( );
		}
		else if ( def instanceof FieldValueExpression )
		{
			FieldValueExpression fvexpr = (FieldValueExpression)def;
			return fvexpr.toDefinition( );
		}
		else
		{
			throw new RuntimeException( "bad def" );
		}
	}
}

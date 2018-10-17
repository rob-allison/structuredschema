package org.structuredschema;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StructuredContext
{
	private static final StructuredContext corecontext;

	static
	{
		corecontext = new StructuredContext( );
		List<Object> corelib = StructuredContext.coreLibrary( );
		readLibrary( corelib, corecontext.declarations );
		StructuredSchema libraryschema = corecontext.read( "Library", false );
		try
		{
			libraryschema.validate( corelib );
		}
		catch ( ValidationException e )
		{
			throw new SchemaValidationException( e );
		}
	}

	private final Map<String,TypeDeclaration> declarations = new HashMap<>( );

	public static StructuredContext core( )
	{
		return corecontext;
	}

	public static StructuredContext withLibrary( Object library )
	{
		List<Object> list = new LinkedList<>( );
		list.add( library );
		return withLibraries( list );
	}

	public static StructuredContext withLibraries( List<Object> libraries )
	{
		StructuredContext context = new StructuredContext( );
		StructuredSchema libraryschema = corecontext.read( "Library" );
		for ( Object lib : libraries )
		{
			try
			{
				libraryschema.validate( lib );
			}
			catch ( ValidationException e )
			{
				throw new SchemaValidationException( e );
			}
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)lib;
			readLibrary( list, context.declarations );
		}

		context.declarations.putAll( corecontext.declarations );
		return context;
	}

	private static void readLibrary( List<Object> library, Map<String,TypeDeclaration> declarations )
	{
		for ( Object item : library )
		{
			TypeDeclaration decl = TypeDeclaration.read( item );
			declarations.put( decl.getName( ), decl );
		}
	}

	public StructuredSchema read( Object def )
	{
		return read( def, true );
	}

	private StructuredSchema read( Object def, boolean validate )
	{
		if ( validate )
		{
			StructuredSchema schemaschema = corecontext.read( "Schema", false );
			try
			{
				schemaschema.validate( def );
			}
			catch ( ValidationException e )
			{
				throw new SchemaValidationException( e );
			}
		}
		return new StructuredSchema( declarations, StructuredContext.readDef( def ) );
	}

	public static Object readDef( Object def )
	{
		if ( def instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)def;
			Map<String,Object> result = new HashMap<>( );
			for ( Map.Entry<String,Object> entry : map.entrySet( ) )
			{
				String key = entry.getKey( );
				Object value = entry.getValue( );

				if ( value instanceof Map || value instanceof List )
				{
					result.put( key, readDef( value ) );
				}
				else if ( value instanceof String )
				{
					result.put( key, FieldValueExpression.read( (String)value ) );
				}
				else
				{
					throw new RuntimeException( "bad def" );
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
			return TypeExpression.parse( (String)def );
		}
		else
		{
			throw new RuntimeException( "bad def" );
		}
	}

	public static List<Object> coreLibrary( )
	{
		List<Object> core = new LinkedList<>( );
		core.add( StructuredContext.decl( "Boolean", "true|false" ) );
		core.add( StructuredContext.decl( "Integer", ".." ) );
		core.add( StructuredContext.decl( "Decimal", "..." ) );
		core.add( StructuredContext.decl( "String", "/.*/" ) );
		core.add( StructuredContext.decl( "PositiveInteger", "0.." ) );
		core.add( StructuredContext.decl( "PositiveDecimal", "0.0..." ) );
		core.add( StructuredContext.decl( "Number", "..|..." ) );
		core.add( StructuredContext.decl( "PositiveNumber", "0..|0.0..." ) );
		core.add( StructuredContext.decl( "WholeNumber", "..|.../1.0" ) );
		core.add( StructuredContext.decl( "NonEmptyString", "/.+/" ) );
		core.add( StructuredContext.decl( "IntegerString", "/\\-?\\d+([eE][\\+\\-]?\\d+)?/" ) );
		core.add( StructuredContext.decl( "DecimalString", "/\\-?\\d+\\.\\d+([eE][\\+\\-]?\\d+)?/" ) );
		core.add( StructuredContext.decl( "NumberString", "IntegerString|DecimalString" ) );
		core.add( StructuredContext.decl( "IsoDate", "/\\d{4}-[01]\\d-[0-3]\\d/" ) );
		core.add( StructuredContext.decl( "IsoDateTimeHours", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( StructuredContext.decl( "IsoDateTimeMinutes", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( StructuredContext.decl( "IsoDateTimeSeconds", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( StructuredContext.decl( "IsoDateTimeMillis", "/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d{3}([+-][0-2]\\d:[0-5]\\d|Z)/" ) );
		core.add( StructuredContext.decl( "IsoDateTime", "IsoDateTimeSeconds" ) );
		core.add( StructuredContext.decl( "Scalar", "Boolean|Integer|Decimal|String" ) );
		core.add( StructuredContext.decl( "NotNull", "Scalar|Object|Array" ) );
		core.add( StructuredContext.decl( "Tree(T,X)", "T|Array(Tree(T),X)" ) );
		core.add( StructuredContext.decl( "Graph(T)", "T|Object(Graph(T))" ) );
		core.add( StructuredContext.decl( "Structured(T,X)", "T|Object(Structured(T))|Array(Structured(T,X))" ) );
		core.add( StructuredContext.decl( "Grid(T,X,Y)", "Array(Array(T,Y),X)" ) );
		core.add( StructuredContext.decl( "Grid3d(T,X,Y,Z)", "Array(Array(Array(T,Z),Y),X)" ) );

		Map<String,Object> keyed = new LinkedHashMap<>( );
		keyed.put( "key", "K" );
		keyed.put( "value", "V" );
		core.add( StructuredContext.decl( "Entry(K,V)", keyed ) );
		core.add( StructuredContext.decl( "Map(K,V,X)", "Array(Entry(K,V),X)" ) );

		Map<String,Object> directory = new LinkedHashMap<>( );
		directory.put( "name", "String" );
		directory.put( "contents", "Array(T|Directory(T),X)" );
		core.add( StructuredContext.decl( "Directory(T,X)", directory ) );

		Map<String,Object> type = new LinkedHashMap<>( );
		type.put( "name", "String" );
		type.put( "abstract", "Boolean?" );
		type.put( "extends", "String?" );
		type.put( "def", "Structured(String)?" );
		core.add( StructuredContext.decl( "Type", type ) );

		core.add( StructuredContext.decl( "Schema", "Structured(String)" ) );
		core.add( StructuredContext.decl( "Library", "Array(Type)" ) );

		return core;
	}

	static Map<String,Object> decl( String name, Object def )
	{
		Map<String,Object> map = new LinkedHashMap<>( );
		map.put( "name", name );
		map.put( "def", def );
		return map;
	}
}

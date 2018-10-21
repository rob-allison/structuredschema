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
		return withLibs( list );
	}

	public static StructuredContext withLibs( List<Object> libraries )
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
		core.add( decl( "Structured(T,X)", "T|Object(Structured(T))|Array(Structured(T,X))" ) );
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
		type.put( "def", "Structured(String)?" );
		core.add( decl( "Type", type ) );

		core.add( decl( "Schema", "Structured(String)" ) );
		core.add( decl( "Library", "Array(Type)" ) );
		
		Map<String,Object> error = new LinkedHashMap<>( );
		error.put( "code", "_missing-field_|_extra-field_|_missing-item_|_extra-item_|_unmatched-type_|_unmatched-union_|_bad-discriminator_" );
		error.put( "is", "String|PositiveInteger" );
		error.put( "path", "Array(String|PositiveInteger)" );
		error.put( "value", "*" );
		core.add( decl( "Error", error ) );
		core.add( decl( "Errors", "Array(Error,1..)" ) );
		
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

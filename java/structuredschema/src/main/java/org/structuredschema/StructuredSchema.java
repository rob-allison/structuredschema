package org.structuredschema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StructuredSchema
{
	private final Object root;
	private final Map<String,TypeDeclaration> rootcontext;

	public StructuredSchema( Object schema ) throws ValidationException
	{
		this( schema, true );
	}

	@SuppressWarnings("unchecked")
	private StructuredSchema( Object schema, boolean validate ) throws ValidationException
	{
		if ( validate )
		{
			Object schsch = buildSchemaSchema( );
			StructuredSchema sch = new StructuredSchema( schsch, false );
			sch.validate( schsch );
			sch.validate( schema );
		}

		Map<String,Object> map = (Map<String,Object>)schema;
		this.root = map.get( "def" );
		List<Object> defs = (List<Object>)map.get( "context" );
		this.rootcontext = buildContext( defs );
	}

	@SuppressWarnings("unchecked")
	private Map<String,TypeDeclaration> buildContext( List<Object> defs )
	{
		Map<String,TypeDeclaration> context = new HashMap<String,TypeDeclaration>( );

		for ( Object def : defs )
		{
			Map<String,Object> td = (Map<String,Object>)def;
			ParameterisedName ptype = ParameterisedName.parse( (String)td.get( "name" ) );
			Boolean abs = (Boolean)td.getOrDefault( "abstract", Boolean.FALSE );
			TypeDeclaration tdef = new TypeDeclaration( ptype, td.get( "def" ), (String)td.get( "extends" ), abs );
			if ( context.put( ptype.getName( ), tdef ) != null )
			{
				throw new RuntimeException( "duplicate defs" );
			}
		}

		return context;
	}

	public void validate( Object data ) throws ValidationException
	{
		List<String> fails = new LinkedList<String>( );
		validate( rootcontext, "", data, root, fails );
		if ( !fails.isEmpty( ) )
		{
			throw new ValidationException( fails );
		}
	}

	@SuppressWarnings("unchecked")
	private void validate( Map<String,TypeDeclaration> context, String path, Object data, Object type, List<String> fails )
	{
		if ( type instanceof String )
		{
			ParameterisedName[] ptypes = ParameterisedName.parseTypes( (String)type );
			List<String> culmfails = new LinkedList<String>( );
			for ( ParameterisedName ptype : ptypes )
			{
				String tname = ptype.getName( );
				String[] params = ptype.getTypes( );
				String[] ranges = ptype.getRanges( );
				List<String> typefails = new LinkedList<String>( );

				switch ( tname )
				{
					case "Any":
						return;

					case "String":
						if ( data instanceof String )
						{
							if ( ranges.length == 1 )
							{
								String range = ranges[0];
								String str = (String)data;
								Pattern p = Pattern.compile( range );
								Matcher m = p.matcher( str );
								if ( !m.matches( ) )
								{
									typefails.add( path + " - regex failed: " + range );
								}
								else
								{
									return;
								}
							}
							else
							{
								return;
							}
						}
						else
						{
							typefails.add( path + " - String expected" );
						}
						break;

					case "Integer":
						if ( data instanceof Integer | data instanceof Long | data instanceof BigInteger )
						{
							if ( ranges.length == 1 )
							{
								String range = ranges[0];
								if ( !checkIntegerRange( range, promoteInteger( data ) ) )
								{
									typefails.add( path + " - range failed: " + range );
								}
								else
								{
									return;
								}
							}
							else
							{
								return;
							}
						}
						else
						{
							typefails.add( path + " - Integral expected" );
						}
						break;

					case "Decimal":
						if ( data instanceof Float | data instanceof Double | data instanceof BigDecimal )
						{
							if ( ranges.length == 1 )
							{
								String range = ranges[0];
								if ( !checkDecimalRange( range, promoteDecimal( data ) ) )
								{
									typefails.add( path + " - range failed: " + range );
								}
								else
								{
									return;
								}
							}
							else
							{
								return;
							}
						}
						else
						{
							typefails.add( path + " - Decimal expected" );
						}
						break;

					case "Boolean":
						if ( data instanceof Boolean )
						{
							return;
						}
						else
						{
							typefails.add( path + " - Boolean expected" );
						}
						break;

					case "Map":
						if ( data instanceof Map )
						{
							Map<String,Object> map = (Map<String,Object>)data;
							String param = params[0];
							boolean opt = param.endsWith( "?" );
							String iparam = opt ? param.substring( 0, param.length( ) - 1 ) : param;
							for ( Map.Entry<String,Object> entry : map.entrySet( ) )
							{
								String k = path + "." + entry.getKey( );
								Object val = entry.getValue( );
								if ( val != null )
								{
									validate( context, k, val, iparam, typefails );
								}
								else
								{
									if ( !opt )
									{
										fails.add( path + " - missing" );
									}
								}
							}
							if ( ranges.length == 1 )
							{
								String range = ranges[0];
								if ( !checkIntegerRange( range, promoteInteger( map.size( ) ) ) )
								{
									typefails.add( path + " - map size: " + map.size( ) + " range failed: " + range );
								}
							}
						}
						else
						{
							typefails.add( path + " - Map expected" );
						}
						break;

					case "Scalar":
						if ( data instanceof String || data instanceof Long || data instanceof Double || data instanceof Boolean )
						{
							return;
						}
						else
						{
							typefails.add( path + " - Scalar expected" );
						}
						break;

					case "Discriminator":
						if ( data instanceof String )
						{
							return;
						}
						else
						{
							typefails.add( path + " - String expected" );
						}
						break;

					case "List":
						if ( data instanceof List )
						{
							List<Object> list = (List<Object>)data;
							String param = params[0];
							int i = 0;
							boolean opt = param.endsWith( "?" );
							String iparam = opt ? param.substring( 0, param.length( ) - 1 ) : param;
							for ( Object item : list )
							{
								String k = String.format( path + "[%d]", i );
								if ( item != null )
								{
									validate( context, k, item, iparam, typefails );
								}
								else
								{
									if ( !opt )
									{
										fails.add( path + " - missing" );
									}
								}
								i++;
							}
							if ( ranges.length == 1 )
							{
								String range = ranges[0];
								if ( !checkIntegerRange( range, promoteInteger( list.size( ) ) ) )
								{
									typefails.add( path + " - list size: " + list.size( ) + " range failed: " + range );
								}
							}
						}
						else
						{
							typefails.add( path + " - List expected" );
						}
						break;

					default:
						TypeDeclaration td = context.get( tname );
						if ( td != null )
						{
							td = td.applyParameters( params, ranges );
							if ( td.isAlias( ) )
							{
								validate( context, path, data, td.getAlias( ), typefails );
							}
							else
							{
								if ( data instanceof Map )
								{
									Map<String,Object> map = (Map<String,Object>)data;
									String discrim = null;
									for ( Map.Entry<String,Object> entry : td.getDef( ).entrySet( ) )
									{
										if ( "Discriminator".equals( entry.getValue( ) ) )
										{
											discrim = entry.getKey( );
											break;
										}
									}

									if ( discrim != null )
									{
										String polyname = (String)map.get( discrim );
										TypeDeclaration polytd = context.get( polyname );
										if ( polytd != null )
										{
											if ( isSubtype( context, polytd, tname ) )
											{
												if ( !polytd.isAbstract( ) )
												{
													polytd = polytd.applyParameters( params, ranges );
													validateObject( context, path, map, inheritDefinitions( context, polytd ), typefails );
												}
												else
												{
													typefails.add( path + " - can't instantiate abstract type: " + polyname );
												}
											}
											else
											{
												typefails.add( path + " - Discriminated type " + polyname + " does not extend base type " + tname );
											}
										}
										else
										{
											typefails.add( path + " - Discriminated type not found: " + polyname );
										}
									}
									else
									{
										if ( !td.isAbstract( ) )
										{
											validateObject( context, path, map, inheritDefinitions( context, td ), typefails );
										}
										else
										{
											typefails.add( path + " - can't instantiate abstract type: " + tname );
										}
									}
								}
								else
								{
									typefails.add( path + " - Map expected" );
								}
							}
						}
						else
						{
							typefails.add( "SCHEMA! no typedef: " + tname );
						}
				}

				if ( typefails.isEmpty( ) )
				{
					return;
				}
				else
				{
					for ( String tfail : typefails )
					{
						culmfails.add( tfail );
					}
				}
			}

			fails.addAll( culmfails );
		}
		else
		{
			if ( data instanceof Map )
			{
				Map<String,Object> map = (Map<String,Object>)data;
				Map<String,Object> def = (Map<String,Object>)type;
				validateObject( context, path, map, def, fails );
			}
			else
			{
				fails.add( path + " - Map expected" );
			}
		}
	}

	private Map<String,Object> inheritDefinitions( Map<String,TypeDeclaration> context, TypeDeclaration typ )
	{
		Map<String,Object> def = new HashMap<String,Object>( );
		String ext = typ.getExtends( );
		if ( ext != null )
		{
			TypeDeclaration supertyp = context.get( ext );
			Map<String,Object> superdef = inheritDefinitions( context, supertyp );
			def.putAll( superdef );
		}
		def.putAll( typ.getDef( ) );
		return def;
	}

	private boolean isSubtype( Map<String,TypeDeclaration> context, TypeDeclaration typ, String base )
	{
		String ext = typ.getExtends( );
		if ( ext != null )
		{
			if ( ext.equals( base ) )
			{
				return true;
			}
			else
			{
				TypeDeclaration supertyp = context.get( ext );
				return isSubtype( context, supertyp, base );
			}
		}
		return false;
	}

	private BigInteger promoteInteger( Object d )
	{
		if ( d instanceof Integer )
		{
			return BigInteger.valueOf( (Integer)d );
		}
		else if ( d instanceof Long )
		{
			return BigInteger.valueOf( (Long)d );
		}
		else
		{
			return (BigInteger)d;
		}
	}

	private boolean checkIntegerRange( String range, BigInteger d )
	{
		String[] ranges = range.split( "," );
		for ( String r : ranges )
		{
			if ( r.contains( ".." ) )
			{
				int dots = r.indexOf( ".." );
				boolean passed = true;
				if ( dots > 0 )
				{
					BigInteger low = new BigInteger( r.substring( 0, dots ) );
					if ( d.compareTo( low ) < 0 )
					{
						passed = false;
					}
				}
				if ( dots < r.length( ) - 2 )
				{
					BigInteger high = new BigInteger( r.substring( dots + 2, r.length( ) ) );
					if ( d.compareTo( high ) > 0 )
					{
						passed = false;
					}
				}
				if ( passed )
				{
					return true;
				}
			}
			else
			{
				BigInteger x = new BigInteger( r );
				if ( x.equals( d ) )
				{
					return true;
				}
			}
		}
		return false;
	}

	private BigDecimal promoteDecimal( Object d )
	{
		if ( d instanceof Float )
		{
			return BigDecimal.valueOf( (Float)d );
		}
		else if ( d instanceof Double )
		{
			return BigDecimal.valueOf( (Double)d );
		}
		else
		{
			return (BigDecimal)d;
		}
	}
	
	private boolean checkDecimalRange( String range, BigDecimal d )
	{
		String[] ranges = range.split( "," );
		for ( String r : ranges )
		{
			if ( r.contains( ".." ) )
			{
				int dots = r.indexOf( ".." );
				boolean passed = true;
				if ( dots > 0 )
				{
					BigDecimal low = new BigDecimal( r.substring( 0, dots ) );
					if ( d.compareTo( low ) < 0 )
					{
						passed = false;
					}
				}
				if ( dots < r.length( ) - 2 )
				{
					BigDecimal high = new BigDecimal( r.substring( dots + 2, r.length( ) ) );
					if ( d.compareTo( high ) > 0 )
					{
						passed = false;
					}
				}
				if ( passed )
				{
					return true;
				}
			}
			else
			{
				BigDecimal x = new BigDecimal( r );
				if ( x.equals( d ) )
				{
					return true;
				}
			}
		}
		return false;
	}

	private void validateObject( Map<String,TypeDeclaration> context, String path, Map<String,Object> data, Map<String,Object> schema, List<String> fails )
	{
		for ( Map.Entry<String,Object> sent : schema.entrySet( ) )
		{
			String key = sent.getKey( );
			String typ = (String)sent.getValue( );
			boolean opt = typ.endsWith( "?" );
			typ = opt ? typ.substring( 0, typ.length( ) - 1 ) : typ;
			Object child = data.get( key );
			String pathkey = path + "." + key;
			if ( child != null )
			{
				validate( context, pathkey, child, typ, fails );
			}
			else
			{
				if ( !opt )
				{
					fails.add( pathkey + " - missing" );
				}
			}
		}

		for ( String key : data.keySet( ) )
		{
			if ( !schema.containsKey( key ) )
			{
				String pathkey = path + "." + key;
				fails.add( pathkey + " - unexpected" );
			}
		}
	}

	private Object buildSchemaSchema( )
	{
		Map<String,Object> schema = new HashMap<String,Object>( );
		Map<String,Object> def = new HashMap<String,Object>( );
		def.put( "def", "Tree[String]" );
		def.put( "context", "List[Type]" );
		List<Object> context = new LinkedList<Object>( );
		Map<String,Object> type = new HashMap<String,Object>( );
		type.put( "name", "Type" );
		Map<String,Object> typedef = new HashMap<String,Object>( );
		typedef.put( "name", "String" );
		typedef.put( "abstract", "Boolean?" );
		typedef.put( "extends", "String?" );
		typedef.put( "def", "Tree[String]?" );
		type.put( "def", typedef );
		Map<String,Object> tree = new HashMap<String,Object>( );
		tree.put( "name", "Tree[T]" );
		tree.put( "def", "T|Map[Tree[T]]" );
		context.add( type );
		context.add( tree );
		schema.put( "def", def );
		schema.put( "context", context );
		return schema;
	}
}

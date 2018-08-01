package org.structuredschema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TypeDeclaration
{
	private final ParameterisedName name;
	private final String alias;
	private final Map<String,Object> def;
	private final String extnds;
	private final boolean abstrct;

	@SuppressWarnings("unchecked")
	public TypeDeclaration( ParameterisedName name, Object obj, String extnds, boolean abstrct )
	{
		this.name = name;
		
		if ( obj == null )
		{
			obj = new HashMap<String,Object>( );
		}
		
		if ( obj instanceof String )
		{
			this.alias = (String)obj;
			this.def = null;
		}
		else
		{
			this.alias = null;
			this.def = (Map<String,Object>)obj;
			
			if ( this.def.containsValue( "Discriminator" ) )
			{
				abstrct = true;
			}
		}
		this.extnds = extnds;
		this.abstrct = abstrct;
	}

	public boolean isAlias( )
	{
		return alias != null;
	}

	public String getAlias( )
	{
		return alias;
	}

	public Map<String,Object> getDef( )
	{
		return def;
	}

	public String getExtends( )
	{
		return extnds;
	}

	public boolean isAbstract( )
	{
		return abstrct;
	}

	public TypeDeclaration applyParameters( String[] tvals, String[] rvals )
	{
		String[] tparams = name.getTypes( );
		String[] rparams = name.getRanges( );
		
		int ti = tparams.length - tvals.length;
		int ri = rparams.length - rvals.length;
		
		tparams = Arrays.copyOfRange( tparams, ti, tparams.length );
		rparams = Arrays.copyOfRange( rparams, ri, rparams.length );
		
		if ( isAlias( ) )
		{
			String al = replaceParameters( alias, tparams, tvals, rparams, rvals );
			return new TypeDeclaration( name.stripParameters( ti, ri ), al, null, abstrct );
		}
		else
		{
			String ex = null;
			if ( extnds != null )
			{
				ex = replaceParameters( extnds, tparams, tvals, rparams, rvals );
			}
			Map<String,Object> df = copy( def );
			for ( Map.Entry<String,Object> en : def.entrySet( ) )
			{
				df.put( en.getKey( ), replaceParameters( (String)en.getValue( ), tparams, tvals, rparams, rvals ) );
			}

			return new TypeDeclaration( name.stripParameters( ti, ri ), df, ex, abstrct );
		}
	}

	private String replaceParameters( String line, String[] tparams, String[] tvals, String[] rparams, String[] rvals )
	{
		line = replaceTypeParameters( line, tparams, tvals );
		return replaceRangeParameters( line, rparams, rvals );
	}

	private String replaceTypeParameters( String line, String[] tparams, String[] tvals )
	{
		for ( int i = 0; i < tparams.length; i++ )
		{
			if ( line.equals( tparams[i] ) )
			{
				return tvals[i];
			}
			else
			{
				line = line.replace( "|" + tparams[i], "|" + tvals[i] );
				line = line.replace( tparams[i] + "|", tvals[i] + "|" );
				line = line.replace( "[" + tparams[i] + "]", "[" + tvals[i] + "]" );
			}
		}
		return line;
	}

	private String replaceRangeParameters( String line, String[] rparams, String[] rvals )
	{
		for ( int i = 0; i < rparams.length; i++ )
		{
			line = line.replace( "{" + rparams[i] + "}", "{" + rvals[i] + "}" );
		}
		return line;
	}
	
	public static <T> T copy( Object obj )
	{
		return makeCopy( obj );
	}

	@SuppressWarnings("unchecked")
	private static <T> T makeCopy( Object obj )
	{
		if ( obj instanceof Map )
		{
			Map<String,Object> map = (Map<String,Object>)obj;
			Map<String,Object> copy = new HashMap<String,Object>( );
			for ( Map.Entry<String,Object> entry : map.entrySet( ) )
			{
				copy.put( entry.getKey( ), makeCopy( entry.getValue( ) ) );
			}
			return (T)map;
		}
		else if ( obj instanceof List )
		{
			List<Object> list = (List<Object>)obj;
			List<Object> copy = new LinkedList<Object>( );
			for ( Object item : list )
			{
				copy.add( makeCopy( item ) );
			}
			return (T)list;
		}
		else
		{
			return (T)obj;
		}
	}

}
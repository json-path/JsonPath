/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.spi.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.codehaus.jettison.json.JSONException;
import com.jayway.jsonpath.InvalidJsonException;

public class JettisonProvider extends AbstractJsonProvider
{
	private static Object jettisonUnwrap(Object obj)
	{
		if( obj!=null && obj.equals(org.codehaus.jettison.json.JSONObject.NULL) )
		{
			return null;
		}
		return obj;
	}
	
	private static Object jettisonWrap(Object obj)
	{
		if( obj==null )
		{
			return org.codehaus.jettison.json.JSONObject.NULL;
		}
		return obj;
	}
	
	/** JSON Object implementation */
	private static class JettisonTokener extends org.codehaus.jettison.json.JSONTokener
	{
		public JettisonTokener(String s)
		{
			super(s);
		}
		@Override
		protected JettisonObject newJSONObject() throws JSONException 
		{
			return new JettisonObject(this);
		}
		@Override
		protected JettisonArray newJSONArray() throws JSONException 
		{
			return new JettisonArray(this);
		}
	}
	
	/** JSON Object implementation */
	private static class JettisonObject extends org.codehaus.jettison.json.JSONObject implements Iterable<Object>
	{
		private static final long serialVersionUID = 1L;
		
		private JettisonObject(JettisonTokener tokener) throws JSONException
		{
			super(tokener);
		}
		
		private JettisonObject()
		{
			super();
		}
		
		@Override
		public Iterator<Object> iterator() 
		{
			return new JettisonObjectIterator(this);
		}
	}
	/** JSON Array implementation */
	private static class JettisonArray extends org.codehaus.jettison.json.JSONArray implements Iterable<Object>
	{
		private static final long serialVersionUID = 2L;

		private JettisonArray(JettisonTokener tokener) throws JSONException
		{
			super(tokener);
		}
		
		private JettisonArray() 
		{
			super();
		}

		@Override
		public Iterator<Object> iterator() 
		{
			return new JettisonArrayIterator(this);
		}
	}
	
	private static class JettisonArrayIterator implements Iterator<Object>
	{
		private final org.codehaus.jettison.json.JSONArray jsonArray;
		private int index = 0;
		
		private JettisonArrayIterator(org.codehaus.jettison.json.JSONArray jsonArray)
		{
			this.jsonArray = jsonArray;
		}
		
		@Override
		public boolean hasNext() 
		{
			return index < jsonArray.length();
		}

		@Override
		public Object next() 
		{
			try
			{
				return jettisonUnwrap(jsonArray.get(index++));
			}
			catch( org.codehaus.jettison.json.JSONException jsonException )
			{
				throw new NoSuchElementException(jsonException.toString());
			}
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	private static class JettisonObjectIterator implements Iterator<Object>
	{
		private final org.codehaus.jettison.json.JSONObject jsonObject;
		private final Iterator<?> jsonKeysIt;
		
		
		private JettisonObjectIterator(org.codehaus.jettison.json.JSONObject jsonObject)
		{
			this.jsonObject = jsonObject;
			this.jsonKeysIt = jsonObject.keys();
		}
		
		@Override
		public boolean hasNext() 
		{
			return jsonKeysIt.hasNext();
		}

		@Override
		public Object next() 
		{
			try
			{
				return jettisonUnwrap(jsonObject.get(String.valueOf(jsonKeysIt.next())));
			}
			catch( org.codehaus.jettison.json.JSONException jsonException )
			{
				throw new NoSuchElementException(jsonException.toString());
			}
		}

		@Override
		public void remove() 
		{
			jsonKeysIt.remove();
		}
		
	}
	
	private Object parse(JettisonTokener JsonTokener)
	{
		try
		{
			char nextChar = JsonTokener.nextClean();
			JsonTokener.back();
			if (nextChar == '{') 
			{
				return new JettisonObject(JsonTokener);
			}
			if (nextChar == '[') 
			{
				return new JettisonArray(JsonTokener);
			}
			throw new JSONException("Invalid JSON");
		}
		catch( org.codehaus.jettison.json.JSONException jsonException )
		{
			throw new IllegalStateException(jsonException);
		}
	}
	
	@Override
	public Object parse(String json) throws InvalidJsonException 
	{
		return parse(new JettisonTokener(json));
	}
	
	@Override
	public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException
	{
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int size;
			while( (size=jsonStream.read(buffer))>0 )
			{
				stream.write(buffer, 0, size);
			}
			return parse(new JettisonTokener(new String(stream.toByteArray(), charset)));
		}
		catch( IOException ioe )
		{
			throw new InvalidJsonException(ioe);
		}
	}

	@Override
	public String toJson(Object obj) 
	{
		try
		{
			if( obj instanceof org.codehaus.jettison.json.JSONArray )
			{
				return ((org.codehaus.jettison.json.JSONArray)obj).toString(2);
			}
			if( obj instanceof org.codehaus.jettison.json.JSONObject )
			{
				return ((org.codehaus.jettison.json.JSONObject)obj).toString(2);
			}
			return String.valueOf(obj);
			//return "\"" + String.valueOf(obj).replaceAll("\"", "\\\"") + "\"";
		}
		catch( org.codehaus.jettison.json.JSONException jsonException )
		{
			throw new IllegalStateException(jsonException);
		}
	}

	@Override
	public Object createMap() 
	{
		return new JettisonObject();
	}

	@Override
	public Iterable<?> createArray() 
	{
		return new JettisonArray();
	}
	
	@Override 
	public Object unwrap(Object obj)
	{
		return jettisonUnwrap(obj);
	}
	
	@Override
	public boolean isArray(Object obj) 
	{
		return obj instanceof org.codehaus.jettison.json.JSONArray;
	}
	
	@Override
	public boolean isMap(Object obj) 
	{
		return obj instanceof org.codehaus.jettison.json.JSONObject;
	}

	@Override
	public int length(Object obj) 
	{
		if( obj instanceof org.codehaus.jettison.json.JSONArray )
		{
			return ((org.codehaus.jettison.json.JSONArray) obj).length();
		}
		if( obj instanceof org.codehaus.jettison.json.JSONObject )
		{
			return ((org.codehaus.jettison.json.JSONObject)obj).length();
		}
		if( obj instanceof String )
		{
			return ((String)obj).length();
		}
		return 0;
	}

	@Override
	public Iterable<?> toIterable(final Object obj) 
	{
		return new Iterable<Object>()
		{
			@Override
			public Iterator<Object> iterator() 
			{
				if( obj instanceof org.codehaus.jettison.json.JSONArray )
				{
					return new JettisonArrayIterator((org.codehaus.jettison.json.JSONArray)obj);
				}
				if( obj instanceof org.codehaus.jettison.json.JSONObject )
				{
					return new JettisonObjectIterator((org.codehaus.jettison.json.JSONObject)obj);
				}
				return Collections.emptyList().iterator();
			}
		};

	}

	public Collection<String> getPropertyKeys(Object obj) 
	{
		List<String> keys = new ArrayList<String>(length(obj));
		
		if( obj instanceof org.codehaus.jettison.json.JSONArray )
		{
			for (int i = 0; i < length(obj); i++)
			{
				keys.add(String.valueOf(i));
			}
		}
		if( obj instanceof org.codehaus.jettison.json.JSONObject )
		{
			Iterator<?> keysIt = ((org.codehaus.jettison.json.JSONObject)obj).keys();
			while (keysIt.hasNext())
			{
				keys.add(String.valueOf(keysIt.next()));
			}
		}
		return keys;
	}
	
	
	@Override
	public Object getArrayIndex(Object obj, int index)
	{
		return jettisonUnwrap(((org.codehaus.jettison.json.JSONArray)obj).opt(index));
	}
	
	@Override public void setArrayIndex(Object array, int index, Object value)
	{
		if( !isArray(array) )
		{
			throw new UnsupportedOperationException();
		}
		
		try
		{
			((org.codehaus.jettison.json.JSONArray)array).put(index, jettisonWrap(value));
		}
		catch( org.codehaus.jettison.json.JSONException jsonException )
		{
			throw new IllegalArgumentException(jsonException);
		}
	}
	
	@Override 
	public Object getMapValue(Object obj, String key)
	{
		Object value = ((org.codehaus.jettison.json.JSONObject)obj).opt(key);
		if( value==null )
		{
			return com.jayway.jsonpath.spi.json.JsonProvider.UNDEFINED;
		}
		return jettisonUnwrap(value);
	}
	
	@Override
	public void setProperty(Object obj, Object key, Object value) 
	{
		try
		{
			if( obj instanceof org.codehaus.jettison.json.JSONArray )
			{
				int index = key instanceof Integer? (Integer) key : Integer.parseInt(key.toString());
				((org.codehaus.jettison.json.JSONArray)obj).put(index, jettisonWrap(value));
			}
			if( obj instanceof org.codehaus.jettison.json.JSONObject )
			{
				((org.codehaus.jettison.json.JSONObject)obj).put(String.valueOf(key), jettisonWrap(value));
			}
		}
		catch( org.codehaus.jettison.json.JSONException jsonException )
		{
			throw new IllegalStateException(jsonException);
		}
	}
	
	@Override
	public void removeProperty(Object obj, Object key)
	{
		try
		{
			if( obj instanceof org.codehaus.jettison.json.JSONArray )
			{
				int index = key instanceof Integer? (Integer) key : Integer.parseInt(key.toString());
				if( index<length(obj) )
				{
					Object temp = new Object(); // Need FIX: JSONArray.remove(int)
					((org.codehaus.jettison.json.JSONArray)obj).put(index, temp);
					((org.codehaus.jettison.json.JSONArray)obj).remove(temp);
				}
			}
			if( obj instanceof org.codehaus.jettison.json.JSONObject )
			{
				((org.codehaus.jettison.json.JSONObject)obj).remove(String.valueOf(key));
			}
		}
		catch( org.codehaus.jettison.json.JSONException jsonException )
		{
			throw new IllegalStateException(jsonException);
		}
	}
	
}
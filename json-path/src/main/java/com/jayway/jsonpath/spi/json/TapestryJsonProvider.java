package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONCollection;
import org.apache.tapestry5.json.JSONObject;

import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

public class TapestryJsonProvider extends AbstractJsonProvider {

  public static final TapestryJsonProvider INSTANCE = new TapestryJsonProvider();

  @Override
  public Object parse(final String json) throws InvalidJsonException {
    return new JSONObject(json);
  }

  @Override
  public Object parse(final InputStream jsonStream, final String charset) throws InvalidJsonException {
    Scanner sc = null;
    try {
      sc = new Scanner(jsonStream, charset);
      return parse(sc.useDelimiter("\\A").next());
    } finally {
      if (sc != null) {
        sc.close();
      }
    }
  }

  @Override
  public String toJson(final Object obj) {
    return ((JSONCollection) obj).toCompactString();
  }

  @Override
  public Object createArray() {
    return new JSONArray();
  }

  @Override
  public Object createMap() {
    return new JSONObject();
  }

  @Override
  public void setProperty(final Object obj, final Object key, final Object value) {
    Object v = value == null ? JSONObject.NULL : value;
    if (isMap(obj)) {
      ((JSONObject) obj).put(key.toString(), v);
    }
  }

  @Override
  public boolean isMap(final Object obj) {
    return obj instanceof JSONObject;
  }

  @Override
  public Object getArrayIndex(final Object obj, final int idx) {
    return ((JSONArray) obj).get(idx);
  }

  @Override
  public Collection<String> getPropertyKeys(final Object obj) {
    return ((JSONObject) obj).keys();
  }

  @Override
  public Object getMapValue(final Object obj, final String key) {
    JSONObject json = (JSONObject) obj;
    if (!json.has(key)) {
      return UNDEFINED;
    }
    return json.get(key);
  }

  @Override
  public int length(final Object obj) {
    if (obj instanceof JSONArray) {
      return ((JSONArray) obj).length();
    } else if (obj instanceof JSONObject) {
      return ((JSONObject) obj).length();
    } else {
      throw new IllegalArgumentException("Cannot determine length of " + obj + ", unsupported type.");
    }
  }

  @Override
  public boolean isArray(final Object obj) {
    return (obj instanceof JSONArray);
  }

  @Override
  public void setArrayIndex(final Object array, final int index, final Object newValue) {
    Object v = newValue == null ? JSONObject.NULL : newValue;
    JSONArray list = (JSONArray) array;
    list.put(index, v);
  }

}

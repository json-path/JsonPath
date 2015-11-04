package com.jayway.jsonpath.internal;

import java.util.EnumSet;
import java.util.Set;

import com.jayway.jsonpath.Configuration.Defaults;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.cache.DefaultCache;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public final class DefaultsImpl implements Defaults {

  public static final DefaultsImpl INSTANCE = new DefaultsImpl();

  private final MappingProvider mappingProvider = new JsonSmartMappingProvider();
  
  private final CacheProvider cacheProvider = new DefaultCache(200);

  @Override
  public JsonProvider jsonProvider() {
    return new JsonSmartJsonProvider();
  }

  @Override
  public Set<Option> options() {
    return EnumSet.noneOf(Option.class);
  }

  @Override
  public MappingProvider mappingProvider() {
    return mappingProvider;
  }

  private DefaultsImpl() {
  }

	@Override
	public CacheProvider cacheProvider() {
		return cacheProvider;
	};

}

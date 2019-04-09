package com.jayway.jsonpath.impl;

import java.util.EnumSet;
import java.util.Set;

import com.jayway.jsonpath.Configuration.Defaults;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.impl.json.TapestryJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.BasicMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;


public class DefaultsImpl implements Defaults {
	
	private final MappingProvider mappingProvider = new BasicMappingProvider();
	
	/**
	 * 
	 */
	public DefaultsImpl()
	{}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#jsonProvider()
	 */
	@Override
	public JsonProvider jsonProvider()
	{
		return new TapestryJsonProvider();
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#options()
	 */
	@Override
	public Set<Option> options()
	{
		return EnumSet.noneOf(Option.class);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#mappingProvider()
	 */
	@Override
	public MappingProvider mappingProvider()
	{
		return mappingProvider;
	}
}
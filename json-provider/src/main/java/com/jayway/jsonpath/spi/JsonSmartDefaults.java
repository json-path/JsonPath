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
package com.jayway.jsonpath.spi;

import java.util.EnumSet;
import java.util.Set;

import com.jayway.jsonpath.Defaults;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.builder.JsonSmartNodeBuilder;
import com.jayway.jsonpath.spi.builder.NodeBuilder;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

/**
 *
 */
public class JsonSmartDefaults implements Defaults
{
	private final MappingProvider mappingProvider = new JsonSmartMappingProvider();

	public JsonSmartDefaults(){}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#jsonProvider()
	 */
	@Override
	public JsonProvider jsonProvider(){
		return new JsonSmartJsonProvider();
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#options()
	 */
	@Override
	public Set<Option> options(){
		return EnumSet.noneOf(Option.class);
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#mappingProvider()
	 */
	@Override
	public MappingProvider mappingProvider(){
		return mappingProvider;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.jayway.jsonpath.Defaults#nodeBuilder()
	 */
	@Override
	public NodeBuilder nodeBuilder() {
		return new JsonSmartNodeBuilder();
	}
}

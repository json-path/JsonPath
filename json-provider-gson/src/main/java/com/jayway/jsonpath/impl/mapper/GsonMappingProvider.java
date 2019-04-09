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
package com.jayway.jsonpath.impl.mapper;

import java.io.StringReader;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JsonWrapper;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class GsonMappingProvider implements MappingProvider {

    private final Gson gson;
    
    public GsonMappingProvider() {
        super();
        this.gson = new GsonBuilder(
        ).registerTypeAdapter(Date.class,new GsonDateAdapter()
        		).create();
    }

    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        if(source == null){
            return (T) null;
        }
        if(targetType.isAssignableFrom(source.getClass())) {
        	return (T) source;
        }
        if(JsonElement.class.isAssignableFrom(source.getClass())) {
        	if(!((JsonElement)source).isJsonPrimitive()) {
		        try {
		            return gson.getAdapter(targetType).fromJsonTree((JsonElement) source);
		        } catch (Exception e){
		            throw new MappingException(e);
		        }
        	} else {
        	} 
        }
    	try {
        	JsonReader reader = new JsonReader(new StringReader(source.toString()));
        	reader.setLenient(true);
        	if((targetType.isPrimitive() && targetType!=char.class && targetType!=boolean.class)
        			||(Number.class.isAssignableFrom(targetType))){	        		
        		Double d = gson.getAdapter(Double.class).read(reader);
        		if(targetType == byte.class ||targetType==Byte.class) {
        			return (T) new Byte(d.byteValue());
        		}
        		if(targetType == short.class || targetType == Short.class) {
        			return (T) new Short(d.shortValue());
        		}
        		if(targetType == int.class || targetType == Integer.class) {
        			return (T) new Integer(d.intValue());
        		}
        		if(targetType == long.class || targetType == Long.class) {
        			return (T) new Long(d.longValue());
        		}
        		if(targetType == float.class || targetType == Float.class) {
        			return (T) new Float(d.floatValue());
        		}
        		if(targetType == double.class || targetType == Double.class) {
        			return (T) d;
        		}
        		reader = new JsonReader(new StringReader(source.toString()));
             	reader.setLenient(true);
        	} 
        	return gson.getAdapter(targetType).read(reader);
        } catch (Exception e){
            throw new MappingException(e);
        }
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        try {
	        if(JsonElement.class.isAssignableFrom(source.getClass())) {
		            return (T) gson.getAdapter(TypeToken.get(targetType.getType())).fromJsonTree(
		            		(JsonElement) source);
	        } else if(JsonWrapper.class.isAssignableFrom(source.getClass())) {		        	
		            return (T) gson.getAdapter(TypeToken.get(targetType.getType())).fromJsonTree(
		            		(JsonElement) ((JsonWrapper)source).unwrap());
	        }
	        return (T) gson.getAdapter(TypeToken.get(targetType.getType())).fromJson(
            		String.valueOf(source));
	    } catch (Exception e){
		    throw new MappingException(e);
		}
	}
}

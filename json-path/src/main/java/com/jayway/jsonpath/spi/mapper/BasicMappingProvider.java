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
package com.jayway.jsonpath.spi.mapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;

public class BasicMappingProvider implements MappingProvider {

	  private static Map<Class,Converter> DEFAULT = new HashMap<Class,Converter>();

	    static {
	        DEFAULT.put(Long.class, new LongConverter());
	        DEFAULT.put(long.class, new LongConverter());
	        DEFAULT.put(Integer.class, new IntegerConverter());
	        DEFAULT.put(int.class, new IntegerConverter());
	        DEFAULT.put(Double.class, new DoubleConverter());
	        DEFAULT.put(double.class, new DoubleConverter());
	        DEFAULT.put(Float.class, new FloatConverter());
	        DEFAULT.put(float.class, new FloatConverter());
	        DEFAULT.put(BigDecimal.class, new BigDecimalConverter());
	        DEFAULT.put(String.class, new StringConverter());
	        DEFAULT.put(Date.class, new DateConverter());
	        DEFAULT.put(BigInteger.class, new BigIntegerConverter());
	        DEFAULT.put(boolean.class, new BooleanConverter());
	    }
	    
		public interface Converter<T>
		{
			T convert(Object o);
		}

	    private static class StringConverter implements Converter<String>{
	        public String convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            return src.toString();
	        }
	    }
	    private static class IntegerConverter implements Converter<Integer> {
	        public Integer convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            if(Integer.class.isAssignableFrom(src.getClass())){
	               return (Integer) src;
	            } else if (Long.class.isAssignableFrom(src.getClass())) {
	                return ((Long) src).intValue();
	            } else if (Double.class.isAssignableFrom(src.getClass())) {
	                return ((Double) src).intValue();
	            } else if (BigDecimal.class.isAssignableFrom(src.getClass())) {
	                return ((BigDecimal) src).intValue();
	            } else if (Float.class.isAssignableFrom(src.getClass())) {
	                return ((Float) src).intValue();
	            } else if (String.class.isAssignableFrom(src.getClass())) {
	                return Integer.valueOf(src.toString());
	            }
	            throw new MappingException("can not map a " + src.getClass() + " to " + Integer.class.getName());
	        }
	    }
	    
	    private static class LongConverter implements Converter<Long> {
	        public Long convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            if(Long.class.isAssignableFrom(src.getClass())){
	                return (Long) src;
	            } else if (Integer.class.isAssignableFrom(src.getClass())) {
	                return ((Integer) src).longValue();
	            } else if (Double.class.isAssignableFrom(src.getClass())) {
	                return ((Double) src).longValue();
	            } else if (BigDecimal.class.isAssignableFrom(src.getClass())) {
	                return ((BigDecimal) src).longValue();
	            } else if (Float.class.isAssignableFrom(src.getClass())) {
	                return ((Float) src).longValue();
	            } else if (String.class.isAssignableFrom(src.getClass())) {
	                return Long.valueOf(src.toString());
	            }
	            throw new MappingException("can not map a " + src.getClass() + " to " + Long.class.getName());
	        }
	    }

	    private static class DoubleConverter implements Converter<Double> {

	        public Double convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            if(Double.class.isAssignableFrom(src.getClass())){
	                return (Double) src;
	            } else if (Integer.class.isAssignableFrom(src.getClass())) {
	                return ((Integer) src).doubleValue();
	            } else if (Long.class.isAssignableFrom(src.getClass())) {
	                return ((Long) src).doubleValue();
	            } else if (BigDecimal.class.isAssignableFrom(src.getClass())) {
	                return ((BigDecimal) src).doubleValue();
	            } else if (Float.class.isAssignableFrom(src.getClass())) {
	                return ((Float) src).doubleValue();
	            } else if (String.class.isAssignableFrom(src.getClass())) {
	                return Double.valueOf(src.toString());
	            }
	            throw new MappingException("can not map a " + src.getClass() + " to " + Double.class.getName());
	        }
	    }
	    private static class FloatConverter implements Converter<Float> {
	        public Float convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            if(Float.class.isAssignableFrom(src.getClass())){
	                return (Float) src;
	            } else if (Integer.class.isAssignableFrom(src.getClass())) {
	                return ((Integer) src).floatValue();
	            } else if (Long.class.isAssignableFrom(src.getClass())) {
	                return ((Long) src).floatValue();
	            } else if (BigDecimal.class.isAssignableFrom(src.getClass())) {
	                return ((BigDecimal) src).floatValue();
	            } else if (Double.class.isAssignableFrom(src.getClass())) {
	                return ((Double) src).floatValue();
	            } else if (String.class.isAssignableFrom(src.getClass())) {
	                return Float.valueOf(src.toString());
	            }
	            throw new MappingException("can not map a " + src.getClass() + " to " + Float.class.getName());
	        }
	    }
	    private static class BigDecimalConverter implements Converter<BigDecimal> {
	        public BigDecimal convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            return new BigDecimal(src.toString());
	        }
	    }
	    private static class BigIntegerConverter implements Converter<BigInteger> {
	        public BigInteger convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            return new BigInteger(src.toString());
	        }
	    }
	    private static class DateConverter  implements Converter<Date>{
	        public Date convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            if(Date.class.isAssignableFrom(src.getClass())){
	                return (Date) src;
	            } else if(Long.class.isAssignableFrom(src.getClass())){
	                return new Date((Long) src);
	            } else if(String.class.isAssignableFrom(src.getClass())){
	                try {
	                    return DateFormat.getInstance().parse(src.toString());
	                } catch (ParseException e) {
	                	try {
	                		Long l = (Long) DEFAULT.get(Long.class).convert(src);
	                		return new Date((Long) l);
	                	} catch(MappingException ex) {
	                		throw new MappingException(e);
	                	}
	                }
	            }
	            throw new MappingException("can not map a " + src.getClass() + " to " + Date.class.getName());
	        }
	    }
	    private static class BooleanConverter  implements Converter<Boolean>{
	        public Boolean convert(Object src) {
	            if(src == null){
	                return null;
	            }
	            if (Boolean.class.isAssignableFrom(src.getClass())) {
	                return (Boolean) src;
	            }
	            throw new MappingException("can not map a " + src.getClass() + " to " + Boolean.class.getName());
	        }
	    }
	    
	    @Override
	    public <T> T map(Object source, TypeRef<T> typeRef, Configuration configuration) {
	    	try {
			     ParameterizedType pt = (ParameterizedType) typeRef.getType();
			     Class targetType = (Class) pt.getRawType();
			     Type[] generics = pt.getActualTypeArguments();
			     if(Map.class.isAssignableFrom(targetType)){
			         Class keyType = Object.class;
			         Class valueType = Object.class;
			         if(generics.length > 0) {
			             keyType = (Class) generics[0];
			        	 if(generics.length > 1) {
			        	     valueType = (Class) generics[1];
			        	 }
			         }
			         return (T) mapToMap(source, keyType, valueType, configuration);
			     }
			     if(List.class.isAssignableFrom(targetType)){
		        	Class componentType = Object.class;
		        	if(generics.length > 0) {
		        		componentType = (Class) generics[0];
		        	}
		        	return (T) mapToList(source, componentType, configuration);
			     }
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	return (T) map(source,(Class)typeRef.getType(), configuration);
	    }

	    private <K,V> Map<K,V> mapToMap(Object source, Class<K> genericKey, Class<V> genericValue, Configuration configuration){
	    	int idx =0;
	    	try {
	            Map<K,V> mapped = new HashMap<K,V>();            
	    		Iterable iterable = configuration.jsonProvider().toIterable(source);
	    		Iterator iterator = iterable.iterator();
	    		Object current = null;
	    		for(;iterator.hasNext();) {
	    			current = null;
	    			current = iterator.next();
	    			try {
	    				Entry entry = (Entry) current;
	    				mapped.put(map(entry.getKey(),genericKey,configuration),map(entry.getValue(),genericValue,configuration));
	    			} catch(ClassCastException e) {
	    				mapped.put(map(idx,genericKey,configuration),map(current,genericValue,configuration));
	    				idx+=1;
	    			}
	    		}
	    		return mapped;
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	    
	    private <T> List<T> mapToList(Object source, Class<T> genericType, Configuration configuration){
	    	try {
	            List<T> mapped = new ArrayList<T>();	            
	    		Iterable iterable = configuration.jsonProvider().toIterable(source);
	    		Iterator iterator = iterable.iterator();
	    		for(;iterator.hasNext();) {
	    			mapped.add(map(iterator.next(),genericType,configuration));
	    		}
	    		return mapped;
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }

	    private <B> B mapToBean(Object source, Class<B> beanType, Configuration configuration){
	    	try {
	    		B bean = beanType.newInstance();
	    		Map<String, Method> methods  = new HashMap<String,Method>();
	    		Method[] beanMethods = beanType.getDeclaredMethods();
	    		for(Method m : beanMethods) {
	    			methods.put(m.getName(), m);
	    		}
 	    		Iterable iterable = configuration.jsonProvider().toIterable(source);
 	    		Iterator iterator = iterable.iterator();
 	    		Object current = null;
 	    		for(;iterator.hasNext();) {
 	    			try {
 	    				Entry entry = (Entry) current;
 	    				String name = entry.getKey().toString();
 	    				name = name.substring(0,1).toUpperCase().concat(name.substring(1));
 	    				name = "set".concat(name);
 	    				Method method = methods.get(name);
 	    				if(method != null && method.getParameterTypes().length == 1) {
 	    					method.invoke(bean, map(entry.getValue(),method.getParameterTypes()[0],configuration));
 	    				}
 	    			} catch(ClassCastException e) {
 	    				break;
 	    			}
 	    		}	    	
 	    		return bean;
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	   
		@Override
	    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
	    	if(source == null){
	            return null;
	        }
	        if (targetType.isAssignableFrom(source.getClass())) {
	            return (T) source;
	        }
	        if(Map.class.isAssignableFrom(targetType)){
	            return (T) mapToMap(source, Object.class, Object.class, configuration);
	        }
	        if(List.class.isAssignableFrom(targetType)){
	            return (T) mapToList(source, Object.class, configuration);
	        }
	        Converter c = DEFAULT.get(targetType);
	        if(c != null) {
	        	return (T) c.convert(source);
	        }
	        return mapToBean(source, targetType, configuration);
	    }
}

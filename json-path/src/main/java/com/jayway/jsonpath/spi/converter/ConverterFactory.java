package com.jayway.jsonpath.spi.converter;

import com.jayway.jsonpath.Configuration;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterFactory {

    private static Map<Class<?>, Converter<?>> converters = new ConcurrentHashMap<Class<?>, Converter<?>>();

    static {
        registerConverter(Long.class, new LongConverter());
        registerConverter(Integer.class, new IntegerConverter());
        registerConverter(BigDecimal.class, new BigDecimalConverter());
        registerConverter(Double.class, new DoubleConverter());
        registerConverter(Date.class, new DateConverter());
        registerConverter(String.class, new StringConverter());
    }

    public static <T> Converter<T> createConverter(Class<T> target){
        Converter<T> converter = (Converter<T>) converters.get(target);
        if(converter == null){
            converter = new Converter<T>() {
                @Override
                public T convert(Object o, Configuration conf) {
                    return (T)o;
                }
            };
        }
        return converter;
    }

    public static <T> void registerConverter(Class<T> target, Converter<T> converter){
        converters.put(target, converter);
    }

    public static <T> void unRegisterConverter(Class<T> target){
        converters.remove(target);
    }

    private static class StringConverter implements Converter<String> {
        @Override
        public String convert(Object o, Configuration conf) {
            if(o == null){
                return null;
            } else {
                return o.toString();
            }
        }
    }

    private static class DateConverter implements Converter<Date> {
        @Override
        public Date convert(Object o, Configuration conf) {

            if(o == null){
                return null;
            } else if(o instanceof Date){
                return (Date)o;
            } else if(o instanceof Long){
                return new Date(((Long)o).longValue());
            } else if(o instanceof String){
                try {
                    return DateFormat.getInstance().parse(o.toString());
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Can not convert: " + o.getClass().getName() + " to: " + Integer.class.getName(), e);
                }
            }
            throw new IllegalArgumentException("Can not convert: " + o.getClass().getName() + " to: " + Integer.class.getName());
        }
    }

    private static class IntegerConverter implements Converter<Integer> {
        @Override
        public Integer convert(Object o, Configuration conf) {

            if(o == null){
                return null;
            } else if(o instanceof Integer){
                return (Integer)o;
            } else if(o instanceof Long){
                return ((Long)o).intValue();
            } else if(o instanceof Double){
                return ((Double)o).intValue();
            } else if(o instanceof BigDecimal){
                return ((BigDecimal)o).intValue();
            } else if(o instanceof Float){
                return ((Float)o).intValue();
            } else if(o instanceof String){
                return Integer.parseInt(o.toString());
            }
            throw new IllegalArgumentException("Can not convert: " + o.getClass().getName() + " to: " + Integer.class.getName());
        }
    }

    private static class LongConverter implements Converter<Long> {
        @Override
        public Long convert(Object o, Configuration conf) {

            if(o == null){
                return null;
            } else if(o instanceof Long){
                return (Long)o;
            } else if(o instanceof Integer){
                return ((Integer)o).longValue();
            } else if(o instanceof Double){
                return ((Double)o).longValue();
            } else if(o instanceof BigDecimal){
                return ((BigDecimal)o).longValue();
            } else if(o instanceof Float){
                return ((Float)o).longValue();
            } else if(o instanceof String){
                return Long.parseLong(o.toString());
            }
            throw new IllegalArgumentException("Can not convert: " + o.getClass().getName() + " to: " + Long.class.getName());
        }
    }

    private static class DoubleConverter implements Converter<Double> {
        @Override
        public Double convert(Object o, Configuration conf) {

            if(o == null){
                return null;
            } else if(o instanceof Double){
                return (Double)o;
            } else if(o instanceof Integer){
                return Double.valueOf(o.toString());
            } else if(o instanceof Long){
                return Double.valueOf(o.toString());
            }  else if(o instanceof BigDecimal){
                return ((BigDecimal)o).doubleValue();
            } else if(o instanceof Float){
                return ((Float)o).doubleValue();
            } else if(o instanceof String){
                return Double.parseDouble(o.toString());
            }
            throw new IllegalArgumentException("Can not convert: " + o.getClass().getName() + " to: " + Double.class.getName());
        }
    }

    private static class BigDecimalConverter implements Converter<BigDecimal> {
        @Override
        public BigDecimal convert(Object o, Configuration conf) {

            if(o == null){
                return null;
            } else if(o instanceof BigDecimal){
                return (BigDecimal)o;
            } else if(o instanceof Integer){
                return new BigDecimal(o.toString());
            } else if(o instanceof Long){
                return new BigDecimal(o.toString());
            } else if(o instanceof Float){
                return BigDecimal.valueOf(((Float)o).doubleValue());
            } else if(o instanceof String){
                return new BigDecimal(o.toString());
            }
            throw new IllegalArgumentException("Can not convert: " + o.getClass().getName() + " to: " + BigDecimal.class.getName());
        }
    }
}








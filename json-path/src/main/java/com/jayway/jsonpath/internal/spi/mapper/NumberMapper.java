package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.mapper.MappingException;

import java.math.BigDecimal;

public class NumberMapper extends MapperBase {

    public NumberMapper() {
        //to long
        register(Integer.class, Long.class);
        register(Double.class, Long.class);
        register(Float.class, Long.class);
        register(BigDecimal.class, Long.class);
        register(String.class, Long.class);

        //to int
        register(Long.class, Integer.class);
        register(Double.class, Integer.class);
        register(Float.class, Integer.class);
        register(BigDecimal.class, Integer.class);
        register(String.class, Integer.class);

        //to double
        register(Long.class, Double.class);
        register(Integer.class, Double.class);
        register(Float.class, Double.class);
        register(BigDecimal.class, Double.class);
        register(String.class, Double.class);

        //to float
        register(Long.class, Float.class);
        register(Integer.class, Float.class);
        register(Double.class, Float.class);
        register(BigDecimal.class, Float.class);
        register(String.class, Float.class);

        //to BigDecimal
        register(Long.class, BigDecimal.class);
        register(Integer.class, BigDecimal.class);
        register(Double.class, BigDecimal.class);
        register(Float.class, BigDecimal.class);
        register(String.class, BigDecimal.class);
    }

    @Override
    public Object convert(Object src, Class<?> srcType, Class<?> targetType, Configuration conf) {

        assertValidConversion(src, srcType, targetType);

        if (src == null) {
            return null;
        }
        //to long
        if(targetType.equals(Long.class)) {
            if (Integer.class.isAssignableFrom(srcType)) {
                return ((Integer) src).longValue();
            } else if (Double.class.isAssignableFrom(srcType)) {
                return ((Double) src).longValue();
            } else if (BigDecimal.class.isAssignableFrom(srcType)) {
                return ((BigDecimal) src).longValue();
            } else if (Float.class.isAssignableFrom(srcType)) {
                return ((Float) src).longValue();
            } else if (String.class.isAssignableFrom(srcType)) {
                return Long.parseLong(src.toString());
            }
        }
        else if(targetType.equals(Integer.class)) {
            //to int
            if (Long.class.isAssignableFrom(srcType)) {
                return ((Long) src).intValue();
            } else if (Double.class.isAssignableFrom(srcType)) {
                return ((Double) src).intValue();
            } else if (BigDecimal.class.isAssignableFrom(srcType)) {
                return ((BigDecimal) src).intValue();
            } else if (Float.class.isAssignableFrom(srcType)) {
                return ((Float) src).intValue();
            } else if (String.class.isAssignableFrom(srcType)) {
                return Integer.parseInt(src.toString());
            }
        }
        else if(targetType.equals(Double.class)) {
            //to double
            if (Long.class.isAssignableFrom(srcType)) {
                return ((Long) src).doubleValue();
            } else if (Integer.class.isAssignableFrom(srcType)) {
                return ((Integer) src).doubleValue();
            } else if (BigDecimal.class.isAssignableFrom(srcType)) {
                return ((BigDecimal) src).doubleValue();
            } else if (Float.class.isAssignableFrom(srcType)) {
                return ((Float) src).doubleValue();
            } else if (String.class.isAssignableFrom(srcType)) {
                return Double.parseDouble(src.toString());
            }
        }
        else if(targetType.equals(Float.class)) {
            //to float
            if (Long.class.isAssignableFrom(srcType) && targetType.equals(Float.class)) {
                return ((Long) src).floatValue();
            } else if (Integer.class.isAssignableFrom(srcType) && targetType.equals(Float.class)) {
                return ((Integer) src).floatValue();
            } else if (BigDecimal.class.isAssignableFrom(srcType) && targetType.equals(Float.class)) {
                return ((BigDecimal) src).floatValue();
            } else if (Double.class.isAssignableFrom(srcType) && targetType.equals(Float.class)) {
                return ((Double) src).floatValue();
            } else if (String.class.isAssignableFrom(srcType) && targetType.equals(Float.class)) {
                return Float.parseFloat(src.toString());
            }
        }
        else if(targetType.equals(BigDecimal.class)) {
            //to BigDecimal
            if (Long.class.isAssignableFrom(srcType) && targetType.equals(BigDecimal.class)) {
                return new BigDecimal(src.toString());
            } else if (Integer.class.isAssignableFrom(srcType) && targetType.equals(BigDecimal.class)) {
                return new BigDecimal(src.toString());
            } else if (Float.class.isAssignableFrom(srcType) && targetType.equals(BigDecimal.class)) {
                return new BigDecimal(src.toString());
            } else if (Double.class.isAssignableFrom(srcType) && targetType.equals(BigDecimal.class)) {
                return new BigDecimal(src.toString());
            } else if (String.class.isAssignableFrom(srcType) && targetType.equals(BigDecimal.class)) {
                return new BigDecimal(src.toString());
            }
        }



        throw new MappingException("Can not map: " + srcType.getName() + " to: " + targetType.getName());
    }
}
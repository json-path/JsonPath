package com.jayway.jsonpath.internal.spi.converter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.converter.ConversionException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateConverter extends ConverterBase {

    public DateConverter() {
        register(Long.class, Date.class);
        register(String.class, Date.class);
    }

    @Override
    public Object convert(Object src, Class<?> srcType, Class<?> targetType, Configuration conf) {

        assertValidConversion(src, srcType, targetType);

        if(src == null){
            return null;
        }
        if(Long.class.isAssignableFrom(srcType)){
            return new Date((Long) src);
        }
        else if(String.class.isAssignableFrom(srcType)){
            try {
                return DateFormat.getInstance().parse(src.toString());
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }

        throw new ConversionException("Can not convert: " + srcType.getName() + " to: " + targetType.getName());
    }
}

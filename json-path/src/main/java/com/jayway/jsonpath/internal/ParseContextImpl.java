package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.ParseContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.jayway.jsonpath.internal.Utils.notEmpty;
import static com.jayway.jsonpath.internal.Utils.notNull;

public class ParseContextImpl implements ParseContext {

    private final Configuration configuration;

    public ParseContextImpl() {
        this(Configuration.defaultConfiguration());
    }

    public ParseContextImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public DocumentContext parse(Object json) {
       return parse(json, false);
    }

    @Override
    public DocumentContext parse(String json) {
    	return parse(json, false);
    }

    @Override
    public DocumentContext parse(InputStream json) {
        return parse(json, "UTF-8", false);
    }
    
    @Override
    public DocumentContext parse(InputStream json, String charset) {
        return parse(json, charset, false);
    }

    @Override
    public DocumentContext parse(File json) throws IOException {
        return parse(json, false);
    }

    @Override
    public DocumentContext parse(URL url) throws IOException {
        return parse(url, false);
    }

    @Override
    public DocumentContext parse(Object json, boolean strict) {
        notNull(json, "json object can not be null");
        return new JsonContext(json, configuration);
    }

    @Override
    public DocumentContext parse(String json, boolean strict) {
        notEmpty(json, "json string can not be null or empty");
        Object obj = configuration.jsonProvider().parse(json, strict);
        return new JsonContext(obj, configuration);
    }

    @Override
    public DocumentContext parse(InputStream json, boolean strict) {
        return parse(json, "UTF-8", strict);
    }

    @Override
    public DocumentContext parse(InputStream json, String charset, boolean strict) {
        notNull(json, "json input stream can not be null");
        notNull(json, "charset can not be null");
        try {
            Object obj = configuration.jsonProvider().parse(json, charset, strict);
            return new JsonContext(obj, configuration);
        } finally {
            Utils.closeQuietly(json);
        }
    }

    @Override
    public DocumentContext parse(File json, boolean strict) throws IOException {
        notNull(json, "json file can not be null");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(json);
            return parse(fis, strict);
        } finally {
            Utils.closeQuietly(fis);
        }
    }

    @Override
    public DocumentContext parse(URL url, boolean strict) throws IOException {
        notNull(url, "url can not be null");
        InputStream fis = null;
        try {
            fis = url.openStream();
            return parse(fis, strict);
        } finally {
            Utils.closeQuietly(fis);
        }
    }
}

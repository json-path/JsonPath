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
package com.jayway.jsonpath;


import com.jayway.jsonpath.internal.JsonReader;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.http.HttpProviderFactory;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.jayway.jsonpath.internal.Utils.*;

/**
 * <p/>
 * JsonPath is to JSON what XPATH is to XML, a simple way to extract parts of a given document. JsonPath is
 * available in many programming languages such as Javascript, Python and PHP.
 * <p/>
 * JsonPath allows you to compile a json path string to use it many times or to compile and apply in one
 * single on demand operation.
 * <p/>
 * Given the Json document:
 * <p/>
 * <code>
 * String json =
 * "{
 * "store":
 * {
 * "book":
 * [
 * {
 * "category": "reference",
 * "author": "Nigel Rees",
 * "title": "Sayings of the Century",
 * "price": 8.95
 * },
 * {
 * "category": "fiction",
 * "author": "Evelyn Waugh",
 * "title": "Sword of Honour",
 * "price": 12.99
 * }
 * ],
 * "bicycle":
 * {
 * "color": "red",
 * "price": 19.95
 * }
 * }
 * }";
 * </code>
 * <p/>
 * A JsonPath can be compiled and used as shown:
 * <p/>
 * <code>
 * JsonPath path = JsonPath.compile("$.store.book[1]");
 * <br/>
 * List&lt;Object&gt; books = path.read(json);
 * </code>
 * </p>
 * Or:
 * <p/>
 * <code>
 * List&lt;Object&gt; authors = JsonPath.read(json, "$.store.book[*].author")
 * </code>
 * <p/>
 * If the json path returns a single value (is definite):
 * </p>
 * <code>
 * String author = JsonPath.read(json, "$.store.book[1].author")
 * </code>
 */
public class JsonPath {

    private final Path path;

    private JsonPath(String jsonPath, Predicate[] filters) {
        notNull(jsonPath, "path can not be null");
        this.path = PathCompiler.compile(jsonPath, filters);
    }

    /**
     * Returns the string representation of this JsonPath
     *
     * @return path as String
     */
    public String getPath() {
        return this.path.toString();
    }

    /**
     * @see JsonPath#isDefinite()
     */
    public static boolean isPathDefinite(String path) {
        return compile(path).isDefinite();
    }


    /**
     * Checks if a path points to a single item or if it potentially returns multiple items
     * <p/>
     * a path is considered <strong>not</strong> definite if it contains a scan fragment ".."
     * or an array position fragment that is not based on a single index
     * <p/>
     * <p/>
     * definite path examples are:
     * <p/>
     * $store.book
     * $store.book[1].title
     * <p/>
     * not definite path examples are:
     * <p/>
     * $..book
     * $.store.book[*]
     * $.store.book[1,2]
     * $.store.book[?(@.category = 'fiction')]
     *
     * @return true if path is definite (points to single item)
     */
    public boolean isDefinite() {
        return path.isDefinite();
    }

    /**
     * Applies this JsonPath to the provided json document.
     * Note that the document must be identified as either a List or Map by
     * the {@link JsonProvider}
     *
     * @param jsonObject a container Object
     * @param <T>        expected return type
     * @return object(s) matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(Object jsonObject) {
        return read(jsonObject, Configuration.defaultConfiguration());
    }

    /**
     * Applies this JsonPath to the provided json document.
     * Note that the document must be identified as either a List or Map by
     * the {@link JsonProvider}
     *
     * @param jsonObject    a container Object
     * @param configuration configuration to use
     * @param <T>           expected return type
     * @return object(s) matched by the given path
     */
    @SuppressWarnings("unchecked")
    public <T> T read(Object jsonObject, Configuration configuration) {
        boolean optAsPathList = configuration.containsOption(Option.AS_PATH_LIST);
        boolean optAlwaysReturnList = configuration.containsOption(Option.ALWAYS_RETURN_LIST);
        boolean optSuppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS);

        try {
            if(optAsPathList){
                return  (T)path.evaluate(jsonObject, configuration).getPath();
            } else {
                Object res = path.evaluate(jsonObject, configuration).getValue();
                if(optAlwaysReturnList && path.isDefinite()){
                    Object array = configuration.getProvider().createArray();
                    configuration.getProvider().setProperty(array, 0, res);
                    return (T)array;
                } else {
                    return (T)res;
                }
            }
        } catch (RuntimeException e){
            if(!optSuppressExceptions){
                throw e;
            }
        }
        if(optAsPathList){
            return (T)configuration.getProvider().createArray();
        } else {
            if(optAlwaysReturnList){
                return (T)configuration.getProvider().createArray();
            } else {
                return (T)(path.isDefinite() ? null : configuration.getProvider().createArray());
            }
        }

    }


    /**
     * Applies this JsonPath to the provided json string
     *
     * @param json a json string
     * @param <T>  expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(String json) {
        return read(json, Configuration.defaultConfiguration());
    }

    /**
     * Applies this JsonPath to the provided json string
     *
     * @param json          a json string
     * @param configuration configuration to use
     * @param <T>           expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(String json, Configuration configuration) {
        notEmpty(json, "json can not be null or empty");
        notNull(configuration, "jsonProvider can not be null");

        return read(configuration.getProvider().parse(json), configuration);
    }

    /**
     * Applies this JsonPath to the provided json URL
     *
     * @param jsonURL url to read from
     * @param <T>     expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL) throws IOException {
        return read(jsonURL, Configuration.defaultConfiguration());
    }

    /**
     * Applies this JsonPath to the provided json URL
     *
     * @param jsonURL       url to read from
     * @param configuration configuration to use
     * @param <T>           expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(URL jsonURL, Configuration configuration) throws IOException {
        notNull(jsonURL, "json URL can not be null");
        notNull(configuration, "jsonProvider can not be null");

        InputStream in = null;
        try {
            in = HttpProviderFactory.getProvider().get(jsonURL);
            return read(configuration.getProvider().parse(in), configuration);
        } finally {
            Utils.closeQuietly(in);
        }
    }

    /**
     * Applies this JsonPath to the provided json file
     *
     * @param jsonFile file to read from
     * @param <T>      expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile) throws IOException {
        return read(jsonFile, Configuration.defaultConfiguration());
    }


    /**
     * Applies this JsonPath to the provided json file
     *
     * @param jsonFile      file to read from
     * @param configuration configuration to use
     * @param <T>           expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(File jsonFile, Configuration configuration) throws IOException {
        notNull(jsonFile, "json file can not be null");
        isTrue(jsonFile.exists(), "json file does not exist");
        notNull(configuration, "jsonProvider can not be null");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jsonFile);
            return read(configuration.getProvider().parse(fis), configuration);
        } finally {
            Utils.closeQuietly(fis);
        }
    }

    /**
     * Applies this JsonPath to the provided json input stream
     *
     * @param jsonInputStream input stream to read from
     * @param <T>             expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream) throws IOException {
        return read(jsonInputStream, Configuration.defaultConfiguration());
    }

    /**
     * Applies this JsonPath to the provided json input stream
     *
     * @param jsonInputStream input stream to read from
     * @param configuration   configuration to use
     * @param <T>             expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    @SuppressWarnings({"unchecked"})
    public <T> T read(InputStream jsonInputStream, Configuration configuration) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notNull(configuration, "configuration can not be null");

        try {
            return read(configuration.getProvider().parse(jsonInputStream), configuration);
        } finally {
            Utils.closeQuietly(jsonInputStream);
        }
    }

    // --------------------------------------------------------
    //
    // Static factory methods
    //
    // --------------------------------------------------------

    /**
     * Compiles a JsonPath
     *
     * @param jsonPath to compile
     * @param filters  filters to be applied to the filter place holders  [?] in the path
     * @return compiled JsonPath
     */
    public static JsonPath compile(String jsonPath, Predicate... filters) {
        notEmpty(jsonPath, "json can not be null or empty");

        return new JsonPath(jsonPath, filters);
    }


    // --------------------------------------------------------
    //
    // Static utility functions
    //
    // --------------------------------------------------------

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     *
     * @param json     a json object
     * @param jsonPath the json path
     * @param filters  filters to be applied to the filter place holders  [?] in the path
     * @param <T>      expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T read(Object json, String jsonPath, Predicate... filters) {
        return parse(json).read(jsonPath, filters);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json string
     *
     * @param json     a json string
     * @param jsonPath the json path
     * @param filters  filters to be applied to the filter place holders  [?] in the path
     * @param <T>      expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T read(String json, String jsonPath, Predicate... filters) {
        return new JsonReader().parse(json).read(jsonPath, filters);
    }


    /**
     * Creates a new JsonPath and applies it to the provided Json object
     *
     * @param jsonURL  url pointing to json doc
     * @param jsonPath the json path
     * @param filters  filters to be applied to the filter place holders  [?] in the path
     * @param <T>      expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T read(URL jsonURL, String jsonPath, Predicate... filters) throws IOException {
        return new JsonReader().parse(jsonURL).read(jsonPath, filters);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     *
     * @param jsonFile json file
     * @param jsonPath the json path
     * @param filters  filters to be applied to the filter place holders  [?] in the path
     * @param <T>      expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T read(File jsonFile, String jsonPath, Predicate... filters) throws IOException {
        return new JsonReader().parse(jsonFile).read(jsonPath, filters);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     *
     * @param jsonInputStream json input stream
     * @param jsonPath        the json path
     * @param filters         filters to be applied to the filter place holders  [?] in the path
     * @param <T>             expected return type
     * @return list of objects matched by the given path
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T read(InputStream jsonInputStream, String jsonPath, Predicate... filters) throws IOException {
        return new JsonReader().parse(jsonInputStream).read(jsonPath, filters);
    }


    // --------------------------------------------------------
    //
    // Static Fluent API
    //
    // --------------------------------------------------------


    /**
     * Creates a {@link ParseContext} that can be used to parse a given JSON input.
     *
     * @param configuration configuration to use when parsing JSON
     * @return a parsing context based on given configuration
     */
    public static ParseContext using(Configuration configuration) {
        return new JsonReader(configuration);
    }

    /**
     * Creates a {@link ParseContext} that will parse a given JSON input.
     *
     * @param provider provider to use when parsing JSON
     * @return a parsing context based on given provider
     */
    public static ParseContext using(JsonProvider provider) {
        return new JsonReader(Configuration.builder().jsonProvider(provider).build());
    }

    /**
     * Parses the given JSON input using the default {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json input
     * @return a read context
     */
    public static ReadContext parse(Object json) {
        return new JsonReader().parse(json);
    }

    /**
     * Parses the given JSON input using the default {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json string
     * @return a read context
     */
    public static ReadContext parse(String json) {
        return new JsonReader().parse(json);
    }

    /**
     * Parses the given JSON input using the default {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json stream
     * @return a read context
     */
    public static ReadContext parse(InputStream json) {
        return new JsonReader().parse(json);
    }

    /**
     * Parses the given JSON input using the default {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json file
     * @return a read context
     */
    public static ReadContext parse(File json) throws IOException {
        return new JsonReader().parse(json);
    }

    /**
     * Parses the given JSON input using the default {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json url
     * @return a read context
     */
    public static ReadContext parse(URL json) throws IOException {
        return new JsonReader().parse(json);
    }

    /**
     * Parses the given JSON input using the provided {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json input
     * @return a read context
     */
    public static ReadContext parse(Object json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    /**
     * Parses the given JSON input using the provided {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json input
     * @return a read context
     */
    public static ReadContext parse(String json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    /**
     * Parses the given JSON input using the provided {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json input
     * @return a read context
     */
    public static ReadContext parse(InputStream json, Configuration configuration) {
        return new JsonReader(configuration).parse(json);
    }

    /**
     * Parses the given JSON input using the provided {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json input
     * @return a read context
     */
    public static ReadContext parse(File json, Configuration configuration) throws IOException {
        return new JsonReader(configuration).parse(json);
    }

    /**
     * Parses the given JSON input using the provided {@link Configuration} and
     * returns a {@link ReadContext} for path evaluation
     *
     * @param json input
     * @return a read context
     */
    public static ReadContext parse(URL json, Configuration configuration) throws IOException {
        return new JsonReader(configuration).parse(json);
    }
}

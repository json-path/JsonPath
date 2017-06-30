package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.path.PathCompiler;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
import com.jayway.jsonpath.spi.json.JsonProvider;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class ValueNode {

    public static final NullNode NULL_NODE = new NullNode();
    public static final BooleanNode TRUE = new BooleanNode("true");
    public static final BooleanNode FALSE = new BooleanNode("false");
    public static final UndefinedNode UNDEFINED = new UndefinedNode();


    public abstract Class<?> type(Predicate.PredicateContext ctx);

    public boolean isPatternNode() {
        return false;
    }

    public PatternNode asPatternNode() {
        throw new InvalidPathException("Expected regexp node");
    }

    public boolean isPathNode() {
        return false;
    }

    public PathNode asPathNode() {
        throw new InvalidPathException("Expected path node");
    }

    public boolean isNumberNode() {
        return false;
    }

    public NumberNode asNumberNode() {
        throw new InvalidPathException("Expected number node");
    }

    public boolean isStringNode() {
        return false;
    }

    public StringNode asStringNode() {
        throw new InvalidPathException("Expected string node");
    }

    public boolean isBooleanNode() {
        return false;
    }

    public BooleanNode asBooleanNode() {
        throw new InvalidPathException("Expected boolean node");
    }

    public boolean isJsonNode() {
        return false;
    }

    public JsonNode asJsonNode() {
        throw new InvalidPathException("Expected json node");
    }

    public boolean isPredicateNode() {
        return false;
    }

    public PredicateNode asPredicateNode() {
        throw new InvalidPathException("Expected predicate node");
    }

    public boolean isValueListNode() {
        return false;
    }

    public ValueListNode asValueListNode() {
        throw new InvalidPathException("Expected value list node");
    }

    public boolean isNullNode() {
        return false;
    }

    public NullNode asNullNode() {
        throw new InvalidPathException("Expected null node");
    }

    public UndefinedNode asUndefinedNode() {
        throw new InvalidPathException("Expected undefined node");
    }

    public boolean isUndefinedNode() {
        return false;
    }

    public boolean isClassNode() {
        return false;
    }

    public ClassNode asClassNode() {
        throw new InvalidPathException("Expected class node");
    }

    private static boolean isPath(Object o) {
        if(o == null || !(o instanceof String)){
            return false;
        }
        String str = o.toString().trim();
        if (str.length() <= 0) {
            return false;
        }
        char c0 = str.charAt(0);
        if(c0 == '@' || c0 == '$'){
            try {
                PathCompiler.compile(str);
                return true;
            } catch(Exception e){
                return false;
            }
        }
        return false;
    }

    private static boolean isJson(Object o) {
        if(o == null || !(o instanceof String)){
            return false;
        }
        String str = o.toString().trim();
        if (str.length() <= 1) {
            return false;
        }
        char c0 = str.charAt(0);
        char c1 = str.charAt(str.length() - 1);
        if ((c0 == '[' && c1 == ']') || (c0 == '{' && c1 == '}')){
            try {
                new JSONParser(JSONParser.MODE_PERMISSIVE).parse(str);
                return true;
            } catch(Exception e){
                return false;
            }
        }
        return false;
    }



    //----------------------------------------------------
    //
    // Factory methods
    //
    //----------------------------------------------------
    public static ValueNode toValueNode(Object o){
        if(o == null) return ValueNode.NULL_NODE;
        if(o instanceof ValueNode) return (ValueNode)o;
        if(o instanceof Class) return createClassNode((Class)o);
        else if(isPath(o)) return new PathNode(o.toString(), false, false);
        else if(isJson(o)) return createJsonNode(o.toString());
        else if(o instanceof String) return createStringNode(o.toString(), true);
        else if(o instanceof Character) return createStringNode(o.toString(), false);
        else if(o instanceof Number) return createNumberNode(o.toString());
        else if(o instanceof Boolean) return createBooleanNode(o.toString());
        else if(o instanceof Pattern) return createPatternNode((Pattern)o);
        else throw new JsonPathException("Could not determine value type");
    }

    public static StringNode createStringNode(CharSequence charSequence, boolean escape){
        return new StringNode(charSequence, escape);
    }

    public static ClassNode createClassNode(Class<?> clazz){
        return new ClassNode(clazz);
    }

    public static NumberNode createNumberNode(CharSequence charSequence){
        return new NumberNode(charSequence);
    }

    public static BooleanNode createBooleanNode(CharSequence charSequence){
        return Boolean.parseBoolean(charSequence.toString()) ? TRUE : FALSE;
    }

    public static NullNode createNullNode(){
        return NULL_NODE;
    }

    public static JsonNode createJsonNode(CharSequence json) {
        return new JsonNode(json);
    }

    public static JsonNode createJsonNode(Object parsedJson) {
        return new JsonNode(parsedJson);
    }

    public static PatternNode createPatternNode(CharSequence pattern) {
        return new PatternNode(pattern);
    }

    public static PatternNode createPatternNode(Pattern pattern) {
        return new PatternNode(pattern);
    }

    public static UndefinedNode createUndefinedNode() {
        return UNDEFINED;
    }

    public static PathNode createPathNode(CharSequence path, boolean existsCheck, boolean shouldExists) {
        return new PathNode(path, existsCheck, shouldExists);
    }

    public static ValueNode createPathNode(Path path) {
        return new PathNode(path);
    }

    //----------------------------------------------------
    //
    // ValueNode Implementations
    //
    //----------------------------------------------------
    public static class PatternNode extends ValueNode {
        private final String pattern;
        private final Pattern compiledPattern;

        private PatternNode(CharSequence charSequence) {
            String tmp = charSequence.toString();
            int begin = tmp.indexOf('/');
            int end = tmp.lastIndexOf('/');
            int flags = tmp.endsWith("/i") ? Pattern.CASE_INSENSITIVE : 0;
            this.pattern = tmp.substring(begin + 1, end);
            this.compiledPattern  = Pattern.compile(pattern, flags);
        }

        public PatternNode(Pattern pattern) {
            this.pattern = pattern.pattern();
            this.compiledPattern = pattern;
        }


        public Pattern getCompiledPattern() {
            return compiledPattern;
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Void.TYPE;
        }

        public boolean isPatternNode() {
            return true;
        }

        public PatternNode asPatternNode() {
            return this;
        }

        @Override
        public String toString() {

            String flags = "";
            if((compiledPattern.flags() & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE){
                flags = "i";
            }
            if(!pattern.startsWith("/")){
                return "/" + pattern + "/" + flags;
            } else {
                return pattern;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PatternNode)) return false;

            PatternNode that = (PatternNode) o;

            return !(compiledPattern != null ? !compiledPattern.equals(that.compiledPattern) : that.compiledPattern != null);

        }
    }

    public static class JsonNode extends ValueNode {
        private final Object json;
        private final boolean parsed;

        private JsonNode(CharSequence charSequence) {
            json = charSequence.toString();
            parsed = false;
        }

        public JsonNode(Object parsedJson) {
            json = parsedJson;
            parsed = true;
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            if(isArray(ctx)) return List.class;
            else if(isMap(ctx)) return Map.class;
            else if(parse(ctx) instanceof Number) return Number.class;
            else if(parse(ctx) instanceof String) return String.class;
            else if(parse(ctx) instanceof Boolean) return Boolean.class;
            else return Void.class;
        }

        public boolean isJsonNode() {
            return true;
        }

        public JsonNode asJsonNode() {
            return this;
        }

        public ValueNode asValueListNode(Predicate.PredicateContext ctx){
            if(!isArray(ctx)){
                return UNDEFINED;
            } else {
                return new ValueListNode(Collections.unmodifiableList((List) parse(ctx)));
            }
        }

        public Object parse(Predicate.PredicateContext ctx){
            try {
              return parsed ? json : new JSONParser(JSONParser.MODE_PERMISSIVE).parse(json.toString());
            } catch (ParseException e) {
              throw new IllegalArgumentException(e);
            }
        }

        public boolean isParsed() {
            return parsed;
        }

        public Object getJson() {
            return json;
        }

        public boolean isArray(Predicate.PredicateContext ctx) {
            return ctx.configuration().jsonProvider().isArray(parse(ctx));
        }

        public boolean isMap(Predicate.PredicateContext ctx) {
            return ctx.configuration().jsonProvider().isMap(parse(ctx));
        }

        public int length(Predicate.PredicateContext ctx) {
            return isArray(ctx) ? ctx.configuration().jsonProvider().length(parse(ctx)) : -1;
        }

        public boolean isEmpty(Predicate.PredicateContext ctx) {
            if (isArray(ctx) || isMap(ctx)) return ctx.configuration().jsonProvider().length(parse(ctx)) == 0;
            else if((parse(ctx) instanceof String)) return ((String)parse(ctx)).length() == 0;
            return true;
        }

        @Override
        public String toString() {
            return json.toString();
        }

        public boolean equals(JsonNode jsonNode, Predicate.PredicateContext ctx) {
            if (this == jsonNode) return true;
            return !(json != null ? !json.equals(jsonNode.parse(ctx)) : jsonNode.json != null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof JsonNode)) return false;

            JsonNode jsonNode = (JsonNode) o;

            return !(json != null ? !json.equals(jsonNode.json) : jsonNode.json != null);
        }
    }

    public static class StringNode extends ValueNode {
        private final String string;
        private boolean useSingleQuote = true;

        private StringNode(CharSequence charSequence, boolean escape) {
            if(charSequence.length() > 1){
                char open = charSequence.charAt(0);
                char close = charSequence.charAt(charSequence.length()-1);

                if(open == '\'' && close == '\''){
                    charSequence = charSequence.subSequence(1, charSequence.length()-1);
                } else if(open == '"' && close == '"'){
                    charSequence = charSequence.subSequence(1, charSequence.length()-1);
                    useSingleQuote = false;
                }
            }
            string = escape ? Utils.unescape(charSequence.toString()) : charSequence.toString();
        }

        @Override
        public NumberNode asNumberNode() {
            BigDecimal number = null;
            try {
                number = new BigDecimal(string);
            } catch (NumberFormatException nfe){
                return NumberNode.NAN;
            }
            return new NumberNode(number);
        }

        public String getString() {
            return string;
        }

        public int length(){
            return getString().length();
        }

        public boolean isEmpty(){
            return getString().isEmpty();
        }

        public boolean contains(String str) {
            return getString().contains(str);
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return String.class;
        }

        public boolean isStringNode() {
            return true;
        }

        public StringNode asStringNode() {
            return this;
        }

        @Override
        public String toString() {
            String quote = useSingleQuote ? "'" : "\"";
            return quote + Utils.escape(string, true) + quote;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StringNode) && !(o instanceof NumberNode)) return false;

            StringNode that = ((ValueNode) o).asStringNode();

            return !(string != null ? !string.equals(that.getString()) : that.getString() != null);

        }
    }

    public static class NumberNode extends ValueNode {

        public static NumberNode NAN = new NumberNode((BigDecimal)null);

        private final BigDecimal number;

        private NumberNode(BigDecimal number) {
            this.number = number;
        }
        private NumberNode(CharSequence num) {
            number = new BigDecimal(num.toString());
        }

        @Override
        public StringNode asStringNode() {
            return new StringNode(number.toString(), false);
        }

        public BigDecimal getNumber() {
            return number;
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Number.class;
        }

        public boolean isNumberNode() {
            return true;
        }

        public NumberNode asNumberNode() {
            return this;
        }

        @Override
        public String toString() {
            return number.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NumberNode) && !(o instanceof StringNode)) return false;

            NumberNode that = ((ValueNode)o).asNumberNode();

            if(that == NumberNode.NAN){
                return false;
            } else {
                return number.compareTo(that.number) == 0;
            }
        }
    }

    public static class BooleanNode extends ValueNode {
        private final Boolean value;

        private BooleanNode(CharSequence boolValue) {
            value = Boolean.parseBoolean(boolValue.toString());
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Boolean.class;
        }

        public boolean isBooleanNode() {
            return true;
        }

        public BooleanNode asBooleanNode() {
            return this;
        }

        public boolean getBoolean() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BooleanNode)) return false;

            BooleanNode that = (BooleanNode) o;

            return !(value != null ? !value.equals(that.value) : that.value != null);
        }
    }

    public static class ClassNode extends ValueNode {
        private final Class clazz;

        private ClassNode(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Class.class;
        }

        public boolean isClassNode() {
            return true;
        }

        public ClassNode asClassNode() {
            return this;
        }

        public Class getClazz() {
            return clazz;
        }

        @Override
        public String toString() {
            return clazz.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassNode)) return false;

            ClassNode that = (ClassNode) o;

            return !(clazz != null ? !clazz.equals(that.clazz) : that.clazz != null);
        }
    }

    public static class NullNode extends ValueNode {

        private NullNode() {}

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Void.class;
        }

        @Override
        public boolean isNullNode() {
            return true;
        }

        @Override
        public NullNode asNullNode() {
            return this;
        }

        @Override
        public String toString() {
            return "null";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NullNode)) return false;

            return true;
        }
    }

    public static class UndefinedNode extends ValueNode {

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Void.class;
        }

        public UndefinedNode asUndefinedNode() {
            return this;
        }

        public boolean isUndefinedNode() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    public static class PredicateNode extends ValueNode {

        private final Predicate predicate;

        public PredicateNode(Predicate predicate) {
            this.predicate = predicate;
        }

        public Predicate getPredicate() {
            return predicate;
        }

        public PredicateNode asPredicateNode() {
            return this;
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Void.class;
        }

        public boolean isPredicateNode() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public String toString() {
            return predicate.toString();
        }
    }

    public static class ValueListNode extends ValueNode implements Iterable<ValueNode> {

        private List<ValueNode> nodes = new ArrayList<ValueNode>();

        public ValueListNode(Collection<?> values) {
            for (Object value : values) {
                nodes.add(toValueNode(value));
            }
        }

        public boolean contains(ValueNode node){
            return nodes.contains(node);
        }

        public boolean subsetof(ValueListNode right) {
            for (ValueNode leftNode : nodes) {
                if (!right.nodes.contains(leftNode)) {
                    return false;
                }
            }
            return true;
        }

        public List<ValueNode> getNodes() {
            return Collections.unmodifiableList(nodes);
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return List.class;
        }

        public boolean isValueListNode() {
            return true;
        }

        public ValueListNode asValueListNode() {
            return this;
        }

        @Override
        public String toString() {
            return "[" + Utils.join(",", nodes) + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ValueListNode)) return false;

            ValueListNode that = (ValueListNode) o;

            return !(that != null ? !nodes.equals(that.nodes) : that.nodes != null);
        }

        @Override
        public Iterator<ValueNode> iterator() {
            return nodes.iterator();
        }
    }

    public static class PathNode extends ValueNode {

        private static final Logger logger = LoggerFactory.getLogger(PathNode.class);

        private final Path path;
        private final boolean existsCheck;
        private final boolean shouldExist;

        PathNode(Path path) {
            this(path, false, false);
        }

        PathNode(CharSequence charSequence, boolean existsCheck, boolean shouldExist) {
            this(PathCompiler.compile(charSequence.toString()), existsCheck, shouldExist);
        }

        PathNode(Path path, boolean existsCheck, boolean shouldExist) {
            this.path = path;
            this.existsCheck = existsCheck;
            this.shouldExist = shouldExist;
            logger.trace("PathNode {} existsCheck: {}", path, existsCheck);
        }

        public Path getPath() {
            return path;
        }

        public boolean isExistsCheck() {
            return existsCheck;
        }

        public boolean shouldExists() {
            return shouldExist;
        }

        @Override
        public Class<?> type(Predicate.PredicateContext ctx) {
            return Void.class;
        }

        public boolean isPathNode() {
            return true;
        }

        public PathNode asPathNode() {
            return this;
        }

        public PathNode asExistsCheck(boolean shouldExist) {
            return new PathNode(path, true, shouldExist);
        }

        @Override
        public String toString() {
            return existsCheck && ! shouldExist ? Utils.concat("!" , path.toString()) : path.toString();
        }

        public ValueNode evaluate(Predicate.PredicateContext ctx) {
            if (isExistsCheck()) {
                try {
                    Configuration c = Configuration.builder().jsonProvider(ctx.configuration().jsonProvider()).options(Option.REQUIRE_PROPERTIES).build();
                    Object result = path.evaluate(ctx.item(), ctx.root(), c).getValue(false);
                    return result == JsonProvider.UNDEFINED ? ValueNode.FALSE : ValueNode.TRUE;
                } catch (PathNotFoundException e) {
                    return ValueNode.FALSE;
                }
            } else {
                try {
                    Object res;
                    if (ctx instanceof PredicateContextImpl) {
                        //This will use cache for document ($) queries
                        PredicateContextImpl ctxi = (PredicateContextImpl) ctx;
                        res = ctxi.evaluate(path);
                    } else {
                        Object doc = path.isRootPath() ? ctx.root() : ctx.item();
                        res = path.evaluate(doc, ctx.root(), ctx.configuration()).getValue();
                    }
                    res = ctx.configuration().jsonProvider().unwrap(res);

                    if (res instanceof Number) return ValueNode.createNumberNode(res.toString());
                    else if (res instanceof BigDecimal) return ValueNode.createNumberNode(res.toString());
                    else if (res instanceof String) return ValueNode.createStringNode(res.toString(), false);
                    else if (res instanceof Boolean) return ValueNode.createBooleanNode(res.toString());
                    else if (res == null) return ValueNode.NULL_NODE;
                    else if (ctx.configuration().jsonProvider().isArray(res)) return ValueNode.createJsonNode(res);
                    else if (ctx.configuration().jsonProvider().isMap(res)) return ValueNode.createJsonNode(res);
                    else throw new JsonPathException("Could not convert " + res.toString() + " to a ValueNode");
                } catch (PathNotFoundException e) {
                    return ValueNode.UNDEFINED;
                }
            }
        }


    }
}

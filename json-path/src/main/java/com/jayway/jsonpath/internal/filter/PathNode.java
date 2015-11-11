package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.token.PredicateContextImpl;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class PathNode extends ValueNode {

    private static final Logger logger = LoggerFactory.getLogger(PathNode.class);

    private final Path path;
    private final boolean existsCheck;
    private final boolean shouldExist;

    public PathNode(Path path) {
        this(path, false, false);
    }

    public PathNode(CharSequence charSequence, boolean existsCheck, boolean shouldExist) {
        this(PathCompiler.compile(charSequence.toString()), existsCheck, shouldExist);
    }

    public PathNode(Path path, boolean existsCheck, boolean shouldExist) {
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
        return path.toString();
    }

    public ValueNode evaluate(Predicate.PredicateContext ctx) {
        Configuration c = Configuration.builder().jsonProvider(ctx.configuration().jsonProvider()).options(Option.REQUIRE_PROPERTIES).build();
        if (isExistsCheck()) {
            try {
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
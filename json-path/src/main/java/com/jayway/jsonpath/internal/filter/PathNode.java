package com.jayway.jsonpath.internal.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.path.PathCompiler;

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
        return existsCheck && ! shouldExist ? Utils.concat("!" , path.toString()) : path.toString();
    }
}
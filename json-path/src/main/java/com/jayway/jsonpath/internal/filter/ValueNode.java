package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;

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
}

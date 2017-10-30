package com.jayway.jsonpath.internal.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Utils;

public class ValueListNode extends ValueNode implements Iterable<ValueNode> {
	
    private List<ValueNode> nodes = new ArrayList<ValueNode>();

    public ValueListNode(Collection<ValueNode> values) {
        for (ValueNode value : values) {
            nodes.add(value);
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
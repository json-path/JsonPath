package com.jayway.jsonpath.internal.filter;

import java.math.BigDecimal;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Utils;

public class StringNode extends ValueNode {
    private final String string;
    private boolean useSingleQuote = true;

    public StringNode(CharSequence charSequence, boolean escape) {
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
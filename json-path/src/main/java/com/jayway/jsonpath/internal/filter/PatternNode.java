package com.jayway.jsonpath.internal.filter;

import java.util.regex.Pattern;

import com.jayway.jsonpath.Predicate;

//----------------------------------------------------
//
// ValueNode Implementations
//
//----------------------------------------------------
public class PatternNode extends ValueNode {
    private final String pattern;
    private final Pattern compiledPattern;

    public PatternNode(CharSequence charSequence) {
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
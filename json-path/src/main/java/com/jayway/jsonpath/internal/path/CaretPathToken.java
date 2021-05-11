package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class CaretPathToken extends PathToken {

    public final static char SINGLE_QUOTE = '\'';
    public final static char CARET = '^';

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if ( isRoot() ) return;
        String evalPath = Utils.concat(currentPath, getPathFragment());
        Object p = getParentModel(currentPath, model, ctx);
        if ( p == null ) return;
        PathRef pathRef = ctx.forUpdate() ? parent : PathRef.NO_OP;
        if ( isLeaf() ) {
            ctx.addResult(evalPath, pathRef, p);
        } else {
            next().evaluate(evalPath, pathRef, p, ctx);
        }
    }

    private Object getParentModel(final String path, final Object model, final EvaluationContextImpl ctx) {
        Object root = ctx.rootDocument();
        List list = tokenizePath(path);
        list = list.subList(0, list.size() - getParentDepth(path));
        Object p = root;
        Object last = null;
        for ( Object o : list ) {
            last = p;
            p = searchObject(o, p, ctx);
        }
        return p;
    }
    private int getParentDepth(final String path) {
        int i = 0;
        while ( true ) {
            if ( path.charAt(path.length() - 1 - i) == CARET ) {
                i++;
            } else {
                break;
            }
        }
        return i;
    }

    private Object searchObject(Object key, Object o, final EvaluationContextImpl ctx) {
        if ( key instanceof String ) {
            return ctx.jsonProvider().getMapValue(o, (String)key);
        } else if ( key instanceof Integer ) {
            return ctx.jsonProvider().getArrayIndex(o, (Integer)key);
        }
        return null;
    }

    private List<Object> tokenizePath(final String path) {
        StringTokenizer st = new StringTokenizer(path, "$[]^");
        List obj = new ArrayList();
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken().trim();
            if ( token.indexOf(SINGLE_QUOTE) == 0 ) {
                obj.add(token.substring(1, token.length() - 1));
            } else {
                try {
                    obj.add(Integer.parseInt(token));
                } catch ( NumberFormatException ex) {
                    ;
                }
            }
        }
        return obj;
    }
    @Override
    public boolean isTokenDefinite() {
        return false;
    }

    @Override
    protected String getPathFragment() {
        return "^";
    }
}

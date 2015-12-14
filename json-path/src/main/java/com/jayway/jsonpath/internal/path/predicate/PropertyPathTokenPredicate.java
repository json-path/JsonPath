package com.jayway.jsonpath.internal.path.predicate;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.token.PathToken;
import com.jayway.jsonpath.internal.path.token.PropertyPathToken;

import java.util.Collection;

public final class PropertyPathTokenPredicate implements PathTokenPredicate {
    private final EvaluationContextImpl ctx;
    private PropertyPathToken propertyPathToken;

    public PropertyPathTokenPredicate(PathToken target, EvaluationContextImpl ctx) {
        this.ctx = ctx;
        propertyPathToken = (PropertyPathToken) target;
    }

    @Override
    public boolean matches(Object model) {
        if (! ctx.jsonProvider().isMap(model)) {
            return false;
        }

//
// The commented code below makes it really hard understand, use and predict the result
// of deep scanning operations. It might be correct but was decided to be
// left out until the behavior of REQUIRE_PROPERTIES is more strictly defined
// in a deep scanning scenario. For details read conversation in commit
// https://github.com/jayway/JsonPath/commit/1a72fc078deb16995e323442bfb681bd715ce45a#commitcomment-14616092
//
//            if (ctx.options().contains(Option.REQUIRE_PROPERTIES)) {
//                // Have to require properties defined in path when an indefinite path is evaluated,
//                // so have to go there and search for it.
//                return true;
//            }

        if (! propertyPathToken.isTokenDefinite()) {
            // It's responsibility of PropertyPathToken code to handle indefinite scenario of properties,
            // so we'll allow it to do its job.
            return true;
        }

        if (propertyPathToken.isLeaf() && ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
            // In case of DEFAULT_PATH_LEAF_TO_NULL missing properties is not a problem.
            return true;
        }

        Collection<String> keys = ctx.jsonProvider().getPropertyKeys(model);
        return keys.containsAll(propertyPathToken.getProperties());
    }
}
